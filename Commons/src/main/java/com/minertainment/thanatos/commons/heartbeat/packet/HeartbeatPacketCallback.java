package com.minertainment.thanatos.commons.heartbeat.packet;

public interface HeartbeatPacketCallback {

    void heartbeatReceived(HeartbeatPacket packet);

}