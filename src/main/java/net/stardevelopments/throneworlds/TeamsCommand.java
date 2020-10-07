package net.stardevelopments.throneworlds;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class TeamsCommand implements CommandExecutor {
    @Override
    public boolean onCommand( CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1){return false;}
        if (args[0].equals("yourMum"))
        return true;
    }
}
