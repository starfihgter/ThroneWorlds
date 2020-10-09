package net.stardevelopments.throneworlds;

import com.onarandombox.MultiverseCore.api.MVWorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class GameThread implements CommandExecutor {
    Main plugin;

    public GameThread(Main mPlugin){
        this.plugin = mPlugin;
    }

    //Dual Output
    public void out(String message, CommandSender sender){
        System.out.println(message);
        sender.sendMessage(message);
    }

    //Relocate Portals
    public void portalScatter(){

    }

    //Game Start
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        FileConfiguration teamsDB = Main.teamsDB.getUserRecord();
        FileConfiguration worldState = Main.worldState.getUserRecord();

        //Check Gamestate
        MVWorldManager wm = plugin.wm;
        if (worldState.getInt("GameState", 0) != 0) {
            out("Unable to start game, game state currently " + worldState.getInt("GameState", 0), sender);
            return false;
        }

        //Create overworld
        if(wm.cloneWorld("OverworldTemplate", "Overworld")){
            out("Overworld created", sender);

            //Create Throne Worlds
            int totalTeams = plugin.getConfig().getInt("Teams", 4);
            for (int i = 0; i < totalTeams; i++){
                if(!(wm.cloneWorld("ThroneTemplate", "Throne" + i))){
                    out("Unable to create Throne " + i, sender);
                    return false;
                }
                teamsDB.set("team" + i + ".WorldName", "Throne" + i);
                out("Created Throne World " + i, sender);
            }

            // Send players to thrones (set spawn points and kill all players)
            for (int i = 0; i < totalTeams; i++){
                List<String> teamPlayers = teamsDB.getStringList("team" + i +".members");
                for (String playerName : teamPlayers){
                    Player player = Bukkit.getPlayer(playerName);
                    if (player != null){
                        Location spawn = wm.getMVWorld("Throne" + i).getSpawnLocation();
                        player.setBedSpawnLocation(spawn, true);
                        player.setHealth(0);
                    }
                }
            }
            Bukkit.getServer().broadcastMessage("Throne Worlds created!");
            portalScatter();
            return true;
        }else{
        out("Unable to find Overworld Template!", sender);
        return  false;
        }
    }
}
