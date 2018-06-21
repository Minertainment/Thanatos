package com.minertainment.thanatos.proxy.cluster;

import com.minertainment.athena.configuration.MongoConfig;

import java.util.HashMap;

public class ProxyClusterConfig extends MongoConfig {

    private HashMap<String, ClusterReg> clusterMap;

    public ProxyClusterConfig() {
        super("ClusterRegistration");

        clusterMap = new HashMap<>();
    }

    public void registerCluster(String clusterId) {
        clusterMap.put(clusterId, new ClusterReg(clusterId));
    }

    public void registerSlave(String clusterId, String slaveId) {
        getCluster(clusterId).registerSlave(slaveId);
    }

    public ClusterReg getCluster(String clusterId) {
        return clusterMap.get(clusterId);
    }

    public boolean isClusterRegistered(String clusterId) {
        return clusterMap.containsKey(clusterId);
    }

    public HashMap<String, ClusterReg> getClusterMap() {
        return clusterMap;
    }
}

class ClusterReg {

    private String clusterId;
    private HashMap<String, SlaveReg> slaveMap;

    public ClusterReg(String clusterId) {
        this.clusterId = clusterId;
        slaveMap = new HashMap<>();
    }

    public String getClusterId() {
        return clusterId;
    }

    public void registerSlave(String slaveId) {
        slaveMap.put(slaveId, new SlaveReg(slaveId));
    }

    public boolean isSlaveRegistered(String slaveId) {
        return slaveMap.containsKey(slaveId);
    }

    public HashMap<String, SlaveReg> getSlaveMap() {
        return slaveMap;
    }

}

class SlaveReg {

    private String serverId;

    public SlaveReg(String serverId) {
        this.serverId = serverId;
    }

    public String getServerId() {
        return serverId;
    }

}