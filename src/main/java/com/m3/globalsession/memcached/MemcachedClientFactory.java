package com.m3.globalsession.memcached;

import com.m3.globalsession.memcached.adaptor.MemcachedClientAdaptor;
import com.m3.globalsession.util.Assertion;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class MemcachedClientFactory {

    private static final String DEFAULT_CLIENT_ADAPTOR_NAME = MemcachedClientAdaptor.Spymemcached;

    private static final Map<String, MemcachedClient> CACHED_CLIENTS = new ConcurrentHashMap<String, MemcachedClient>();

    private MemcachedClientFactory() {
    }

    public static MemcachedClient create(List<InetSocketAddress> addresses) throws Exception {
        return create(null, addresses, null);
    }

    @SuppressWarnings("unchecked")
    public static MemcachedClient create(
            String namespace, List<InetSocketAddress> addresses, Class<? extends MemcachedClientAdaptor> adaptorClass) throws Exception {

        if (namespace == null) {
            namespace = MemcachedClient.DEFAULT_NAMESPACE;
        }

        // cached client instance
        MemcachedClient memcached = CACHED_CLIENTS.get(namespace);
        if (memcached != null) {
            return memcached;
        }

        // create new client instance
        if (adaptorClass == null) {
            adaptorClass = (Class<? extends MemcachedClientAdaptor>) Class.forName(DEFAULT_CLIENT_ADAPTOR_NAME);
        }
        memcached = new MemcachedClient(adaptorClass.newInstance());

        Assertion.notNullValue("addresses", addresses);
        memcached.initialize(addresses, namespace);

        // saved in the client pool
        CACHED_CLIENTS.put(namespace, memcached);

        return memcached;
    }

}