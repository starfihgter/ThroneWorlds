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
        Main.setItemName(item, "Power Funnel - Build Zone", Arrays.asList("Creates a power-dependant building zone", "Zone will have a radius of RADIUS blocks", "Lasts until next portal scatter"));
        return item;
    }

    //Get BuildZone Blocker
    public static ItemStack getZoneBlocker(){
        ItemStack item;
        item = new ItemStack(Material.BARRIER, 1);
        Main.setItemName(item, "Power Funnel - Build Zone Blocker", Arrays.asList("Creates an area in which Build Zone Power Funnels cannot be used.", "This item does not destroy pre-existing Build Zones.", "Lasts until next portal scatter"));
        return item;
    }

    //Check validity
    public boolean checkValid(Player player){
        int playerX = player.getLocation().getBlockX();
        int playerZ = player.getLocation().getBlockZ();
        //THE GREAT MACHINE WILL FINALLY KNOW OUR PAIN
        int totalTeams = config.getInt("Teams", 4);
        for (int i = 0; i < totalTeams; i++){
            List<String> members= teamsDB.getStringList("team" + i + ".members");
            if (members.contains(player.getName())){
                Set<String> zoneList = WorldState.getConfigurationSection("BuildZones.team" + i).getKeys(false);
                for (String key : zoneList){
                    int x = WorldState.getInt("BuildZones.team" + i + "." + key + ".x");
                    int z = WorldState.getInt("BuildZones.team" + i + "." + key + ".z");

                    double distance = Math.sqrt(Math.pow(playerX - x, 2) + Math.pow(playerZ - z, 2));
                    double radius = 10;
                    radius = radius + (Math.pow((teamsDB.getInt("team" + i + ".power", 0)), 2) /2);
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
        if (worldName.equals("Overworld")){
            if (!checkValid(player)){
                event.setCancelled(true);
                player.sendMessage("You are not in a building zone!");
            }
        }
    }
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        MVWorldManager wm = plugin.wm;
        Player player = event.getPlayer();
        String worldName = wm.getMVWorld(player.getWorld()).getName();
        if (worldName.equals("Overworld")){
            if (!checkValid(player)){
                event.setCancelled(true);
                player.sendMessage("You are not in a building zone!");
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (player.getWorld().getName().equals("Overworld")) {
            try {
                item.getType();
            } catch (NullPointerException e) {
                return;
            }
            if (item.getType().equals(Material.NETHER_STAR)) {
                try {
                    Set<String> blockZones = WorldState.getConfigurationSection("BlockZones").getKeys(false);
                    for (String key : blockZones){
                        int blockX = WorldState.getInt("BlockZones." + key + ".x");
                        int blockZ = WorldState.getInt("BlockZones." + key + ".z");
                        double distance = Math.sqrt(Math.pow(player.getLocation().getBlockX() - blockX, 2) + Math.pow(player.getLocation().getBlockZ() - blockZ, 2));
                        int radius = 30;
                        if (distance < radius){
                            player.sendMessage("This area is being blocked against build zones!");
                            return;
                        }
                    }
                } catch (NullPointerException ignored){}
                int totalTeams = config.getInt("Teams", 4);
                for (int i = 0; i < totalTeams; i++) {
                    List<String> members = teamsDB.getStringList("team" + i + ".members");
                    if (members.contains(player.getName())) {
                        Set<String> zoneList = WorldState.getConfigurationSection("BuildZones.team" + i).getKeys(false);
                        WorldState.set("BuildZones.team" + i + ".z" + (zoneList.size()) + ".x", player.getLocation().getBlockX());
                        WorldState.set("BuildZones.team" + i + ".z" + (zoneList.size()) + ".z", player.getLocation().getBlockZ());
                        player.getInventory().remove(item);
                        player.sendMessage("Build Zone Created!");
                    }
                }
            }else if (item.getType().equals(Material.BARRIER)){
                int size;
                try { size = WorldState.getConfigurationSection("BlockZones").getKeys(false).size();}
                catch (NullPointerException ignored){
                    size = 0;
                }
                WorldState.set("BlockZones.z" + size + ".x", player.getLocation().getBlockX());
                WorldState.set("BlockZones.z" + size + ".z", player.getLocation().getBlockZ());
                player.getInventory().remove(item);
                player.sendMessage("Blocking field created!");
            }
        }
    }
}
