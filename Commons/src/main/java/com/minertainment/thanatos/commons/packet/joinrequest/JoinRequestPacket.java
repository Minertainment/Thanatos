package com.minertainment.thanatos.commons.packet.joinrequest;

import com.minertainment.athena.packets.callback.AbstractCallbackPacket;
import com.minertainment.thanatos.commons.slave.Slave;

public class JoinRequestPacket extends AbstractCallbackPacket<JoinRequestCallback> {

    private Slave slave;

    public JoinRequestPacket(Slave slave, JoinRequestCallback callback) {
        super("THANATOS_JOIN_REQUEST", callback);
        this.slave = slave;
    }

    public Slave getSlave() {
        return slave;
    }

}