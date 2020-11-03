package net.stardevelopments.throneworlds.essence;

import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.utils.WorldManager;
import net.stardevelopments.throneworlds.Main;
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

public class Essence implements Listener {

    MVWorldManager wm;
    FileConfiguration worldState = Main.worldState.getUserRecord();
    FileConfiguration teamsDB = Main.teamsDB.getUserRecord();

    public Essence(Main pp){
        this.wm = pp.wm;
    }
    //Get essence item
    public static ItemStack getEssence(){

        ItemStack essence = new ItemStack(Material.EMERALD);
        ItemMeta essenceMeta = essence.getItemMeta();
        essenceMeta.setDisplayName("Essence");
        essence.setItemMeta(essenceMeta);

        return essence;
    }

    //Add to drops
    @EventHandler
    public void onDrop(EntityDeathEvent event){
        if (event.getEntity().getKiller() != null) {
            event.getDrops().add(getEssence());
        }
    }

    //Tracking Throne Power THRONE1

    @EventHandler
    public void onPlace(BlockPlaceEvent event){
        Block block = event.getBlockPlaced();
        String worldName = wm.getMVWorld(block.getWorld()).getName();
        if (block.getType().equals(Material.EMERALD_BLOCK) && worldName.contains("Throne")){
            char team = worldName.charAt(6);
            int power = teamsDB.getInt("team" + team + ".power", 0);
            teamsDB.set("team" + team + ".power", power + 1);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event){
        Block block = event.getBlock();
        String worldName = wm.getMVWorld(block.getWorld()).getName();
        if (block.getType().equals(Material.EMERALD_BLOCK) && worldName.contains("Throne")){
            char team = worldName.charAt(6);
            int power = teamsDB.getInt("team" + team + ".power", 0);
            teamsDB.set("team" + team + ".power", power - 1);
        }
    }
}
