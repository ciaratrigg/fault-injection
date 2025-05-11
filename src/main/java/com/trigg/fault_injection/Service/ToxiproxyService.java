package com.trigg.fault_injection.Service;

import eu.rekawek.toxiproxy.ToxiproxyClient;
import eu.rekawek.toxiproxy.Proxy;
import eu.rekawek.toxiproxy.model.Toxic;
import eu.rekawek.toxiproxy.model.ToxicDirection;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class ToxiproxyService {

    private ToxiproxyClient client;
    private Map<String, Proxy> proxies = new HashMap<>();
    private static final Logger LOGGER = Logger.getLogger(ToxiproxyService.class.getName());


    public ToxiproxyService() throws IOException {
        this.client = new ToxiproxyClient("host.docker.internal", 8474);
    }

    public Proxy createProxy(String name, String listen, String upstream) throws IOException {
        Proxy proxy = client.createProxy(name, listen, upstream);
        proxies.put(name, proxy);
        return proxy;
    }

    public void addLatency(String proxyName, long latency) throws IOException {
        Proxy proxy = proxies.get(proxyName);
        if (proxy != null) {
            proxy.toxics().latency("latency", ToxicDirection.DOWNSTREAM, latency);
        }
    }

    public void addBandwidth(String proxyName, long rate) throws IOException {
        Proxy proxy = proxies.get(proxyName);
        if (proxy != null) {
            proxy.toxics().bandwidth("bandwidth", ToxicDirection.DOWNSTREAM, rate);
        }
    }


    public void removeToxic(String proxyName, String toxicName) throws IOException {
        Proxy proxy = proxies.get(proxyName);
        if (proxy != null) {
            Toxic toxic = proxy.toxics().get(toxicName);
            if (toxic != null) {
                toxic.remove();
            }
        }
    }

    public void deleteProxy(String proxyName) throws IOException {
        Proxy proxy = proxies.get(proxyName);
        if (proxy != null) {
            proxy.delete();
            proxies.remove(proxyName);
        }
    }

    @PreDestroy
    public void cleanup() {
        try {
            for (String proxyName : proxies.keySet()) {
                deleteProxy(proxyName);
            }
        } catch (IOException e) {
            LOGGER.severe("Error during cleanup: " + e.getMessage());
        }
    }
}
