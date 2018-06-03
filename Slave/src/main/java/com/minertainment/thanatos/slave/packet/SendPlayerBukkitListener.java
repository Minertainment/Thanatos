package com.minertainment.thanatos.slave.packet;

import com.minertainment.athena.configuration.GSONUtils;
import com.minertainment.athena.configuration.serializable.LazyLocation;
import com.minertainment.athena.packets.PacketListener;
import com.minertainment.athena.tasks.DelayedTask;
import com.minertainment.thanatos.commons.packet.SendPlayerPacket;
import com.minertainment.thanatos.slave.SlaveModule;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.UUID;

public class SendPlayerBukkitListener extends PacketListener<SendPlayerPacket> implements Listener {

    private SlaveModule slaveModule;

    private HashMap<UUID, LazyLocation> locationMap;
    private HashMap<UUID, UUID> uuidMap;

    public SendPlayerBukkitListener(SlaveModule slaveModule) {
        super("THANATOS_SEND_PLAYER");
        this.slaveModule = slaveModule;
        this.locationMap = new HashMap<>();
        this.uuidMap = new HashMap<>();

        slaveModule.getServer().getPluginManager().registerEvents(this, slaveModule);
    }

    @Override
    public SendPlayerPacket parsePacket(String json) {
        return GSONUtils.getGson().fromJson(json, SendPlayerPacket.class);
    }

    @Override
    public void readPacket(final SendPlayerPacket packet) {

        // Only read on the server the player will be connecting to.
        if(!slaveModule.getGlobalConfiguration().getServerId().equals(packet.getSlave().getServerId())) {
            return;
        }

        // Check if the player is already connected.
        final Player player;
        if((player = slaveModule.getServer().getPlayer(packet.getUniqueId())) != null && player.isOnline()) {
            if(packet.getLocation() != null) {
                slaveModule.getServer().getScheduler().runTask(slaveModule, new Runnable() {
                    @Override
                    public void run() {
                        player.teleport(packet.getLocation().getLocation());
                    }
                });
                return;
            }

            final Player target;
            if(packet.getTargetPlayer() != null && (target = slaveModule
                    .getServer().getPlayer(packet.getTargetPlayer())) != null && target.isOnline()) {
                slaveModule.getServer().getScheduler().runTask(slaveModule, new Runnable() {
                    @Override
                    public void run() {
                        player.teleport(target.getLocation());
                    }
                });
                return;
            }
        }

        // Add the location to a map to teleport the player on login.
        if(packet.getLocation() != null) {
            locationMap.put(packet.getUniqueId(), packet.getLocation());
        } else if(packet.getTargetPlayer() != null) {
            uuidMap.put(packet.getUniqueId(), packet.getTargetPlayer());
        }
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent e) {
        if(!(locationMap.containsKey(e.getPlayer().getUniqueId()) || uuidMap.containsKey(e.getPlayer().getUniqueId()))) {
            return;
        }

        final Player player = e.getPlayer();
        new DelayedTask(new Runnable() {
            @Override
            public void run() {
                if(locationMap.containsKey(player.getUniqueId())) {
                    if(locationMap.get(player.getUniqueId()).getLocation() != null) {
                        player.teleport(locationMap.get(player.getUniqueId()).getLocation());
                    }
                    locationMap.remove(player.getUniqueId());
                    return;
                }

                Player target;
                if(uuidMap.containsKey(player.getUniqueId()) && (target = slaveModule
                        .getServer().getPlayer(uuidMap.get(player.getUniqueId()))) != null && target.isOnline()) {
                    player.teleport(target.getLocation());
                    uuidMap.remove(player.getUniqueId());
                    return;
                }
            }
        }, 2L).run();
    }

    @Override
    public void disable() {
        super.disable();
        locationMap.clear();
    }

}