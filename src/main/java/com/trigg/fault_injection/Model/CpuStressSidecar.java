package com.trigg.fault_injection.Model;

public class CpuStressSidecar extends Fault{
    int num_threads;

    public CpuStressSidecar(int f_id, String username, String name, int duration, int scheduled_for, String fault_type, int num_threads){
        super(f_id, username, name, duration, scheduled_for, fault_type);
        this.num_threads = num_threads;
    }

    public CpuStressSidecar() {

    }

    public int getNum_threads() {
        return num_threads;
    }

    public void setNum_threads(int num_threads) {
        this.num_threads = num_threads;
    }
}
