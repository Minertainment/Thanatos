package com.minertainment.thanatos.slave.player;

import com.minertainment.athena.configuration.GSONUtils;
import com.minertainment.athena.packets.PacketListener;
import com.minertainment.thanatos.commons.packet.ThanatosPlayerPacket;

public class ThanatosPlayerListener extends PacketListener<ThanatosPlayerPacket> {

    public ThanatosPlayerListener() {
        super("THANATOS_PLAYER");
    }

    @Override
    public ThanatosPlayerPacket parsePacket(String s) {
        return GSONUtils.getGson().fromJson(s, ThanatosPlayerPacket.class);
    }

    @Override
    public void readPacket(ThanatosPlayerPacket thanatosPlayerPacket) {
        if(thanatosPlayerPacket.isAdd()) {
            ThanatosPlayer.addPlayer(thanatosPlayerPacket.getUUID(), thanatosPlayerPacket.getName());
        } else {
            ThanatosPlayer.removePlayer(thanatosPlayerPacket.getUUID());
        }
    }
}
