package com.trigg.fault_injection.Model;

import com.trigg.fault_injection.Database.FaultDAO;
import com.trigg.fault_injection.Service.DockerService;

public class NodeCrash implements Fault {
    int f_id;
    String username;
    String name;
    int duration;
    int scheduled_for;
    String fault_type;
    int num_nodes;

    public NodeCrash(int f_id, String username, String name, int duration, int scheduled_for, String fault_type, int num_nodes) {
        this.f_id = f_id;
        this.username = username;
        this.name = name;
        this.duration = duration;
        this.scheduled_for = scheduled_for;
        this.fault_type = fault_type;
        this.num_nodes = num_nodes;
    }

    public NodeCrash() {
    }

    @Override
    public void inject(DockerService dockerService) {
        dockerService.stopContainersAsync(this.num_nodes, this.duration);
    }

    @Override
    public int insert(FaultDAO faultDAO) {
        return faultDAO.insertNodeCrash(this);
    }

    @Override
    public void setUniqueAttr(int num_nodes) {
        this.num_nodes = num_nodes;
    }

    @Override
    public void setUniqueAttr(int num_nodes, int frequency) {
        //N/A
    }

    @Override
    public void setCommonAttr(String username, String name, int duration, int scheduled_for, String fault_type) {
        this.username = username;
        this.name = name;
        this.duration = duration;
        this.scheduled_for = scheduled_for;
        this.fault_type = fault_type;
    }

    public int getF_id() {
        return f_id;
    }

    public void setF_id(int f_id) {
        this.f_id = f_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getScheduled_for() {
        return scheduled_for;
    }

    public void setScheduled_for(int scheduled_for) {
        this.scheduled_for = scheduled_for;
    }

    public String getFault_type() {
        return fault_type;
    }

    public void setFault_type(String fault_type) {
        this.fault_type = fault_type;
    }

    public int getNum_nodes() {
        return num_nodes;
    }

    public void setNum_nodes(int num_nodes) {
        this.num_nodes = num_nodes;
    }
}
