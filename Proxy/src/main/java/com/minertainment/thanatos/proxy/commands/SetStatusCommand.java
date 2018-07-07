package com.minertainment.thanatos.proxy.commands;

import com.minertainment.athena.commands.CommandContext;
import com.minertainment.athena.commands.exceptions.CommandException;
import com.minertainment.thanatos.commons.cluster.Cluster;
import com.minertainment.thanatos.commons.slave.Slave;
import com.minertainment.thanatos.commons.slave.SlaveStatus;
import com.minertainment.thanatos.proxy.ProxyModule;
import com.minertainment.thanatos.proxy.cluster.ProxyClusterManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.apache.commons.lang.WordUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class SetStatusCommand extends ThanatosCommand {

    private ProxyModule proxyModule;

    public SetStatusCommand(ProxyModule proxyModule) {
        super("/thanatos setstatus [slave] [status]");

        this.proxyModule = proxyModule;
    }


    @Override
    public void execute(CommandSender sender, CommandContext args) throws CommandException {
        if(args.argsLength() != 2) {
            throw new CommandException(ChatColor.RED + getUsage());
        }

        Slave slave;
        if((slave = proxyModule.getClusterManager().getSlave(args.getString(0))) == null) {
            throw new CommandException(ChatColor.RED + "Unknown slave '" + args.getString(0) + "'!");
        }

        SlaveStatus slaveStatus;
        if((slaveStatus = SlaveStatus.valueOf(args.getString(1).toUpperCase())) == null) {
            throw new CommandException(ChatColor.RED + "Unknown status '" + args.getString(1) + "'! Available statuses: " + getStatuses());
        }

        slave.setStatus(slaveStatus);
        sender.sendMessage(ChatColor.GREEN + "Set " + slave.getServerId() + "'s status to " + WordUtils.capitalize(slaveStatus.name()) + ".");
    }

    private String getStatuses() {
        StringBuilder builder = new StringBuilder();
        for(SlaveStatus status : SlaveStatus.values()) {
            builder.append(WordUtils.capitalize(status.name()) + ", ");
        }
        return builder.substring(0, builder.length()-2);
    }

}