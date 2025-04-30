package com.trigg.fault_injection.Model;

import com.trigg.fault_injection.Database.FaultDAO;
import com.trigg.fault_injection.Service.DockerService;

public class PacketLoss implements Fault{
    @Override
    public String getUsername() {
        return "";
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public int getScheduled_for() {
        return 0;
    }

    @Override
    public String getFault_type() {
        return "";
    }

    @Override
    public int getF_id() {
        return 0;
    }

    @Override
    public void inject(DockerService dockerService) {

    }

    @Override
    public int insert(FaultDAO faultDAO) {
        return 0;
    }

    @Override
    public void setUniqueAttr(int num_nodes) {

    }

    @Override
    public void setUniqueAttr(int num_nodes, int frequency) {

    }

    @Override
    public void setCommonAttr(String username, String name, int duration, int scheduled_for, String fault_type) {

    }
}
