package com.minertainment.thanatos.proxy.commands;

import com.minertainment.athena.Athena;
import com.minertainment.athena.commands.CommandContext;
import com.minertainment.athena.commands.Permission;
import com.minertainment.athena.commands.bungee.AthenaBungeeCommand;
import com.minertainment.athena.commands.exceptions.CommandException;
import com.minertainment.thanatos.commons.Thanatos;
import com.minertainment.thanatos.commons.cluster.Cluster;
import com.minertainment.thanatos.commons.packet.ShutdownPacket;
import com.minertainment.thanatos.commons.slave.Slave;
import com.minertainment.thanatos.commons.slave.SlaveStatus;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Iterator;

public class RestartCommand extends AthenaBungeeCommand {

    public RestartCommand() {
        super("restart", "/restart [slave]", "Restart a server.", new Permission("thanatos.stop"));
        Athena.getCommandManager().registerCommand(this);
    }

    @Override
    public void onCommand(CommandSender sender, CommandContext args) throws CommandException {
        if(args.argsLength() > 0 && args.getString(0).equalsIgnoreCase("all")) {
            sender.sendMessage(ChatColor.YELLOW + "Executing restart procedure for all servers...");
            for(Cluster cluster : Thanatos.getClusterManager().getClusterMap().values()) {
                Iterator<Slave> slaveIterator = cluster.getSlaves().values().iterator();
                while(slaveIterator.hasNext()) {
                    Slave slave = slaveIterator.next();
                    if(slave.getStatus() == SlaveStatus.ONLINE) {
                        new ShutdownPacket(slave, true).send();
                    }
                }
            }
            return;
        }

        Slave slave;
        if(args.argsLength() == 0) {
            if(sender instanceof ProxiedPlayer) {
                slave = Thanatos.getClusterManager().getSlave(((ProxiedPlayer) sender).getServer().getInfo().getName());
            } else {
                throw new CommandException(ChatColor.RED + "Usage: " + getUsage());
            }
        } else {
            slave = Thanatos.getClusterManager().getSlave(args.getString(0));
        }

        if(slave == null) {
            throw new CommandException(ChatColor.RED + "Could not find slave!");
        }

        if(slave.getStatus() == SlaveStatus.ONLINE) {
            sender.sendMessage(ChatColor.YELLOW + "Executing restart procedure on '" + slave.getServerId() + "'...");
        } else {
            sender.sendMessage(ChatColor.YELLOW + "Slave '" + slave.getServerId() + "' is " + slave.getStatus().name() + ". Attempting to execute restart procedure regardless...");
        }

        new ShutdownPacket(slave, true).send();
    }

}