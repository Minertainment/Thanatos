package com.minertainment.thanatos.commons.cluster;

import com.minertainment.thanatos.commons.heartbeat.Heartbeat;
import com.minertainment.thanatos.commons.plugin.TPSMeter;
import com.minertainment.thanatos.commons.slave.Slave;

import java.util.HashMap;

public class Cluster {

    private String clusterId;

    private transient HashMap<String, Slave> slaveMap;

    public Cluster() {
        slaveMap = new HashMap<>();
    }

    public Cluster(String clusterId) {
        this.clusterId = clusterId;
        slaveMap = new HashMap<>();
    }

    public String getClusterId() {
        return clusterId;
    }

    public int getPlayerCount() {
        int count = 0;
        for(Slave slave : slaveMap.values()) {
            count += slave.getOnlinePlayers();
        }
        return count;
    }

    public double getAverageTPS() {
        double tps = 0.0;
        for(Slave slave : slaveMap.values()) {
            tps += slave.getTPS();
        }
        return tps / slaveMap.size();
    }

    public Slave getNextSlave() {
        Slave next = null;
        for(Slave slave : slaveMap.values()) {
            if(slave.getOnlinePlayers() < ClusterConfig.getHardPlayerLimit() && slave.getTPS() > ClusterConfig.getHardTPSLimit()) {
                if(next == null || TPSMeter.fromTPS(slave.getTPS()).isHigherThan(TPSMeter.fromTPS(next.getTPS())) || slave.getOnlinePlayers() < next.getOnlinePlayers()) {
                    next = slave;
                }
            }
        }
        if(next == null) {
            next = slaveMap.entrySet().iterator().next().getValue();
        }
        return next;
    }

    public Slave getSlave(String serverId) {
        return slaveMap.get(serverId);
    }

    public Slave registerSlave(Heartbeat heartbeat) {
        return registerSlave(heartbeat.getServerId(), heartbeat.getServerIP(), heartbeat.getServerPort(), heartbeat.getOnlinePlayers(), heartbeat.getTPS());
    }

    public Slave registerSlave(String serverId, String serverIP, int serverPort, int onlinePlayers, double tps) {
        Slave slave = new Slave(serverId, serverIP, serverPort, onlinePlayers, tps);
        slaveMap.put(serverId, slave);
        return slave;
    }

    public HashMap<String, Slave> getSlaves() {
        return slaveMap;
    }

}