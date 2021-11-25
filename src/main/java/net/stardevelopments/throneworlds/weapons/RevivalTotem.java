package net.stardevelopments.throneworlds.weapons;

import net.stardevelopments.throneworlds.GameThread;
import net.stardevelopments.throneworlds.Main;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class RevivalTotem extends TWAbility implements Listener {

    Main plugin;
    FileConfiguration teamsDB = Main.teamsDB.getUserRecord();
    public RevivalTotem(Main passPlug) {
        super("Radiance Anchor", Material.TOTEM_OF_UNDYING,1,"Type \"Light the path home\" to revive you and your team from beyond the grave!");
        this.plugin = passPlug;
    }

    @EventHandler
    //Get for chat message to revive players
    public void onChatEvent(AsyncPlayerChatEvent e){
        Player player = e.getPlayer();
        //Check if player said the phrase, and has the Revive totem in their inventory.
            if(player.getInventory().contains(getItem())){
                if(e.getMessage().equals("Light the path home")){
                    int team = GameThread.getPlayerTeam(player);
                    //For each player in the game, check if they're in spectator (or in the inbetween after update), and on the team.
                    //if so, respawn them with a different message.
                    for (Player targetedPlayer : Bukkit.getServer().getOnlinePlayers()){
                        if (GameThread.getPlayerTeam(targetedPlayer) == team && targetedPlayer.getGameMode().equals(GameMode.SPECTATOR)) {
                            Location spawn = plugin.wm.getMVWorld(teamsDB.getString("team" + team + ".WorldName")).getSpawnLocation();
                            player.teleport(spawn);
                            player.setHealth(20);
                            player.setGameMode(GameMode.SURVIVAL);
                            player.setBedSpawnLocation(spawn, true);
                            Bukkit.getServer().broadcastMessage("[TEAM]'s radiance has brought them back to this realm!");
                        }
                    }
                }
            }
    }


}
