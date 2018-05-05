package com.minertainment.thanatos.commons.heartbeat;

import com.minertainment.thanatos.commons.plugin.ThanatosServerType;

public class Heartbeat {

    private String clusterId;
    private String serverId;
    private ThanatosServerType serverType;
    private int onlinePlayers;
    private double tps;
    private long lastDisconnect;

    public Heartbeat(String clusterId, String serverId, ThanatosServerType serverType, int onlinePlayers, double tps, long lastDisconnect) {
        if(clusterId == null || serverId == null || clusterId.isEmpty() || serverId.isEmpty()) {
            throw new IllegalArgumentException("ids cannot be null or empty");
        }

        if(onlinePlayers < 0) {
            throw new IllegalArgumentException("open slots cannot be negative");
        }

        this.clusterId = clusterId;
        this.serverId = serverId;
        this.serverType = serverType;
        this.onlinePlayers = onlinePlayers;
        this.tps = tps;
        this.lastDisconnect = lastDisconnect;
    }

    public String getClusterId() {
        return clusterId;
    }

    public String getServerId() {
        return serverId;
    }

    public ThanatosServerType getServerType() {
        return serverType;
    }

    public int getOnlinePlayers() {
        return onlinePlayers;
    }

    public double getTPS() {
        return tps;
    }

    public long getLastDisconnect() {
        return lastDisconnect;
    }

}