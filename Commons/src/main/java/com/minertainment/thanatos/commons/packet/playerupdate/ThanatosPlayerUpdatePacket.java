package com.minertainment.thanatos.commons.packet.playerupdate;

import com.minertainment.athena.packets.callback.AbstractCallbackPacket;
import com.minertainment.athena.packets.callback.PacketCallback;

public class ThanatosPlayerUpdatePacket extends AbstractCallbackPacket<ThanatosPlayerUpdateData> {

    public ThanatosPlayerUpdatePacket(PacketCallback<ThanatosPlayerUpdateData> callback) {
        super("THANATOS_PLAYER_UPDATE", callback);
    }


}
