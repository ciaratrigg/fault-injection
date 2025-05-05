package com.trigg.fault_injection.Service;

import eu.rekawek.toxiproxy.ToxiproxyClient;
import eu.rekawek.toxiproxy.Proxy;
import eu.rekawek.toxiproxy.model.Toxic;
import eu.rekawek.toxiproxy.model.ToxicDirection;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class ToxiproxyService {

    private final ToxiproxyClient client;
    private final Map<String, Proxy> proxies = new HashMap<>();

    public ToxiproxyService() throws IOException {
        this.client = new ToxiproxyClient("localhost", 8474);
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

    public void addPacketLoss(String proxyName, long lossPercentage) throws IOException {
        Proxy proxy = proxies.get(proxyName);
        if (proxy != null) {
            proxy.toxics().limitData("packet_loss", ToxicDirection.DOWNSTREAM, lossPercentage);
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
}
