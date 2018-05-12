package com.minertainment.thanatos.commons.packet.joinrequest;

import com.minertainment.athena.packets.callback.AbstractCallbackPacket;
import com.minertainment.athena.packets.callback.PacketCallback;
import com.minertainment.thanatos.commons.slave.Slave;

public class JoinRequestPacket extends AbstractCallbackPacket<JoinRequestData> {

    private Slave slave;

    public JoinRequestPacket(Slave slave, PacketCallback<JoinRequestData> callback) {
        super("THANATOS_JOIN_REQUEST", callback);
        this.slave = slave;
    }

    public Slave getSlave() {
        return slave;
    }

}