package com.trigg.fault_injection.Model;

import com.trigg.fault_injection.Database.FaultDAO;
import com.trigg.fault_injection.Service.DockerService;

public interface Fault {
    String getUsername();
    String getName();
    int getDuration();
    int getScheduled_for();
    String getFault_type();
    int getF_id();
    void inject(DockerService dockerService);
    int insert(FaultDAO faultDAO);
    void setUniqueAttr(int num_nodes);
    void setUniqueAttr(int num_nodes, int frequency);
    void setCommonAttr(String username, String name, int duration, int scheduled_for, String fault_type);
}
