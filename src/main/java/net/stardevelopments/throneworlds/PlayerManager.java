package net.stardevelopments.throneworlds;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerManager implements Listener{
    Main plugin;

    public PlayerManager(Main pp){this.plugin=pp;}
    FileConfiguration teamsDB = Main.teamsDB.getUserRecord();
    FileConfiguration worldState = Main.worldState.getUserRecord();

    //check for and override player deaths
    @EventHandler
    public void onPlayerDeath(EntityDamageEvent e){
        //Check if player

        Player player;
        if (e.getEntityType().equals(EntityType.PLAYER)){
            player = (Player) e.getEntity();
        } else{ return;}

        //Check if the damage is going to be fatal
        if (player.getHealth() - e.getFinalDamage() <= 0){
            //Stop event, set to spectator, drop essence
            e.setCancelled(true);
            player.setGameMode(GameMode.SPECTATOR);

            //Respawn logic
            int totalTeams = Main.plugin.getConfig().getInt("Teams", 4);
            //For each player of each team, is THIS player one of them? If so, are they out? If so, set them to spectator and state that they've been eliminated.
            for (int i = 0; i < totalTeams; i++){
                for (String member : teamsDB.getStringList("team" + i + ".members")){
                    if (player.getName().equals(member)){
                        //Eliminated
                        if (teamsDB.getInt("team" + i + ".State") == 4){
                            player.sendTitle("ELIMINATED", "You have suffered your final death.");
                            player.sendMessage("You have been eliminated! Thanks for playing Starfihgter's Throne Worlds! You can still spectate.");
                            Bukkit.getServer().broadcastMessage(player.getDisplayName() + " has been eliminated!");
                            return;
                        }
                        //If respawn is blocked
                        else if (teamsDB.getBoolean("team" + i + ".RespawnBlocked")){
                            player.sendTitle("REVIVE BLOCKED", "Eliminate hostiles on your Throne World to respawn");
                        }
                        //Final case, respawn all good
                        else{
                            Location spawn = plugin.wm.getMVWorld(teamsDB.getString("team" + i + ".WorldName")).getSpawnLocation();
                            player.sendTitle("DEFEATED", "Respawning in 15 seconds");
                            //Get and send player to spawn
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    //Run a check here to ensure that respawn is still viable and not blocked. No announcement needed, whatever method starts the block will anounce to player. This basically just canels.
                            player.teleport(spawn);
                            player.setHealth(20);
                            player.setGameMode(GameMode.SURVIVAL);
                            player.setBedSpawnLocation(spawn, true);
                                }
                            }.runTaskLater(plugin, 150);
                        }
                    }
                }
            }
        }
    }
}
