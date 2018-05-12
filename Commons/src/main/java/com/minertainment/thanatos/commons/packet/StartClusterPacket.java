package com.minertainment.thanatos.commons.packet;

import com.minertainment.athena.packets.AbstractPacket;

public class StartClusterPacket extends AbstractPacket {

    private String clusterId;

    public StartClusterPacket(String clusterId) {
        super("THANATOS_START_CLUSTER");
        this.clusterId = clusterId;
    }

    public String getClusterId() {
        return clusterId;
    }

}