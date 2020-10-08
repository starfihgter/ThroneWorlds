package net.stardevelopments.throneworlds;

import com.onarandombox.MultiverseCore.api.MVWorldManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class GameStart implements CommandExecutor {
    Main plugin;
    public GameStart(Main mPlugin){
        this.plugin = mPlugin;
    }

    public void out(String message, CommandSender sender){
        System.out.println(message);
        sender.sendMessage(message);
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration teamsDB = Main.teamsDB.getUserRecord();
        FileConfiguration worldState = Main.worldState.getUserRecord();
        MVWorldManager wm = plugin.wm;
        if (worldState.getInt("GameState", 0) != 0) {
            out("Tried to start game, game state currently " + worldState.getInt("GameState", 0), sender);
            return false;
        }

        if(wm.cloneWorld("OverworldTemplate", "Overworld")){
            out("Overworld created", sender);
            int totalTeams = plugin.getConfig().getInt("Teams", 4);
            for (int i = 0; i < totalTeams; i++){
                if(!(wm.cloneWorld("ThroneTemplate", "Throne" + i))){
                    out("Unable to create Throne " + i, sender);
                    return false;
                }
                teamsDB.set("team" + i + ".WorldName", "Throne" + i);
                out("Created Throne World " + i, sender);
            }
            // Send players to thrones
            return true;
        }else{
        out("Unable to find Overworld Template!", sender);
        return  false;
        }
    }
}
