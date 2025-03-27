package com.trigg.fault_injection.Service;

import com.trigg.fault_injection.Model.Fault;
import com.trigg.fault_injection.Model.FaultDAOImpl;
import com.trigg.fault_injection.Model.NodeCrash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FaultService {
    private FaultFactory faultFactory;
    private FaultDAOImpl faultDAO;

    @Autowired
    public FaultService(FaultFactory faultFactory, FaultDAOImpl faultDAO){
        this.faultFactory = faultFactory;
        this.faultDAO = faultDAO;
    }

    public int defineFault(String type, String name, int duration){
        //TODO duplicate name handling
        return faultFactory.defineFault(type, name, duration);
    }

    public Fault selectRequestedFault(String type, String name){
        if(type.equalsIgnoreCase("node-crash")){
            return faultDAO.selectNodeCrash(name);
        }
        else if(type.equalsIgnoreCase("node-restart")){
            return faultDAO.selectNodeRestart(name);
        }
        return null;
    }

    public List<Fault> listAllFaults(){
        return faultDAO.selectAllFaults();
    }
}
