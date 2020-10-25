package net.stardevelopments.throneworlds;

import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import net.stardevelopments.throneworlds.weapons.PortalCompass;
import net.stardevelopments.throneworlds.weapons.TntBow;
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

import java.util.Arrays;
import java.util.Objects;

public class QueenManager implements Listener {
    public Entity[] queens;
    Main plugin;

    public QueenManager(Main pp){
        this.plugin = pp;
    }
    FileConfiguration teamsDB = Main.teamsDB.getUserRecord();
    FileConfiguration worldState = Main.worldState.getUserRecord();

    //Game start
    public void CreateQueens(){

        int totalTeams = Main.plugin.getConfig().getInt("Teams", 4);
        for (int i = 0; i < totalTeams; i++){
            World world = Bukkit.getWorld(Objects.requireNonNull(teamsDB.getString("team" + i + ".WorldName")));
            Location queenLoc = new Location(world, 14, 51, -4); // Those are placeholder values. Waiting to build world
            Entity queen = world.spawnEntity(queenLoc, EntityType.VILLAGER);
            queen.setCustomName("Queen " + i);
            LivingEntity livingQueen = (LivingEntity) queen;
            livingQueen.setAI(false);
            //queens[0] = queen;
        }
    }
    //Generate Queen UI
    public void generateMainScreen(Player player){
        Inventory gui = Bukkit.createInventory(player, 27, "The Queen");
        ItemStack abilities = new ItemStack(Material.HONEY_BOTTLE, 1);
        ItemStack upgrade = new ItemStack(Material.ENDER_CHEST, 1);
        ItemStack power = new ItemStack(Material.EMERALD, 1);

        char team = plugin.wm.getMVWorld(player.getWorld()).getName().charAt(6);
        Main.setItemName(abilities, "Weapon and Ability Store", null);
        Main.setItemName(upgrade, "Upgrade shit!", null);
        Main.setItemName(power, "All the power you could want.", Arrays.asList("Current Power: " + teamsDB.getInt("team" + team + ".power", 0)
                ,"Power Funnels can create Build Zones with a radius of " + (10 + (Math.pow((teamsDB.getInt("team" + team + ".power", 0)), 2) /2))));
        gui.setItem(11, abilities);
        gui.setItem(13, upgrade);
        gui.setItem(15, power);
        player.openInventory(gui);
    }

    //Set array list on startup
    public static void findQueens(){}

    //Abilites Screen
    public void generateAbilityScreen(Player player){
        Inventory gui = Bukkit.createInventory(player, 36, "Weapons and Ability Store");

        ItemStack back = new ItemStack(Material.ARROW, 1);
        Main.setItemName(back, "Go Back", null);

        gui.setItem(0, TntBow.getTntBow());
        gui.setItem(1, BuildingCheck.getZonePlacer());
        gui.setItem(2, BuildingCheck.getZoneBlocker());
        gui.setItem(35, back);

        int totalTeams = Main.plugin.getConfig().getInt("Teams", 4);
        for (int i = 0; i < totalTeams; i++){
            gui.setItem(9 + i, PortalCompass.getPortalCompass(i));
        }

        player.openInventory(gui);
    }
    @EventHandler
    public void onRightClick(PlayerInteractEntityEvent e){
        Entity queen = e.getRightClicked();
        Player player = e.getPlayer();
        if (queen.getCustomName() != null) {
            if (queen.getCustomName().contains("Queen ")) {
                generateMainScreen(player);
            }
        }
    }

    //Use Queen UI
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        Player player = (Player) e.getWhoClicked();
        if (e.getView().getTitle().equals("The Queen")) {
            e.setCancelled(true);
            if (e.getCurrentItem().getItemMeta() != null) {
                switch (e.getCurrentItem().getItemMeta().getDisplayName()) {
                    case "Go Back": {
                        player.closeInventory();
                    }
                    case "Weapon and Ability Store": {
                        generateAbilityScreen(player);
                    }
                }
            }
        }

        //Yeah I really gotta think of a better way to do this... Kinda wanna do it like tokens, except idk how I'd do internal methods in config...
        if (e.getView().getTitle().equals("Weapons and Ability Store")){
            if (e.getCurrentItem().getItemMeta() != null) {
                e.setCancelled(true);
                switch (e.getCurrentItem().getItemMeta().getDisplayName()) {
                    case "TNT Bow": {
                        player.getInventory().addItem(TntBow.getTntBow());
                        player.sendMessage("You bought a " + e.getCurrentItem().getItemMeta().getDisplayName());
                    }
                    case "Go Back": {
                        generateMainScreen(player);
                    }
                    case "Power Funnel - Build Zone": {
                        player.getInventory().addItem(BuildingCheck.getZonePlacer());
                        player.sendMessage("You bought a " + e.getCurrentItem().getItemMeta().getDisplayName());
                    }
                    case "Power Funnel - Build Zone Blocker": {
                        player.getInventory().addItem(BuildingCheck.getZoneBlocker());
                        player.sendMessage("You bought a " + e.getCurrentItem().getItemMeta().getDisplayName());
                    }
                    default: {
                        int totalTeams = Main.plugin.getConfig().getInt("Teams", 4);
                        for (int i = 0; i < totalTeams; i++){
                            if(e.getCurrentItem().getItemMeta().getDisplayName().equals("Team " + i + " portal tracker")){
                                player.getInventory().addItem(PortalCompass.getPortalCompass(i));
                                player.sendMessage("You bought a " + e.getCurrentItem().getItemMeta().getDisplayName());
                            }
                        }
                    }
                }
                }
        }
    }

    //Queen Death
    @EventHandler
    public void onEntityDeath(EntityDeathEvent e){
        //play epic music
    }
}
