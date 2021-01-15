package net.stardevelopments.throneworlds;

import com.onarandombox.MultiverseCore.MVWorld;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import net.stardevelopments.throneworlds.essence.Essence;
import net.stardevelopments.throneworlds.weapons.*;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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

    //Ability master array - ADD CLASSES HERE TO AUTOMATICALLY ADD TO STORE
    TWAbility[] itemsList = {new WitherBow(), new MagicMirror(), new Scaffolding()};

    //This method checks if the player can afford a given item.
    public Boolean removeMoneys(ItemStack item, int cost, Player player){
        int initCost = cost;
        //Checking if player can pay
        ItemStack essence = Essence.getEssence(1);
        Inventory inventory = player.getInventory();

        //Null Check
        for (ItemStack slot : inventory.getContents()){
                if (slot != null) {
                    if (slot.isSimilar(essence)) {
                        if (slot.getAmount() >= cost) {
                            //If they have enough in THAT STACK, buys the item
                            slot.setAmount(slot.getAmount() - cost);
                            inventory.addItem(item);
                            player.sendMessage("You bought " + item.getItemMeta().getDisplayName());
                            return true;
                        } else {
                            cost = cost - slot.getAmount();
                            slot.setAmount(0);
                        }
                    }
                }
        }
        //If not enough Essence was found, exits the loop and refunds essence
        inventory.addItem(Essence.getEssence(initCost - cost));
        player.sendMessage("You need " + (cost) + " more essence to buy " + item.getItemMeta().getDisplayName());
        return false;
    }
    //Game start
    public void CreateQueens(){
        int totalTeams = Main.plugin.getConfig().getInt("Teams", 4);
        for (int i = 0; i < totalTeams; i++){

            //Creates a queen in each throne world
            World world = Bukkit.getWorld(Objects.requireNonNull(teamsDB.getString("team" + i + ".WorldName")));
            Location queenLoc = new Location(world, -12.5, 46, 0.5,-90,0);
            Entity queen = world.spawnEntity(queenLoc, EntityType.VILLAGER);
            queen.setCustomName("Queen " + i);
            LivingEntity livingQueen = (LivingEntity) queen;
            livingQueen.setAI(false);
            //queens[0] = queen;
        }
    }
    //Generate Queen UI
    public void generateMainScreen(Player player){

        //Define inventory, and main screen icons.
        Inventory gui = Bukkit.createInventory(player, 27, "The Queen");
        ItemStack abilities = new ItemStack(Material.HONEY_BOTTLE, 1);
        ItemStack upgrade = new ItemStack(Material.ENDER_CHEST, 1);
        ItemStack power = new ItemStack(Material.EMERALD, 1);

        char team = plugin.wm.getMVWorld(player.getWorld()).getName().charAt(6);
        Main.setItemName(abilities, "Weapon and Ability Store", null);
        Main.setItemName(upgrade, "Upgrade shit!", null);
        Main.setItemName(power, "All the power you could want.", Arrays.asList("Current Power: " + teamsDB.getInt("team" + team + ".power", 0)
                ,"Power Funnels can create Build Zones with a radius of " + (10 + (9*Math.sqrt(teamsDB.getInt("team" + team + ".power", 0))))));

        //Setting items and opening inventory
        gui.setItem(11, abilities);
        gui.setItem(13, upgrade);
        gui.setItem(15, power);
        player.openInventory(gui);
    }

    //Set array list on startup
    public static void findQueens(){}

    //Abilities Screen
    public void generateAbilityScreen(Player player){

        //Defining basics
        Inventory gui = Bukkit.createInventory(player, 36, "Weapons and Ability Store");

        ItemStack back = new ItemStack(Material.ARROW, 1);
        Main.setItemName(back, "Go Back", null);
        gui.setItem(0, BuildingCheck.getZonePlacer());
        gui.setItem(1, BuildingCheck.getZoneBlocker());


        //This loop starts at slot 2 (after the zone blocker and placer cause they're hard coded), and then runs through each item
        //in the itemsList array from earlier, and then assigns the item to a slot.
        int slot = 2;
        for (TWAbility item : itemsList){
            gui.setItem(slot, item.getItem());
            slot++;
        }
        gui.setItem(35, back);

        //Checks how many teams many teams there are, then runs through them. If they aren't eliminated, adds a compass to the store.
        int totalTeams = Main.plugin.getConfig().getInt("Teams", 4);
        for (int i = 0; i < totalTeams; i++){
            if (teamsDB.getInt("team" + i + ".State") != 4){
            gui.setItem(27 + i, new PortalCompass(i).getItem());
            }
        }

        player.openInventory(gui);
    }

    // Upgrades screen
    public void generateUpgradeScreen(Player player){
        Inventory gui = Bukkit.createInventory(player, 36, "Team Upgrades");

        //Checks how many teams there are,
        int totalTeams = Main.plugin.getConfig().getInt("Teams", 4);
        for (int i = 0; i < totalTeams; i++){
            //Then for each player of each team
            for (String member : teamsDB.getStringList("team" + i + ".members")){
                //Checks if they are the player who is using the inventory
                if (player.getName().equals(member)){
                    //Checks if the team is eliminated
                    if (teamsDB.getInt("team" + i + ".State") != 4) {
                        //Now we know what team the player is on, and we know they're not eliminated.
                        //Grabs the current efficiency and output from team database
                        int efficiency = teamsDB.getInt("team" + i + ".upgrades.forge-e",1) * 25;
                        int output = teamsDB.getInt("team" + i + ".upgrades.forge-o",1);

                        ItemStack back = new ItemStack(Material.ARROW, 1);
                        Main.setItemName(back, "Go Back", null);
                        gui.setItem(35, back);

                        //Creating upgrade items based on Efficiency and output values.
                        ItemStack forgeE = new ItemStack(Material.BLAST_FURNACE, 1);
                        Main.setItemName(forgeE, "Forge Efficiency", Arrays.asList("The Forge is currently operating at " + efficiency + "% efficiency!"));
                        gui.setItem(0, forgeE);

                        ItemStack forgeO = new ItemStack(Material.FURNACE_MINECART, 1);
                        Main.setItemName(forgeO, "Forge Output", Arrays.asList("The Forge is currently outputting " + output + " essence per cycle!"));
                        gui.setItem(1, forgeO);

                        player.openInventory(gui);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEntityEvent e){
        //Checks if its a queen, opens the menu if so.
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
        //Checks the entity was a queen
        if (e.getView().getTitle().equals("The Queen")) {
            e.setCancelled(true);
            //Checks that the player is not clicking an empty space (console spam)
            if (e.getCurrentItem().hasItemMeta()) {
                //Hardcoded standard buttons
                switch (e.getCurrentItem().getItemMeta().getDisplayName()) {
                    case "Go Back": {
                        player.closeInventory();
                        break;
                    }
                    case "Weapon and Ability Store": {
                        generateAbilityScreen(player);
                        break;
                    }
                    case "Upgrade shit!": {
                        generateUpgradeScreen(player);
                        break;
                    }
                }
            }
        }

        if (e.getView().getTitle().equals("Team Upgrades")) {
            if (e.getCurrentItem().getItemMeta() != null) {
                e.setCancelled(true);
                //Grabs player team and grabs team upgrade values
                int i = GameThread.getPlayerTeam(player);
                int efficiency = teamsDB.getInt("team" + i + ".upgrades.forge-e");
                int output = teamsDB.getInt("team" + i + ".upgrades.forge-o");
                int factor = 1;
                //Code execution depending on what was clicked. Each option varies too much to not hard code somewhere.
                switch (e.getCurrentItem().getItemMeta().getDisplayName()) {
                    case "Go Back": {
                        generateMainScreen(player);
                        break;
                    }
                    case "Forge Efficiency":{
                        //Checks the efficiency is less than 4 (max), and then sets the 'factor' accordingly
                        //The factor determines how much to multiply the base price by for this upgrade. Probably should add this
                        //to configs rather than hardcoding
                        if (efficiency < 4){
                            if (efficiency == 1){factor = 1;}
                            if (efficiency == 2){factor = 4;}
                            if (efficiency == 3){factor = 9;}
                            //If they can afford the upgrade, increase the efficiency by one and store the new efficiency
                            if (removeMoneys(e.getCurrentItem(), Main.plugin.getConfig().getInt("ForgeE", 4) * factor, player)){
                                efficiency++;
                                teamsDB.set("team" + i + ".upgrades.forge-e", efficiency);
                                player.sendMessage("Forge Efficiency upgraded to " + efficiency * 25 + "%");
                            }
                            generateUpgradeScreen(player);
                        }
                        break;
                    }
                    case "Forge Output":{
                        //Basically the same as efficiency, but with output instead.
                        if (output < 4){
                            if (output == 1){factor = 1;}
                            if (output == 2){factor = 4;}
                            if (output == 3){factor = 9;}
                            if (removeMoneys(e.getCurrentItem(), Main.plugin.getConfig().getInt("ForgeO", 4) * factor, player)) {
                                output++;
                                teamsDB.set("team" + i + ".upgrades.forge-o", output);
                                player.sendMessage("Forge output upgraded to " + output + " per cycle!");
                            }
                            generateUpgradeScreen(player);
                        }
                        break;
                    }
                }
            }
        }

        if (e.getView().getTitle().equals("Weapons and Ability Store")) {
            if (e.getCurrentItem().getItemMeta() != null) {
                e.setCancelled(true);
                //Hardcoded compasses and Power Funnels.
                switch (e.getCurrentItem().getItemMeta().getDisplayName()) {
                    case "Power Funnel - Build Zone": {
                        removeMoneys(BuildingCheck.getZonePlacer(), Main.plugin.getConfig().getInt("ZPlacer", 4), player);
                        break;
                    }
                    case "Power Funnel - Build Zone Blocker": {
                        removeMoneys(BuildingCheck.getZoneBlocker(), Main.plugin.getConfig().getInt("ZBlocker", 4), player);
                        break;
                    }
                    default: {
                        //for all taems that are in, check which one they want. Check and give the player the appropriate compass.
                        int totalTeams = Main.plugin.getConfig().getInt("Teams", 4);
                        for (int i = 0; i < totalTeams; i++) {
                            if (e.getCurrentItem().getItemMeta().getDisplayName().equals(new PortalCompass(i).getName())) {
                                removeMoneys(new PortalCompass(i).getItem(), new PortalCompass(i).getCost(), player);
                            }
                        }
                        break;
                    }
                }
                //For each item in the items list, check if the item clicked is equal to it. If so, check and give the player said item.
                for (TWAbility item : itemsList){
                    if (e.getCurrentItem().getItemMeta().getDisplayName().equals(item.getName())){
                        removeMoneys(item.getItem(), item.getCost(), player);
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
        //For each player of each team, is THIS player one of them? If so, are they out? If so, set them to spectator and state that they've been eliminated.
        for (int i = 0; i < totalTeams; i++){
            for (String member : teamsDB.getStringList("team" + i + ".members")){
                if (player.getName().equals(member)){
                    if (teamsDB.getInt("team" + i + ".State") == 4){
                        player.setGameMode(GameMode.SPECTATOR);
                        player.sendMessage("You have been eliminated! Thanks for playing Starfihgter's Throne Worlds! You can still spectate.");
                        Bukkit.getServer().broadcastMessage(player.getDisplayName() + " has been eliminated!");
                    }
                }
            }
        }
    }
    //Queen Death
    @EventHandler
    public void onEntityDeath(EntityDeathEvent e){
        //Checks if the queen died
        Entity queen = e.getEntity();
        if (queen.getCustomName() != null) {
            if (queen.getCustomName().contains("Queen ")) {
                //If the queen did die, check which one it was, and mark that team as out.
                char team = plugin.wm.getMVWorld(queen.getWorld()).getName().charAt(6);
                teamsDB.set("team" + team + ".State", 4);
                String teamName = teamsDB.getString("team" + team + ".name");
                Bukkit.getServer().broadcastMessage("The "+ teamName + "' Queen has been slain! Their Throne world is collapsing!");
                //Get the throne world and start closing the border (1 minute)
                MultiverseWorld world = plugin.wm.getMVWorld(queen.getWorld());
                World cbWorld = world.getCBWorld();
                cbWorld.getWorldBorder().setCenter(26,-2);
                cbWorld.getWorldBorder().setSize(1, 60);
                cbWorld.getWorldBorder().setDamageAmount(10);
                //After one minute and 2 seconds (just to be sure), execute run()
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        //For each player still in the throne world, teleport them to 0 , 0 , 0 in the overworld to move them
                        //out of the TW, as worlds cannot be deleted if players are in them.
                        for (Player player : cbWorld.getPlayers()){
                            Location tpDest = new Location(Bukkit.getWorld("world"), 0, 0, 0);
                            player.teleport(tpDest);
                            //If the player was on the eliminated team, set their new spawn point to the overworld, as they can
                            //No longer respawn in their TW.
                            if (GameThread.getPlayerTeam(player) == team){
                            player.setBedSpawnLocation(plugin.wm.getMVWorld("Overworld").getSpawnLocation(), true);}
                            player.setHealth(0);
                        }
                        plugin.wm.deleteWorld(world.getName(), true);
                        Bukkit.getServer().broadcastMessage("The Throne World of the " + teamName + " has collapsed.");
                        plugin.gt.portalScatter();
                        cancel();
                    }
                }.runTaskLater(plugin, 1240);

            }
        }
    }
}
