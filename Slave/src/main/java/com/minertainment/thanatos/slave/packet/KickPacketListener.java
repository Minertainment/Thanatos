package com.minertainment.thanatos.slave.packet;

import com.minertainment.athena.configuration.GSONUtils;
import com.minertainment.athena.packets.PacketListener;
import com.minertainment.thanatos.commons.packet.KickPacket;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class KickPacketListener extends PacketListener<KickPacket> {

    public KickPacketListener() {
        super("THANATOS_KICK");
    }

    @Override
    public KickPacket parsePacket(String s) {
        return GSONUtils.getGson().fromJson(s, KickPacket.class);
    }

    @Override
    public void readPacket(KickPacket kickPacket) {
        Player player = Bukkit.getPlayer(kickPacket.getPlayer());
        if(player != null) {
            player.kickPlayer(kickPacket.getMessage());
        }
    }
}
