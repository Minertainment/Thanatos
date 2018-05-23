package com.minertainment.thanatos.commons.packet;

import com.minertainment.athena.packets.AbstractPacket;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.UUID;

public class SendMessagePacket extends AbstractPacket {

    private BaseComponent[] message;
    private UUID recepient;

    public SendMessagePacket(UUID recepient, String message) {
        this(recepient, new TextComponent(message));
    }

    public SendMessagePacket(UUID recepient, BaseComponent... message) {
        super("THANATOS_MESSAGE_PACKET");
        this.recepient = recepient;
        this.message = message;
    }

    public UUID getRecepient() {
        return recepient;
    }

    public BaseComponent[] getMessage() {
        return message;
    }

}
