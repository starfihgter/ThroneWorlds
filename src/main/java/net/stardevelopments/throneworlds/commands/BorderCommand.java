package net.stardevelopments.throneworlds.commands;

import com.onarandombox.MultiverseCore.api.MVWorldManager;
import net.stardevelopments.throneworlds.GameThread;
import net.stardevelopments.throneworlds.Main;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class BorderCommand implements CommandExecutor {
    Main plugin;
    GameThread gt;
    public BorderCommand(Main impPlugin, GameThread impGT){
        plugin = impPlugin;
        gt = impGT;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        //Announce and record border change
        int radius = plugin.getConfig().getInt("border-radius");
        int newRadius = Integer.parseInt(args[0]);
        int secondsToChange = Integer.parseInt(args[1]);
        Long currentTime = System.currentTimeMillis();
        currentTime += (secondsToChange * 1000);
        plugin.getConfig().set("next-change", currentTime);
        Bukkit.getServer().broadcastMessage("The play area is now shrinking from a radius of " + radius + " blocks to " + newRadius + "!");
        plugin.getConfig().set("border-radius", newRadius);
        gt.onBorderUpdate();
        //Shrink border
        //MVWorldManager wm = plugin.wm;
       // World world = wm.getMVWorld("Overworld").getCBWorld();
       // world.getWorldBorder().setSize(newRadius * 2, 60);
        return true;
    }
}
