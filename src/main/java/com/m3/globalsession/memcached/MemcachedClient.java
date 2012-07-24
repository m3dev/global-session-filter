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
package com.m3.globalsession.memcached;

import com.m3.globalsession.memcached.adaptor.MemcachedClientAdaptor;
import com.m3.globalsession.memcached.impl.ClientImpl;
import com.m3.globalsession.util.Assertion;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

public class MemcachedClient {

    public static final String DEFAULT_NAMESPACE = "default";

    private ClientImpl clientImpl;

    MemcachedClient(MemcachedClientAdaptor clientAdaptor) throws Exception {
        Assertion.notNullValue("clientAdaptor", clientAdaptor);
        Class<?> clazz = clientAdaptor.getClientImplClass();
        clientImpl = (ClientImpl) clazz.newInstance();
    }

    public ClientImpl getClientImpl() {
        return clientImpl;
    }

    public void initialize(List<InetSocketAddress> addresses)
            throws IOException {
        clientImpl.initialize(addresses);
    }

    public void initialize(List<InetSocketAddress> addresses, String namespace)
            throws IOException {
        clientImpl.initialize(addresses);
        clientImpl.setNamespace(namespace);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) throws IOException {
        ensureInitialized();
        return (T) clientImpl.get(key);
    }

    public <T> void set(String key, int secondsToExpire, T value)
            throws IOException {
        ensureInitialized();
        clientImpl.set(key, secondsToExpire, value);
    }

    public <T> void setAndEnsure(String key, int secondsToExpire, T value)
            throws IOException {
        ensureInitialized();
        clientImpl.setAndEnsure(key, secondsToExpire, value);
    }

    void ensureInitialized() throws IllegalStateException {
        if (clientImpl == null || !clientImpl.isInitialized()) {
            throw new IllegalStateException("Not yet initialized.");
        }
    }

    public void delete(String key) throws IOException {
        ensureInitialized();
        clientImpl.delete(key);
    }

}

