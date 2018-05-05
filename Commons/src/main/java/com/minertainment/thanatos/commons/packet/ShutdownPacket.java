package com.minertainment.thanatos.commons.packet;

import com.minertainment.athena.packets.AbstractPacket;
import com.minertainment.thanatos.commons.slave.Slave;

public class ShutdownPacket extends AbstractPacket {

    private Slave slave;

    public ShutdownPacket(Slave slave) {
        super("THANATOS_SHUTDOWN_SLAVE");
        this.slave = slave;
    }

    public Slave getSlave() {
        return slave;
    }

}