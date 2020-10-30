package net.stardevelopments.throneworlds;

import com.onarandombox.MultiverseCore.MVWorld;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import net.stardevelopments.throneworlds.weapons.PortalCompass;
import net.stardevelopments.throneworlds.weapons.TWAbility;
import net.stardevelopments.throneworlds.weapons.TntBow;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

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
    TWAbility[] itemsList = {new TntBow()};

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

    //Abilities Screen
    public void generateAbilityScreen(Player player){
        Inventory gui = Bukkit.createInventory(player, 36, "Weapons and Ability Store");

        ItemStack back = new ItemStack(Material.ARROW, 1);
        Main.setItemName(back, "Go Back", null);

        gui.setItem(0, BuildingCheck.getZonePlacer());
        gui.setItem(1, BuildingCheck.getZoneBlocker());

        int slot = 2;
        for (TWAbility item : itemsList){
            gui.setItem(slot, item.getItem());
            slot++;
        }
        gui.setItem(35, back);

        int totalTeams = Main.plugin.getConfig().getInt("Teams", 4);
        for (int i = 0; i < totalTeams; i++){
            if (teamsDB.getInt("team" + i + ".State") != 4){
            gui.setItem(27 + i, new PortalCompass(i).getItem());
            }
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
    public void onInventoryClick(InventoryClickEvent e) {
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
        if (e.getView().getTitle().equals("Weapons and Ability Store")) {
            if (e.getCurrentItem().getItemMeta() != null) {
                e.setCancelled(true);
                switch (e.getCurrentItem().getItemMeta().getDisplayName()) {
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
                        for (int i = 0; i < totalTeams; i++) {
                            if (e.getCurrentItem().getItemMeta().getDisplayName().equals(new PortalCompass(i).getName())) {
                                player.getInventory().addItem(new PortalCompass(i).getItem());
                                player.sendMessage("You bought a " + e.getCurrentItem().getItemMeta().getDisplayName());
                            }
                        }
                    }
                }
                for (TWAbility item : itemsList){
                    if (e.getCurrentItem().getItemMeta().getDisplayName().equals(item.getName())){
                        player.getInventory().addItem(item.getItem());
                        player.sendMessage("You bought a " + item.getName());
                    }
                }
            }
        }
    }

    //Check Respawn validity
    @EventHandler
    public void onRespawn(PlayerRespawnEvent e){
        Player player = e.getPlayer();
        int totalTeams = Main.plugin.getConfig().getInt("Teams", 4);
        for (int i = 0; i < totalTeams; i++){
            for (String member : teamsDB.getStringList("team" + i + ".members")){
                if (player.getName().equals(member)){
                    if (teamsDB.getInt("team" + i + ".State") == 4){
                        player.setGameMode(GameMode.SPECTATOR);
                        player.sendMessage("You have been eliminated! Thanks for playing Starfihgter's Throne Worlds! You can still spectate.");
                    }
                }
            }
        }
    }
    //Queen Death
    @EventHandler
    public void onEntityDeath(EntityDeathEvent e){
        Entity queen = e.getEntity();
        if (queen.getCustomName() != null) {
            if (queen.getCustomName().contains("Queen ")) {
                char team = plugin.wm.getMVWorld(queen.getWorld()).getName().charAt(6);
                teamsDB.set("team" + team + ".State", 4);
                Bukkit.getServer().broadcastMessage("Team " + team + "'s Queen has been slain! Their Throne world is collapsing!");
                MultiverseWorld world = plugin.wm.getMVWorld(queen.getWorld());
                World cbWorld = world.getCBWorld();
                cbWorld.getWorldBorder().setSize(1, 60);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (Player player : cbWorld.getPlayers()){
                            Location tpDest = new Location(Bukkit.getWorld("world"), 0, 0, 0);
                            player.teleport(tpDest);
                            player.setBedSpawnLocation(plugin.wm.getMVWorld("Overworld").getSpawnLocation(), true);
                            player.setHealth(0);
                        }
                        plugin.wm.deleteWorld(world.getName(), true);
                        Bukkit.getServer().broadcastMessage("Throne World " + team + " has collapsed.");
                        plugin.gt.portalScatter();
                        cancel();
                    }
                }.runTaskLater(plugin, 1240);

            }
        }
    }
}
