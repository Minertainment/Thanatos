package com.minertainment.thanatos.commons.packet.joinrequest;

import com.minertainment.athena.packets.callback.AbstractCallbackData;

public class JoinRequestData extends AbstractCallbackData {

    private String serverId;
    private int playerCount;
    private double tps;

    public JoinRequestData() {}

    public JoinRequestData(String serverId, int playerCount, double tps) {
        this.serverId = serverId;
        this.playerCount = playerCount;
        this.tps = tps;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }

    public double getTPS() {
        return tps;
    }

    public void setTPS(double tps) {
        this.tps = tps;
    }

}