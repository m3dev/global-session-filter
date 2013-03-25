/*
 * Copyright 2012 M3, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.m3.globalsession;

import com.m3.globalsession.store.SessionStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("deprecation")
public class GlobalHttpSession implements HttpSession {

    public static class Metadata implements Serializable {

        private Boolean invalidated;
        private Date creationTime;
        private Date lastAccessedTime;

        public Metadata() {
        }

        public Boolean getInvalidated() {
            return invalidated;
        }

        public void setInvalidated(Boolean invalidated) {
            this.invalidated = invalidated;
        }

        public Date getCreationTime() {
            return creationTime;
        }

        public void setCreationTime(Date creationTime) {
            this.creationTime = creationTime;
        }

        public Date getLastAccessedTime() {
            return lastAccessedTime;
        }

        public void setLastAccessedTime(Date lastAccessedTime) {
            this.lastAccessedTime = lastAccessedTime;
        }

        @Override
        public String toString() {
            return "com.m3.globalsession.GlobalHttpSession$Metadata(invalidated: " + getInvalidated()
                    + ", creationTime: " + getCreationTime() + ", lastAccessedTime: " + getLastAccessedTime() + ")";
        }

    }

    public static final String ATTRIBUTES_KEY = "__attributes__";
    public static final String METADATA_KEY = "__metadata__";

    private static final Logger log = LoggerFactory.getLogger(GlobalHttpSession.class);

    // should be Serializable type
    private ConcurrentHashMap<String, Object> attributes = new ConcurrentHashMap<String, Object>();
    private Metadata metadata;

    private final String sessionId;
    private final SessionStore store;
    private final HttpSession session;
    private final StoreKeyGenerator keyGenerator;

    private boolean isNewlyCreated = false;
    private Integer maxInactiveIntervalSeconds = null;

    public StoreKeyGenerator getKeyGenerator() {
        return keyGenerator;
    }

    public boolean isValid() {

        Metadata metadata = store.get(keyGenerator.generate(METADATA_KEY));
        boolean isNotInvalidated = metadata != null &&
                metadata.getInvalidated() != null &&
                metadata.getInvalidated() == false;

        boolean isNotExpired = store.get(keyGenerator.generate(ATTRIBUTES_KEY)) != null;

        if (log.isDebugEnabled()) {
            log.debug("isValid is called. (isNotInvalidated: " + isNotInvalidated + ", isNotExpired: " + isNotExpired + ")");
        }

        return isNotInvalidated && isNotExpired;
    }

    public synchronized void reloadAttributes() {
        attributes.clear();

        ConcurrentHashMap<String, Object> cachedAttributes = store.get(keyGenerator.generate(ATTRIBUTES_KEY));
        if (cachedAttributes != null) {
            attributes.putAll(cachedAttributes);
        }
    }

    public ConcurrentHashMap<String, Object> toMap() {
        return attributes;
    }

    public void save() {
        if (isValid()) {
            saveAttributesToStore();
            if (!isNewlyCreated) {
                metadata.setLastAccessedTime(new Date());
            }
            store.set(keyGenerator.generate(METADATA_KEY), getMaxInactiveInterval(), metadata);
        } else {
            removeAttributesFromStore();
        }
    }

    public GlobalHttpSession(String sessionId, SessionStore store, String namespace, Integer timeoutMinutes, HttpSession session) {

        if (sessionId == null || sessionId.trim().length() == 0) {
            throw new IllegalArgumentException("sessionId should not be empty.");
        }
        if (store == null) {
            throw new IllegalArgumentException("store should not be empty.");
        }
        if (namespace == null || namespace.trim().length() == 0) {
            throw new IllegalArgumentException("namespace should not be empty.");
        }
        if (timeoutMinutes == null) {
            throw new IllegalArgumentException("timeoutMinutes should not be empty.");
        }
        if (session == null) {
            throw new IllegalArgumentException("session should not be empty.");
        }

        this.sessionId = sessionId;
        this.store = store;
        this.session = session;
        this.keyGenerator = new StoreKeyGenerator(sessionId, namespace);

        setMaxInactiveInterval(timeoutMinutes * 60);

        metadata = store.get(keyGenerator.generate(METADATA_KEY));
        if (metadata == null) {
            isNewlyCreated = true;
            metadata = new Metadata();
            metadata.setInvalidated(false);
            metadata.setCreationTime(new Date());
            store.set(keyGenerator.generate(METADATA_KEY), getMaxInactiveInterval(), metadata);
            store.set(keyGenerator.generate(ATTRIBUTES_KEY), getMaxInactiveInterval(), attributes);
        }
        reloadAttributes();

        if (log.isDebugEnabled()) {
            log.debug("A new GlobalHttpSession is created. (sessionId: " + sessionId + ", attributes: " + attributes + ")");
        }
    }

    @Override
    public Object getAttribute(String name) {

        Object value = null;
        if (isValid()) {
            value = attributes.get(name);
        } else {
            value = null;
        }

        if (log.isDebugEnabled()) {
            log.debug("getAttribute is called. (sessionId: " + sessionId + ", " + name + " -> " + value + ")");
        }

        return value;
    }

    @Override
    public Enumeration<?> getAttributeNames() {
        if (isValid()) {
            final Iterator<String> names = attributes.keySet().iterator();
            return new Enumeration<Object>() {

                public boolean hasMoreElements() {
                    return names.hasNext();
                }

                public Object nextElement() {
                    return names.next();
                }
            };
        } else {
            return new Enumeration<Object>() {

                public boolean hasMoreElements() {
                    return false;
                }

                public Object nextElement() {
                    return null;
                }
            };
        }
    }

    @Override
    public void invalidate() {

        if (log.isDebugEnabled()) {
            log.debug("invalidate is called. (sessionId: " + sessionId + ")");
        }

        session.invalidate();
        attributes.clear();
        metadata.setInvalidated(true);
        removeAttributesFromStore();
    }

    @Override
    public void removeAttribute(String name) {
        reloadAttributes();
        attributes.remove(name);
        saveAttributesToStore();
    }

    @Override
    public void setAttribute(String name, Object value) {

        if (log.isDebugEnabled()) {
            log.debug("setAttribute is called. (sessionId: " + sessionId + ", " + name + " -> " + value + ")");
        }

        if (value == null) {
            removeAttribute(name);
        }
        if (value instanceof Serializable) {
            reloadAttributes();
            attributes.put(name, (Serializable) value);
            saveAttributesToStore();
        } else {
            String message = "The value should be an instance of java.io.Serializable. (" + value + ")";
            throw new IllegalArgumentException(message);
        }
    }

    @Override
    public Object getValue(String name) {
        return getAttribute(name);
    }

    @Override
    public String[] getValueNames() {
        Enumeration<String> names = (Enumeration<String>) getAttributeNames();
        return Collections.list(names).toArray(new String[]{});
    }

    @Override
    public String getId() {
        return sessionId;
    }

    @Override
    public long getCreationTime() {
        if (metadata == null || metadata.getCreationTime() == null) {
            return 0L;
        } else {
            return metadata.getCreationTime().getTime();
        }
    }

    @Override
    public long getLastAccessedTime() {
        if (metadata == null || metadata.getLastAccessedTime() == null) {
            return 0L;
        } else {
            return metadata.getLastAccessedTime().getTime();
        }
    }

    @Override
    public int getMaxInactiveInterval() {
        return this.maxInactiveIntervalSeconds;
    }

    @Override
    public ServletContext getServletContext() {
        return session.getServletContext();
    }

    @Override
    public HttpSessionContext getSessionContext() {
        // We don't support HttpSessionContext as a part of GlobalHttpSession
        // because it's already deprecated API.
        return session.getSessionContext();
    }

    @Override
    public boolean isNew() {
        return isNewlyCreated;
    }

    @Override
    public void putValue(String name, Object value) {
        setAttribute(name, value);
    }

    @Override
    public void removeValue(String name) {
        removeAttribute(name);
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        this.maxInactiveIntervalSeconds = interval;
        session.setMaxInactiveInterval(interval);
    }


    private void saveAttributesToStore() {
        store.set(keyGenerator.generate(ATTRIBUTES_KEY), getMaxInactiveInterval(), toMap());
    }

    private void removeAttributesFromStore() {
        store.remove(keyGenerator.generate(ATTRIBUTES_KEY));
    }

    @Override
    public String toString() {
        StringBuilder attributes = new StringBuilder();
        for (Object attr : Collections.list(getAttributeNames())) {
            attributes.append(attr);
            attributes.append(",");
        }
        return "com.m3.globalsession.GlobalHttpSession(id: " + getId() + ", attributes: ["
                + attributes.toString().replaceFirst(",$", "")
                + "], creationTime: " + getCreationTime() + ", lastAccessedTime: " + getLastAccessedTime()
                + ", maxInactiveInterval: " + getMaxInactiveInterval() + ")";
    }

}
