package com.minertainment.thanatos.slave;

import com.minertainment.thanatos.commons.Thanatos;
import com.minertainment.thanatos.commons.cluster.ClusterManager;
import com.minertainment.thanatos.commons.configuration.GlobalConfiguration;
import com.minertainment.thanatos.commons.heartbeat.BeatingHeart;
import com.minertainment.thanatos.commons.plugin.ThanatosServer;
import com.minertainment.thanatos.commons.plugin.ThanatosServerType;
import com.minertainment.thanatos.commons.profile.ThanatosProfileManager;
import com.minertainment.thanatos.slave.packet.JoinRequestListener;
import com.minertainment.thanatos.slave.packet.SendPlayerBukkitListener;
import com.minertainment.thanatos.slave.packet.ShutdownListener;
import com.minertainment.thanatos.slave.something.PlayerListener;
import net.minecraft.server.v1_12_R1.MinecraftServer;
import org.bukkit.plugin.java.JavaPlugin;

public class SlaveModule extends JavaPlugin implements ThanatosServer {

    private Thanatos thanatos;

    private GlobalConfiguration globalConfiguration;

    private BeatingHeart beatingHeart;
    private ClusterManager clusterManager;
    private ThanatosProfileManager profileManager;

    private JoinRequestListener joinRequestListener;
    private ShutdownListener shutdownListener;
    private SendPlayerBukkitListener sendPlayerBukkitListener;

    private long lastDisconnect;

    @Override
    public void onEnable() {
        thanatos = new Thanatos(this);

        globalConfiguration = new GlobalConfiguration(getDataFolder(), "thanatos");
        globalConfiguration.saveDefaultConfig();
        globalConfiguration.loadConfig();

        beatingHeart = new BeatingHeart(this);
        clusterManager = new ClusterManager(this);
        profileManager = new ThanatosProfileManager();

        joinRequestListener = new JoinRequestListener(this);
        shutdownListener = new ShutdownListener(this);
        sendPlayerBukkitListener = new SendPlayerBukkitListener(this);

        lastDisconnect = -1;

        new PlayerListener(this);
        getServer().getScheduler().runTaskLater(this, new Runnable() {
            @Override
            public void run() {
                beatingHeart.start();
            }
        }, 100L);
    }

    public void setLastDisconnect(long lastDisconnect) {
        this.lastDisconnect = lastDisconnect;
    }

    @Override
    public void onDisable() {
        beatingHeart.kill();
        clusterManager.disable();
        profileManager.disable();
        joinRequestListener.disable();
        shutdownListener.disable();
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
    public int getOnlinePlayers() {
        return getServer().getOnlinePlayers().size();
    }

    @Override
    public double getTPS() {
        return MinecraftServer.getServer().recentTps[0];
        //return Bukkit.getTPS()[0];
    }

    @Override
    public long getLastDisconnect() {
        return lastDisconnect;
    }

}