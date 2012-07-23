package com.m3.globalsession.memcached.adaptor;

import com.m3.globalsession.memcached.impl.ClientImpl;

public interface MemcachedClientAdaptor {

    public static final String Spymemcached = "com.m3.globalsession.memcached.adaptor.SpymemcachedAdaptor";

    public static final String Xmemcached = "com.m3.globalsession.memcached.adaptor.XmemcachedAdaptor";

    Class<? extends ClientImpl> getClientImplClass();

}

