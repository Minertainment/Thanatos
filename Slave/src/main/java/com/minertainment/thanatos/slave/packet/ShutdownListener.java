package com.minertainment.thanatos.slave.packet;

import com.minertainment.athena.configuration.GSONUtils;
import com.minertainment.athena.packets.PacketListener;
import com.minertainment.thanatos.commons.packet.ShutdownPacket;
import com.minertainment.thanatos.slave.SlaveModule;

public class ShutdownListener extends PacketListener<ShutdownPacket> {

    private SlaveModule module;

    public ShutdownListener(SlaveModule module) {
        super("THANATOS_SHUTDOWN_SLAVE");
        this.module = module;
    }

    @Override
    public ShutdownPacket parsePacket(String json) {
        return GSONUtils.getGson().fromJson(json, ShutdownPacket.class);
    }

    @Override
    public void readPacket(ShutdownPacket packet) {
        if(!module.getGlobalConfiguration().getServerId().equals(packet.getSlave().getServerId())) {
            module.getClusterManager().getClusterFromSlave(packet.getSlave().getServerId()).shutdown(packet.getSlave().getServerId());
            module.getLogger().info("Slave '" + packet.getSlave().getServerId() + "' has been shutdown for reaching maximum idle time.");
            return;
        }

        // TODO: Log
        module.getLogger().info("Received shutdown request.");
        module.getServer().shutdown();
    }

}