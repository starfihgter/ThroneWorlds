package net.stardevelopments.throneworlds;

import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiversePortals.MultiversePortals;
import com.onarandombox.MultiversePortals.PortalLocation;
import com.onarandombox.MultiversePortals.utils.PortalManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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
        FileConfiguration teamsDB = Main.teamsDB.getUserRecord();
        FileConfiguration worldState = Main.worldState.getUserRecord();

        int totalTeams = plugin.getConfig().getInt("Teams", 4);
        for (int i = 0; i < totalTeams; i++){

            //Randomly generate x and z within play area
            int radius = plugin.getConfig().getInt("border-radius");
            int x = ThreadLocalRandom.current().nextInt(-radius, radius);
            int z = ThreadLocalRandom.current().nextInt(-radius, radius);
            teamsDB.set("team" + i + ".portal.x", x);
            teamsDB.set("team" + i + ".portal.z", z);

            //get y value of random coord
            World overWorld = Bukkit.getWorld("Overworld");
            Block yBlock = overWorld.getHighestBlockAt(x ,z);
            int y = yBlock.getY();
            teamsDB.set("team" + i + ".portal.y", y);
            System.out.println("Creating portal at ");
            System.out.println("x " + x );
            System.out.println("y " + y );
            System.out.println("z " + z );
            //Generate portal. I could not think of a better way to do this. Please feel free to change if I'm a moron
            Material obsidian = Material.OBSIDIAN;
            yBlock.setType(obsidian);
            Block block;
            for (int b = 1; b < 5; b++){
                block = overWorld.getBlockAt(x, y + b, z);
                block.setType(obsidian);
            }
            //Thought that was bad? oooooh boy.
            block = overWorld.getBlockAt(x + 1, y, z);
            block.setType(obsidian);
            block = overWorld.getBlockAt(x + 1, y, z);
            block.setType(obsidian);
            x = yBlock.getX();
            y = yBlock.getX();
            block = overWorld.getBlockAt(x + 1, y, z);
            block.setType(obsidian);
            block = overWorld.getBlockAt(x + 1, y, z);
            block.setType(obsidian);
            block = overWorld.getBlockAt(x + 1, y, z);
            block.setType(obsidian);
            for (int b = 1; b < 5; b++){
                block = overWorld.getBlockAt(x, y + b, z);
                block.setType(obsidian);
            }
            //*cries*

            //MVC portal linking
            MVWorldManager wm = plugin.wm;
            PortalManager pm = plugin.pm.getPortalManager();
            MultiverseWorld world = wm.getMVWorld(teamsDB.getString("Overworld"));
            if (pm.isPortal("team" + i + "out")){
                pm.removePortal("team" + i + "out", true);
            }
            PortalLocation pl = new PortalLocation();
            pl.setLocation(yBlock.getLocation().toVector(), block.getLocation().toVector(), world);
            pm.addPortal(world, "team" + i + "out", "starfihgter", pl);
            pm.getPortal("team" + i + "out").setDestination("p:team" + i + "home");
            pm.getPortal("team" + i + "home").setDestination("p:team" + i + "out");
            Bukkit.getServer().broadcastMessage("Portals scattered!");
        }
    }

    //Game Start
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        FileConfiguration teamsDB = Main.teamsDB.getUserRecord();
        FileConfiguration worldState = Main.worldState.getUserRecord();

        //Check Gamestate
        MVWorldManager wm = plugin.wm;
        PortalManager pm = plugin.pm.getPortalManager();
        if (worldState.getInt("GameState", 0) != 0) {
            out("Unable to start game, game state currently " + worldState.getInt("GameState", 0), sender);
            return false;
        }

        //Create overworld
        if(wm.cloneWorld("OverworldTemplate", "Overworld")){
            World overWorld = Bukkit.getWorld("Overworld");
            overWorld.getWorldBorder().setCenter(0,0);
            overWorld.getWorldBorder().setSize(plugin.getConfig().getInt("border-radius"));
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

            // Send players to thrones (set spawn points and kill all players). Setup portals
            for (int i = 0; i < totalTeams; i++){
                List<String> teamPlayers = teamsDB.getStringList("team" + i +".members");

                //Set home portal
                MultiverseWorld world = wm.getMVWorld(teamsDB.getString("team" + i + ".WorldName"));
                PortalLocation pl = new PortalLocation();
                Vector bottomLeft = new Vector(0, 0, 0);
                Vector topRight = new Vector(3, 4, 0);
                pl.setLocation(bottomLeft, topRight, world);
                pm.addPortal(world, "team" + i + "home", "starfihgter", pl);

                //Kill players and send to throne world
                for (String playerName : teamPlayers){
                    Player player = Bukkit.getPlayer(playerName);
                    if (player != null){
                        Location spawn = wm.getMVWorld(teamsDB.getString("team" + i + ".WorldName")).getSpawnLocation();
                        player.setBedSpawnLocation(spawn, true);
                        player.setHealth(0);
                    }
                }
            }
            Bukkit.getServer().broadcastMessage("Throne Worlds created!");
            portalScatter();
            worldState.set("GameState:", 2);
            return true;
        }else{
        out("Unable to find Overworld Template!", sender);
        return  false;
        }
    }
}
