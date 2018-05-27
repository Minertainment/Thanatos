package com.minertainment.thanatos.commons.packet;

import com.minertainment.athena.packets.AbstractPacket;

import java.util.UUID;

public class TeleportPlayerPacket extends AbstractPacket {

    private UUID uuid;
    private UUID target;
    private boolean preload;

    public TeleportPlayerPacket(UUID uuid, UUID target, boolean preload) {
        super("THANATOS_TELEPORT_PLAYER");
        this.uuid = uuid;
        this.target = target;
        this.preload = preload;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public UUID getTarget() {
        return target;
    }

    public boolean preload() {
        return preload;
    }

}