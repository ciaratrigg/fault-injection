package com.trigg.fault_injection;

public class NodeCrash extends Fault{
    int num_nodes;

    public NodeCrash(int f_id, String username, String name, int duration, int scheduled_for, String fault_type, int num_nodes) {
        super(f_id, username, name, duration, scheduled_for, fault_type);
        this.num_nodes = num_nodes;
    }

    public NodeCrash() {
    }

    public int getNum_nodes() {
        return num_nodes;
    }

    public void setNum_nodes(int num_nodes) {
        this.num_nodes = num_nodes;
    }

}
