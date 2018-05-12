package com.minertainment.thanatos.commons.slave;

public class Slave {

    private transient long lastHeartbeat;

    private String serverId;
    private SlaveStatus status;
    private int onlinePlayers;
    private double tps;
    private long lastDisconnect;

    public Slave(String serverId, SlaveStatus status, int onlinePlayers, double tps) {
        this.serverId = serverId;
        this.status = status;
        this.onlinePlayers = onlinePlayers;
        this.tps = tps;
        this.lastDisconnect = -1;

        lastHeartbeat = System.currentTimeMillis();
    }

    public String getServerId() {
        return serverId;
    }

    public SlaveStatus getStatus() {
        return status;
    }

    public void setStatus(SlaveStatus status) {
        this.status = status;
    }

    public int getOnlinePlayers() {
        return onlinePlayers;
    }

    public void setOnlinePlayers(int onlinePlayers) {
        this.onlinePlayers = onlinePlayers;
    }

    public double getTPS() {
        return tps;
    }

    public void setTPS(double tps) {
        this.tps = tps;
    }

    public long getLastDisconnect() {
        return lastDisconnect;
    }

    public void setLastDisconnect(long lastDisconnect) {
        this.lastDisconnect = lastDisconnect;
    }

    public long getLastHeartbeat() {
        return lastHeartbeat;
    }

    public void heartbeat() {
        this.lastHeartbeat = System.currentTimeMillis();
    }

}