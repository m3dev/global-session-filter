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
package com.m3.globalsession.filter;

import com.m3.globalsession.memcached.MemcachedClient;
import com.m3.globalsession.memcached.MemcachedClientFactory;
import com.m3.globalsession.memcached.adaptor.MemcachedClientAdaptor;
import com.m3.globalsession.store.MemcachedSessionStore;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class MemcachedSessionFilter extends GlobalSessionFilter implements Filter {

    public static final String MEMCACHED_SERVERS_KEY = "memcachedServers";

    public static final String CLIENT_ADAPTOR_CLASS_NAME = "memcachedClientAdaptorClassName";

    protected Class<? extends MemcachedClientAdaptor> getMemcachedClientAdaptorClass(FilterConfig config) throws ClassNotFoundException {
        String className = getConfigValue(config, CLIENT_ADAPTOR_CLASS_NAME);
        if (className == null) {
            // default: spymemcached
            className = MemcachedClientAdaptor.Spymemcached;
        }
        return (Class<? extends MemcachedClientAdaptor>) Class.forName(className);
    }

    protected List<InetSocketAddress> getMemcachedServerAddresses(FilterConfig config) {
        List<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>();
        String servers = getConfigValue(config, MEMCACHED_SERVERS_KEY);
        if (servers == null) {
            throw new IllegalStateException("initParam: " + MEMCACHED_SERVERS_KEY + " is required.");
        }
        for (String server : servers.split(",")) {
            try {
                String hostname = server.split(":")[0];
                int port = Integer.valueOf(server.split(":")[1]);
                addresses.add(new InetSocketAddress(hostname, port));
            } catch (Exception e) {
                String message = "initParam: " + MEMCACHED_SERVERS_KEY + " is invalid. (" + server + ")";
                throw new IllegalStateException(message, e);
            }
        }
        return addresses;
    }

    @Override
    public void init(FilterConfig config) throws ServletException {

        super.init(config);

        List<InetSocketAddress> addresses = getMemcachedServerAddresses(config);
        try {
            Class<? extends MemcachedClientAdaptor> adaptorClass = getMemcachedClientAdaptorClass(config);
            MemcachedClient client = MemcachedClientFactory.create(settings.getNamespace(), addresses, adaptorClass);
            store = new MemcachedSessionStore(client);
        } catch (Exception e) {
            String message = "Failed to instantiate MemcachedClient because of " + e.getMessage();
            throw new IllegalStateException(message, e);
        }

    }

}
