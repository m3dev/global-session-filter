package com.m3.globalsession.util;

import org.junit.Test;

import javax.servlet.http.Cookie;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class CookieUtilTest {

    @Test
    public void type() throws Exception {
        assertThat(CookieUtil.class, notNullValue());
    }

    @Test
    public void createSetCookieHeaderValue_A$Cookie$boolean() throws Exception {
        // given
        Cookie c = new Cookie("k", "v");
        c.setSecure(true);
        c.setComment("Commented");
        c.setDomain("example.com");
        c.setMaxAge(10);
        c.setPath("/admin/");
        boolean isHttpOnly = false;
        // when
        String actual = CookieUtil.createSetCookieHeaderValue(c, isHttpOnly);
        // then
        String expected = "k=v;Domain=example.com;Path=/admin/;Comment=Commented;Max-Age=10;Secure";
        assertThat(actual, is(equalTo(expected)));
    }

}
