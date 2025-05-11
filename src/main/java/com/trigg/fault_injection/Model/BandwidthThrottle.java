package com.trigg.fault_injection.Model;

import com.trigg.fault_injection.Database.FaultDAO;
import com.trigg.fault_injection.Service.DockerService;

public class BandwidthThrottle implements Fault{
    private int f_id;
    private String username;
    private String name;
    private int duration;
    private int scheduled_for;
    private String fault_type;
    private long rate;

    public BandwidthThrottle(int f_id, String username, String name, int duration, int scheduled_for, String fault_type, long rate) {
        this.f_id = f_id;
        this.username = username;
        this.name = name;
        this.duration = duration;
        this.scheduled_for = scheduled_for;
        this.fault_type = fault_type;
        this.rate = rate;
    }

    public BandwidthThrottle() {

    }

    public long getRate() {
        return rate;
    }

    public void setRate(long rate) {
        this.rate = rate;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getDuration() {
        return this.duration;
    }

    @Override
    public int getScheduled_for() {
        return this.scheduled_for;
    }

    @Override
    public void setScheduled_for(int scheduled_for) {
        this.scheduled_for = scheduled_for;
    }

    @Override
    public String getFault_type() {
        return this.fault_type;
    }

    @Override
    public int getF_id() {
        return this.f_id;
    }

    @Override
    public void inject(DockerService dockerService) {
        dockerService.scheduleFault(this.name, this.fault_type, this.scheduled_for, this.duration, -1, -1, name, rate);
    }

    @Override
    public int insert(FaultDAO faultDAO) {
        return faultDAO.insertBandwidthThrottle(this);
    }

    @Override
    public void setUniqueAttr(int num_nodes) {

    }

    @Override
    public void setUniqueAttr(int num_nodes, int frequency) {

    }

    @Override
    public void setUniqueAttr(long latency) {
        this.rate = latency;
    }

    @Override
    public void setCommonAttr(String username, String name, int duration, String fault_type) {
        this.username = username;
        this.name = name;
        this.duration = duration;
        this.fault_type = fault_type;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Username       : ").append(username).append("\n");
        sb.append("Name           : ").append(name).append("\n");
        sb.append("Duration       : ").append(duration).append("s\n");
        sb.append("Fault Type     : ").append(fault_type).append("\n");
        sb.append("Rate           : ").append(rate).append("\n");

        return sb.toString();
    }
}
