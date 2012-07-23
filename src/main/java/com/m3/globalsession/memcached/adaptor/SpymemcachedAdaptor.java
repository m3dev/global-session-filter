package com.m3.globalsession.memcached.adaptor;

import com.m3.globalsession.memcached.impl.ClientImpl;
import com.m3.globalsession.memcached.impl.SpymemcachedClientImpl;

public class SpymemcachedAdaptor implements MemcachedClientAdaptor {

    @Override
    public Class<? extends ClientImpl> getClientImplClass() {
        return SpymemcachedClientImpl.class;
    }

}