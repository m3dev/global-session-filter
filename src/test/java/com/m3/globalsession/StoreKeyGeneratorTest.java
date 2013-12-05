package com.m3.globalsession;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class StoreKeyGeneratorTest {

    @Test
    public void type() throws Exception {
        assertThat(StoreKeyGenerator.class, notNullValue());
    }

    @Test
    public void instantiation() throws Exception {
        String sessionId = "a";
        String namespace = "b";
        StoreKeyGenerator target = new StoreKeyGenerator(sessionId, namespace);
        assertThat(target, notNullValue());
    }

    @Test
    public void generate_A$String() throws Exception {
        String sessionId = "a";
        String namespace = "b";
        StoreKeyGenerator target = new StoreKeyGenerator(sessionId, namespace);
        // given
        String name = "c";
        // when
        String actual = target.generate(name);
        // then
        String expected = "GlobalSession::a::b::c";
        assertThat(actual, is(equalTo(expected)));
    }

}
