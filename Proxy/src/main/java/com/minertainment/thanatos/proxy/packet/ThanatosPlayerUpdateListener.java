package com.minertainment.thanatos.proxy.packet;

import com.minertainment.athena.configuration.GSONUtils;
import com.minertainment.athena.packets.PacketListener;
import com.minertainment.thanatos.commons.packet.playerupdate.ThanatosPlayerUpdateData;
import com.minertainment.thanatos.commons.packet.playerupdate.ThanatosPlayerUpdatePacket;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.HashMap;
import java.util.UUID;

public class ThanatosPlayerUpdateListener extends PacketListener<ThanatosPlayerUpdatePacket> {

    private Plugin plugin;

    public ThanatosPlayerUpdateListener(Plugin plugin) {
        super("THANATOS_PLAYER_UPDATE");
        this.plugin = plugin;
    }

    @Override
    public ThanatosPlayerUpdatePacket parsePacket(String s) {
        return GSONUtils.getGson().fromJson(s, ThanatosPlayerUpdatePacket.class);
    }

    @Override
    public void readPacket(ThanatosPlayerUpdatePacket thanatosPlayerUpdatePacket) {
        HashMap<UUID, String> players = new HashMap<>();
        for(ProxiedPlayer player : plugin.getProxy().getPlayers()) {
            players.put(player.getUniqueId(), player.getName());
        }
        thanatosPlayerUpdatePacket.setCallbackData(new ThanatosPlayerUpdateData(players));
        thanatosPlayerUpdatePacket.respond();
    }
}
