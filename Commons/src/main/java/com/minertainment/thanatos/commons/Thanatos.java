package com.minertainment.thanatos.commons;

import com.minertainment.thanatos.commons.cluster.ClusterManager;
import com.minertainment.thanatos.commons.plugin.ThanatosServer;

public class Thanatos {

    private static ThanatosServer server;

    public Thanatos(ThanatosServer server) {
        Thanatos.server = server;
    }

    public static ThanatosServer getServer() {
        return server;
    }

    public static ClusterManager getClusterManager() {
        return server.getClusterManager();
    }

}