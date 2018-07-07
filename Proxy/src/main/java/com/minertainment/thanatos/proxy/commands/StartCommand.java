package com.minertainment.thanatos.proxy.commands;

import com.minertainment.athena.Athena;
import com.minertainment.athena.commands.CommandContext;
import com.minertainment.athena.commands.Permission;
import com.minertainment.athena.commands.bungee.AthenaBungeeCommand;
import com.minertainment.athena.commands.exceptions.CommandException;
import com.minertainment.thanatos.commons.Thanatos;
import com.minertainment.thanatos.commons.packet.ShutdownPacket;
import com.minertainment.thanatos.commons.slave.Slave;
import com.minertainment.thanatos.commons.slave.SlaveStatus;
import com.minertainment.thanatos.proxy.ProxyModule;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;

public class StartCommand extends AthenaBungeeCommand {

    private ProxyModule proxy;

    public StartCommand(ProxyModule proxy) {
        super("start", "/start [slave]", "Start a server.", new Permission("thanatos.start"));
        this.proxy = proxy;
        Athena.getCommandManager().registerCommand(this);
    }

    @Override
    public void onCommand(CommandSender sender, CommandContext args) throws CommandException {
        if(args.argsLength() != 1) {
            throw new CommandException(ChatColor.RED + "Usage: " + getUsage());
        }

        Slave slave = Thanatos.getClusterManager().getSlave(args.getString(0));
        if(slave == null) {
            throw new CommandException(ChatColor.RED + "Could not find slave!");
        }

        if(slave.getStatus() != SlaveStatus.OFFLINE) {
            throw new CommandException(ChatColor.RED + "The slave you wish to start is not offline.");
        }

        sender.sendMessage(ChatColor.YELLOW + "Executing startup procedure on '" + slave.getServerId() + "'...");
        proxy.getClusterManager().getClusterFromSlave(slave.getServerId()).start(slave);
    }

}