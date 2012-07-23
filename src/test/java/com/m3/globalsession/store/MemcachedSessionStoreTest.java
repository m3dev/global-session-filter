package com.m3.globalsession.store;

import com.m3.globalsession.memcached.MemcachedClient;
import com.m3.globalsession.memcached.MemcachedClientFactory;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class MemcachedSessionStoreTest {

    InetSocketAddress address = new InetSocketAddress("memcached", 11211);

    @Test
    public void type() throws Exception {
        assertThat(MemcachedSessionStore.class, notNullValue());
    }

    @Test
    public void instantiation() throws Exception {
        MemcachedClient client = MemcachedClientFactory.create(Arrays.asList(address));
        MemcachedSessionStore instance = new MemcachedSessionStore(client);
        assertThat(instance, notNullValue());
    }

    @Test
    public void get_A$String() throws Exception {
        MemcachedClient client = MemcachedClientFactory.create(Arrays.asList(address));
        MemcachedSessionStore store = new MemcachedSessionStore(client);
        // given
        String key = "MemcachedSessionStoreTest#get_A$String_" + System.currentTimeMillis();
        String value = "cached" + System.currentTimeMillis();
        store.set(key, 60, value);
        // when
        String actual = store.get(key);
        // then
        assertThat(actual, is(equalTo(value)));
    }

    @Test
    public void set_A$String$int$Object_Expire() throws Exception {
        MemcachedClient client = MemcachedClientFactory.create(Arrays.asList(address));
        MemcachedSessionStore store = new MemcachedSessionStore(client);
        // given
        String key = "MemcachedSessionStoreTest#set_A$String$int$Object_" + System.currentTimeMillis();
        int expire = 2;
        String value = "aaa";
        // when
        store.set(key, expire, value);
        Thread.sleep(3000L);
        String actual = store.get(key);
        // then
        assertThat(actual, is(nullValue()));
    }

    @Test
    public void set_A$String$int$Object_NullValue() throws Exception {
        MemcachedClient client = MemcachedClientFactory.create(Arrays.asList(address));
        MemcachedSessionStore store = new MemcachedSessionStore(client);
        // given
        String key = "MemcachedSessionStoreTest#set_A$String$int$Object_NullValue_" + System.currentTimeMillis();
        int expire = 100;
        String value = "aaa";
        // when
        store.set(key, expire, value);
        store.set(key, expire, null);
        String actual = store.get(key);
        // then
        assertThat(actual, is(nullValue()));
    }

    @Test
    public void remove_A$String() throws Exception {
        MemcachedClient client = MemcachedClientFactory.create(Arrays.asList(address));
        MemcachedSessionStore store = new MemcachedSessionStore(client);
        // given
        String key = "MemcachedSessionStoreTest#remove_A$String_" + System.currentTimeMillis();
        // when
        store.set(key, 60, "aaa");
        store.remove(key);
        String actual = store.get(key);
        // then
        assertThat(actual, is(nullValue()));
    }

}
