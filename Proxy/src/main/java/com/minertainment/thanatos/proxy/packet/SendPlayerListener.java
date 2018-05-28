package com.minertainment.thanatos.proxy.packet;

import com.minertainment.athena.configuration.GSONUtils;
import com.minertainment.athena.packets.PacketListener;
import com.minertainment.athena.packets.callback.PacketCallback;
import com.minertainment.athena.profile.packet.preload.PreloadPacket;
import com.minertainment.athena.profile.packet.preload.complete.PreloadCompleteListener;
import com.minertainment.athena.profile.packet.save.SaveProfileData;
import com.minertainment.athena.profile.packet.save.SaveProfilePacket;
import com.minertainment.athena.profile.packet.save.complete.SaveCompleteListener;
import com.minertainment.thanatos.commons.packet.SendPlayerPacket;
import com.minertainment.thanatos.proxy.ProxyModule;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;

public class SendPlayerListener extends PacketListener<SendPlayerPacket> {

    private ProxyModule proxy;

    public SendPlayerListener(ProxyModule proxy) {
        super("THANATOS_SEND_PLAYER");
        this.proxy = proxy;
        //new SaveCompleteListener(packet -> new PreloadPacket(packet.getUniqueId(), packet.getConnectServerId()).send());

        new PreloadCompleteListener(packet -> {
            ProxiedPlayer player = proxy.getProxy().getPlayer(packet.getUniqueId());
            if(player == null) {
                // TODO: Throw exception?
                System.out.println(" --- NULL PLAYER (PROXY)");
                return;
            }

            ServerInfo serverInfo = proxy.getProxy().getServerInfo(packet.getServerId());
            if(serverInfo == null) {
                // TODO: Throw exception?
                System.out.println(" --- NULL SERVERINFO");
                return;
            }

            // TODO: Add debug messages
            player.connect(serverInfo);
        });
    }

    @Override
    public SendPlayerPacket parsePacket(String json) {
        return GSONUtils.getGson().fromJson(json, SendPlayerPacket.class);
    }

    @Override
    public void readPacket(SendPlayerPacket packet) {
        ProxiedPlayer player = proxy.getProxy().getPlayer(packet.getUniqueId());
        if(player == null) {
            // TODO: Throw exception?
            return;
        }

        // TODO: Add debug messages
        if(packet.preload()) {
            if(player.getServer() != null && player.getServer().getInfo() != null &&
                    !proxy.getClusterManager().getClusterFromSlave(player.getServer().getInfo().getName())
                            .getClusterId().equals(proxy.getProxyConfiguration().getFallbackCluster())) {
                new SaveProfilePacket(player.getUniqueId(), player.getServer().getInfo().getName(), packet.getSlave().getServerId(), new PacketCallback<SaveProfileData>() {
                    @Override
                    public void onResponse(SaveProfileData saveProfileData) {
                        new PreloadPacket(packet.getUniqueId(), packet.getSlave().getServerId(), saveProfileData.getProfiles()).send();
                    }
                }).send();
                //System.out.println(" --- SAVE PROFILE (PLAYER HAS A SERVER)");
            } else {
                new PreloadPacket(packet.getUniqueId(), packet.getSlave().getServerId(), new HashMap<>()).send();
                //System.out.println(" --- PRELOAD (FRESH PLAYER)");
            }
        } else {
            ServerInfo serverInfo = proxy.getProxy().getServerInfo(packet.getSlave().getServerId());
            if(serverInfo == null) {
                // TODO: Throw exception?
                return;
            }
            player.connect(serverInfo);
        }
    }

}