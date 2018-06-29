package com.minertainment.thanatos.commons.configuration;

import com.minertainment.athena.configuration.GSONConfig;
import com.minertainment.thanatos.commons.plugin.ThanatosServer;

public class SlaveConfiguration extends GSONConfig {

    private static String clusterId, serverId;

    public SlaveConfiguration(ThanatosServer server) {
        super(server.getDataFolder(), "slave.json");

        this.clusterId = "Cluster";
        this.serverId = "Server";
    }

    public static String getClusterId() {
        return clusterId;
    }

    public static String getServerId() {
        return serverId;
    }

}