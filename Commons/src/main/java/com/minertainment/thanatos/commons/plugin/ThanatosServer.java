package com.minertainment.thanatos.commons.plugin;

import com.minertainment.thanatos.commons.cluster.ClusterManager;
import com.minertainment.thanatos.commons.profile.ThanatosProfile;
import com.minertainment.thanatos.commons.profile.ThanatosProfileManager;

import java.io.File;
import java.util.logging.Logger;

public interface ThanatosServer {

    ThanatosServerType getServerType();

    ClusterManager getClusterManager();

    ThanatosProfileManager getProfileManager();

    Logger getLogger();

    File getDataFolder();

    void onProfileLeave(ThanatosProfile profile);

    int getOnlinePlayers();

    double getTPS();

    long getLastDisconnect();

}