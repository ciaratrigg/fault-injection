package com.trigg.fault_injection.Utilities;

import com.trigg.fault_injection.Database.FaultDAO;
import com.trigg.fault_injection.Model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FaultFactory {
//TODO change everything to LOGGER statements
    private FaultDAO dao;

    @Autowired
    public FaultFactory(FaultDAO dao){
        this.dao = dao;
    }

    //todo add more cases as more faults are created
    public int defineFault(String type, String name, int duration){
        if(type.equalsIgnoreCase("node-crash")){
            System.out.println("Creating new Node Crash fault...");
            NodeCrash fault = new NodeCrash();
            setCommonAttr(fault, type, name, duration);
            //TODO add other setters
            int id = dao.insertNodeCrash(fault); //do i want to do anything with this return value?
            System.out.println("Successfully inserted fault with id " + id);

        }
        else if(type.equalsIgnoreCase("node-restart")){
            System.out.println("Creating new Node Restart fault...");
            NodeRestart fault = new NodeRestart();
            setCommonAttr(fault, type, name, duration);
            // TODO add other setters
            int id = dao.insertNodeRestart(fault);
            System.out.println("Successfully inserted fault with id " + id);

        }
        else if(type.equalsIgnoreCase("network-delay")){
            System.out.println("Creating new Network Delay fault...");
            NetworkDelay fault = new NetworkDelay();
            setCommonAttr(fault, type, name, duration);
            //TODO add other setters
            //TODO add dao call

        }
        else if(type.equalsIgnoreCase("cpu-stress-sc")){
            System.out.println("Creating new CPU Stress Sidecar fault...");
            CpuStressSidecar fault = new CpuStressSidecar();
            setCommonAttr(fault, type, name, duration);
            // TODO add other setters
            int id = dao.insertCpuStressSidecar(fault);
            System.out.println("Successfully inserted fault with id " + id);
        }
        else{
            System.out.println("Specified fault type does not exist.");
            return -1;
        }
        return 1;
    }

    public void setCommonAttr(Fault fault, String type, String name, int duration){
        fault.setFault_type(type);
        fault.setName(name);
        fault.setDuration(duration);
    }
}
