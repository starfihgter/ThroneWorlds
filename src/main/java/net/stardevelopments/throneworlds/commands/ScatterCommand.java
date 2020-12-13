package net.stardevelopments.throneworlds.commands;

import net.stardevelopments.throneworlds.GameThread;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ScatterCommand implements CommandExecutor {
    GameThread gt;

    public ScatterCommand(GameThread pgt){
        this.gt = pgt;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        gt.portalScatter();
        return true;
    }
}
