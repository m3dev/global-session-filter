package com.m3.globalsession.memcached.impl;

import net.spy.memcached.MemcachedClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.m3.globalsession.util.Assertion.notNullValue;

public class SpymemcachedClientImpl extends ClientImplBase {

    private MemcachedClient memcached;

    void waitForConnectionReady() {
        // If no wait, you will see the following warning log message...
        // WARN net.spy.memcached.MemcachedConnection: Could not redistribute to
        // another node, retrying primary node for xxx
        try {
            Thread.sleep(100L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public boolean isInitialized() {
        return memcached != null;
    }

    @Override
    public void initialize(List<InetSocketAddress> addresses)
            throws IOException {
        notNullValue("addresses", addresses);
        memcached = new MemcachedClient(addresses);
        waitForConnectionReady();
    }

    @Override
    public void initialize(List<InetSocketAddress> addresses, String namespace)
            throws IOException {
        notNullValue("addresses", addresses);
        memcached = new MemcachedClient(addresses);
        setNamespace(namespace);
        waitForConnectionReady();
    }

    @Override
    public <T> void set(String key, int secondsToExpire, T value)
            throws IOException {
        notNullValue("key", key);
        memcached.set(getKey(key), secondsToExpire, value);
    }

    @Override
    public <T> void setAndEnsure(String key, int secondsToExpire, T value)
            throws IOException {
        notNullValue("key", key);
        Future<Boolean> future = memcached.set(getKey(key), secondsToExpire, value);
        try {
            boolean result = future.get(5, TimeUnit.SECONDS);
            if (!result) {
                String failedMessage = "Failed to set the value on memcached! " +
                        "(key:" + key + ",secondsToExpire:" + secondsToExpire + ",value:" + value + ")";
                throw new IOException(failedMessage);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            String failedMessage = "Failed to set the value on memcached! " +
                    "(key:" + key + ",secondsToExpire:" + secondsToExpire + ",value:" + value + ")";
            throw new IOException(failedMessage, e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key) throws IOException {
        notNullValue("key", key);
        return (T) memcached.get(getKey(key));
    }

    @Override
    public void delete(String key) throws IOException {
        notNullValue("key", key);
        memcached.delete(getKey(key));
    }


}