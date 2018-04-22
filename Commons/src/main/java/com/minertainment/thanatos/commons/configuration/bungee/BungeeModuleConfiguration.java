package com.minertainment.thanatos.commons.configuration.bungee;

import com.minertainment.athena.configuration.GSONConfig;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeModuleConfiguration extends GSONConfig {

    public BungeeModuleConfiguration(Plugin plugin, String configurationName) {
        super(plugin.getDataFolder(), configurationName);
    }

}