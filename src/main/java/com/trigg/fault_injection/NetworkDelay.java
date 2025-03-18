package com.trigg.fault_injection;

public class NetworkDelay extends Fault{
    private int delay;
    private int delta;

    public NetworkDelay(int f_id, int u_id, String name, int duration, int scheduled_for,String fault_type, int delay, int delta){
        super(f_id, u_id, name, duration, scheduled_for, fault_type);
        this.delay = delay;
        this.delta = delta;
    }

    public NetworkDelay() {
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getDelta() {
        return delta;
    }

    public void setDelta(int delta) {
        this.delta = delta;
    }
}
