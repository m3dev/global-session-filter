package com.m3.globalsession.memcached.impl;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.XMemcachedClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

import static com.m3.globalsession.util.Assertion.notNullValue;

public class XmemcachedClientImpl extends ClientImplBase {

    private MemcachedClient memcached;

    @Override
    public boolean isInitialized() {
        return memcached != null;
    }

    @Override
    public void initialize(List<InetSocketAddress> addresses)
            throws IOException {
        notNullValue("addresses", addresses);
        memcached = new XMemcachedClient(addresses);
    }

    @Override
    public void initialize(List<InetSocketAddress> addresses, String namespace)
            throws IOException {
        notNullValue("addresses", addresses);
        memcached = new XMemcachedClient(addresses);
        setNamespace(namespace);
    }

    @Override
    public <T> void set(String key, int secondsToExpire, T value)
            throws IOException {
        notNullValue("key", key);
        try {
            memcached.set(getKey(key), secondsToExpire, value);
        } catch (Exception e) {
            String failedMessage = "Failed to set value on memcached! " +
                    "(key:" + key + ",secondsToExpire:" + secondsToExpire + ",value:" + value + ")";
            throw new IOException(failedMessage, e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void setAndEnsure(String key, int secondsToExpire, T value)
            throws IOException {
        notNullValue("key", key);
        try {
            set(key, secondsToExpire, value);
            T cached = (T) memcached.get(getKey(key));
            if (cached == null) {
                String failedMessage = "Failed to set value on memcached! " +
                        "(key:" + key + ",secondsToExpire:" + secondsToExpire + ",value:" + value + ")";
                throw new IOException(failedMessage);
            }
        } catch (Exception e) {
            String failedMessage = "Failed to set value on memcached! " +
                    "(key:" + key + ",secondsToExpire:" + secondsToExpire + ",value:" + value + ")";
            throw new IOException(failedMessage, e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key) throws IOException {
        notNullValue("key", key);
        try {
            return (T) memcached.get(getKey(key));
        } catch (Exception e) {
            String failedMessage = "Failed to get value on memcached! (key:" + key + ")";
            throw new IOException(failedMessage, e);
        }
    }

    @Override
    public void delete(String key) throws IOException {
        try {
            memcached.delete(getKey(key));
        } catch (Exception e) {
            String failedMessage = "Failed to delete value on memcached! (key:" + key + ")";
            throw new IOException(failedMessage, e);
        }
    }

}