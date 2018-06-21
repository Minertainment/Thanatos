package com.minertainment.thanatos.commons.packet;

import com.minertainment.athena.packets.AbstractPacket;
import com.minertainment.thanatos.commons.slave.Slave;

public class ShutdownPacket extends AbstractPacket {

    private Slave slave;
    private boolean restart;

    public ShutdownPacket(Slave slave, boolean restart) {
        super("THANATOS_SHUTDOWN_SLAVE");
        this.slave = slave;
        this.restart = restart;
    }

    public Slave getSlave() {
        return slave;
    }

    public boolean isRestart() {
        return restart;
    }

}