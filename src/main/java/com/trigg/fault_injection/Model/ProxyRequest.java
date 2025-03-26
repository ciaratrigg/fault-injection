package com.trigg.fault_injection.Model;

public class ProxyRequest {
    private String name;
    private String listen;
    private String upstream;

    // Default constructor
    public ProxyRequest() {}

    // Constructor
    public ProxyRequest(String name, String listen, String upstream) {
        this.name = name;
        this.listen = listen;
        this.upstream = upstream;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getListen() {
        return listen;
    }

    public void setListen(String listen) {
        this.listen = listen;
    }

    public String getUpstream() {
        return upstream;
    }

    public void setUpstream(String upstream) {
        this.upstream = upstream;
    }
}

