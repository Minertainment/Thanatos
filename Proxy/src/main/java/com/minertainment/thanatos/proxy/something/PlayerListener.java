package com.minertainment.thanatos.proxy.something;

import com.minertainment.athena.configuration.GSONUtils;
import com.minertainment.athena.configuration.serializable.LazyLocation;
import com.minertainment.athena.packets.PacketListener;
import com.minertainment.athena.plugin.bukkit.packet.SaveCompletePacket;
import com.minertainment.jcore.JCore;
import com.minertainment.jcore.warps.Warp;
import com.minertainment.thanatos.commons.cluster.Cluster;
import com.minertainment.thanatos.commons.cluster.SlaveCallback;
import com.minertainment.thanatos.commons.packet.ThanatosPlayerPacket;
import com.minertainment.thanatos.commons.packet.sendplayer.SendPlayerPacket;
import com.minertainment.thanatos.commons.profile.ThanatosProfile;
import com.minertainment.thanatos.commons.slave.Slave;
import com.minertainment.thanatos.commons.slave.SlaveStatus;
import com.minertainment.thanatos.proxy.ProxyModule;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.HashMap;
import java.util.UUID;

public class PlayerListener extends PacketListener<SaveCompletePacket> implements Listener {

    private ProxyModule proxy;
    private HashMap<UUID, Long> recentlyDisconnected = new HashMap<>();

    public PlayerListener(ProxyModule proxy) {
        super("SAVE_COMPLETE_QUIT");
        this.proxy = proxy;

        proxy.getProxy().getPluginManager().registerListener(proxy, this);
    }

    @EventHandler
    public void onPreLogin(PreLoginEvent e) {
        if(proxy.getProxy().getPlayer(e.getConnection().getUniqueId()) != null || proxy.getProxy().getPlayer(e.getConnection().getName()) != null) {
            e.setCancelled(true);
            e.setCancelReason("You are already connected to this server");
            return;
        }

        Long time = recentlyDisconnected.get(e.getConnection().getUniqueId());
        if(time != null) {
            if(time < 5000) {
                e.setCancelled(true);
                e.setCancelReason("Tried re-connecting to quickly, please try again in a couple seconds");
            } else {
                recentlyDisconnected.remove(e.getConnection().getUniqueId());
                proxy.getLogger().warning("Allowing user " + e.getConnection().getName() + " on the server after hitting 5 second limit.");
            }
        }
    }

    @EventHandler
    public void onPreLogin(ServerConnectEvent e) {

        // Ignore existing connections to the proxy.
        if(e.getPlayer().getServer() != null) {
            return;
        }

        // Make sure one slave is online from each cluster.
        for(Cluster cluster : proxy.getClusterManager().getClusterMap().values()) {

            // Find a slave online.
            boolean clusterReady = false;
            for(Slave slave : cluster.getSlaves().values()) {
                if(slave.getStatus() == SlaveStatus.ONLINE) {
                    clusterReady = true;
                    break;
                }
            }

            // Cluster has no slaves online.
            if(!clusterReady) {
                e.getPlayer().disconnect(new ComponentBuilder("Please wait a few seconds before reconnecting...").color(ChatColor.RED).create());
                e.setCancelled(true);
                return;
            }
        }

        // Get the default cluster if it exists.
        Cluster defaultCluster = proxy.getClusterManager().getCluster(proxy.getProxyConfiguration().getDefaultCluster());
        if(defaultCluster == null) {
            proxy.getLogger().warning("Default cluster ['" + proxy.getProxyConfiguration().getDefaultCluster() + "'] does not exist!");
            return;
        }

        Cluster fallbackCluster = proxy.getClusterManager().getCluster(proxy.getProxyConfiguration().getFallbackCluster());
        if(fallbackCluster == null) {
            proxy.getLogger().warning("Fallback cluster ['" + proxy.getProxyConfiguration().getFallbackCluster() + "'] does not exist!");
            return;
        }

        // Send the player to the fallback cluster to pre-load their profile data.
        Slave fallbackSlave = fallbackCluster.getNextSlaveCached();
        ServerInfo fallbackInfo = proxy.getProxy().getServerInfo(fallbackSlave.getServerId());
        if(fallbackInfo != null && !e.getTarget().getName().equals(fallbackInfo.getName())) {
            e.setTarget(fallbackInfo);
        }

        // Pre-load the data onto the default cluster if no specific cluster is specified.
        // Ignore cache because the player should have their data stored in Mongo.
        proxy.getProfileManager().loadProfile(e.getPlayer().getUniqueId(), profile -> {
            ThanatosProfile thanatosProfile = (ThanatosProfile) profile;

            Cluster lastCluster = proxy.getClusterManager().getCluster(thanatosProfile.getLastCluster());

            Cluster finalCluster = (lastCluster != null ? lastCluster : defaultCluster);
            finalCluster.getNextSlave(new SlaveCallback() {
                @Override
                public void onCallback(Slave slave) {
                    if(slave == null) {
                        proxy.getLogger().severe("Could not find a suitable slave to move (" + e.getPlayer().getName() + ") to! (Pre-Login)");
                        return;
                    }

                    LazyLocation location = thanatosProfile.getLastLocation();
                    if(location == null) {
                        Warp warp = JCore.getWarpManager().getWarp("spawn");
                        if(warp != null && warp.getLocation() != null) {
                            location = warp.getLocation();
                        }
                    }

                    new ThanatosPlayerPacket(e.getPlayer().getUniqueId(), e.getPlayer().getName(), true).send();
                    new SendPlayerPacket(e.getPlayer().getUniqueId(), slave, location, true).send();
                }
            });
        });
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent e) {
        ProxiedPlayer proxiedPlayer = e.getPlayer();
        recentlyDisconnected.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());
        new ThanatosPlayerPacket(proxiedPlayer.getUniqueId(), proxiedPlayer.getName(), false).send();
    }

    @Override
    public SaveCompletePacket parsePacket(String s) {
        return GSONUtils.getGson().fromJson(s, SaveCompletePacket.class);
    }

    @Override
    public void readPacket(SaveCompletePacket saveCompletePacket) {
        recentlyDisconnected.remove(saveCompletePacket.getPlayer());
    }

    public void disable() {

    }

}