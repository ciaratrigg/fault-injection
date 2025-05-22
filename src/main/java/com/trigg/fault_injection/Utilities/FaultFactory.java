package com.trigg.fault_injection.Utilities;

import com.trigg.fault_injection.Database.FaultDAO;
import com.trigg.fault_injection.Model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Component
public class FaultFactory {

    /*
     * Centralizes and manages the creation of all supported Fault objects based on
     * user input.
     */

    private Map<String, Supplier<Fault>> faultSupplier = new HashMap<>();

    @Autowired
    public FaultFactory(){
        faultSupplier.put("node-crash", NodeCrash::new);
        faultSupplier.put("node-restart", NodeRestart::new);
        faultSupplier.put("cpu-stress-sc", CpuStressSidecar::new);
        faultSupplier.put("network-delay", NetworkDelay::new);
        faultSupplier.put("bandwidth-throttle", BandwidthThrottle::new);
    }

    public Fault createFault(String type){
        Supplier<Fault> supplier = faultSupplier.get(type.toLowerCase());
        if(supplier != null){
            return supplier.get();
        }
        throw new IllegalArgumentException("Unknown fault type: " + type);
    }
}
