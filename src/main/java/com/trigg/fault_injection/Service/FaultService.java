package com.trigg.fault_injection.Service;

import com.trigg.fault_injection.Model.Fault;
import com.trigg.fault_injection.Model.FaultDAOImpl;
import com.trigg.fault_injection.Model.NodeCrash;
import com.trigg.fault_injection.Model.NodeRestart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FaultService {
    private FaultFactory faultFactory;
    private FaultDAOImpl faultDAO;
    private DockerService dockerService;

    @Autowired
    public FaultService(FaultFactory faultFactory, FaultDAOImpl faultDAO, DockerService dockerService){
        this.faultFactory = faultFactory;
        this.dockerService = dockerService;
        this.faultDAO = faultDAO;
    }

    public int defineFault(String type, String name, int duration){
        //TODO duplicate name handling
        return faultFactory.defineFault(type, name, duration);
    }

    //TODO each of these should have more parameters
    public void selectRequestedFault(String type, String name){
        if(type.equalsIgnoreCase("node-crash")){
            NodeCrash fault = faultDAO.selectNodeCrash(name);
            dockerService.stopContainersAsync(fault.getNum_nodes());
        }
        else if(type.equalsIgnoreCase("node-restart")){
            NodeRestart fault = faultDAO.selectNodeRestart(name);
            dockerService.restartContainersAsync(fault.getNum_nodes());
        }
    }

    public List<Fault> listAllFaults(){
        return faultDAO.selectAllFaults();
    }
}
