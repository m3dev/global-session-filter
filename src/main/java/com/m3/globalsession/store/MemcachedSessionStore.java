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

import com.m3.globalsession.memcached.MemcachedClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class MemcachedSessionStore implements SessionStore {

    private static final Logger log = LoggerFactory.getLogger(MemcachedSessionStore.class);

    private final MemcachedClient client;

    public MemcachedSessionStore(MemcachedClient client) {
        this.client = client;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V extends Serializable> V get(String key) {
        try {
            V value = (V) client.get(key);
            if (log.isDebugEnabled()) {
                StackTraceElement stackTrace = new Throwable().getStackTrace()[1];
                String method = stackTrace.getClassName() + "#" + stackTrace.getMethodName();
                log.debug("___ GET [" + key + " -> " + value + "] (" + method + ")");
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
            StackTraceElement stackTrace = new Throwable().getStackTrace()[1];
            String method = stackTrace.getClassName() + "#" + stackTrace.getMethodName();
            log.debug("$$$ SET (expire:" + expire + ") [" + key + " -> " + value + "] (" + method + ")");
        }

        try {
            if (value == null) {
                client.delete(key);
            } else {
                client.set(key, expire, value);
            }
        } catch (Exception e) {
            log.debug("Failed to set value for " + key, e);
        }
    }

    @Override
    public void remove(String key) {

        if (log.isDebugEnabled()) {
            log.debug("*** DELETE: [" + key + "]");
        }

        try {
            client.delete(key);
        } catch (Exception e) {
            log.debug("Failed to delete value for " + key, e);
        }
    }

}
