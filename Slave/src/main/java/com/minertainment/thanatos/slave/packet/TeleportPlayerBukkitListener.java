package com.minertainment.thanatos.slave.packet;

import com.minertainment.athena.configuration.GSONUtils;
import com.minertainment.athena.packets.PacketListener;
import com.minertainment.athena.tasks.DelayedTask;
import com.minertainment.thanatos.commons.packet.TeleportPlayerPacket;
import com.minertainment.thanatos.slave.SlaveModule;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.UUID;

public class TeleportPlayerBukkitListener extends PacketListener<TeleportPlayerPacket> implements Listener {

    private SlaveModule slaveModule;

    private HashMap<UUID, UUID> locationMap;

    public TeleportPlayerBukkitListener(SlaveModule slaveModule) {
        super("THANATOS_TELEPORT_PLAYER");
        this.slaveModule = slaveModule;
        this.locationMap = new HashMap<>();

        slaveModule.getServer().getPluginManager().registerEvents(this, slaveModule);
    }

    @Override
    public TeleportPlayerPacket parsePacket(String json) {
        return GSONUtils.getGson().fromJson(json, TeleportPlayerPacket.class);
    }

    @Override
    public void readPacket(TeleportPlayerPacket packet) {

        // Make sure the target is on this slave.
        Player player = slaveModule.getServer().getPlayer(packet.getTarget());
        if(player == null || !player.isOnline()) {
            return;
        }

        // Add the target to a map to teleport the player on login.
        locationMap.put(packet.getUniqueId(), packet.getTarget());
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent e) {
        if(!locationMap.containsKey(e.getPlayer().getUniqueId())) {
            return;
        }

        new DelayedTask(new Runnable() {
            @Override
            public void run() {
                Player target = slaveModule.getServer().getPlayer(locationMap.get(e.getPlayer().getUniqueId()));
                if(target != null && target.isOnline()) {
                    e.getPlayer().teleport(target.getLocation());
                }
                locationMap.remove(e.getPlayer().getUniqueId());
            }
        }, 2L).run();
    }

    @Override
    public void disable() {
        super.disable();
        locationMap.clear();
    }

}