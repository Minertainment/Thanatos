package com.minertainment.thanatos.commons.packet.playerupdate;

import com.minertainment.athena.packets.callback.AbstractCallbackData;

import java.util.Map;
import java.util.UUID;

public class ThanatosPlayerUpdateData extends AbstractCallbackData {

    private Map<UUID, String> players;

    public ThanatosPlayerUpdateData(Map<UUID, String> players) {
        this.players = players;
    }

    public Map<UUID, String> getPlayers() {
        return players;
    }
}
