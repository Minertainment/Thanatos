package com.minertainment.thanatos.commons.heartbeat.packet;

import com.minertainment.athena.Athena;
import com.minertainment.athena.configuration.GSONUtils;
import com.minertainment.athena.packets.PacketListener;

public class HeartbeatPacketListener extends PacketListener<HeartbeatPacket> {

    private HeartbeatPacketCallback packetCallback;

    public HeartbeatPacketListener(HeartbeatPacketCallback packetCallback) {
        super("HEARTBEAT_PACKET");

        this.packetCallback = packetCallback;
    }

    @Override
    public HeartbeatPacket parsePacket(String json) {
        return GSONUtils.getGson().fromJson(json, HeartbeatPacket.class);
    }

    @Override
    public void readPacket(HeartbeatPacket heartbeatPacket) {
        packetCallback.heartbeatReceived(heartbeatPacket);
    }

}

