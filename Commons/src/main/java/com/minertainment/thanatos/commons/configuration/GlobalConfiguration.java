package com.minertainment.thanatos.commons.configuration;

import com.minertainment.athena.configuration.GSONConfig;

import java.io.File;

public class GlobalConfiguration extends GSONConfig {

    private static String clusterId, serverId;

    public GlobalConfiguration(File directory, String configurationName) {
        super(directory, configurationName);

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