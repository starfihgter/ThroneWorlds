package net.stardevelopments.throneworlds;

import net.stardevelopments.throneworlds.weapons.PoisonShank;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerManager implements Listener{
    Main plugin;

    public PlayerManager(Main pp){this.plugin=pp;}
    FileConfiguration teamsDB = Main.teamsDB.getUserRecord();
    FileConfiguration worldState = Main.worldState.getUserRecord();

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
                }
            }
            if(numEssence>0){Essence.doEssenceForgeDrop(player.getLocation(),numEssence);}
            //get player team. are they out? If so, set them to spectator and state that they've been eliminated.
                        int i = GameThread.getPlayerTeam(player);
                        //Eliminated
                        if (teamsDB.getInt("team" + i + ".State") == 4){
                            //player.teleport(inBetweenTeamSpawn);
                            player.sendTitle("ELIMINATED", "You have suffered your final death.");
                            player.sendMessage("You have been eliminated! Thanks for playing Starfihgter's Throne Worlds!");
                            player.sendMessage("If an ally survived the collapse... maybe they can bring you back somehow...");
                            Bukkit.getServer().broadcastMessage(player.getDisplayName() + " has been eliminated!");
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
                                        player.sendMessage("Your essence was consumed to bring you back to your Throne World.");
                                    }
                                }
                            }.runTaskLater(plugin, 150);
                        }
                    }
                }
    }
