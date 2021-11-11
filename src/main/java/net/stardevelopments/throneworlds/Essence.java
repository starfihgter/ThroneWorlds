package net.stardevelopments.throneworlds;

import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.utils.WorldManager;
import net.stardevelopments.throneworlds.Main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class Essence implements Listener {

    MVWorldManager wm;
    FileConfiguration worldState = Main.worldState.getUserRecord();
    FileConfiguration teamsDB = Main.teamsDB.getUserRecord();

    public Essence(Main pp){
        this.wm = pp.wm;
    }
    //Get essence item
    public static ItemStack getEssence(int amount){

        ItemStack essence = new ItemStack(Material.EMERALD, amount);
        ItemMeta essenceMeta = essence.getItemMeta();
        essenceMeta.setDisplayName("Essence");
        essence.setItemMeta(essenceMeta);

        return essence;
    }

    //Add to drops
    @EventHandler
    public void onDrop(EntityDeathEvent event){
        if (event.getEntity().getKiller() != null) {
            event.getDrops().add(getEssence(1));
        }
    }

    //Tracking Throne Power THRONE1

    @EventHandler
    public void onPlace(BlockPlaceEvent event){
        Block block = event.getBlockPlaced();
        String worldName = wm.getMVWorld(block.getWorld()).getName();
        //If the player is placing an Emerald block in a throne,
        if (block.getType().equals(Material.EMERALD_BLOCK) && worldName.contains("Throne")){
            //Get the team of that throne and add the block to its power
            char team = worldName.charAt(6);
            int power = teamsDB.getInt("team" + team + ".power", 0);
            teamsDB.set("team" + team + ".power", power + 1);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event){
        //Same as onPlace, but for break.
        Block block = event.getBlock();
        String worldName = wm.getMVWorld(block.getWorld()).getName();
        if (block.getType().equals(Material.EMERALD_BLOCK) && worldName.contains("Throne")){
            char team = worldName.charAt(6);
            int power = teamsDB.getInt("team" + team + ".power", 0);
            teamsDB.set("team" + team + ".power", power - 1);
        }
    }

    public static void doEssenceForgeDrop(Location location, int num){
        //Drop a single essence at location.
        ItemStack item = getEssence(num);
        location.getWorld().dropItem(location, item);
    }

    public void toBeExecutedEvery5Ticks(){
        //This method is executed every 40 ticks, despite the method name. Next line is a debug step that will be removed.
        //For each team, if the are not out...
        int totalTeams = Main.plugin.getConfig().getInt("Teams", 4);
        for (int i = 0; i < totalTeams; i++){
            if (teamsDB.getInt("team" + i + ".State") != 4){
                //Get efficiency and output of forge, and a random number between 0 and 4
                int efficiency = teamsDB.getInt("team" + i + ".upgrades.forge-e", 1);
                int output = teamsDB.getInt("team" + i + ".upgrades.forge-o", 1);
                int random = ThreadLocalRandom.current().nextInt(0, 4);
                //if the efficiency value (chance) is greater than or equal to the random number, drop OUTPUT amount of essence at the forge.
                if (efficiency >= random){
                    Location location = new Location(wm.getMVWorld("Throne" + i).getCBWorld(), 2.5, 53, -11.5);
                        doEssenceForgeDrop(location, output);
                }
            }
        }
    }
}
