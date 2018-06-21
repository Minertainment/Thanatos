package com.minertainment.thanatos.proxy.cluster;

import com.minertainment.thanatos.commons.cluster.Cluster;
import com.minertainment.thanatos.commons.cluster.ClusterManager;
import com.minertainment.thanatos.commons.packet.ShutdownPacket;
import com.minertainment.thanatos.commons.slave.Slave;
import com.minertainment.thanatos.commons.slave.SlaveStatus;
import com.minertainment.thanatos.proxy.ProxyModule;

import java.util.Iterator;

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

                // Server reaches maximum idle time.
                if(slave.getOnlinePlayers() == 0 && slave.getLastDisconnect() != -1 && !slave.getServerId().endsWith("01") &&
                        System.currentTimeMillis()-slave.getLastDisconnect() > (60*1000) /*module.getClusterManager().getConfig().getShutdownTimer()*/) {
                    module.getLogger().info("Slave '" + slave.getServerId() + "' has reached max idle time, shutting down...");
                    new ShutdownPacket(slave, false).send();
                    continue;
                }
            }
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