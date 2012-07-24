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
package com.m3.globalsession;

import com.m3.globalsession.store.SessionStore;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class GlobalSessionHttpRequest extends HttpServletRequestWrapper {

    private final String sessionId;
    private final String namespace;
    private final SessionStore store;
    private final GlobalHttpSession session;

    public GlobalSessionHttpRequest(ServletRequest request, String sessionId, String namespace,
                                    Integer timeoutMinutes, SessionStore store) {
        super((HttpServletRequest) request);
        this.sessionId = sessionId;
        this.namespace = namespace;
        this.store = store;
        session = new GlobalHttpSession(sessionId, store, namespace, timeoutMinutes, super.getSession());
    }

    @Override
    public GlobalHttpSession getSession() {
        return session;
    }

    @Override
    public GlobalHttpSession getSession(boolean create) {
        return session;
    }

}
