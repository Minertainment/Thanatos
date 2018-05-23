package com.minertainment.thanatos.commons.packet;

import com.minertainment.athena.packets.AbstractPacket;
import org.bukkit.Sound;

import java.util.UUID;

public class SoundPacket extends AbstractPacket {

    private UUID player;
    private Sound sound;
    private float pitch;
    private float volume;

    public SoundPacket(UUID player, Sound sound) {
        this(player, sound, 1, 1);
    }

    public SoundPacket(UUID player, Sound sound, float pitch, float volume) {
        super("THANATOS_SOUND");
        this.player = player;
        this.sound = sound;
        this.pitch = pitch;
        this.volume = volume;
    }

    public UUID getPlayer() {
        return player;
    }

    public Sound getSound() {
        return sound;
    }

    public float getPitch() {
        return pitch;
    }

    public float getVolume() {
        return volume;
    }

}
