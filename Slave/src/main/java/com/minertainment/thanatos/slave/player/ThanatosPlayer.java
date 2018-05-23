package com.minertainment.thanatos.slave.player;

import com.minertainment.athena.configuration.serializable.LazyLocation;
import com.minertainment.thanatos.commons.Thanatos;
import com.minertainment.thanatos.commons.configuration.GlobalConfiguration;
import com.minertainment.thanatos.commons.packet.KickPacket;
import com.minertainment.thanatos.commons.packet.SendMessagePacket;
import com.minertainment.thanatos.commons.packet.SendPlayerPacket;
import com.minertainment.thanatos.commons.packet.SoundPacket;
import com.minertainment.thanatos.commons.slave.Slave;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ThanatosPlayer {

    private static List<ThanatosPlayer> onlinePlayers = new ArrayList<>();
    private static HashMap<UUID, ThanatosPlayer> playersByUUID = new HashMap<>();
    private static HashMap<String, ThanatosPlayer> playersByName = new HashMap<>();

    private UUID id;
    private String name;

    public ThanatosPlayer(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public boolean isLocal() {
        return Bukkit.getPlayer(id) != null;
    }

    public UUID getUUID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void kick(String message) {
        if(!isLocal()) {
            new KickPacket(id, message).send();
        } else {
            Bukkit.getPlayer(id).kickPlayer(message);
        }
    }

    public void sendMessage(String message) {
        sendMessage(new TextComponent(message));
    }

    public void sendMessage(BaseComponent... components) {
        new SendMessagePacket(id, components);
    }

    public void playSound(Sound sound) {
        playSound(sound, 1, 1);
    }

    public void playSound(Sound sound, float pitch, float volume) {
        if(!isLocal()) {
            new SoundPacket(id, sound, pitch, volume).send();
        } else {
            Player player = Bukkit.getPlayer(id);
            player.playSound(player.getLocation(), sound, volume, pitch);
        }
    }

    public void teleport(Location location) {
        if(!isLocal()) {
            Slave slave = Thanatos.getClusterManager().getSlave(GlobalConfiguration.getServerId());
            LazyLocation loc = new LazyLocation(location.getWorld().getName(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
            new SendPlayerPacket(id, slave, loc, true).send();
        } else {
            Bukkit.getPlayer(id).teleport(location);
        }
    }

    @Override
    public int hashCode() {
        return id.hashCode() ^ name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof ThanatosPlayer)) {
            return false;
        }

        ThanatosPlayer other = (ThanatosPlayer)obj;
        return other.id.equals(id) && other.name.equalsIgnoreCase(name);
    }

    public static void addPlayer(UUID id, String name) {
        if(!playersByUUID.containsKey(id)) {
            ThanatosPlayer player = new ThanatosPlayer(id, name);
            onlinePlayers.add(player);
            playersByUUID.put(id, player);
            playersByName.put(name.toLowerCase(), player);
        }
    }

    public static void removePlayer(UUID id) {
        ThanatosPlayer player = getPlayer(id);
        if(player != null) {
            removePlayer(getPlayer(id));
        } else System.out.println("Tried removing Thanatos player with id=" + id.toString());
    }

    public static void removePlayer(String name) {
        ThanatosPlayer player = getPlayer(name);
        if(player != null) {
            removePlayer(getPlayer(name));
        } else System.out.println("Tried removing Thanatos player with name=" + name);
    }

    public static void removePlayer(ThanatosPlayer player) {
        if(player == null) {
            System.out.println("Tried removing null Thanatos player");
            return;
        }

        boolean idContains = playersByUUID.containsKey(player.getUUID());
        boolean nameContains = playersByName.containsKey(player.getName());
        if(idContains ^ nameContains) {
            System.out.println("Tried removing Thanatos player with mismatched name and id, id=" + player.getUUID().toString() + " name=" + player.getName());
        }

        if(idContains && nameContains) {
            onlinePlayers.remove(player);
            playersByUUID.remove(player.getUUID());
            playersByName.remove(player.getName());
        }
    }

    public static ThanatosPlayer getPlayer(UUID id) {
        return playersByUUID.get(id);
    }

    public static ThanatosPlayer getPlayer(String name) {
        return playersByName.get(name.toLowerCase());
    }

    public static ThanatosPlayer[] getOnlinePlayers() {
        return onlinePlayers.toArray(new ThanatosPlayer[onlinePlayers.size()]);
    }

}
