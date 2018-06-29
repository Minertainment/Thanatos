package com.minertainment.thanatos.commons.packet.sendplayer;

import com.minertainment.athena.packets.callback.AbstractCallbackData;

public class SendPlayerData extends AbstractCallbackData {

    private boolean sent;

    public SendPlayerData(boolean sent) {
        this.sent = sent;
    }

    public boolean isSent() {
        return sent;
    }

}