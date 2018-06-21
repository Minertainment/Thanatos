package com.minertainment.thanatos.commons.cluster;

import com.minertainment.thanatos.commons.configuration.ThanatosConfiguration;
import com.minertainment.thanatos.commons.heartbeat.Heartbeat;
import com.minertainment.thanatos.commons.heartbeat.packet.HeartbeatPacketListener;
import com.minertainment.thanatos.commons.plugin.ThanatosServer;
import com.minertainment.thanatos.commons.slave.Slave;
import com.minertainment.thanatos.commons.slave.SlaveStatus;

import java.util.HashMap;
import java.util.Iterator;

public class ClusterManager {

    public static final String LOGGER_PREFIX = "[CM] ";

    private ThanatosServer thanatosServer;

    private HashMap<String, Cluster> clusterMap;

    private HeartbeatPacketListener packetListener;

    public ClusterManager(ThanatosServer thanatosServer) {
        this.thanatosServer = thanatosServer;

        clusterMap = new HashMap<>();
        for(Cluster cluster : ThanatosConfiguration.getClusters()) {
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

                if(slave.getStatus() == SlaveStatus.OFFLINE) {
                    slave.setStatus(SlaveStatus.ONLINE);
                    thanatosServer.getLogger().info(LOGGER_PREFIX + "Slave '" + slave.getServerId() + "' registered as online.");
                } else if(slave.getStatus() == SlaveStatus.STARTUP) {
                    slave.setStatus(SlaveStatus.ONLINE);
                    thanatosServer.getLogger().info(LOGGER_PREFIX + "Slave '" + slave.getServerId() + "' has finished starting up.");
                }

                // TODO: Debug options?
                //plugin.getLogger().info(LOGGER_PREFIX + "Updated slave '" + slave.getServerId() + "' - [Players: " + slave.getOnlinePlayers() + ", TPS: " + slave.getTPS() + "]");
            }

            refreshServers();
        });
    }

    public void refreshServers() {
        for(Cluster cluster : clusterMap.values()) {
            Iterator<Slave> slaveIterator = cluster.getSlaves().values().iterator();
            while(slaveIterator.hasNext()) {
                Slave slave = slaveIterator.next();

                if(slave.getStatus() != SlaveStatus.ONLINE) {
                    continue;
                }

                // Server becomes unresponsive.
                if((System.currentTimeMillis()-slave.getLastHeartbeat()) > 2000) {
                    // TODO: Admin broadcast / status command
                    thanatosServer.getLogger().warning(LOGGER_PREFIX + "Slave '" + slave.getServerId() + "' has not sent a heartbeat, disabling...");
                    slave.setStatus(SlaveStatus.OFFLINE);
                    slave.setOnlinePlayers(-1);
                    slave.setTPS(-1);
                    continue;
                }
            }
        }
    }

    public Slave getSlave(String slaveId) {
        for(Cluster cluster : clusterMap.values()) {
            Slave slave;
            if((slave = cluster.getSlave(slaveId)) != null) {
                return slave;
            }
        }
        return null;
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

    public HashMap<String, Cluster> getClusterMap() {
        return clusterMap;
    }

    public void disable() {
        clusterMap.clear();
        packetListener.disable();
    }

}