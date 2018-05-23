package com.minertainment.thanatos.commons.configuration;

import com.minertainment.athena.configuration.GSONConfig;

import java.io.File;

public class GlobalConfiguration extends GSONConfig {

    private static String clusterId, serverId;

    private static int softPlayerLimit, hardPlayerLimit;
    private static double softTPSLimit, hardTPSLimit;

    public GlobalConfiguration(File directory, String configurationName) {
        super(directory, configurationName);

        this.clusterId = "Cluster";
        this.serverId = "Server";

        this.softPlayerLimit = 100;
        this.hardPlayerLimit = 150;
        this.softTPSLimit = 16.0;
        this.hardTPSLimit = 13.0;
    }

    public static int getSoftPlayerLimit() {
        return softPlayerLimit;
    }

    public static int getHardPlayerLimit() {
        return hardPlayerLimit;
    }

    public static double getHardTPSLimit() {
        return hardTPSLimit;
    }

    public static double getSoftTPSLimit() {
        return softTPSLimit;
    }

    public static String getClusterId() {
        return clusterId;
    }

    public static String getServerId() {
        return serverId;
    }

}