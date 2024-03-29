package com.minertainment.thanatos.slave;

import com.minertainment.athena.configuration.serializable.LazyLocation;
import com.minertainment.athena.packets.callback.PacketCallback;
import com.minertainment.thanatos.commons.Thanatos;
import com.minertainment.thanatos.commons.cluster.ClusterManager;
import com.minertainment.thanatos.commons.configuration.SlaveConfiguration;
import com.minertainment.thanatos.commons.configuration.ThanatosConfiguration;
import com.minertainment.thanatos.commons.heartbeat.BeatingHeart;
import com.minertainment.thanatos.commons.packet.playerupdate.ThanatosPlayerUpdateData;
import com.minertainment.thanatos.commons.packet.playerupdate.ThanatosPlayerUpdatePacket;
import com.minertainment.thanatos.commons.plugin.ThanatosServer;
import com.minertainment.thanatos.commons.plugin.ThanatosServerType;
import com.minertainment.thanatos.commons.profile.ThanatosProfile;
import com.minertainment.thanatos.commons.profile.ThanatosProfileManager;
import com.minertainment.thanatos.slave.packet.*;
import com.minertainment.thanatos.slave.player.ThanatosPlayer;
import com.minertainment.thanatos.slave.player.ThanatosPlayerListener;
import com.minertainment.thanatos.slave.something.PlayerListener;
import net.minecraft.server.v1_12_R1.MinecraftServer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;

public class SlaveModule extends JavaPlugin implements ThanatosServer {

    private Thanatos thanatos;

    private ThanatosConfiguration thanatosConfiguration;
    private SlaveConfiguration slaveConfiguration;

    private BeatingHeart beatingHeart;
    private ClusterManager clusterManager;
    private ThanatosProfileManager profileManager;

    private FindPlayerListener findPlayerListener;
    private JoinRequestListener joinRequestListener;
    private ShutdownListener shutdownListener;
    private SendPlayerBukkitListener sendPlayerBukkitListener;
    private KickPacketListener kickPacketListener;
    private SoundPacketListener soundPacketListener;
    private ThanatosPlayerListener thanatosPlayerListener;

    private long lastDisconnect;

    @Override
    public void onEnable() {
        thanatos = new Thanatos(this);

        thanatosConfiguration = new ThanatosConfiguration(this);
        thanatosConfiguration.saveDefaultConfigSync();
        thanatosConfiguration.loadConfigSync();

        slaveConfiguration = new SlaveConfiguration(this);
        slaveConfiguration.saveDefaultConfig();
        slaveConfiguration.loadConfig();

        beatingHeart = new BeatingHeart(this);
        clusterManager = new ClusterManager(this);
        profileManager = new ThanatosProfileManager();

        findPlayerListener = new FindPlayerListener(this);
        joinRequestListener = new JoinRequestListener(this);
        shutdownListener = new ShutdownListener(this);
        sendPlayerBukkitListener = new SendPlayerBukkitListener(this);
        kickPacketListener = new KickPacketListener();
        soundPacketListener = new SoundPacketListener();
        thanatosPlayerListener = new ThanatosPlayerListener();

        lastDisconnect = -1;

        new PlayerListener(this);
        getServer().getScheduler().runTaskLater(this, () -> beatingHeart.start(), 100L);

        new ThanatosPlayerUpdatePacket(new PacketCallback<ThanatosPlayerUpdateData>() {
            @Override
            public void onResponse(ThanatosPlayerUpdateData data) {
                for(Map.Entry<UUID, String> player : data.getPlayers().entrySet()) {
                    ThanatosPlayer.addPlayer(player.getKey(), player.getValue());
                }
            }
        }).send();
    }

    public void setLastDisconnect(long lastDisconnect) {
        this.lastDisconnect = lastDisconnect;
    }

    @Override
    public void onDisable() {
        beatingHeart.kill();
        clusterManager.disable();
        findPlayerListener.disable();
        joinRequestListener.disable();
        shutdownListener.disable();
        sendPlayerBukkitListener.disable();
    }

    public BeatingHeart getBeatingHeart() {
        return beatingHeart;
    }

    @Override
    public ThanatosServerType getServerType() {
        return ThanatosServerType.SLAVE;
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
        if(SlaveConfiguration.getClusterId().equalsIgnoreCase("Jail")) {
            return;
        }

        Player player = getServer().getPlayer(profile.getUniqueId());
        if(player != null && player.isOnline()) {
            profile.setLastLocation(new LazyLocation().setLocation(player.getLocation()));
        }

        profile.setLastSlave(SlaveConfiguration.getServerId());
        profile.setLastCluster(SlaveConfiguration.getClusterId());
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