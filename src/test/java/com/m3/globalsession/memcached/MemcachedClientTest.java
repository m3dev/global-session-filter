package com.m3.globalsession.memcached;

import com.m3.globalsession.memcached.adaptor.MemcachedClientAdaptor;
import com.m3.globalsession.memcached.adaptor.SpymemcachedAdaptor;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class MemcachedClientTest {

    String namespace = "MemcachedClientTest";

    List<InetSocketAddress> addresses = Arrays.asList(new InetSocketAddress("memcached", 11211));

    MemcachedClientAdaptor clientAdaptor = new SpymemcachedAdaptor();

    @Test
    public void type() throws Exception {
        assertThat(MemcachedClient.class, notNullValue());
    }

    @Test
    public void instantiation() throws Exception {
        MemcachedClient client = new MemcachedClient(clientAdaptor);
        assertThat(client, notNullValue());
    }

    @Test
    public void initialize_A$List() throws Exception {
        // given
        MemcachedClient client = new MemcachedClient(clientAdaptor);
        // when
        client.initialize(addresses);
        // then
    }

    @Test
    public void initialize_A$List$String() throws Exception {
        // given
        MemcachedClient client = new MemcachedClient(clientAdaptor);
        // when
        String namespace = null;
        client.initialize(addresses, namespace);
        // then
    }

    @Test
    public void get_A$String() throws Exception {

        MemcachedClient client = new MemcachedClient(clientAdaptor);
        client.initialize(addresses, namespace);

        String key = "get_A$String";
        assertThat(client.get(key), is(nullValue()));

        Object value = "vvv";
        client.set(key, 3, value);
        assertThat(client.get(key), is(equalTo(value)));

    }

    @Test
    public void set_A$String$int$Object() throws Exception {
        // given
        MemcachedClient client = new MemcachedClient(clientAdaptor);
        client.initialize(addresses, namespace);
        // when
        String key = "set_A$String$int$Object";
        int secondsToExpire = 10;
        Object value = "xxxx";
        client.set(key, secondsToExpire, value);
        // then
        assertThat(client.get(key), is(equalTo(value)));
    }

    @Test
    public void setAndEnsure_A$String$int$Object() throws Exception {
        // given
        MemcachedClient client = new MemcachedClient(clientAdaptor);
        client.initialize(addresses, namespace);
        // when
        String key = "setAndEnsure_A$String$int$Object";
        int secondsToExpire = 10;
        Object value = "yyy";
        client.setAndEnsure(key, secondsToExpire, value);
        // then
        assertThat(client.get(key), is(equalTo(value)));
    }

    @Test(expected = IllegalStateException.class)
    public void ensureInitialized_A$_NotYet() throws Exception {
        // given
        MemcachedClient client = new MemcachedClient(clientAdaptor);
        // when
        client.ensureInitialized();
        // then
    }


    @Test
    public void ensureInitialized_A$() throws Exception {
        // given
        MemcachedClient client = new MemcachedClient(clientAdaptor);
        client.initialize(addresses, namespace);
        // when
        client.ensureInitialized();
        // then
    }

    @Test
    public void delete_A$String() throws Exception {
        // given
        MemcachedClient client = new MemcachedClient(clientAdaptor);
        client.initialize(addresses, namespace);
        // when
        String key = "delete_A$String";
        client.delete(key);
        // then
        assertThat(client.get(key), is(nullValue()));
    }

}
