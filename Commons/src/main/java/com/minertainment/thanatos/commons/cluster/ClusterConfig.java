package com.minertainment.thanatos.commons.cluster;

import com.minertainment.athena.configuration.GSONConfig;
import com.minertainment.thanatos.commons.plugin.ThanatosServer;

public class ClusterConfig extends GSONConfig {

    private static String directory;

    private static long shutdownTimer;

    private static Cluster[] clusters;

    public ClusterConfig(ThanatosServer server) {
        super(server.getDataFolder(), "clusters.json");

        directory = server.getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getParentFile().getAbsolutePath();

        shutdownTimer = (20*60*1000);
        clusters = new Cluster[]{
                new Cluster("Cluster")
        };
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