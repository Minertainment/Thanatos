package com.minertainment.thanatos.slave.packet;

import com.minertainment.athena.configuration.GSONUtils;
import com.minertainment.athena.packets.PacketListener;
import com.minertainment.thanatos.commons.packet.SoundPacket;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SoundPacketListener extends PacketListener<SoundPacket> {

    public SoundPacketListener() {
        super("THANATOS_SOUND");
    }

    @Override
    public SoundPacket parsePacket(String s) {
        return GSONUtils.getGson().fromJson(s, SoundPacket.class);
    }

    @Override
    public void readPacket(SoundPacket soundPacket) {
        Player player = Bukkit.getPlayer(soundPacket.getPlayer());
        if(player != null) {
            player.playSound(player.getLocation(), soundPacket.getSound(), soundPacket.getVolume(), soundPacket.getPitch());
        }
    }
}
