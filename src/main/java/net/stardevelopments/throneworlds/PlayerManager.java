package net.stardevelopments.throneworlds;

import net.stardevelopments.throneworlds.weapons.PoisonShank;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerManager implements Listener{
    Main plugin;

    public PlayerManager(Main pp){this.plugin=pp;}
    FileConfiguration teamsDB = Main.teamsDB.getUserRecord();
    FileConfiguration worldState = Main.worldState.getUserRecord();
    public boolean playersCanMove = true;
    //Introduction Titles
    public void runIntroduction(Player player){
        //Defining messages upfront
        String opening = "§b§lTHRONE WORLDS";
        String openingSub = "§cDestroy enemy worlds and protect your own to win!";
        String firstMessage = "§b§lSEEK OUT ENEMY THRONE WORLDS";
        String firstSub = "§cLocate their portals in the Overworld and kill their queens";
        String secondMessage = "§b§lHOARD ESSENCE";
        String secondSub = "§cGain essence from the generator, killing mobs or vanquishing enemies";
        String thirdMessage = "§b§lINCREASE YOUR POWER";
        String thirdSub = "§cCraft and place Condensed Essence or purchase items and upgrades from your queen";
        String finaleMessage = "§b§lBE THE LAST THRONE WORLD STANDING";
        String finaleSub = "§cGame starts now.";
        String[] messagesArray = {opening,firstMessage,secondMessage,thirdMessage,finaleMessage};
        String[] subtitlesArray = {openingSub,firstSub,secondSub,thirdSub,finaleSub};
        //Loop through each message, with a two second delay
        int delayTime = 180;
        int i =0;
        for(String title : messagesArray) {
            int iPassed = i;
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1f, 1f);
                    player.sendTitle(title, subtitlesArray[iPassed]);
                    Bukkit.getServer().broadcastMessage(title);
                    Bukkit.getServer().broadcastMessage(subtitlesArray[iPassed]);
                }
            }.runTaskLater(plugin, delayTime);
            i++;
            delayTime = delayTime + 120;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                playersCanMove = true;
            }
        }.runTaskLater(plugin, delayTime);

        //Allow players to move


    }
    //Called when a player enters their throne world
    public static void onPlayerEntry(Player player){
        //Set upgraded health, and if player is close enough to full health, give them the bonus hearts.
        FileConfiguration teamsDB = Main.teamsDB.getUserRecord();
        int currentHBonus = teamsDB.getInt("team" + GameThread.getPlayerTeam(player) + ".upgrades.health-bonus",25);
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(currentHBonus);
        if (player.getHealth() >= 18){player.setHealth(currentHBonus);}
    }

    //Called when a player exits their throne world.
    public static void onPlayerExit(Player player){
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
        //Reset ANY AND ALL possible modified attributes.
    }
    //Check if move allowed
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e){
        if (!playersCanMove){e.setCancelled(true);}
    }

    //check for and override player deaths
    @EventHandler
    public void onPlayerDeath(EntityDamageEvent e){
        //Check if player

        Player player;
        if (e.getEntityType().equals(EntityType.PLAYER)){
            player = (Player) e.getEntity();
        } else{ return;}

        if(worldState.getInt("GameState")==0){return;}

        //Check if the damage is going to be fatal
        if (player.getHealth() - e.getFinalDamage() <= 0){
            //Stop event, set to spectator, drop essence
            e.setCancelled(true);
            Location deathLocation = player.getLocation();
            player.setGameMode(GameMode.SPECTATOR);

            ItemStack essence = Essence.getEssence(1);
            Inventory inventory = player.getInventory();
            //remove essence
            int numEssence = 0;
            for (ItemStack slot : inventory.getContents()){
                if (slot != null) {
                    if (slot.isSimilar(essence)) {
                        numEssence += slot.getAmount();
                        slot.setAmount(0);
                    }
                    if (slot.isSimilar(Essence.getEssenceBlock(1))) {
                        numEssence += slot.getAmount() * 9;
                        slot.setAmount(0);
                    }
                }
            }
            if(numEssence>0){Essence.doEssenceForgeDrop(player.getLocation(),numEssence);}
            //get player team. are they out? If so, set them to spectator and state that they've been eliminated.
            int i = GameThread.getPlayerTeam(player);
            String teamName = teamsDB.getString("team" + i + ".name");
            Bukkit.getServer().broadcastMessage(player.getDisplayName() + " of the " +teamName+" has been defeated!");
            plugin.qm.bandaidSolutionToBeFixedOops.onPlayerDeath(player, deathLocation);
                        //Eliminated
            if (teamsDB.getInt("team" + i + ".State") == 4){
                            //player.teleport(inBetweenTeamSpawn);
                            player.sendTitle("ELIMINATED", "You have suffered your final death.");
                            player.sendMessage("You have been eliminated! Thanks for playing Starfihgter's Throne Worlds!");
                            //player.sendMessage("If an ally survived the collapse... maybe they can bring you back somehow..."); //Omitted from this playtest
                            Bukkit.getServer().broadcastMessage(player.getDisplayName() + " of the " +teamName+" has been §4§leliminated!");
                        }
                        //If respawn is blocked
                        else if (teamsDB.getBoolean("team" + i + ".RespawnBlocked")){
                            player.sendTitle("RESURRECTION BLOCKED", "Eliminate hostiles on your Throne World to respawn");
                        }
                        //Final case, respawn all good
                        else{
                            Location spawn = plugin.wm.getMVWorld(teamsDB.getString("team" + i + ".WorldName")).getSpawnLocation();
                            player.sendTitle("DEFEATED", "Respawning in 15 seconds");
                            //Get and send player to spawn
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    //Check respawn is still valid and respawn. Also check that player has not already been respawned.
                                    if (player.getGameMode().equals(GameMode.SPECTATOR) && !teamsDB.getBoolean("team" + i + ".RespawnBlocked")) {
                                        player.teleport(spawn);
                                        player.setHealth(20);
                                        player.setGameMode(GameMode.SURVIVAL);
                                        player.setBedSpawnLocation(spawn, true);
                                        onPlayerEntry(player);
                                        player.sendMessage("Your essence was consumed to bring you back to your Throne World.");
                                    }
                                }
                            }.runTaskLater(plugin, 300);
                        }
                    }
                }
    }
