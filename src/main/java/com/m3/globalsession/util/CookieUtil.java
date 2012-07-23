package com.m3.globalsession.util;

import javax.servlet.http.Cookie;

public class CookieUtil {

    private CookieUtil() {
    }

    public static String createSetCookieHeaderValue(Cookie c, boolean isHttpOnly) {
        StringBuilder s = new StringBuilder();
        s = s.append(c.getName()).append("=").append(c.getValue());
        if (c.getDomain() != null) {
            s.append(";Domain=").append(c.getDomain());
        }
        if (c.getPath() != null) {
            s.append(";Path=").append(c.getPath());
        }
        if (c.getComment() != null) {
            s.append(";Comment=").append(c.getComment());
        }
        if (c.getMaxAge() > -1) {
            s.append(";Max-Age=").append(c.getMaxAge());
        }
        if (c.getSecure()) {
            s.append(";Secure");
        }
        if (isHttpOnly) {
            s.append(";HttpOnly");
        }
        return s.toString();
    }

}
