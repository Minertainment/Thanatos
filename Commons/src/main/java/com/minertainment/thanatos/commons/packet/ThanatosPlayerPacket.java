package com.minertainment.thanatos.commons.packet;

import com.minertainment.athena.packets.AbstractPacket;

import java.util.UUID;

public class ThanatosPlayerPacket extends AbstractPacket {

    private UUID id;
    private String name;
    private boolean add;

    public ThanatosPlayerPacket(UUID id, String name, boolean add) {
        super("THANATOS_PLAYER");
        this.id = id;
        this.name = name;
        this.add = add;
    }

    public UUID getUUID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isAdd() {
        return add;
    }

}
