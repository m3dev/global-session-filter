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

import com.m3.globalsession.store.MemcachedSessionStore;
import com.m3.memcached.facade.Configuration;
import com.m3.memcached.facade.MemcachedClientPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class MemcachedSessionFilter extends GlobalSessionFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(MemcachedSessionFilter.class);

    public static final String MEMCACHED_SERVERS_KEY = "memcachedServers";

    private MemcachedClientPool memcached;

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
        try {
            Configuration memcachedConfig = new Configuration();
            memcachedConfig.setNamespace("global-session-filter");
            memcachedConfig.setAddresses(getMemcachedServerAddresses(config));
            memcached = new MemcachedClientPool(memcachedConfig);
            store = new MemcachedSessionStore(memcached);
        } catch (Exception e) {
            String message = "Failed to instantiate MemcachedClient because of " + e.getMessage();
            throw new IllegalStateException(message, e);
        }

    }

    public void shutdownMemcachedClientPool() {
        try {
            if (memcached != null) {
                memcached.shutdown();
            }
        } catch (Exception e) {
            log.info("Failed to shutdown memcached client pool", e);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            super.finalize();
        } finally {
            shutdownMemcachedClientPool();
        }
    }

}
