package com.m3.globalsession;

public class StoreKeyGenerator {

    private final String sessionId;
    private final String namespace;

    public StoreKeyGenerator(String sessionId, String namespace) {
        this.sessionId = sessionId;
        this.namespace = namespace;
    }

    public String generate(String name) {
        return "GlobalSession::" + sessionId + "::" + namespace + "::" + name;
    }

}
