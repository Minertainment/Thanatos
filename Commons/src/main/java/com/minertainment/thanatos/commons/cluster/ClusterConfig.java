package com.minertainment.thanatos.commons.cluster;

import com.minertainment.athena.configuration.GSONConfig;
import com.minertainment.thanatos.commons.plugin.ThanatosServer;

public class ClusterConfig extends GSONConfig {

    private static int softPlayerLimit, hardPlayerLimit;
    private static double softTPSLimit, hardTPSLimit;

    private Cluster[] clusters;

    public ClusterConfig(ThanatosServer server) {
        super(server.getDataFolder(), "clusters.json");

        softPlayerLimit = 150;
        hardPlayerLimit = 250;
        softTPSLimit = 15.0;
        hardTPSLimit = 10.0;
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

    public Cluster[] getClusters() {
        return clusters;
    }

}