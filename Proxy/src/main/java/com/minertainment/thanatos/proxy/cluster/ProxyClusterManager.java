package com.minertainment.thanatos.proxy.cluster;

import com.minertainment.athena.misc.SimpleCallback;
import com.minertainment.athena.packets.callback.PacketCallback;
import com.minertainment.thanatos.commons.cluster.Cluster;
import com.minertainment.thanatos.commons.cluster.ClusterManager;
import com.minertainment.thanatos.commons.configuration.ThanatosConfiguration;
import com.minertainment.thanatos.commons.packet.ShutdownPacket;
import com.minertainment.thanatos.commons.packet.findplayer.FindPlayerData;
import com.minertainment.thanatos.commons.packet.findplayer.FindPlayerPacket;
import com.minertainment.thanatos.commons.packet.sendplayer.SendPlayerData;
import com.minertainment.thanatos.commons.packet.sendplayer.SendPlayerPacket;
import com.minertainment.thanatos.commons.slave.Slave;
import com.minertainment.thanatos.commons.slave.SlaveStatus;
import com.minertainment.thanatos.proxy.ProxyModule;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ProxyClusterManager extends ClusterManager {

    private ProxyModule module;
    private ProxyClusterConfig proxyClusterConfig;

    public ProxyClusterManager(ProxyModule module) {
        super(module);
        this.module = module;

        proxyClusterConfig = new ProxyClusterConfig();
        proxyClusterConfig.loadConfigSync();
        for(ClusterReg clusterReg : proxyClusterConfig.getClusterMap().values()) {
            for(SlaveReg slaveReg : clusterReg.getSlaveMap().values()) {
                getCluster(clusterReg.getClusterId()).registerSlave(slaveReg.getServerId(), SlaveStatus.OFFLINE, -1, -1);
            }
        }
    }

    @Override
    public void refreshServers() {
        super.refreshServers();
        for(Cluster cluster : getClusterMap().values()) {
            Iterator<Slave> slaveIterator = cluster.getSlaves().values().iterator();
            while(slaveIterator.hasNext()) {
                Slave slave = slaveIterator.next();

                if(slave.getStatus() != SlaveStatus.ONLINE) {
                    continue;
                }

                // Server reaches maximum idle time.
                if(slave.getOnlinePlayers() == 0 && slave.getLastDisconnect() != -1 && !slave.getServerId().endsWith("01") &&
                        System.currentTimeMillis()-slave.getLastDisconnect() > (60*1000) /*module.getClusterManager().getConfig().getShutdownTimer()*/) {
                    module.getLogger().info("Slave '" + slave.getServerId() + "' has reached max idle time, shutting down...");
                    new ShutdownPacket(slave, false).send();
                    continue;
                }

                // Merge servers
                if(slave.getOnlinePlayers() > 0 && slave.getServerNum() != 1 && slave.getOnlinePlayers() < ThanatosConfiguration.getSoftPlayerLimit()) {
                    Slave merge = findMerge(cluster, slave);
                    if(merge == null) {
                        continue;
                    }

                    module.getLogger().info("Merging '" + slave.getServerId() + "' with '" + merge.getServerId() + "...");
                    slave.setStatus(SlaveStatus.MERGING);
                    send(merge, new ArrayList<>(Arrays.asList(new Slave[]{slave})), () -> {
                        module.getLogger().info("Players on '" + slave.getServerId() + "' merged to '" + merge.getServerId() + "' successfully.");
                        new ShutdownPacket(slave, false).send();
                    });
                }
            }
        }
    }

    public Slave findMerge(Cluster cluster, Slave slave) {
        Slave next = null;
        Iterator<Slave> slaveIterator = cluster.getSlaves().values().iterator();
        while(slaveIterator.hasNext()) {
            Slave it = slaveIterator.next();
            if(it.getStatus() != SlaveStatus.ONLINE || it.getServerId().equals(slave.getServerId()) ||
                    it.getOnlinePlayers() <= 0 || it.getOnlinePlayers() + slave.getOnlinePlayers() >= ThanatosConfiguration.getSoftPlayerLimit()) {
                continue;
            }

            if(next == null || next.getServerNum() < it.getServerNum()) {
                next = it;
            }
        }
        return next;
    }

    public void send(Slave to, List<Slave> from, SimpleCallback callback) {
        if(from.size() > 0) {
            Slave slave = from.get(0);
            from.remove(slave);
            if(slave.getOnlinePlayers() > 0) {
                ServerInfo fromServer = module.getProxy().getServerInfo(slave.getServerId());
                if(fromServer != null) {
                    sendPlayers(to, new ArrayList<>(Arrays.asList(fromServer.getPlayers()
                            .toArray(new ProxiedPlayer[fromServer.getPlayers().size()]))), () -> send(to, from, callback));
                }
            }
        } else {
            callback.onCallback();
        }
    }

    public void sendPlayers(Slave to, List<ProxiedPlayer> players, SimpleCallback callback) {
        if(players.size() > 0) {
            ProxiedPlayer player = players.get(0);
            players.remove(player);
            new FindPlayerPacket(player.getUniqueId(), new PacketCallback<FindPlayerData>() {
                @Override
                public void onResponse(FindPlayerData data) {
                    new SendPlayerPacket(player.getUniqueId(), to, data.getLocation(), null, true, new PacketCallback<SendPlayerData>() {
                        @Override
                        public void onResponse(SendPlayerData sendPlayerData) {
                            sendPlayers(to, players, callback);
                        }
                    }).send();
                }
            }).send();
        } else {
            callback.onCallback();
        }
    }

    @Override
    public void disable() {
        for(Cluster cluster : getClusterMap().values()) {
            if(!proxyClusterConfig.isClusterRegistered(cluster.getClusterId())) {
                proxyClusterConfig.registerCluster(cluster.getClusterId());
            }

            for(Slave slave : cluster.getSlaves().values()) {
                if(!proxyClusterConfig.getCluster(cluster.getClusterId()).isSlaveRegistered(slave.getServerId())) {
                    proxyClusterConfig.registerSlave(cluster.getClusterId(), slave.getServerId());
                }
            }
        }
        proxyClusterConfig.saveConfigSync();
        super.disable();
    }

}