package com.minertainment.thanatos.commons.heartbeat;

import com.minertainment.thanatos.commons.plugin.ThanatosServerType;
import com.minertainment.thanatos.commons.serialization.Encoding;
import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

public class Heartbeat {

    private String clusterId;
    private String serverId;
    private ThanatosServerType serverType;
    private String serverIP;
    private int serverPort;
    private int onlinePlayers;
    private double tps;

    public Heartbeat(String clusterId, String serverId, ThanatosServerType serverType,
                     String serverIP, int serverPort, int onlinePlayers, double tps) {
        if(clusterId == null || serverId == null || clusterId.isEmpty() || serverId.isEmpty()) {
            throw new IllegalArgumentException("ids cannot be null or empty");
        }

        if(onlinePlayers < 0) {
            throw new IllegalArgumentException("open slots cannot be negative");
        }

        this.clusterId = clusterId;
        this.serverId = serverId;
        this.serverType = serverType;
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.onlinePlayers = onlinePlayers;
        this.tps = tps;
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

    public String getServerIP() {
        return serverIP;
    }

    public int getServerPort() {
        return serverPort;
    }

    public int getOnlinePlayers() {
        return onlinePlayers;
    }

    public double getTPS() {
        return tps;
    }

}