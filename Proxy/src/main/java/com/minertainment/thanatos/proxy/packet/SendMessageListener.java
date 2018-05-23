package com.minertainment.thanatos.proxy.packet;

import com.minertainment.athena.configuration.GSONUtils;
import com.minertainment.athena.packets.PacketListener;
import com.minertainment.thanatos.commons.packet.SendMessagePacket;
import com.minertainment.thanatos.proxy.ProxyModule;

public class SendMessageListener extends PacketListener<SendMessagePacket> {

    private ProxyModule module;

    public SendMessageListener(ProxyModule module) {
        super("THANATOS_MESSAGE_PACKET");
        this.module = module;
    }

    @Override
    public SendMessagePacket parsePacket(String s) {
        return GSONUtils.getGson().fromJson(s, SendMessagePacket.class);
    }

    @Override
    public void readPacket(SendMessagePacket sendMessagePacket) {
        module.getProxy().getPlayer(sendMessagePacket.getRecepient()).sendMessage(sendMessagePacket.getMessage());
    }
}
