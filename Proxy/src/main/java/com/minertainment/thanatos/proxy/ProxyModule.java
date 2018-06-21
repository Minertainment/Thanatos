package com.minertainment.thanatos.proxy;

import com.minertainment.thanatos.commons.Thanatos;
import com.minertainment.thanatos.commons.cluster.ClusterManager;
import com.minertainment.thanatos.commons.configuration.SlaveConfiguration;
import com.minertainment.thanatos.commons.configuration.ThanatosConfiguration;
import com.minertainment.thanatos.commons.plugin.ThanatosServer;
import com.minertainment.thanatos.commons.plugin.ThanatosServerType;
import com.minertainment.thanatos.commons.profile.ThanatosProfile;
import com.minertainment.thanatos.commons.profile.ThanatosProfileManager;
import com.minertainment.thanatos.proxy.cluster.ProxyClusterManager;
import com.minertainment.thanatos.proxy.commands.BaseCommand;
import com.minertainment.thanatos.proxy.commands.StatusCommand;
import com.minertainment.thanatos.proxy.config.ProxyConfiguration;
import com.minertainment.thanatos.proxy.packet.*;
import com.minertainment.thanatos.proxy.something.PlayerListener;
import net.md_5.bungee.api.plugin.Plugin;

public class ProxyModule extends Plugin implements ThanatosServer {

    private Thanatos thanatos;

    private ThanatosConfiguration thanatosConfiguration;
    private ProxyConfiguration proxyConfiguration;

    private StartClusterListener startClusterListener;
    private FindPlayerListener findPlayerListener;
    private SendPlayerListener sendPlayerListener;
    private ShutdownListener shutdownListener;
    private SendMessageListener sendMessageListener;
    private ThanatosPlayerUpdateListener thanatosPlayerUpdateListener;

    private ProxyClusterManager clusterManager;
    private ThanatosProfileManager profileManager;

    private PlayerListener playerListener;

    @Override
    public void onEnable() {
        thanatos = new Thanatos(this);

        thanatosConfiguration = new ThanatosConfiguration();
        thanatosConfiguration.saveDefaultConfigSync();
        thanatosConfiguration.loadConfigSync();

        proxyConfiguration = new ProxyConfiguration(this);
        proxyConfiguration.saveDefaultConfig();
        proxyConfiguration.loadConfig();

        findPlayerListener = new FindPlayerListener(this);
        startClusterListener = new StartClusterListener(this);
        sendPlayerListener = new SendPlayerListener(this);
        shutdownListener = new ShutdownListener(this);
        sendMessageListener = new SendMessageListener(this);
        thanatosPlayerUpdateListener = new ThanatosPlayerUpdateListener(this);

        clusterManager = new ProxyClusterManager(this);
        profileManager = new ThanatosProfileManager();

        playerListener = new PlayerListener(this);

        new BaseCommand(this);
        new StatusCommand(this);
    }

    @Override
    public void onDisable() {
        sendPlayerListener.disable();
        clusterManager.disable();
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
    public ClusterManager getClusterManager() {
        return clusterManager;
    }

    @Override
    public ThanatosProfileManager getProfileManager() {
        return profileManager;
    }

    @Override
    public void onProfileLeave(ThanatosProfile profile) {

    }

    @Override
    public int getOnlinePlayers() {
        return getProxy().getOnlineCount();
    }

    @Override
    public double getTPS() {
        return 43.21;
    }

    @Override
    public long getLastDisconnect() {
        return -1;
    }

}