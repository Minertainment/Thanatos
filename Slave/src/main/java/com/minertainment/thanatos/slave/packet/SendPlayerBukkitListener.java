package com.minertainment.thanatos.slave.packet;

import com.minertainment.athena.configuration.GSONUtils;
import com.minertainment.athena.configuration.serializable.LazyLocation;
import com.minertainment.athena.packets.PacketListener;
import com.minertainment.thanatos.commons.packet.SendPlayerPacket;
import com.minertainment.thanatos.slave.SlaveModule;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class SendPlayerBukkitListener extends PacketListener<SendPlayerPacket> implements Listener {

    private SlaveModule slaveModule;

    private HashMap<UUID, LazyLocation> locationMap;
    private ArrayList<UUID> ignoreDisconnect;

    public SendPlayerBukkitListener(SlaveModule slaveModule) {
        super("THANATOS_SEND_PLAYER");
        this.slaveModule = slaveModule;
        this.locationMap = new HashMap<>();
        ignoreDisconnect = new ArrayList<>();

        slaveModule.getServer().getPluginManager().registerEvents(this, slaveModule);
    }

    @Override
    public SendPlayerPacket parsePacket(String json) {
        return GSONUtils.getGson().fromJson(json, SendPlayerPacket.class);
    }

    @Override
    public void readPacket(SendPlayerPacket packet) {

        // Make sure the location is present and this is the correct slave.
        if(packet.getLocation() == null) {
            //System.out.println(" -- NO LOCATION");
        }
        // TODO: Better handling of null locations
        if(!slaveModule.getGlobalConfiguration().getServerId().equals(packet.getSlave().getServerId())/* || packet.getLocation() == null*/) {
            return;
        }

        // Do not save the last location of players simply switching servers.
        OfflinePlayer player;
        if((player = slaveModule.getServer().getPlayer(packet.getUniqueId())) != null && player.isOnline()) {
            ignoreDisconnect.add(packet.getUniqueId());
        }

        // Add the location to a map to teleport the player on login.
        if(packet.getLocation() != null) {
            locationMap.put(packet.getUniqueId(), packet.getLocation());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if(!locationMap.containsKey(e.getPlayer().getUniqueId())) {
            return;
        }

        e.getPlayer().teleport(locationMap.get(e.getPlayer().getUniqueId()).getLocation());
        //System.out.println(" --- TELEPROTING TO: " + locationMap.get(e.getPlayer().getUniqueId()).toString());
        locationMap.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent e) {
        if(ignoreDisconnect.contains(e.getPlayer().getUniqueId())) {
            ignoreDisconnect.remove(e.getPlayer().getUniqueId());
            //System.out.println(" --- IGNORING DISCONNECT");
        } else {
            slaveModule.getProfileManager().getProfile(e.getPlayer().getUniqueId())
                    .setLastLocation(new LazyLocation().setLocation(e.getPlayer().getLocation()));
            //System.out.println(" --- SET LAST LOCATION");
        }
    }

    @Override
    public void disable() {
        super.disable();
        locationMap.clear();
        ignoreDisconnect.clear();
    }

}