package com.minertainment.thanatos.slave.packet;

import com.minertainment.athena.configuration.GSONUtils;
import com.minertainment.athena.packets.PacketListener;
import com.minertainment.thanatos.commons.packet.ShutdownPacket;
import com.minertainment.thanatos.slave.SlaveModule;

public class ShutdownListener extends PacketListener<ShutdownPacket> {

    private SlaveModule slave;

    public ShutdownListener(SlaveModule slave) {
        super("THANATOS_SHUTDOWN_SERVER");
        this.slave = slave;
    }

    @Override
    public ShutdownPacket parsePacket(String json) {
        return GSONUtils.getGson().fromJson(json, ShutdownPacket.class);
    }

    @Override
    public void readPacket(ShutdownPacket shutdownPacket) {
        if(!slave.getGlobalConfiguration().getServerId().equals(shutdownPacket.getSlave().getServerId())) {
            return;
        }

        // TODO: Log
        slave.getLogger().info("Received shutdown request.");
        slave.getServer().shutdown();
    }

}