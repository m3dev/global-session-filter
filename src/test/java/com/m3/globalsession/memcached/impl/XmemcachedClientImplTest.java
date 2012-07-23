package com.m3.globalsession.memcached.impl;

import org.junit.Test;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class XmemcachedClientImplTest {

    String namespace = "XmemcachedClientImplTest";

    List<InetSocketAddress> addresses = Arrays.asList(new InetSocketAddress("memcached", 11211));

    @Test
    public void type() throws Exception {
        assertThat(XmemcachedClientImpl.class, is(not(nullValue())));
    }

    @Test
    public void instantiation() throws Exception {
        XmemcachedClientImpl client = new XmemcachedClientImpl();
        assertThat(client, is(not(nullValue())));
    }

    @Test
    public void isInitialized_A$() throws Exception {

        XmemcachedClientImpl client = new XmemcachedClientImpl();

        assertThat(client.isInitialized(), is(equalTo(false)));

        client.initialize(addresses);

        assertThat(client.isInitialized(), is(true));
    }

    @Test
    public void initialize_A$List() throws Exception {
        // given
        XmemcachedClientImpl client = new XmemcachedClientImpl();
        // when
        client.initialize(addresses);
        // then
    }

    @Test
    public void initialize_A$List$String() throws Exception {
        // given
        XmemcachedClientImpl client = new XmemcachedClientImpl();
        String namespace = "xxx";
        // when
        client.initialize(addresses, namespace);
        // then
    }

    @Test
    public void set_A$String$int$Object() throws Exception {

        // given
        XmemcachedClientImpl client = new XmemcachedClientImpl();
        client.initialize(addresses, namespace);

        // when
        String key = "set_A$String$int$Object";
        int secondsToExpire = 1;
        Object value = "bbb";
        client.set(key, secondsToExpire, value);

        // then
        // should be found
        assertThat(client.get(key), is(equalTo(value)));
        Thread.sleep(3000L);
        // should be expired
        assertThat(client.get(key), is(nullValue()));
    }

    @Test
    public void setAndEnsure_A$String$int$Object() throws Exception {

        // given
        XmemcachedClientImpl client = new XmemcachedClientImpl();
        client.initialize(addresses, namespace);

        // when
        String key = "setAndEnsure_A$String$int$Object";
        int secondsToExpire = 1;
        Object value = "ddd";
        client.setAndEnsure(key, secondsToExpire, value);

        // then
        // should be found
        assertThat(client.get(key), is(equalTo(value)));
        Thread.sleep(3000L);
        // should be expired
        assertThat(client.get(key), is(nullValue()));
    }

    @Test
    public void get_A$String() throws Exception {

        // given
        XmemcachedClientImpl client = new XmemcachedClientImpl();
        client.initialize(addresses, namespace);

        // when
        String key = "get_A$String";
        Object value = "xxxx";
        client.set(key, 1, value);
        Object actual = client.get(key);

        // then
        // should be found
        assertThat(client.get(key), is(equalTo(value)));
        Thread.sleep(3000L);
        // should be expired
        assertThat(client.get(key), is(nullValue()));
    }

    @Test
    public void delete_A$String() throws Exception {

        // given
        XmemcachedClientImpl client = new XmemcachedClientImpl();
        client.initialize(addresses, namespace);

        // when
        String key = "delete_A$String";

        Object value = "fddsc222";
        client.set(key, 10, value);

        client.delete(key);

        // then
        Thread.sleep(1000L);
        assertThat(client.get(key), is(nullValue()));
    }

}
