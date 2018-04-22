package com.minertainment.thanatos.slave;

import com.minertainment.thanatos.commons.Thanatos;
import com.minertainment.thanatos.commons.cluster.ClusterManager;
import com.minertainment.thanatos.commons.configuration.GlobalConfiguration;
import com.minertainment.thanatos.commons.heartbeat.BeatingHeart;
import com.minertainment.thanatos.commons.plugin.ThanatosServer;
import com.minertainment.thanatos.commons.plugin.ThanatosServerType;
import com.minertainment.thanatos.commons.profile.ThanatosProfileManager;
import com.minertainment.thanatos.slave.config.SlaveConfiguration;
import com.minertainment.thanatos.slave.packet.SendPlayerBukkitListener;
import com.minertainment.thanatos.slave.something.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SlaveModule extends JavaPlugin implements ThanatosServer {

    private Thanatos thanatos;

    private GlobalConfiguration globalConfiguration;
    private SlaveConfiguration slaveConfiguration;

    private BeatingHeart beatingHeart;
    private ClusterManager clusterManager;
    private ThanatosProfileManager profileManager;

    private SendPlayerBukkitListener sendPlayerBukkitListener;

    @Override
    public void onEnable() {
        thanatos = new Thanatos(this);

        globalConfiguration = new GlobalConfiguration(getDataFolder(), "thanatos");
        globalConfiguration.saveDefaultConfig();
        globalConfiguration.loadConfig();

        slaveConfiguration = new SlaveConfiguration(this);
        slaveConfiguration.saveDefaultConfig();
        slaveConfiguration.loadConfig();

        beatingHeart = new BeatingHeart(this);
        clusterManager = new ClusterManager(this);
        profileManager = new ThanatosProfileManager();
        sendPlayerBukkitListener = new SendPlayerBukkitListener(this);
        new PlayerListener(this);
        getServer().getScheduler().runTaskLater(this, new Runnable() {
            @Override
            public void run() {
                beatingHeart.start();
            }
        }, 100L);
    }

    @Override
    public void onDisable() {
        beatingHeart.kill();
        clusterManager.disable();
        profileManager.disable();
        sendPlayerBukkitListener.disable();
    }

    @Override
    public ThanatosServerType getServerType() {
        return ThanatosServerType.SLAVE;
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
        return getServer().getOnlinePlayers().size();
    }

    @Override
    public double getTPS() {
        return Bukkit.getTPS()[0];
    }

}