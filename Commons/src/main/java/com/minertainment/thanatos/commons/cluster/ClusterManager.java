package com.minertainment.thanatos.commons.cluster;

import com.minertainment.thanatos.commons.heartbeat.Heartbeat;
import com.minertainment.thanatos.commons.heartbeat.packet.HeartbeatPacketListener;
import com.minertainment.thanatos.commons.plugin.ThanatosServer;
import com.minertainment.thanatos.commons.slave.Slave;
import java.util.HashMap;
import java.util.Map;

public class ClusterManager {

    private final String LOGGER_PREFIX = "[CM] ";

    private ClusterConfig clusterConfig;
    private HashMap<String, Cluster> clusterMap;

    private HeartbeatPacketListener packetListener;

    public ClusterManager(ThanatosServer server) {
        clusterConfig = new ClusterConfig(server);
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
                server.getLogger().warning(LOGGER_PREFIX + "Slave '" + heartbeat.getServerId() + "' attempted to join invalid cluster '" + heartbeat.getClusterId() + "'.");
                //cluster = registerCluster(heartbeat.getClusterId());
                return;
            }

            Slave slave;
            if((slave = cluster.getSlave(heartbeat.getServerId())) == null) {
                slave = cluster.registerSlave(heartbeat);
                server.getLogger().info(LOGGER_PREFIX + "Registered slave '" + slave.getServerId() + "' - [IP: " + slave.getServerIP() + ", Port: " + slave.getServerPort() + "]");
            } else {
                slave.setOnlinePlayers(heartbeat.getOnlinePlayers());
                slave.setTPS(heartbeat.getTPS());

                // TODO: Debug options?
                //plugin.getLogger().info(LOGGER_PREFIX + "Updated slave '" + slave.getServerId() + "' - [Players: " + slave.getOnlinePlayers() + ", TPS: " + slave.getTPS() + "]");
            }
        });
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