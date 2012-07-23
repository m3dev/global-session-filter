package com.m3.globalsession;

public class GlobalSessionFilterSettings {

    private String namespace;
    private String sessionIdKey;
    private String domain;
    private String path;
    private boolean secure;
    private boolean httpOnly;

    private Integer sessionTimeoutMinutes;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getSessionIdKey() {
        return sessionIdKey;
    }

    public void setSessionIdKey(String sessionIdKey) {
        this.sessionIdKey = sessionIdKey;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isSecure() {
        return secure;
    }

    public void setSecure(Boolean secure) {
        this.secure = secure != null && secure;
    }

    public Integer getSessionTimeoutMinutes() {
        return sessionTimeoutMinutes;
    }

    public void setSessionTimeoutMinutes(Integer sessionTimeoutMinutes) {
        this.sessionTimeoutMinutes = sessionTimeoutMinutes;
    }

    public boolean isHttpOnly() {
        return httpOnly;
    }

    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

}
