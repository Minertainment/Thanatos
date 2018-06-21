package com.minertainment.thanatos.proxy.commands;

import com.minertainment.athena.commands.CommandContext;
import com.minertainment.athena.commands.exceptions.CommandException;
import net.md_5.bungee.api.CommandSender;

public abstract class ThanatosCommand {

    private String usage;
    private String[] aliases;

    public ThanatosCommand(String usage, String... aliases) {
        this.usage = usage;
        this.aliases = aliases;
    }

    public String getUsage() {
        return usage;
    }

    public String[] getAliases() {
        return aliases;
    }

    abstract void execute(CommandSender sender, CommandContext args) throws CommandException;

}