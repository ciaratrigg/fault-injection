package com.trigg.fault_injection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FaultService {
    private FaultFactory faultFactory;

    @Autowired
    public FaultService(FaultFactory faultFactory){
        this.faultFactory = faultFactory;
    }

    public int defineFault(String type, String name, int duration){
        return faultFactory.defineFault(type, name, duration);
    }
}
