package com.minertainment.thanatos.commons.packet;

import com.minertainment.athena.configuration.serializable.LazyLocation;
import com.minertainment.athena.packets.AbstractPacket;
import com.minertainment.thanatos.commons.slave.Slave;

import java.util.UUID;

public class SendPlayerPacket extends AbstractPacket {

    private UUID uuid;
    private Slave slave;
    private LazyLocation location;
    private boolean preload;

    public SendPlayerPacket(UUID uuid, Slave slave, boolean preload) {
        this(uuid, slave, null, preload);
    }

    public SendPlayerPacket(UUID uuid, Slave slave, LazyLocation location, boolean preload) {
        super("THANATOS_SEND_PLAYER");
        this.uuid = uuid;
        this.slave = slave;
        this.location = location;
        this.preload = preload;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public Slave getSlave() {
        return slave;
    }

    public LazyLocation getLocation() {
        return location;
    }

    public boolean preload() {
        return preload;
    }

}