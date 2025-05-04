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

    public Fault defineFault(String type){
        //TODO duplicate name handling
        Fault fault = faultFactory.createFault(type);
        return fault;
    }

    public int saveFault(Fault fault){
        return fault.insert(faultDAO);
    }

    public String injectRequestedFault(String name, String username, String role, int scheduledFor){
        Fault fault = faultDAO.selectFaultByName(name);
        if(fault.getUsername().equalsIgnoreCase(username) || role.equalsIgnoreCase("ROLE_ADMIN")){
            fault.setScheduled_for(scheduledFor);
            fault.inject(dockerService);
            return "Fault scheduled for injection";
        }
        else{
            return "Error: Fault belongs to " + fault.getUsername() + ". Permission denied";
        }
    }

    public List<Fault> listAllFaults(){
        return faultDAO.selectAllFaults();
    }

    public List<DockerService.ScheduledJob> listAllJobs(){ return dockerService.getScheduledJobs();}

}
