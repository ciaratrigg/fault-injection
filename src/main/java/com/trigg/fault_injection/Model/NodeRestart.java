package com.trigg.fault_injection.Model;

public class NodeRestart extends Fault {
    int num_nodes;
    int frequency;

    public NodeRestart(int f_id, String username, String name, int duration, int scheduled_for, String fault_type, int num_nodes, int freq) {
        super(f_id, username, name, duration, scheduled_for, fault_type);
        this.num_nodes = num_nodes;
        this.frequency = freq;
    }

    public NodeRestart() {
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

    @Override
    public String toString() {
        return "NodeRestart{" +
                "num_nodes=" + num_nodes +
                ", frequency=" + frequency +
                ", f_id=" + f_id +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", duration=" + duration +
                ", scheduled_for=" + scheduled_for +
                ", fault_type='" + fault_type + '\'' +
                '}';
    }
}
