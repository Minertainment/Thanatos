package com.minertainment.thanatos.commons.slave;

public class Slave {

    private transient long lastHeartbeat;

    private String serverId;
    private int onlinePlayers;
    private double tps;
    private long lastDisconnect;

    public Slave(String serverId, int onlinePlayers, double tps) {
        this.serverId = serverId;
        this.onlinePlayers = onlinePlayers;
        this.tps = tps;
        this.lastDisconnect = -1;

        lastHeartbeat = System.currentTimeMillis();
    }

    public String getServerId() {
        return serverId;
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