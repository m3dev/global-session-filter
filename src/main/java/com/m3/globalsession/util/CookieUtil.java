/*
 * Copyright 2012 M3, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
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
