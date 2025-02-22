package com.trigg.fault_injection;

public class NodeRestart extends Fault{
    int num_nodes;
    int frequency;

    public NodeRestart(int f_id, int u_id, String name, int duration, int scheduled_for, int num_nodes, int freq) {
        super(f_id, u_id, name, duration, scheduled_for);
        this.num_nodes = num_nodes;
        this.frequency = freq;

    }

    public int getNum_nodes() {
        return num_nodes;
    }

    public void setNum_nodes(int num_nodes) {
        this.num_nodes = num_nodes;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
}
