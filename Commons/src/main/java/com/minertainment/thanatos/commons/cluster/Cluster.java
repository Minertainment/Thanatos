package com.minertainment.thanatos.commons.cluster;

import com.minertainment.athena.packets.PacketManager;
import com.minertainment.athena.tasks.AsyncTask;
import com.minertainment.thanatos.commons.heartbeat.Heartbeat;
import com.minertainment.thanatos.commons.packet.joinrequest.JoinRequestCallback;
import com.minertainment.thanatos.commons.packet.joinrequest.JoinRequestData;
import com.minertainment.thanatos.commons.packet.joinrequest.JoinRequestPacket;
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

    public void getNextSlave(SlaveCallback callback) {
        new AsyncTask(() -> {
            Slave next = null;
            for(Slave slave : slaveMap.values()) {
                JoinRequestData data = getData(slave);
                if(next == null || TPSMeter.fromTPS(data.getTPS()).isHigherThan(
                        TPSMeter.fromTPS(next.getTPS())) || data.getPlayerCount() < next.getOnlinePlayers()) {
                    next = slave;
                    next.setOnlinePlayers(data.getPlayerCount());
                    next.setTPS(data.getTPS());
                }
            }
            callback.onCallback(next);
        }).run();
    }

    private JoinRequestData getData(Slave slave) {
        JoinRequestCallback callback = new JoinRequestCallback();
        JoinRequestPacket packet = new JoinRequestPacket(slave, callback);
        PacketManager.registerCallback(packet);
        packet.send();
        while(true) {
            if(callback.getData() != null && callback.getData().getServerId() != null) {
                break;
            }
            try {
                Thread.sleep(10);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
        return callback.getData();
    }

    public Slave getNextSlaveCached() {
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
        return registerSlave(heartbeat.getServerId(), heartbeat.getOnlinePlayers(), heartbeat.getTPS());
    }

    public Slave registerSlave(String serverId, int onlinePlayers, double tps) {
        Slave slave = new Slave(serverId, onlinePlayers, tps);
        slaveMap.put(serverId, slave);
        return slave;
    }

    public HashMap<String, Slave> getSlaves() {
        return slaveMap;
    }

}

