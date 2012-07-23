package com.m3.globalsession.memcached.adaptor;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class SpymemcachedAdaptorTest {

    @Test
    public void type() throws Exception {
        assertThat(SpymemcachedAdaptor.class, notNullValue());
    }

    @Test
    public void instantiation() throws Exception {
        SpymemcachedAdaptor target = new SpymemcachedAdaptor();
        assertThat(target, notNullValue());
    }

    @Test
    public void getClientImplClass_A$() throws Exception {
        // given
        SpymemcachedAdaptor target = new SpymemcachedAdaptor();
        // when
        Object actual = target.getClientImplClass();
        // then
        Object expected = "class com.m3.globalsession.memcached.impl.SpymemcachedClientImpl";
        assertThat(actual.toString(), is(equalTo(expected)));
    }

}
