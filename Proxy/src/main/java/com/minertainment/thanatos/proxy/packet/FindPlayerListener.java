package com.minertainment.thanatos.proxy.packet;

import com.minertainment.athena.configuration.GSONUtils;
import com.minertainment.athena.packets.PacketListener;
import com.minertainment.thanatos.commons.packet.findplayer.FindPlayerData;
import com.minertainment.thanatos.commons.packet.findplayer.FindPlayerPacket;
import com.minertainment.thanatos.commons.slave.Slave;
import com.minertainment.thanatos.proxy.ProxyModule;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class FindPlayerListener extends PacketListener<FindPlayerPacket> {

    private ProxyModule module;

    public FindPlayerListener(ProxyModule module) {
        super("THANATOS_FIND_PLAYER");
        this.module = module;
    }

    @Override
    public FindPlayerPacket parsePacket(String json) {
        return GSONUtils.getGson().fromJson(json, FindPlayerPacket.class);
    }

    @Override
    public void readPacket(FindPlayerPacket packet) {
        ProxiedPlayer player = module.getProxy().getPlayer(packet.getUniqueId());
        if(player != null) {
            Slave slave = module.getClusterManager().getSlave(player.getServer().getInfo().getName());
            if(slave != null) {
                packet.setCallbackData(new FindPlayerData(packet.getUniqueId(), slave));
            }
        }

        if(packet.getCallbackData() == null) {
            packet.setCallbackData(new FindPlayerData(packet.getUniqueId(), null));
        }

        packet.respond();
    }

}