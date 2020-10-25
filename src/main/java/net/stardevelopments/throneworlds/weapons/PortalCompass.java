package net.stardevelopments.throneworlds.weapons;

import net.stardevelopments.throneworlds.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;

import java.util.Arrays;

public class PortalCompass {
    public static ItemStack getPortalCompass(int team){
        FileConfiguration teamsDB = Main.teamsDB.getUserRecord();

        ItemStack compass = new ItemStack(Material.COMPASS, 1);
        CompassMeta cmp = (CompassMeta) compass.getItemMeta();
        World world = Bukkit.getWorld("Overworld");
        int x = teamsDB.getInt("team" + team + ".portal.x");
        int y = teamsDB.getInt("team" + team + ".portal.y");
        int z = teamsDB.getInt("team" + team + ".portal.z");
        Location location = world.getBlockAt(x, y, z).getLocation();
        cmp.setLodestoneTracked(false);
        cmp.setLodestone(location);
        cmp.setDisplayName("Team " + team + " portal tracker");
        cmp.setLore(Arrays.asList("This tracker is useless after a portal scatter"));
        compass.setItemMeta(cmp);
        return compass;
    }
}
