package com.m3.globalsession.memcached;

import org.junit.Test;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class MemcachedClientFactoryTest {

    @Test
    public void type() throws Exception {
        assertThat(MemcachedClientFactory.class, notNullValue());
    }

    @Test
    public void create_A$List() throws Exception {
        // given
        List<InetSocketAddress> addresses = Arrays.asList(new InetSocketAddress("memcached", 11211));
        // when
        MemcachedClient actual = MemcachedClientFactory.create(addresses);
        // then
        assertThat(actual, is(not(nullValue())));
    }

    @Test
    public void create_A$String$List$Class() throws Exception {
        // given
        String namespace = null;
        List<InetSocketAddress> addresses = Arrays.asList(new InetSocketAddress("memcached", 11211));
        Class adaptorClass = null;
        // when
        MemcachedClient actual = MemcachedClientFactory.create(namespace, addresses, adaptorClass);
        // then
        assertThat(actual, is(not(nullValue())));
    }

}
