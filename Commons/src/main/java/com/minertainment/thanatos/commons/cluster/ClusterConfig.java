package com.minertainment.thanatos.commons.cluster;

import com.minertainment.athena.configuration.GSONConfig;
import com.minertainment.thanatos.commons.plugin.ThanatosServer;

public class ClusterConfig extends GSONConfig {

    private static int softPlayerLimit, hardPlayerLimit;
    private static double softTPSLimit, hardTPSLimit;

    private long shutdownTimer;

    private Cluster[] clusters;

    public ClusterConfig(ThanatosServer server) {
        super(server.getDataFolder(), "clusters.json");

        softPlayerLimit = 100;
        hardPlayerLimit = 150;
        softTPSLimit = 16.0;
        hardTPSLimit = 13.0;

        shutdownTimer = (20*60*1000);
        clusters = new Cluster[]{
                new Cluster("Cluster")
        };
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

    public long getShutdownTimer() {
        return shutdownTimer;
    }

    public Cluster[] getClusters() {
        return clusters;
    }

}