package com.minertainment.thanatos.proxy.config;

import com.minertainment.athena.configuration.GSONConfig;
import net.md_5.bungee.api.plugin.Plugin;

public class ProxyConfiguration extends GSONConfig {

    private String defaultCluster;
    private String fallbackCluster;

    public ProxyConfiguration(Plugin plugin) {
        super(plugin.getDataFolder(), "slave.json");

        defaultCluster = "Cluster";
        fallbackCluster = "Fallback";
    }

    public String getDefaultCluster() {
        return defaultCluster;
    }

    public String getFallbackCluster() {
        return fallbackCluster;
    }

}