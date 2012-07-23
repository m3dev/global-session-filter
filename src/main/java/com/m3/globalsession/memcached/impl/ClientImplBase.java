package com.m3.globalsession.memcached.impl;

import com.m3.globalsession.memcached.MemcachedClient;

public abstract class ClientImplBase implements ClientImpl {

    protected String namespace = MemcachedClient.DEFAULT_NAMESPACE;

    protected String getKey(String key) {
        return (this.namespace + "::" + key).replaceAll("\\s", "_");
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public void setNamespace(String namespace) {
        if (namespace == null) {
            this.namespace = "default";
        } else {
            this.namespace = namespace;
        }
    }

}