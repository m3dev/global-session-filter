package com.m3.globalsession.store;

import java.io.Serializable;

public interface SessionStore {

    <V extends Serializable> V get(String key);

    <V extends Serializable> void set(String key, int expire, V value);

    void remove(String key);

}
