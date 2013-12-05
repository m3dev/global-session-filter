package com.m3.globalsession.util;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class AssertionTest {

    @Test
    public void type() throws Exception {
        assertThat(Assertion.class, notNullValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void notNullValue_A$String$Object_NullValueIsPassed() throws Exception {
        // given
        String name = "aaa";
        Object value = null;
        // when
        Assertion.notNullValue(name, value);
        // then
    }

    @Test
    public void notNullValue_A$String$Object() throws Exception {
        // given
        String name = "aaa";
        Object value = "bbb";
        // when
        Assertion.notNullValue(name, value);
        // then
    }

}
