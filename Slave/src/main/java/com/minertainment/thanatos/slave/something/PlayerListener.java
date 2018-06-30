package com.minertainment.thanatos.slave.something;

import com.minertainment.thanatos.commons.Thanatos;
import com.minertainment.thanatos.commons.configuration.SlaveConfiguration;
import com.minertainment.thanatos.commons.configuration.ThanatosConfiguration;
import com.minertainment.thanatos.commons.packet.StartClusterPacket;
import com.minertainment.thanatos.commons.profile.ThanatosProfile;
import com.minertainment.thanatos.commons.profile.ThanatosProfileManager;
import com.minertainment.thanatos.slave.SlaveModule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;

public class PlayerListener implements Listener {

    private SlaveModule module;

    public PlayerListener(SlaveModule module) {
        this.module = module;
        module.getServer().getPluginManager().registerEvents(this, module);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if(module.getOnlinePlayers() > ThanatosConfiguration.getSoftPlayerLimit()) {
            new StartClusterPacket(SlaveConfiguration.getClusterId()).send();
        }
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent e) {
        module.setLastDisconnect(System.currentTimeMillis());
    }

    @EventHandler(
            priority = EventPriority.LOWEST
    )
    public void onDrop(PlayerDropItemEvent e) {
        if(!e.getPlayer().isOp() && !Thanatos.getServer().getProfileManager().isLoaded(e.getPlayer().getUniqueId())) {
            ThanatosProfile profile = Thanatos.getServer().getProfileManager().getProfile(e.getPlayer().getUniqueId());
            if(profile.isDisconnected()) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(
            priority = EventPriority.LOWEST
    )
    public void onPickUp(PlayerAttemptPickupItemEvent e) {
        ThanatosProfileManager manager = Thanatos.getServer().getProfileManager();
        if(!e.getPlayer().isOp() && (!manager.isLoaded(e.getPlayer().getUniqueId()) || manager.getProfile(e.getPlayer().getUniqueId()).isDisconnected())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(
            priority = EventPriority.LOWEST
    )
    public void onPreProcess(PlayerCommandPreprocessEvent e) {
        ThanatosProfileManager manager = Thanatos.getServer().getProfileManager();
        if(!e.getPlayer().isOp() && (!manager.isLoaded(e.getPlayer().getUniqueId()) || manager.getProfile(e.getPlayer().getUniqueId()).isDisconnected())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(
            priority = EventPriority.LOWEST
    )
    public void onInteract(PlayerInteractEvent e) {
        ThanatosProfileManager manager = Thanatos.getServer().getProfileManager();
        if(!e.getPlayer().isOp() && (!manager.isLoaded(e.getPlayer().getUniqueId()) || manager.getProfile(e.getPlayer().getUniqueId()).isDisconnected())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(
            priority = EventPriority.LOWEST
    )
    public void onOpen(InventoryOpenEvent e) {
        ThanatosProfileManager manager = Thanatos.getServer().getProfileManager();
        if(!e.getPlayer().isOp() && (!manager.isLoaded(e.getPlayer().getUniqueId()) || manager.getProfile(e.getPlayer().getUniqueId()).isDisconnected())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(
            priority = EventPriority.LOWEST
    )
    public void onClick(InventoryClickEvent e) {
        ThanatosProfileManager manager = Thanatos.getServer().getProfileManager();
        if(!e.getWhoClicked().isOp() && (!manager.isLoaded(e.getWhoClicked().getUniqueId()) || manager.getProfile(e.getWhoClicked().getUniqueId()).isDisconnected())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(
            priority = EventPriority.LOWEST
    )
    public void onDrag(InventoryDragEvent e) {
        ThanatosProfileManager manager = Thanatos.getServer().getProfileManager();
        if(!e.getWhoClicked().isOp() && (!manager.isLoaded(e.getWhoClicked().getUniqueId()) || manager.getProfile(e.getWhoClicked().getUniqueId()).isDisconnected())) {
            e.setCancelled(true);
        }
    }

}