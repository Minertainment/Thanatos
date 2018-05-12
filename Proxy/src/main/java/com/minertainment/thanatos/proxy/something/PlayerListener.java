package com.minertainment.thanatos.proxy.something;

import com.minertainment.thanatos.commons.cluster.Cluster;
import com.minertainment.thanatos.commons.cluster.SlaveCallback;
import com.minertainment.thanatos.commons.packet.SendPlayerPacket;
import com.minertainment.thanatos.commons.profile.ThanatosProfile;
import com.minertainment.thanatos.commons.slave.Slave;
import com.minertainment.thanatos.proxy.ProxyModule;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerListener implements Listener {

    private ProxyModule proxy;

    public PlayerListener(ProxyModule proxy) {
        this.proxy = proxy;

        proxy.getProxy().getPluginManager().registerListener(proxy, this);
    }

    @EventHandler
    public void onPreLogin(ServerConnectEvent e) {

        // Ignore existing connections to the proxy.
        if(e.getPlayer().getServer() != null) {
            return;
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
        proxy.getProfileManager().loadProfile(e.getPlayer().getUniqueId(), false, profile -> {
            ThanatosProfile thanatosProfile = (ThanatosProfile) profile;

            Cluster lastCluster = proxy.getClusterManager().getCluster(thanatosProfile.getLastCluster());

            Cluster finalCluster = (lastCluster != null ? lastCluster : defaultCluster);
            //System.out.println("Searching for slave from " + finalCluster.getClusterId());
            finalCluster.getNextSlave(new SlaveCallback() {
                @Override
                public void onCallback(Slave slave) {
                    if(slave == null) {
                        proxy.getLogger().severe("Could not find a suitable slave to move (" + e.getPlayer().getName() + ") to! (Pre-Login)");
                        return;
                    }
                    //System.out.println("Found slave " + slave.getServerId());

                    if(lastCluster == null) {
                        thanatosProfile.setLastCluster(defaultCluster.getClusterId());
                        thanatosProfile.setLastSlave(slave.getServerId());
                        proxy.getProfileManager().saveProfile(thanatosProfile);
                    }

                    new SendPlayerPacket(e.getPlayer().getUniqueId(), slave, thanatosProfile.getLastLocation(), true).send();

                    // TODO: Use this when switching servers
                    /*new AsyncTask(() -> {
                        for(ProfileManager profileManager : ProfileManager.getProfileManagers()) {
                            profileManager.saveProfile(profile.getUniqueId(), true);
                        }
                    }).run();*/
                }
            });
        });
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent e) {
        ProxiedPlayer proxiedPlayer = e.getPlayer();
    }

    public void disable() {

    }

}