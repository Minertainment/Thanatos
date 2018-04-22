package com.minertainment.thanatos.commons.plugin;

import com.minertainment.thanatos.commons.cluster.ClusterManager;
import com.minertainment.thanatos.commons.configuration.GlobalConfiguration;
import com.minertainment.thanatos.commons.profile.ThanatosProfileManager;

import java.io.File;
import java.util.logging.Logger;

public interface ThanatosServer {

    ThanatosServerType getServerType();

    GlobalConfiguration getGlobalConfiguration();

    ClusterManager getClusterManager();

    ThanatosProfileManager getProfileManager();

    Logger getLogger();

    File getDataFolder();

    String getClusterId();

    String getServerId();

    String getServerIP();

    int getServerPort();

    int getOnlinePlayers();

    double getTPS();

}