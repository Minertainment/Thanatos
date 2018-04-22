package com.minertainment.thanatos.slave.something;

import com.minertainment.athena.configuration.serializable.LazyLocation;
import com.minertainment.thanatos.slave.SlaveModule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private SlaveModule module;

    public PlayerListener(SlaveModule module) {
        this.module = module;
        module.getServer().getPluginManager().registerEvents(this, module);
    }

}