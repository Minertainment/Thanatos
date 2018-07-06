package com.minertainment.thanatos.slave.packet;

import com.minertainment.athena.configuration.GSONUtils;
import com.minertainment.athena.configuration.serializable.LazyLocation;
import com.minertainment.athena.packets.PacketListener;
import com.minertainment.thanatos.commons.configuration.SlaveConfiguration;
import com.minertainment.thanatos.commons.packet.findplayer.FindPlayerData;
import com.minertainment.thanatos.commons.packet.findplayer.FindPlayerPacket;
import com.minertainment.thanatos.commons.slave.Slave;
import com.minertainment.thanatos.slave.SlaveModule;
import org.bukkit.entity.Player;

public class FindPlayerListener extends PacketListener<FindPlayerPacket> {

    private SlaveModule module;

    public FindPlayerListener(SlaveModule module) {
        super("THANATOS_FIND_PLAYER");
        this.module = module;
    }

    @Override
    public FindPlayerPacket parsePacket(String json) {
        return GSONUtils.getGson().fromJson(json, FindPlayerPacket.class);
    }

    @Override
    public void readPacket(FindPlayerPacket packet) {
        Player player;
        if(packet.getUniqueId() != null) {
            player = module.getServer().getPlayer(packet.getUniqueId());
        } else {
            player = module.getServer().getPlayer(packet.getUsername());
        }

        if(player != null && player.isOnline()) {
            Slave slave = module.getClusterManager().getSlave(SlaveConfiguration.getServerId());
            if(slave != null) {
                packet.setCallbackData(new FindPlayerData(packet.getUniqueId(), player.getName(), slave, new LazyLocation().setLocation(player.getLocation())));
                packet.respond();
            }
        }
    }

}