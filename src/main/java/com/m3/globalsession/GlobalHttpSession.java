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

    public static final String INVALIDATE_FLAG_KEY = "__invalidated__";
    public static final String ATTRIBUTES_KEY = "__attributes__";
    public static final String CREATION_TIME_KEY = "__creationTime___";
    public static final String LAST_ACCESSED_TIME_KEY = "__lastAccessedTime__";

    private static final Logger log = LoggerFactory.getLogger(GlobalHttpSession.class);

    // should be Serializable type
    private ConcurrentHashMap<String, Object> attributes = new ConcurrentHashMap<String, Object>();

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

        Object invalidated = store.get(keyGenerator.generate(INVALIDATE_FLAG_KEY));
        boolean isNotInvalidated = invalidated != null && invalidated.toString().equals("false");

        boolean isNotExpired = store.get(keyGenerator.generate(ATTRIBUTES_KEY)) != null;

        if (log.isDebugEnabled()) {
            log.debug("isValid is called. (isNotInvalidated: " + isNotInvalidated + ", isNotExpired: " + isNotExpired + ")");
        }

        return isNotInvalidated && isNotExpired;
    }

    public ConcurrentHashMap<String, Object> toMap() {
        return attributes;
    }

    public void save() {
        if (isValid()) {
            saveAttributesToStore();
            if (!isNewlyCreated) {
                updateLastAccessedTime();
            }
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

        if (store.get(keyGenerator.generate(INVALIDATE_FLAG_KEY)) == null) {
            isNewlyCreated = true;
            store.set(keyGenerator.generate(INVALIDATE_FLAG_KEY), getMaxInactiveInterval(), false);
            store.set(keyGenerator.generate(CREATION_TIME_KEY), getMaxInactiveInterval(), new Date());
            store.set(keyGenerator.generate(ATTRIBUTES_KEY), getMaxInactiveInterval(), attributes);
        }

        this.attributes = store.get(keyGenerator.generate(ATTRIBUTES_KEY));

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

        setInvalidFlagOnSessionStore();
        removeAttributesFromStore();
    }

    @Override
    public void removeAttribute(String name) {
        attributes.remove(name);
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
            attributes.put(name, (Serializable) value);
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
        Date creationTime = (Date) store.get(keyGenerator.generate(CREATION_TIME_KEY));
        if (creationTime == null) {
            return 0L;
        } else {
            return creationTime.getTime();
        }
    }

    @Override
    public long getLastAccessedTime() {
        Date lastAccessedTime = (Date) store.get(keyGenerator.generate(LAST_ACCESSED_TIME_KEY));
        if (lastAccessedTime == null) {
            return 0L;
        } else {
            return lastAccessedTime.getTime();
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


    private void setInvalidFlagOnSessionStore() {
        store.set(keyGenerator.generate(INVALIDATE_FLAG_KEY), getMaxInactiveInterval(), true);
    }

    private void saveAttributesToStore() {
        store.set(keyGenerator.generate(ATTRIBUTES_KEY), getMaxInactiveInterval(), toMap());
    }

    private void removeAttributesFromStore() {
        store.remove(keyGenerator.generate(ATTRIBUTES_KEY));
    }

    private void updateLastAccessedTime() {
        store.set(keyGenerator.generate(LAST_ACCESSED_TIME_KEY), getMaxInactiveInterval(), new Date());
    }

}
