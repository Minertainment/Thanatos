package com.minertainment.thanatos.commons.packet.findplayer;

import com.minertainment.athena.packets.callback.AbstractCallbackData;
import com.minertainment.thanatos.commons.slave.Slave;

import java.util.UUID;

public class FindPlayerData extends AbstractCallbackData {

    private UUID uuid;
    private Slave slave;

    public FindPlayerData(UUID uuid, Slave slave) {
        this.uuid = uuid;
        this.slave = slave;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public Slave getSlave() {
        return slave;
    }

}