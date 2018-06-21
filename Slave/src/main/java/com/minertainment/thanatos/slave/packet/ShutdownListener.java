package com.minertainment.thanatos.slave.packet;

import com.minertainment.athena.configuration.GSONUtils;
import com.minertainment.athena.misc.SimpleCallback;
import com.minertainment.athena.packets.PacketListener;
import com.minertainment.athena.profile.ProfileManager;
import com.minertainment.athena.tasks.AsyncTask;
import com.minertainment.thanatos.commons.configuration.SlaveConfiguration;
import com.minertainment.thanatos.commons.packet.ShutdownPacket;
import com.minertainment.thanatos.slave.SlaveModule;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ShutdownListener extends PacketListener<ShutdownPacket> {

    private SlaveModule module;

    public ShutdownListener(SlaveModule module) {
        super("THANATOS_SHUTDOWN_SLAVE");
        this.module = module;
    }

    @Override
    public ShutdownPacket parsePacket(String json) {
        return GSONUtils.getGson().fromJson(json, ShutdownPacket.class);
    }

    @Override
    public void readPacket(ShutdownPacket packet) {
        if(!SlaveConfiguration.getServerId().equals(packet.getSlave().getServerId())) {
            module.getClusterManager().getClusterFromSlave(packet.getSlave().getServerId()).shutdown(packet.getSlave().getServerId());
            module.getLogger().info("Slave '" + packet.getSlave().getServerId() + "' is shutting down.");
            return;
        }

        // TODO: Log
        module.getLogger().info("Received shutdown request.");
        module.getBeatingHeart().kill();
        new AsyncTask(() -> saveNextPlayer(new ArrayList<>(module.getServer().getOnlinePlayers()), () -> module.getServer().getScheduler().runTask(module, () -> {
            if(packet.isRestart()) {
                module.getServer().spigot().restart();
            } else {
                module.getServer().shutdown();
            }
        }))).run();
    }

    public void saveNextPlayer(List<Player> players, SimpleCallback callback) {
        if(players.size() > 0) {
            Player player = players.get(0);
            players.remove(player);
            if(player.isOnline()) {
                saveNextProfile(player.getUniqueId(), new ArrayList<>(Arrays.asList(ProfileManager.getProfileManagers())), () -> {
                    if(player.isOnline()) {
                        module.getServer().getScheduler().runTask(module, () -> player.kickPlayer(ChatColor.RED + "The server you were on is restarting..."));
                    }
                    saveNextPlayer(players, callback);
                });
            }
        } else {
            callback.onCallback();
        }
    }

    public void saveNextProfile(UUID uuid, List<ProfileManager> managers, SimpleCallback finished) {
        if(managers.size() > 0) {
            ProfileManager profileManager = managers.get(0);
            managers.remove(profileManager);
            profileManager.onQuit(uuid);
            profileManager.onSave(uuid, false, profile -> saveNextProfile(uuid, managers, finished));
        } else {
            finished.onCallback();
        }
    }

}