package com.minertainment.thanatos.proxy.packet;

import com.minertainment.athena.configuration.GSONUtils;
import com.minertainment.athena.packets.PacketListener;
import com.minertainment.athena.packets.callback.PacketCallback;
import com.minertainment.athena.profile.packet.preload.PreloadPacket;
import com.minertainment.athena.profile.packet.preload.PreloadProfileData;
import com.minertainment.athena.profile.packet.save.SaveProfileData;
import com.minertainment.athena.profile.packet.save.SaveProfilePacket;
import com.minertainment.thanatos.commons.packet.sendplayer.SendPlayerPacket;
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

        // Already connected to the server.
        if(player.getServer() != null && player.getServer().getInfo() != null &&
                player.getServer().getInfo().getName().equals(packet.getSlave().getServerId())) {
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
                        preload(packet, saveProfileData.getProfiles());
                    }
                }).send();
                //System.out.println(" --- SAVE PROFILE (PLAYER HAS A SERVER)");
            } else {
                preload(packet, new HashMap<>());
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

    public void preload(SendPlayerPacket packet, HashMap<String, String> profileMap) {
        new PreloadPacket(packet.getUniqueId(), packet.getSlave().getServerId(), profileMap, new PacketCallback<PreloadProfileData>() {
            @Override
            public void onResponse(PreloadProfileData data) {
                ProxiedPlayer player = proxy.getProxy().getPlayer(packet.getUniqueId());
                if(player == null) {
                    // TODO: Throw exception?
                    System.out.println(" --- NULL PLAYER (PROXY)");
                    return;
                }

                ServerInfo serverInfo = proxy.getProxy().getServerInfo(packet.getSlave().getServerId());
                if(serverInfo == null) {
                    // TODO: Throw exception?
                    System.out.println(" --- NULL SERVERINFO");
                    return;
                }

                // TODO: Add debug messages
                player.connect(serverInfo);
            }
        }).send();
    }

}