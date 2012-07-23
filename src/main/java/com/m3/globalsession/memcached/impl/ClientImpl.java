package com.m3.globalsession.memcached.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

public interface ClientImpl {

    boolean isInitialized();

    void initialize(List<InetSocketAddress> addresses) throws IOException;

    void initialize(List<InetSocketAddress> addresses, String namespace) throws IOException;

    String getNamespace();

    void setNamespace(String namespace);

    <T> void set(String key, int secondsToExpire, T value) throws IOException;

    <T> void setAndEnsure(String key, int secondsToExpire, T value) throws IOException;

    <T> T get(String key) throws IOException;

    void delete(String key) throws IOException;

}