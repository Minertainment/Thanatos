package com.minertainment.thanatos.commons.configuration;

import com.minertainment.athena.configuration.GSONConfig;

import java.io.File;

public class GlobalConfiguration extends GSONConfig {

    private String clusterId, serverId, serverIP;
    private int serverPort;

    public GlobalConfiguration(File directory, String configurationName) {
        super(directory, configurationName);

        this.clusterId = "cluster";
        this.serverId = "server";
        this.serverIP = "127.0.0.1";
        this.serverPort = 25565;
    }

    public String getClusterId() {
        return clusterId;
    }

    public String getServerId() {
        return serverId;
    }

    public String getServerIP() {
        return serverIP;
    }

    public int getServerPort() {
        return serverPort;
    }

}