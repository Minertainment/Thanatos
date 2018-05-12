package com.minertainment.thanatos.proxy.cluster;

import com.minertainment.thanatos.commons.cluster.Cluster;
import com.minertainment.thanatos.commons.cluster.ClusterManager;
import com.minertainment.thanatos.commons.packet.ShutdownPacket;
import com.minertainment.thanatos.commons.slave.Slave;
import com.minertainment.thanatos.proxy.ProxyModule;

import java.util.Iterator;

public class ProxyClusterManager extends ClusterManager {

    private ProxyModule module;

    public ProxyClusterManager(ProxyModule module) {
        super(module);
        this.module = module;
    }

    @Override
    public void refreshServers() {
        super.refreshServers();
        for(Cluster cluster : getClusterMap().values()) {
            Iterator<Slave> slaveIterator = cluster.getSlaves().values().iterator();
            while(slaveIterator.hasNext()) {
                Slave slave = slaveIterator.next();

                // Server reaches maximum idle time.
                if(slave.getOnlinePlayers() == 0 && slave.getLastDisconnect() != -1 && cluster.getSlaves().size() > 1 &&
                        System.currentTimeMillis()-slave.getLastDisconnect() > /*(10*1000)*/module.getClusterManager().getConfig().getShutdownTimer()) {
                    module.getLogger().info("Slave '" + slave.getServerId() + "' has reached max idle time, shutting down...");
                    new ShutdownPacket(slave).send();
                    cluster.shutdown(slave);
                    slaveIterator.remove();
                    continue;
                }
            }
        }
    }

}