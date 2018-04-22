package com.minertainment.thanatos.proxy;

import com.minertainment.thanatos.commons.Thanatos;
import com.minertainment.thanatos.commons.configuration.GlobalConfiguration;
import com.minertainment.thanatos.commons.plugin.ThanatosServer;
import com.minertainment.thanatos.commons.plugin.ThanatosServerType;
import com.minertainment.thanatos.commons.profile.ThanatosProfileManager;
import com.minertainment.thanatos.commons.cluster.ClusterManager;
import com.minertainment.thanatos.proxy.config.ProxyConfiguration;
import com.minertainment.thanatos.proxy.packet.SendPlayerListener;
import com.minertainment.thanatos.proxy.something.PlayerListener;
import net.md_5.bungee.api.plugin.Plugin;

public class ProxyModule extends Plugin implements ThanatosServer {

    private Thanatos thanatos;

    private GlobalConfiguration globalConfiguration;
    private ProxyConfiguration proxyConfiguration;

    private SendPlayerListener sendPlayerListener;

    private ClusterManager clusterManager;
    private ThanatosProfileManager profileManager;

    private PlayerListener playerListener;

    @Override
    public void onEnable() {
        thanatos = new Thanatos(this);

        globalConfiguration = new GlobalConfiguration(getDataFolder(), "thanatos");
        globalConfiguration.saveDefaultConfig();
        globalConfiguration.loadConfig();

        proxyConfiguration = new ProxyConfiguration(this);
        proxyConfiguration.saveDefaultConfig();
        proxyConfiguration.loadConfig();

        sendPlayerListener = new SendPlayerListener(this);

        clusterManager = new ClusterManager(this);
        profileManager = new ThanatosProfileManager();

        playerListener = new PlayerListener(this);
    }

    @Override
    public void onDisable() {
        sendPlayerListener.disable();
        clusterManager.disable();
        profileManager.disable();
        playerListener.disable();
    }

    public ProxyConfiguration getProxyConfiguration() {
        return proxyConfiguration;
    }

    @Override
    public ThanatosServerType getServerType() {
        return ThanatosServerType.PROXY;
    }

    @Override
    public GlobalConfiguration getGlobalConfiguration() {
        return globalConfiguration;
    }

    @Override
    public ClusterManager getClusterManager() {
        return clusterManager;
    }

    @Override
    public ThanatosProfileManager getProfileManager() {
        return profileManager;
    }

    @Override
    public String getClusterId() {
        return globalConfiguration.getClusterId();
    }

    @Override
    public String getServerId() {
        return globalConfiguration.getServerId();
    }

    @Override
    public String getServerIP() {
        return globalConfiguration.getServerIP();
    }

    @Override
    public int getServerPort() {
        return globalConfiguration.getServerPort();
    }

    @Override
    public int getOnlinePlayers() {
        return getProxy().getOnlineCount();
    }

    @Override
    public double getTPS() {
        return 43.21;
    }
}