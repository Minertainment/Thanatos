package com.minertainment.thanatos.commons.cluster;

import com.minertainment.athena.misc.PostData;
import com.minertainment.athena.packets.callback.PacketCallback;
import com.minertainment.athena.tasks.AsyncTask;
import com.minertainment.thanatos.commons.Thanatos;
import com.minertainment.thanatos.commons.configuration.ServerConfiguration;
import com.minertainment.thanatos.commons.configuration.ThanatosConfiguration;
import com.minertainment.thanatos.commons.heartbeat.Heartbeat;
import com.minertainment.thanatos.commons.packet.joinrequest.JoinRequestData;
import com.minertainment.thanatos.commons.packet.joinrequest.JoinRequestPacket;
import com.minertainment.thanatos.commons.plugin.TPSMeter;
import com.minertainment.thanatos.commons.slave.Slave;
import com.minertainment.thanatos.commons.slave.SlaveStatus;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
                    ThanatosConfiguration.getHardPlayerLimit() && slave.getTPS() > ThanatosConfiguration.getHardTPSLimit()) {
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
        return registerSlave(serverId, SlaveStatus.ONLINE, onlinePlayers, tps);
    }

    public Slave registerSlave(String serverId, SlaveStatus slaveStatus, int onlinePlayers, double tps) {
        Slave slave = new Slave(serverId, slaveStatus, onlinePlayers, tps);
        slaveMap.put(serverId, slave);
        return slave;
    }

    public HashMap<String, Slave> getSlaves() {
        return slaveMap;
    }

    public void startSlave() {
        Iterator<Slave> slaveIterator = slaveMap.values().iterator();
        Slave next = null;
        while(slaveIterator.hasNext()) {
            Slave slave = slaveIterator.next();
            if (slave.getStatus() == SlaveStatus.STARTUP) {
                return;
            } else if(slave.getStatus() != SlaveStatus.OFFLINE) {
                continue;
            }

            if(next == null || next.getServerNum() > slave.getServerNum()) {
                next = slave;
            }
        }
        start(next);
    }

    public void start(Slave slave) {
        if(slave == null) {
            return;
        }

        ServerConfiguration.Server server = ServerConfiguration.getServer(slave);
        if(server == null) {
            // TODO: Throw exception or something?
            return;
        }

        Thanatos.getServer().getLogger().info("Attempting to start slave '" + slave.getServerId() + "' from cluster '" + getClusterId() + "'.");
        slave.setStatus(SlaveStatus.STARTUP);
        JSONObject data = new JSONObject();
        data.put("server", slave.getServerId().toLowerCase());
        data.put("cluster", getClusterId().toLowerCase());
        new PostData("http://" + server.getAddress() + "/api/start", data, res -> {
            if(!(boolean) res.get("success")) {
                // TODO: Throw exception or something?
                Thanatos.getServer().getLogger().severe("Could not start slave '" + slave.getServerId() +
                        "' on '" + server.getAddress() + "' due to: " + res.get("msg"));
                return;
            }
            Thanatos.getServer().getLogger().info("Started slave '" + slave.getServerId() + "' on server '" + server.getAddress() + "' successfully.");
        }).run();
    }

    public void shutdown(String serverId) {
        shutdown(getSlave(serverId));
    }

    public void shutdown(Slave slave) {
        slave.setOnlinePlayers(0);
        slave.setTPS(20.0);
        slave.setLastDisconnect(-1);
        slave.setStatus(SlaveStatus.OFFLINE);
    }

}