package com.trigg.fault_injection;

public class NodeCrash extends Fault{
    int num_nodes;

    public int getNum_nodes() {
        return num_nodes;
    }

    public void setNum_nodes(int num_nodes) {
        this.num_nodes = num_nodes;
    }
}
