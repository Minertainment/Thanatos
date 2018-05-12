package com.minertainment.thanatos.slave.packet;

import com.minertainment.athena.configuration.GSONUtils;
import com.minertainment.athena.packets.PacketListener;
import com.minertainment.thanatos.commons.packet.joinrequest.JoinRequestData;
import com.minertainment.thanatos.commons.packet.joinrequest.JoinRequestPacket;
import com.minertainment.thanatos.slave.SlaveModule;

public class JoinRequestListener extends PacketListener<JoinRequestPacket> {

    private SlaveModule module;

    public JoinRequestListener(SlaveModule module) {
        super("THANATOS_JOIN_REQUEST");
        this.module = module;
    }

    @Override
    public JoinRequestPacket parsePacket(String json) {
        return GSONUtils.getGson().fromJson(json, JoinRequestPacket.class);
    }

    @Override
    public void readPacket(JoinRequestPacket packet) {
        if(!packet.getSlave().getServerId().equals(module.getGlobalConfiguration().getServerId())) {
            return;
        }

        packet.setCallbackData(new JoinRequestData(module.getGlobalConfiguration().getServerId(), module.getOnlinePlayers(), module.getTPS()));
        packet.respond();
    }

}