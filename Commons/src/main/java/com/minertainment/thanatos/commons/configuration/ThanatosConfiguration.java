package com.minertainment.thanatos.commons.configuration;

import com.minertainment.athena.configuration.MongoConfig;
import com.minertainment.thanatos.commons.cluster.Cluster;

public class ThanatosConfiguration extends MongoConfig {

    private static int softPlayerLimit, hardPlayerLimit;
    private static double softTPSLimit, hardTPSLimit;

    private static long shutdownTimer;

    private static Cluster[] clusters;

    public ThanatosConfiguration() {
        super("Thanatos");

        this.softPlayerLimit = 100;
        this.hardPlayerLimit = 150;
        this.softTPSLimit = 16.0;
        this.hardTPSLimit = 13.0;

        this.shutdownTimer = (20*60*1000);
        this.clusters = new Cluster[]{
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

    public static long getShutdownTimer() {
        return shutdownTimer;
    }

    public static Cluster[] getClusters() {
        return clusters;
    }

}