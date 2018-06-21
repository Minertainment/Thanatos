package com.minertainment.thanatos.commons.configuration;

import com.minertainment.athena.configuration.GSONConfig;
import com.minertainment.thanatos.commons.plugin.ThanatosServer;

import java.io.File;

public class SlaveConfiguration extends GSONConfig {

    private static String clusterId, serverId;
    private static String directory;

    public SlaveConfiguration(ThanatosServer server) {
        super(server.getDataFolder(), "slave.json");

        this.clusterId = "Cluster";
        this.serverId = "Server";

        this.directory = server.getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getParentFile().getAbsolutePath();
    }

    public static String getClusterId() {
        return clusterId;
    }

    public static String getServerId() {
        return serverId;
    }

    public static String getDirectory() {
        return directory;
    }

}