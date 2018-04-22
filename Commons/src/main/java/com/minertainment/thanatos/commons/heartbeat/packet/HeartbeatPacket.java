package com.minertainment.thanatos.commons.heartbeat.packet;

import com.minertainment.athena.packets.AbstractPacket;
import com.minertainment.thanatos.commons.heartbeat.Heartbeat;

public class HeartbeatPacket extends AbstractPacket {

    private Heartbeat heartbeat;

    public HeartbeatPacket(Heartbeat heartbeat) {
        super("HEARTBEAT_PACKET");

        this.heartbeat = heartbeat;
    }

    public Heartbeat getHeartbeat() {
        return heartbeat;
    }

}