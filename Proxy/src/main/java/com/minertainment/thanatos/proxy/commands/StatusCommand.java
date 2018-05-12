package com.minertainment.thanatos.proxy.commands;

import com.minertainment.athena.Athena;
import com.minertainment.athena.commands.CommandContext;
import com.minertainment.athena.commands.Permission;
import com.minertainment.athena.commands.bungee.AthenaBungeeCommand;
import com.minertainment.athena.commands.exceptions.CommandException;
import com.minertainment.thanatos.commons.cluster.Cluster;
import com.minertainment.thanatos.commons.plugin.TPSMeter;
import com.minertainment.thanatos.commons.slave.Slave;
import com.minertainment.thanatos.commons.slave.SlaveStatus;
import com.minertainment.thanatos.proxy.ProxyModule;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;

import java.text.DecimalFormat;

public class StatusCommand extends AthenaBungeeCommand {

    private ProxyModule proxy;

    private DecimalFormat decimalFormat;

    public StatusCommand(ProxyModule proxy) {
        super("status", new Permission("thanatos.status"));
        this.proxy = proxy;
        decimalFormat = new DecimalFormat("#.##");

        Athena.getCommandManager().registerCommand(this);
    }

    @Override
    public void onCommand(CommandSender sender, CommandContext args) throws CommandException {
        sender.sendMessage(ChatColor.GOLD + "[Thanatos] " + ChatColor.GRAY + "Cluster Status:");
        for(Cluster cluster : proxy.getClusterManager().getClusterMap().values()) {
            sender.sendMessage(ChatColor.GRAY + " " + cluster.getClusterId() + ":");
            for(Slave slave : cluster.getSlaves().values()) {
                ChatColor color = (slave.getStatus() == SlaveStatus.ONLINE ?
                        ChatColor.GREEN : slave.getStatus() == SlaveStatus.STARTUP ? ChatColor.YELLOW : ChatColor.RED);
                sender.sendMessage("   " + ChatColor.GRAY + slave.getServerId() + ": " + color + slave.getStatus().toString());
                if(slave.getStatus() == SlaveStatus.ONLINE) {
                    sender.sendMessage("     " + ChatColor.GRAY + "TPS: " + TPSMeter.fromTPS(slave.getTPS()).getColor() +
                            decimalFormat.format(slave.getTPS()) + ChatColor.GRAY + "   Players: " + ChatColor.WHITE + slave.getOnlinePlayers());
                }
            }
        }
    }

}