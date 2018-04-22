package com.minertainment.thanatos.commons.configuration.bukkit;

import com.minertainment.athena.configuration.GSONConfig;
import org.bukkit.plugin.Plugin;

public class BukkitModuleConfiguration extends GSONConfig {

    public BukkitModuleConfiguration(Plugin plugin, String configurationName) {
        super(plugin.getDataFolder(), configurationName);
    }

}