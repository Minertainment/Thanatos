package com.minertainment.thanatos.commons.cluster;

import com.minertainment.athena.packets.callback.PacketCallback;
import com.minertainment.athena.tasks.AsyncTask;
import com.minertainment.thanatos.commons.Thanatos;
import com.minertainment.thanatos.commons.configuration.GlobalConfiguration;
import com.minertainment.thanatos.commons.heartbeat.Heartbeat;
import com.minertainment.thanatos.commons.packet.joinrequest.JoinRequestData;
import com.minertainment.thanatos.commons.packet.joinrequest.JoinRequestPacket;
import com.minertainment.thanatos.commons.plugin.TPSMeter;
import com.minertainment.thanatos.commons.slave.Slave;
import com.minertainment.thanatos.commons.slave.SlaveStatus;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Cluster {

    private String clusterId;

    private transient HashMap<String, Slave> slaveMap;
    private transient HashMap<String, Slave> offlineSlaveMap;

    public Cluster() {
        slaveMap = new HashMap<>();
        offlineSlaveMap = new HashMap<>();
    }

    public Cluster(String clusterId) {
        this.clusterId = clusterId;
        slaveMap = new HashMap<>();
        offlineSlaveMap = new HashMap<>();
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

    public void getNextSlave(SlaveCallback callback) {
        new AsyncTask(() -> {
            HashMap<Slave, JoinRequestData> joinMap = new HashMap<>();
            for(Slave slave : slaveMap.values()) {
                        new JoinRequestPacket(slave, new PacketCallback<JoinRequestData>() {
                    @Override
                    public void onResponse(JoinRequestData joinRequestData) {
                        joinMap.put(slave, joinRequestData);
                    }
                }).send();
            }

            try {
                Thread.sleep(500);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }

            Slave next = null;
            for(Map.Entry<Slave, JoinRequestData> entry : joinMap.entrySet()) {
                if(entry.getKey().getStatus() == SlaveStatus.STARTUP || entry.getValue() == null) {
                    continue;
                }

                if(next == null || TPSMeter.fromTPS(entry.getValue().getTPS()).isHigherThan(
                        TPSMeter.fromTPS(next.getTPS())) || entry.getValue().getPlayerCount() < next.getOnlinePlayers()) {
                    next = entry.getKey();
                    next.setOnlinePlayers(entry.getValue().getPlayerCount());
                    next.setTPS(entry.getValue().getTPS());
                }
            }
            callback.onCallback(next);
        }).run();
    }

    public Slave getNextSlaveCached() {
        Slave next = null;
        for(Slave slave : slaveMap.values()) {
            if(slave.getStatus() == SlaveStatus.ONLINE && slave.getOnlinePlayers() <
                    GlobalConfiguration.getHardPlayerLimit() && slave.getTPS() > GlobalConfiguration.getHardTPSLimit()) {
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
        return registerSlave(heartbeat.getServerId(), heartbeat.getOnlinePlayers(), heartbeat.getTPS());
    }

    public Slave registerSlave(String serverId, int onlinePlayers, double tps) {
        Slave slave = new Slave(serverId, SlaveStatus.ONLINE, onlinePlayers, tps);
        slaveMap.put(serverId, slave);
        return slave;
    }

    public HashMap<String, Slave> getSlaves() {
        return slaveMap;
    }

    public void startSlave() {
        Iterator<Slave> slaveIterator = offlineSlaveMap.values().iterator();
        Slave next = null;
        while(slaveIterator.hasNext()) {
            Slave slave = slaveIterator.next();
            if (slave.getStatus() == SlaveStatus.STARTUP) {
                return;
            } else if(next == null && slave.getStatus() == SlaveStatus.OFFLINE) {
                next = slave;
            }
        }
        start(next);
    }

    public void start(Slave slave) {
        if(slave == null) {
            return;
        }
        
        Thanatos.getServer().getLogger().info("Starting slave '" + slave.getServerId() + "' from cluster '" + getClusterId() + "'.");
        slave.setStatus(SlaveStatus.STARTUP);
        slaveMap.put(slave.getServerId(), slave);
        offlineSlaveMap.remove(slave.getServerId());

        try {
            Runtime.getRuntime().exec(ClusterConfig.getDirectory() + "\\" + getClusterId()
                    .toLowerCase() + "cluster" + "\\" + slave.getServerId() + "\\" + "launch.bat");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shutdown(String serverId) {
        shutdown(getSlave(serverId));
        slaveMap.remove(serverId);
    }

    public void shutdown(Slave slave) {
        slave.setOnlinePlayers(0);
        slave.setTPS(20.0);
        slave.setLastDisconnect(-1);
        slave.setStatus(SlaveStatus.OFFLINE);
        offlineSlaveMap.put(slave.getServerId(), slave);
    }

}