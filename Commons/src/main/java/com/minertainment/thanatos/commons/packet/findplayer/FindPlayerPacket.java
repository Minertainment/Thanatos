package com.minertainment.thanatos.commons.packet.findplayer;

import com.minertainment.athena.packets.callback.AbstractCallbackPacket;
import com.minertainment.athena.packets.callback.PacketCallback;

import java.util.UUID;

public class FindPlayerPacket extends AbstractCallbackPacket<FindPlayerData> {

    private UUID uuid;
    private String username;

    public FindPlayerPacket(UUID uuid, PacketCallback<FindPlayerData> callback) {
        this(uuid, null, callback);
    }

    public FindPlayerPacket(String username, PacketCallback<FindPlayerData> callback) {
        this(null, username, callback);
    }

    public FindPlayerPacket(UUID uuid, String username, PacketCallback<FindPlayerData> callback) {
        super("THANATOS_FIND_PLAYER", callback);
        this.uuid = uuid;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

}