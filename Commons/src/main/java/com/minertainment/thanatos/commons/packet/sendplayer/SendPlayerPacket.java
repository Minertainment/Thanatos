package com.minertainment.thanatos.commons.packet.sendplayer;

import com.minertainment.athena.configuration.serializable.LazyLocation;
import com.minertainment.athena.packets.callback.AbstractCallbackPacket;
import com.minertainment.athena.packets.callback.PacketCallback;
import com.minertainment.thanatos.commons.slave.Slave;

import java.util.UUID;

public class SendPlayerPacket extends AbstractCallbackPacket<SendPlayerData> {

    private UUID uuid;
    private Slave slave;
    private LazyLocation location;
    private UUID targetPlayer;
    private boolean preload;

    public SendPlayerPacket(UUID uuid, Slave slave, boolean preload) {
        this(uuid, slave, null, null, preload);
    }

    public SendPlayerPacket(UUID uuid, Slave slave, LazyLocation location, boolean preload) {
        this(uuid, slave, location, null, preload);
    }

    public SendPlayerPacket(UUID uuid, Slave slave, UUID targetPlayer, boolean preload) {
        this(uuid, slave, null, targetPlayer, preload);
    }

    public SendPlayerPacket(UUID uuid, Slave slave, LazyLocation location, UUID targetPlayer, boolean preload) {
        this(uuid, slave, location, targetPlayer, preload, null);
    }

    public SendPlayerPacket(UUID uuid, Slave slave, LazyLocation location, UUID targetPlayer, boolean preload, PacketCallback<SendPlayerData> callback) {
        super("THANATOS_SEND_PLAYER", callback);
        this.uuid = uuid;
        this.slave = slave;
        this.location = location;
        this.targetPlayer = targetPlayer;
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

    public UUID getTargetPlayer() {
        return targetPlayer;
    }

    public boolean preload() {
        return preload;
    }

}