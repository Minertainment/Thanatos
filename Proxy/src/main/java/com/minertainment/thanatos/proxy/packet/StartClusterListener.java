package com.minertainment.thanatos.proxy.packet;

import com.minertainment.athena.configuration.GSONUtils;
import com.minertainment.athena.packets.PacketListener;
import com.minertainment.thanatos.commons.cluster.ClusterManager;
import com.minertainment.thanatos.commons.packet.StartClusterPacket;
import com.minertainment.thanatos.proxy.ProxyModule;

public class StartClusterListener extends PacketListener<StartClusterPacket> {

    private ProxyModule module;

    public StartClusterListener(ProxyModule module) {
        super("THANATOS_START_CLUSTER");
        this.module = module;
    }

    @Override
    public StartClusterPacket parsePacket(String json) {
        return GSONUtils.getGson().fromJson(json, StartClusterPacket.class);
    }

    @Override
    public void readPacket(StartClusterPacket packet) {
        module.getLogger().info(ClusterManager.LOGGER_PREFIX + " Attempting to start new slave for cluster: " + packet.getClusterId() + ".");
        module.getClusterManager().getCluster(packet.getClusterId()).startSlave();
    }

}