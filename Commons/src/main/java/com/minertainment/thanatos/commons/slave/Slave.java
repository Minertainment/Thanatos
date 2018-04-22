package com.minertainment.thanatos.commons.slave;

public class Slave {

    private String serverId, serverIP;
    private int serverPort, onlinePlayers;
    private double tps;

    public Slave(String serverId, String serverIP, int serverPort, int onlinePlayers, double tps) {
        this.serverId = serverId;
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.onlinePlayers = onlinePlayers;
        this.tps = tps;
    }

    public String getServerId() {
        return serverId;
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

    public void setOnlinePlayers(int onlinePlayers) {
        this.onlinePlayers = onlinePlayers;
    }

    public double getTPS() {
        return tps;
    }

    public void setTPS(double tps) {
        this.tps = tps;
    }

}