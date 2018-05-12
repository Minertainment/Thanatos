package com.minertainment.thanatos.slave.something;

import com.minertainment.thanatos.commons.cluster.ClusterConfig;
import com.minertainment.thanatos.commons.packet.StartClusterPacket;
import com.minertainment.thanatos.slave.SlaveModule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private SlaveModule module;

    public PlayerListener(SlaveModule module) {
        this.module = module;
        module.getServer().getPluginManager().registerEvents(this, module);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if(module.getOnlinePlayers() > ClusterConfig.getSoftPlayerLimit()) {
            new StartClusterPacket(module.getGlobalConfiguration().getClusterId()).send();
        }
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent e) {
        module.setLastDisconnect(System.currentTimeMillis());
    }

}