package com.minertainment.thanatos.proxy.commands;

import com.minertainment.athena.commands.CommandContext;
import com.minertainment.athena.commands.exceptions.CommandException;
import com.minertainment.thanatos.commons.cluster.Cluster;
import com.minertainment.thanatos.commons.slave.Slave;
import com.minertainment.thanatos.commons.slave.SlaveStatus;
import com.minertainment.thanatos.proxy.ProxyModule;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;

public class SendCommand extends ThanatosCommand {

    private ProxyModule proxyModule;

    public SendCommand(ProxyModule proxyModule) {
        super("/thanatos send [slave/cluster] [slave/cluster]", "send");

        this.proxyModule = proxyModule;
    }


    @Override
    public void execute(CommandSender sender, CommandContext args) throws CommandException {
        if(args.argsLength() != 2) {
            throw new CommandException(ChatColor.RED + "Usage: " + getUsage());
        }

        Slave fromSlave = null, toSlave = null;
        Cluster fromCluster = null, toCluster = null;
        if(parseCheckSlave(args.getString(0))) {
            fromSlave = getSlave(args.getString(0));
        } else {
            fromCluster = getCluster(args.getString(0));
        }

        if(parseCheckSlave(args.getString(1))) {
            toSlave = getSlave(args.getString(1));
        } else {
            toCluster = getCluster(args.getString(1));
        }

        if(toSlave != null) {
            sendPlayers(toSlave, (Slave) (fromSlave != null ? fromSlave : fromCluster.getSlaves().values().toArray(new Slave[fromCluster.getSlaves().size()])));
        } else {
            sendPlayers(toCluster, (Slave) (fromSlave != null ? fromSlave : fromCluster.getSlaves().values().toArray(new Slave[fromCluster.getSlaves().size()])));
        }
    }

    public void sendPlayers(Slave to, Slave... from) {
        ServerInfo toServer = proxyModule.getProxy().getServerInfo(to.getServerId());
        if(toServer != null) {
            for(Slave slave : from) {
                if(slave.getOnlinePlayers() > 0) {
                    ServerInfo fromServer = proxyModule.getProxy().getServerInfo(slave.getServerId());
                    if(fromServer != null) {
                        for(ProxiedPlayer player : fromServer.getPlayers()) {
                            player.connect(toServer);
                        }
                    }
                }
            }
        }
    }

    public void sendPlayers(Cluster to, Slave... from) {
        for(Slave slave : from) {
            if(slave.getOnlinePlayers() > 0) {
                ServerInfo fromServer = proxyModule.getProxy().getServerInfo(slave.getServerId());
                if(fromServer != null) {
                    for(ProxiedPlayer player : fromServer.getPlayers()) {
                        ServerInfo toServer = proxyModule.getProxy().getServerInfo(to.getNextSlaveCached().getServerId());
                        if(toServer != null) {
                            player.connect(toServer);
                        } else {
                            player.disconnect(ChatColor.RED + "An error occurred while transferring your connection.");
                        }
                    }
                }
            }
        }
    }

    public boolean parseCheckSlave(String arg) throws CommandException {
        if(arg.startsWith("s:")) {
            if(getSlave(arg) == null) {
                throw new CommandException(ChatColor.RED + "Unknown slave '" + arg.replace("s:", "") + "'!");
            } else {
                return true;
            }
        } else if(arg.startsWith("c:")) {
            if(getCluster(arg) == null) {
                throw new CommandException(ChatColor.RED + "Unknown cluster '" + arg.replace("c:", "") + "'!");
            } else {
                return false;
            }
        } else {
            throw new CommandException(ChatColor.RED + "Incorrect argument '" + arg + "'! Must start with 'c:' for a cluster or 's:' for a slave.");
        }
    }

    public Slave getSlave(String arg) {
        return proxyModule.getClusterManager().getSlave(arg.replace("s:", ""));
    }

    public Cluster getCluster(String arg) {
        return proxyModule.getClusterManager().getCluster(arg.replace("c:", ""));
    }

}