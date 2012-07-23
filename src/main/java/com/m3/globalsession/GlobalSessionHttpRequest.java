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
