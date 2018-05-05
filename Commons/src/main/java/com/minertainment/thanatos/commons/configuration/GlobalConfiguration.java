package com.minertainment.thanatos.commons.configuration;

import com.minertainment.athena.configuration.GSONConfig;

import java.io.File;

public class GlobalConfiguration extends GSONConfig {

    private String clusterId, serverId;

    public GlobalConfiguration(File directory, String configurationName) {
        super(directory, configurationName);

        this.clusterId = "Cluster";
        this.serverId = "Server";
    }

    public String getClusterId() {
        return clusterId;
    }

    public String getServerId() {
        return serverId;
    }

}