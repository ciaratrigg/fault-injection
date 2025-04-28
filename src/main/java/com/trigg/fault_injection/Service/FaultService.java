package com.trigg.fault_injection.Service;

import com.trigg.fault_injection.Model.Fault;
import com.trigg.fault_injection.Database.FaultDAOImpl;
import com.trigg.fault_injection.Utilities.FaultFactory;
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

    //TODO
    //this should have all the common fault attr
    //unique ones will be set after the object is returned to the Controller
    public Fault defineFault(String type){
        //TODO duplicate name handling
        Fault fault = faultFactory.createFault(type);
        //set common attr
        return fault;
    }

    public int saveFault(Fault fault){
        return fault.insert(faultDAO);
    }

    //TODO each of these should have more parameters
    //todo add as more faults are created
    public void injectRequestedFault(String type, String name){
        Fault fault = faultDAO.selectFaultByName(name);
        fault.inject(dockerService);
    }

    public List<Fault> listAllFaults(){
        return faultDAO.selectAllFaults();
    }

}
