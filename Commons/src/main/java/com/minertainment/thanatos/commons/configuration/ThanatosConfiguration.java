package com.minertainment.thanatos.commons.configuration;

import com.minertainment.athena.configuration.MongoConfig;
import com.minertainment.thanatos.commons.cluster.Cluster;
import com.minertainment.thanatos.commons.plugin.ThanatosServer;

public class ThanatosConfiguration extends MongoConfig {

    private static int softPlayerLimit, hardPlayerLimit;
    private static double softTPSLimit, hardTPSLimit;
    private static String directory;

    private static long shutdownTimer;

    private static Cluster[] clusters;

    public ThanatosConfiguration(ThanatosServer server) {
        super("Thanatos");

        this.softPlayerLimit = 100;
        this.hardPlayerLimit = 150;
        this.softTPSLimit = 16.0;
        this.hardTPSLimit = 13.0;

        this.directory = server.getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getParentFile().getAbsolutePath();

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

    public static String getDirectory() {
        return directory;
    }

    public static long getShutdownTimer() {
        return shutdownTimer;
    }

    public static Cluster[] getClusters() {
        return clusters;
    }

}