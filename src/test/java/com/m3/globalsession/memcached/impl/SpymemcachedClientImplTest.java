package com.m3.globalsession.memcached.impl;

import org.junit.Test;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class SpymemcachedClientImplTest {

    String namespace = "SpymemcachedClientImplTest";

    List<InetSocketAddress> addresses = Arrays.asList(new InetSocketAddress("memcached", 11211));

    @Test
    public void type() throws Exception {
        assertThat(SpymemcachedClientImpl.class, is(not(nullValue())));
    }

    @Test
    public void instantiation() throws Exception {
        SpymemcachedClientImpl client = new SpymemcachedClientImpl();
        assertThat(client, is(not(nullValue())));
    }

    @Test
    public void waitForConnectionReady_A$() throws Exception {
        // given
        SpymemcachedClientImpl client = new SpymemcachedClientImpl();
        // when
        client.waitForConnectionReady();
        // then
    }

    @Test
    public void isInitialized_A$() throws Exception {
        // given
        SpymemcachedClientImpl client = new SpymemcachedClientImpl();
        // when
        boolean actual = client.isInitialized();
        // then
        boolean expected = false;
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void initialize_A$List() throws Exception {
        // given
        SpymemcachedClientImpl client = new SpymemcachedClientImpl();
        // when
        client.initialize(addresses);
        // then
    }

    @Test
    public void initialize_A$List$String() throws Exception {
        // given
        SpymemcachedClientImpl client = new SpymemcachedClientImpl();
        String namespace = "xxx";
        // when
        client.initialize(addresses, namespace);
        // then
    }

    @Test
    public void set_A$String$int$Object() throws Exception {

        // given
        SpymemcachedClientImpl client = new SpymemcachedClientImpl();
        client.initialize(addresses, namespace);

        // when
        String key = "set_A$String$int$Object";
        int secondsToExpire = 1;
        Object value = "xxx";
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
        SpymemcachedClientImpl client = new SpymemcachedClientImpl();
        client.initialize(addresses, namespace);

        // when
        String key = "setAndEnsure_A$String$int$Object";
        int secondsToExpire = 1;
        Object value = "yyy";
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
        SpymemcachedClientImpl client = new SpymemcachedClientImpl();
        client.initialize(addresses, namespace);

        // when
        String key = "get_A$String";
        Object value = "fddsc222";
        client.set(key, 1, value);

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
        SpymemcachedClientImpl client = new SpymemcachedClientImpl();
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
