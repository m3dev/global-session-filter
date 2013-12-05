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
package com.m3.globalsession.store;

import com.m3.memcached.facade.MemcachedClientPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class MemcachedSessionStore implements SessionStore {

    private static final Logger log = LoggerFactory.getLogger(MemcachedSessionStore.class);

    private final MemcachedClientPool memcached;

    public MemcachedSessionStore(MemcachedClientPool memcached) {
        this.memcached = memcached;
    }

    private static String getMethodCalls(Throwable t) {
        StackTraceElement e1 = t.getStackTrace()[1];
        StackTraceElement e2 = t.getStackTrace()[2];
        StackTraceElement e3 = t.getStackTrace()[3];
        return "(" + e1.getClassName() + "#" + e1.getMethodName()
                + " <- " + e2.getClassName() + "#" + e2.getMethodName()
                + " <- " + e3.getClassName() + "#" + e3.getMethodName() + ")";
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V extends Serializable> V get(String key) {
        try {
            V value = (V) memcached.getClient().get(key);
            if (log.isDebugEnabled()) {
                Throwable t = new Throwable();
                String message = "___ GET [" + key + " -> " + value + "]";
                log.debug(message + getMethodCalls(t));
            }
            return value;

        } catch (Exception e) {
            log.debug("Failed to get value for " + key, e);
            return null;
        }
    }

    @Override
    public <V extends Serializable> void set(String key, int expire, V value) {
        if (log.isDebugEnabled()) {
            Throwable t = new Throwable();
            String message = "$$$ SET (expire:" + expire + ") [" + key + " -> " + value + "]";
            log.debug(message + getMethodCalls(t));
        }

        try {
            if (value == null) {
                memcached.getClient().delete(key);
            } else {
                memcached.getClient().set(key, expire, value);
            }
        } catch (Exception e) {
            log.debug("Failed to set value for " + key, e);
        }
    }

    @Override
    public void remove(String key) {
        if (log.isDebugEnabled()) {
            Throwable t = new Throwable();
            String message = "*** DELETE: [" + key + "]";
            log.debug(message + getMethodCalls(t));
        }

        try {
            memcached.getClient().delete(key);
        } catch (Exception e) {
            log.debug("Failed to delete value for " + key, e);
        }
    }

}
