package net.stardevelopments.throneworlds;

import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.utils.WorldManager;
import org.apache.commons.lang.ObjectUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class BuildingCheck implements Listener {
    Main plugin;
    FileConfiguration WorldState = Main.worldState.getUserRecord();
    FileConfiguration config = Main.config;
    FileConfiguration teamsDB = Main.teamsDB.getUserRecord();


    public BuildingCheck(Main pp){
        this.plugin = pp;
    }


    //Get Build Zone Creator
    public static ItemStack getZonePlacer(){
        ItemStack item;
        item = new ItemStack(Material.NETHER_STAR, 1);
        Main.setItemName(item, "Power Funnel - Build Zone", Arrays.asList("§fCreates a power-dependant building zone", "§cZone will have a radius of RADIUS blocks", "§bLasts until next portal scatter", "§eThis item costs " + Main.plugin.getConfig().getInt("ZPlacer", 4)));
        return item;
    }

    //Get BuildZone Blocker
    public static ItemStack getZoneBlocker(){
        ItemStack item;
        item = new ItemStack(Material.BARRIER, 1);
        Main.setItemName(item, "Power Funnel - Build Zone Blocker", Arrays.asList("§fCreates an area in which Build Zone Power Funnels cannot be used.", "§cThis item does not destroy pre-existing Build Zones.", "§bLasts until next portal scatter", "§eThis item costs " + Main.plugin.getConfig().getInt("ZBlocker", 4)));
        return item;
    }

    //Check validity
    public boolean checkValid(Player player){
        //Get the players current location
        int playerX = player.getLocation().getBlockX();
        int playerZ = player.getLocation().getBlockZ();
        int totalTeams = config.getInt("Teams", 4);
        //Check which team the player is on (written before the static getPlayerTeam method)
        for (int i = 0; i < totalTeams; i++){
            List<String> members= teamsDB.getStringList("team" + i + ".members");
            if (members.contains(player.getName())){
                //Get all the buildzones for that team
                Set<String> zoneList = WorldState.getConfigurationSection("BuildZones.team" + i).getKeys(false);
                //For each Build Zone,
                for (String key : zoneList){
                    //Get the location
                    int x = WorldState.getInt("BuildZones.team" + i + "." + key + ".x");
                    int z = WorldState.getInt("BuildZones.team" + i + "." + key + ".z");

                    //Figure out how far the player is from it, and if they are within the radius of the zones according
                    //to the team's power.
                    double distance = Math.sqrt(Math.pow(playerX - x, 2) + Math.pow(playerZ - z, 2));
                    double radius = 10;
                    radius = radius + ((9*Math.sqrt((teamsDB.getInt("team" + i + ".power", 0)))));
                    //Max out at 120, if they are in the radius, return true. If not, false.
                    if (radius > 120){radius = 120;}
                    if (distance < radius){
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        MVWorldManager wm = plugin.wm;
        Player player = event.getPlayer();
        String worldName = wm.getMVWorld(player.getWorld()).getName();
        //Check they are in the overworld. If the block break is NOT valid, cancel the event.
        if (worldName.equals("Overworld")){
            if (!checkValid(player)){
                event.setCancelled(true);
                player.sendMessage("You are not in a building zone!");
            }
        }
        if (event.getBlock().getType().equals(Material.BARRIER)){event.setCancelled(true);}
    }
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        MVWorldManager wm = plugin.wm;
        Player player = event.getPlayer();
        String worldName = wm.getMVWorld(player.getWorld()).getName();
        //Check if the block is a scaffold, if so, its valid so don't do the other shit.
        if (event.getBlockPlaced().getType().equals(Material.SCAFFOLDING)){
            return;
        }
        //Check they are in the overworld. If the block place is NOT valid, cancel the event.
        if (worldName.equals("Overworld")){
            if (!checkValid(player)){
                event.setCancelled(true);
                player.sendMessage("You are not in a building zone!");
            }
        }
        if (event.getBlockPlaced().getType().equals(Material.BARRIER)){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        //Check in overworld, and annoying NPE check.
        if (player.getWorld().getName().equals("Overworld")) {
            try {
                item.getType();
            } catch (NullPointerException e) {
                return;
            }
            //Who cares if i check for nether stars. You can't get them legit.
            if (item.getType().equals(Material.NETHER_STAR)) {
                try {
                    //For every blockzone, check player is not within 100 blocks of said build zone. Functionally similar to CheckValid()
                    Set<String> blockZones = WorldState.getConfigurationSection("BlockZones").getKeys(false);
                    for (String key : blockZones){
                        int blockX = WorldState.getInt("BlockZones." + key + ".x");
                        int blockZ = WorldState.getInt("BlockZones." + key + ".z");
                        double distance = Math.sqrt(Math.pow(player.getLocation().getBlockX() - blockX, 2) + Math.pow(player.getLocation().getBlockZ() - blockZ, 2));
                        int radius = 100;
                        if (distance < radius){
                            player.sendMessage("This area is being blocked against build zones!");
                            return;
                        }
                    }
                } catch (NullPointerException ignored){} //Double NPE catch.
                //If the zone if not blocked (cleared by earlier code)< check which team the player is in, and then
                //add the build zone to that team's Build Zones, and remove the item.
                int totalTeams = config.getInt("Teams", 4);
                for (int i = 0; i < totalTeams; i++) {
                    List<String> members = teamsDB.getStringList("team" + i + ".members");
                    if (members.contains(player.getName())) {
                        Set<String> zoneList = WorldState.getConfigurationSection("BuildZones.team" + i).getKeys(false);
                        WorldState.set("BuildZones.team" + i + ".z" + (zoneList.size()) + ".x", player.getLocation().getBlockX());
                        WorldState.set("BuildZones.team" + i + ".z" + (zoneList.size()) + ".z", player.getLocation().getBlockZ());
                        item.setAmount(item.getAmount() - 1);
                        player.sendMessage("Build Zone Created!");
                    }
                }
            }else if (item.getType().equals(Material.BARRIER)){
                //same process as placer, but for blocking.
                int size;
                try { size = WorldState.getConfigurationSection("BlockZones").getKeys(false).size();}
                catch (NullPointerException ignored){
                    size = 0;
                }
                WorldState.set("BlockZones.z" + size + ".x", player.getLocation().getBlockX());
                WorldState.set("BlockZones.z" + size + ".z", player.getLocation().getBlockZ());
                item.setAmount(item.getAmount() - 1);
                player.sendMessage("Blocking field created!");
            }
        }
    }
}
