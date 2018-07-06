package com.minertainment.thanatos.commons.packet.findplayer;

import com.minertainment.athena.configuration.serializable.LazyLocation;
import com.minertainment.athena.packets.callback.AbstractCallbackData;
import com.minertainment.thanatos.commons.slave.Slave;

import java.util.UUID;

public class FindPlayerData extends AbstractCallbackData {

    private UUID uuid;
    private String name;
    private Slave slave;
    private LazyLocation location;

    public FindPlayerData(UUID uuid, String name, Slave slave, LazyLocation location) {
        this.uuid = uuid;
        this.name = name;
        this.slave = slave;
        this.location = location;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public Slave getSlave() {
        return slave;
    }

    public LazyLocation getLocation() {
        return location;
    }

}