package com.minertainment.thanatos.commons.packet;

import com.minertainment.athena.packets.AbstractPacket;

import java.util.UUID;

public class KickPacket extends AbstractPacket {

    private UUID player;
    private String message;

    public KickPacket(UUID player) {
        this(player, "");
    }

    public KickPacket(UUID player, String message) {
        super("THANATOS_KICK");
        this.player = player;
        this.message = message;
    }

    public UUID getPlayer() {
        return player;
    }

    public String getMessage() {
        return message;
    }

}
