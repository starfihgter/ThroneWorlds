package net.stardevelopments.throneworlds;

import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public class QueenManager implements Listener {
    //Game start
    public void CreateQueens(){
        FileConfiguration teamsDB = Main.teamsDB.getUserRecord();
        FileConfiguration worldState = Main.worldState.getUserRecord();

        int totalTeams = Main.plugin.getConfig().getInt("Teams", 4);
        for (int i = 0; i < totalTeams; i++){
            World world = Bukkit.getWorld(Objects.requireNonNull(teamsDB.getString("team" + i + ".WorldName")));
            Location queenLoc = new Location(world, 10, 10, 10); // Those are placeholder values. Waiting to build world
            Entity queen = world.spawnEntity(queenLoc, EntityType.VILLAGER);
            queen.setCustomName("Queen " + i);
            LivingEntity livingQueen = (LivingEntity) queen;
            livingQueen.setAI(false);
        }
    }

    //Generate Queen UI
    @EventHandler
    public void onRightClick(PlayerInteractEntityEvent e){
        Entity queen = e.getRightClicked();
        Player player = e.getPlayer();
        if (queen.getCustomName().contains("Queen ")){
            Inventory gui = Bukkit.createInventory(player, 27, "The Queen");
            ItemStack abilities = new ItemStack(Material.HONEY_BOTTLE, 1);
            ItemStack upgrade = new ItemStack(Material.ENDER_CHEST, 1);
            ItemStack power = new ItemStack(Material.EMERALD, 1);
            Main.setItemName(abilities, "Weapon and Ability Store");
            Main.setItemName(upgrade, "Upgrade shit!");
            Main.setItemName(power, "All the power you could want.");
            gui.setItem(11, abilities);
            gui.setItem(13, upgrade);
            gui.setItem(15, power);
        }
    }

    //Use Queen UI
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        Player player = (Player) e.getWhoClicked();
        if (e.getView().getTitle().equals("The Queen")){
            e.setCancelled(true);
        }
    }

    //Queen Death
    public void onEntityDeath(EntityDeathEvent e){
        //play epic music
    }
}
