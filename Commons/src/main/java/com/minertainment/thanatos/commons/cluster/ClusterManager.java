package com.minertainment.thanatos.commons.cluster;

import com.minertainment.thanatos.commons.heartbeat.Heartbeat;
import com.minertainment.thanatos.commons.heartbeat.packet.HeartbeatPacketListener;
import com.minertainment.thanatos.commons.packet.ShutdownPacket;
import com.minertainment.thanatos.commons.plugin.ThanatosServer;
import com.minertainment.thanatos.commons.slave.Slave;

import java.util.HashMap;
import java.util.Iterator;

public class ClusterManager {

    private final String LOGGER_PREFIX = "[CM] ";

    private ThanatosServer thanatosServer;

    private ClusterConfig clusterConfig;
    private HashMap<String, Cluster> clusterMap;

    private HeartbeatPacketListener packetListener;

    public ClusterManager(ThanatosServer thanatosServer) {
        this.thanatosServer = thanatosServer;
        clusterConfig = new ClusterConfig(thanatosServer);
        clusterConfig.saveDefaultConfig();
        clusterConfig.loadConfig();

        clusterMap = new HashMap<>();
        for(Cluster cluster : clusterConfig.getClusters()) {
            clusterMap.put(cluster.getClusterId(), cluster);
        }

        packetListener = new HeartbeatPacketListener(packet -> {
            Heartbeat heartbeat = packet.getHeartbeat();

            Cluster cluster;
            if((cluster = getCluster(heartbeat.getClusterId())) == null) {
                // TODO: throw exception
                thanatosServer.getLogger().warning(LOGGER_PREFIX + "Slave '" + heartbeat.getServerId() + "' attempted to join invalid cluster '" + heartbeat.getClusterId() + "'.");
                return;
            }

            Slave slave;
            if((slave = cluster.getSlave(heartbeat.getServerId())) == null) {
                slave = cluster.registerSlave(heartbeat);
                thanatosServer.getLogger().info(LOGGER_PREFIX + "Registered slave '" + slave.getServerId() + "'");
            } else {
                slave.setOnlinePlayers(heartbeat.getOnlinePlayers());
                slave.setTPS(heartbeat.getTPS());
                slave.setLastDisconnect(heartbeat.getLastDisconnect());
                slave.heartbeat();

                // TODO: Debug options?
                //plugin.getLogger().info(LOGGER_PREFIX + "Updated slave '" + slave.getServerId() + "' - [Players: " + slave.getOnlinePlayers() + ", TPS: " + slave.getTPS() + "]");
            }

            refreshServers();
        });
    }

    private void refreshServers() {
        for(Cluster cluster : clusterMap.values()) {
            Iterator<Slave> slaveIterator = cluster.getSlaves().values().iterator();
            while(slaveIterator.hasNext()) {
                Slave slave = slaveIterator.next();

                // Server becomes unresponsive.
                if((System.currentTimeMillis()-slave.getLastHeartbeat()) > 15000) {
                    // TODO: Admin broadcast / status command
                    thanatosServer.getLogger().warning(LOGGER_PREFIX + "Slave '" + slave.getServerId() + "' has not sent a heartbeat for 15 seconds, disabling...");
                    slaveIterator.remove();
                    continue;
                }

                // Server reaches maximum idle time.
                if(slave.getOnlinePlayers() == 0 && slave.getLastDisconnect() != -1 && cluster.getSlaves().size() > 1 &&
                        System.currentTimeMillis()-slave.getLastDisconnect() > clusterConfig.getShutdownTimer()) {
                    thanatosServer.getLogger().info("Slave '" + slave.getServerId() + "' has reached max idle time, shutting down...");
                    new ShutdownPacket(slave).send();
                    slaveIterator.remove();
                    continue;
                }
            }
        }
    }

    public Slave getSlave(String clusterId, String serverId) {
        Cluster cluster;
        if((cluster = getCluster(clusterId)) != null) {
            return cluster.getSlave(serverId);
        }
        return null;
    }

    public Cluster getCluster(String clusterId) {
        return clusterMap.get(clusterId);
    }

    public Cluster getClusterFromSlave(String slaveId) {
        for(Cluster cluster : clusterMap.values()) {
            if(cluster.getSlave(slaveId) != null) {
                return cluster;
            }
        }
        return null;
    }

    public Cluster registerCluster(String clusterId) {
        Cluster cluster = new Cluster(clusterId);
        clusterMap.put(clusterId, cluster);
        return cluster;
    }

    public void disable() {
        clusterMap.clear();
        packetListener.disable();
    }

}