package net.stardevelopments.throneworlds;

import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.utils.WorldManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.util.Vector;

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
                    System.out.println(key);
                    int x = WorldState.getInt("BuildZones.team" + i + "." + key + ".x");
                    int z = WorldState.getInt("BuildZones.team" + i + "." + key + ".z");

                    double distance = Math.sqrt(Math.pow(playerX - x, 2) + Math.pow(playerZ - z, 2));
                    return distance < 10;
                }
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
            }
        }
    }
}
