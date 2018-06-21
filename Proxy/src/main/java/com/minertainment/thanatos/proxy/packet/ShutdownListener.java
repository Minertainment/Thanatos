package com.minertainment.thanatos.proxy.packet;

import com.minertainment.athena.configuration.GSONUtils;
import com.minertainment.athena.packets.PacketListener;
import com.minertainment.thanatos.commons.packet.ShutdownPacket;
import com.minertainment.thanatos.proxy.ProxyModule;

public class ShutdownListener extends PacketListener<ShutdownPacket> {

    private ProxyModule module;

    public ShutdownListener(ProxyModule module) {
        super("THANATOS_SHUTDOWN_SLAVE");
        this.module = module;
    }

    @Override
    public ShutdownPacket parsePacket(String json) {
        return GSONUtils.getGson().fromJson(json, ShutdownPacket.class);
    }

    @Override
    public void readPacket(ShutdownPacket packet) {
        module.getClusterManager().getClusterFromSlave(packet.getSlave().getServerId()).shutdown(packet.getSlave().getServerId());
        module.getLogger().info("Slave '" + packet.getSlave().getServerId() + "' is shutting down.");
    }
}