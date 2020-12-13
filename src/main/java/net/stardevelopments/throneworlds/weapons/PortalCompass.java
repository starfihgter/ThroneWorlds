package net.stardevelopments.throneworlds.weapons;

import net.stardevelopments.throneworlds.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;

import javax.sound.sampled.Port;
import java.util.Arrays;

public class PortalCompass extends TWAbility {
    private int team;
    String name;
    int cost = Main.plugin.getConfig().getInt("PortalCompass", 4);
    public PortalCompass(int pTeam){
        this.team = pTeam;
        name = "Team " + team + " portal tracker";
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getCost() {
        return cost;
    }

    @Override
    public ItemStack getItem(){
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
        cmp.setDisplayName(name);
        cmp.setLore(Arrays.asList("This tracker is useless after a portal scatter", "This item costs " + cost + " essence!"));
        compass.setItemMeta(cmp);
        return compass;
    }

}
