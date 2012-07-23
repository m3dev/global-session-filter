package com.m3.globalsession.memcached.adaptor;


import com.m3.globalsession.memcached.impl.ClientImpl;
import com.m3.globalsession.memcached.impl.XmemcachedClientImpl;

public class XmemcachedAdaptor implements MemcachedClientAdaptor {

    @Override
    public Class<? extends ClientImpl> getClientImplClass() {
        return XmemcachedClientImpl.class;
    }

}