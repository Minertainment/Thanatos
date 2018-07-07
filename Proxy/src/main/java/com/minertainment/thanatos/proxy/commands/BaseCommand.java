package com.minertainment.thanatos.proxy.commands;

import com.minertainment.athena.Athena;
import com.minertainment.athena.commands.CommandContext;
import com.minertainment.athena.commands.Permission;
import com.minertainment.athena.commands.bungee.AthenaBungeeCommand;
import com.minertainment.athena.commands.exceptions.CommandException;
import com.minertainment.thanatos.proxy.ProxyModule;
import net.md_5.bungee.api.CommandSender;

import java.util.ArrayList;

public class BaseCommand extends AthenaBungeeCommand {

    private ProxyModule proxy;

    private ArrayList<ThanatosCommand> subCommands;

    public BaseCommand(ProxyModule proxy) {
        super("thanatos", new Permission("thanatos.admin"));
        this.proxy = proxy;
        subCommands = new ArrayList<>();

        Athena.getCommandManager().registerCommand(this);
        subCommands.add(new SendCommand(proxy));
        subCommands.add(new SetStatusCommand(proxy));
    }

    @Override
    public void onCommand(CommandSender sender, CommandContext args) throws CommandException {

        // Make sure arguments are provided.
        if(args.argsLength() > 0) {

            // Iterate through sub commands.
            for(ThanatosCommand command : subCommands) {

                // Iterate through aliases.
                for(String alias : command.getAliases()) {

                    // Check if argument matches an alias.
                    if(args.getString(0).equalsIgnoreCase(alias)) {

                        // Execute the command.
                        command.execute(sender, new CommandContext(args.getSlice(1), null));
                        return;
                    }
                }
            }
        }

        // Execute help command.
        // todo
    }

}