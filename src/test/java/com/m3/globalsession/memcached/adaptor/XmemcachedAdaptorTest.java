package com.m3.globalsession.memcached.adaptor;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class XmemcachedAdaptorTest {

    @Test
    public void type() throws Exception {
        assertThat(XmemcachedAdaptor.class, notNullValue());
    }

    @Test
    public void instantiation() throws Exception {
        XmemcachedAdaptor target = new XmemcachedAdaptor();
        assertThat(target, notNullValue());
    }

    @Test
    public void getClientImplClass_A$() throws Exception {
        // given
        XmemcachedAdaptor target = new XmemcachedAdaptor();
        // when
        Object actual = target.getClientImplClass();
        // then
        Object expected = "class com.m3.globalsession.memcached.impl.XmemcachedClientImpl";
        assertThat(actual.toString(), is(equalTo(expected)));
    }

}
