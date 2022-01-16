package net.stardevelopments.throneworlds;

import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GameThread implements CommandExecutor {
    Main plugin;
    QueenManager qm;
    public GameThread(Main mPlugin, QueenManager qmp){
        this.plugin = mPlugin;
        this.qm = qmp;
    }

    //Dual Output
    public void out(String message, CommandSender sender){
        Bukkit.getServer().broadcastMessage(message);
    }

    //Get Player Team
    public static int getPlayerTeam(Player player) {
        int totalTeams = Main.plugin.getConfig().getInt("Teams", 4);
        FileConfiguration teamsDB = Main.teamsDB.getUserRecord();
        for (int i = 0; i < totalTeams; i++) {
            for (String member : teamsDB.getStringList("team" + i + ".members")) {
                if (player.getName().equals(member)) {
                    return i;
                }
            }
        }
        return -1;
    }
    //Relocate Portals
    public void portalScatter() {
        FileConfiguration teamsDB = Main.teamsDB.getUserRecord();
        FileConfiguration worldState = Main.worldState.getUserRecord();
        MVWorldManager wm = plugin.wm;

        int totalTeams = plugin.getConfig().getInt("Teams", 4);
        worldState.set("BuildZones", null);
        worldState.set("BlockZones", null);
        for (int i = 0; i < totalTeams; i++) {

            String teamName = teamsDB.getString("team" + i + ".name");
                //Check and delete portals
                if (teamsDB.isInt("team" + i + ".portal.x")) {
                    int x = teamsDB.getInt("team" + i + ".portal.x");
                    int y = teamsDB.getInt("team" + i + ".portal.y");
                    int z = teamsDB.getInt("team" + i + ".portal.z");
                    MultiverseWorld overWorld = wm.getMVWorld("Overworld");
                    Block yBlock = overWorld.getCBWorld().getBlockAt(x, y, z);
                    //Delete portal. I could not think of a better way to do this. Please feel free to change if I'm a moron
                    Material obsidian = Material.AIR;
                    yBlock.setType(obsidian);
                    Block block;
                    for (int b = 1; b < 5; b++) {
                        block = overWorld.getCBWorld().getBlockAt(x, y + b, z);
                        block.setType(obsidian);
                    }
                    //Thought that was bad? oooooh boy.
                    block = overWorld.getCBWorld().getBlockAt(x + 1, y + 4, z);
                    block.setType(obsidian);
                    block = overWorld.getCBWorld().getBlockAt(x + 2, y + 4, z);
                    block.setType(obsidian);
                    block = overWorld.getCBWorld().getBlockAt(x + 1, y, z);
                    block.setType(obsidian);
                    block = overWorld.getCBWorld().getBlockAt(x + 2, y, z);
                    block.setType(obsidian);
                    block = overWorld.getCBWorld().getBlockAt(x + 3, y, z);
                    block.setType(obsidian);
                    for (int b = 1; b < 5; b++) {
                        block = overWorld.getCBWorld().getBlockAt(x + 3, y + b, z);
                        block.setType(obsidian);
                    }
                    //Setting portal fill
                    overWorld.getCBWorld().getBlockAt(x+1, y + 1, z).setType(obsidian);
                    overWorld.getCBWorld().getBlockAt(x+1, y + 2, z).setType(obsidian);
                    overWorld.getCBWorld().getBlockAt(x+1, y + 3, z).setType(obsidian);
                    overWorld.getCBWorld().getBlockAt(x+2, y + 1, z).setType(obsidian);
                    overWorld.getCBWorld().getBlockAt(x+2, y + 2, z).setType(obsidian);
                    overWorld.getCBWorld().getBlockAt(x+2, y + 3, z).setType(obsidian);
                    //*cries*
                }
            if (teamsDB.getInt("team" + i + ".State") != 4){
            //Randomly generate x and z within play area
            int radius = plugin.getConfig().getInt("border-radius");
            int x = ThreadLocalRandom.current().nextInt(-radius, radius);
            int z = ThreadLocalRandom.current().nextInt(-radius, radius);
            teamsDB.set("team" + i + ".portal.x", x);
            teamsDB.set("team" + i + ".portal.z", z);

            //get y value of random coord
            MultiverseWorld overWorld = wm.getMVWorld("Overworld");
            Block yBlock = overWorld.getCBWorld().getHighestBlockAt(x, z);
            int y = yBlock.getY();
            teamsDB.set("team" + i + ".portal.y", y);

            worldState.set("BuildZones.team" + i + ".z0.x", x);
            worldState.set("BuildZones.team" + i + ".z0.z", z);
            //Generate portal. I could not think of a better way to do this. Please feel free to change if I'm a moron
            Material obsidian;
            //Determines colour for generated portal.
            switch (i) {
                case 0:{
                    obsidian = Material.GREEN_GLAZED_TERRACOTTA;
                    break;
                }
                case 1:{
                    obsidian = Material.BLUE_GLAZED_TERRACOTTA;
                    break;
                }
                case 2:{
                    obsidian = Material.PURPLE_GLAZED_TERRACOTTA;
                    break;
                }
                case 3:{
                    obsidian = Material.RED_GLAZED_TERRACOTTA;
                    break;
                }
                default:
                    throw new IllegalStateException("Unexpected team value: " + i);
            }
            yBlock.setType(obsidian);
            Block block;
            for (int b = 1; b < 5; b++) {
                block = overWorld.getCBWorld().getBlockAt(x, y + b, z);
                block.setType(obsidian);
            }
            //Thought that was bad? oooooh boy.
            block = overWorld.getCBWorld().getBlockAt(x + 1, y + 4, z);
            block.setType(obsidian);
            block = overWorld.getCBWorld().getBlockAt(x + 2, y + 4, z);
            block.setType(obsidian);
            block = overWorld.getCBWorld().getBlockAt(x + 1, y, z);
            block.setType(obsidian);
            block = overWorld.getCBWorld().getBlockAt(x + 2, y, z);
            block.setType(obsidian);
            block = overWorld.getCBWorld().getBlockAt(x + 3, y, z);
            block.setType(obsidian);
            for (int b = 1; b < 5; b++) {
                block = overWorld.getCBWorld().getBlockAt(x + 3, y + b, z);
                block.setType(obsidian);
            }
            //Filling portals
                Material fillMaterial = Material.END_GATEWAY;
                overWorld.getCBWorld().getBlockAt(x+1, y + 1, z).setType(fillMaterial);
                overWorld.getCBWorld().getBlockAt(x+1, y + 2, z).setType(fillMaterial);
                overWorld.getCBWorld().getBlockAt(x+1, y + 3, z).setType(fillMaterial);
                overWorld.getCBWorld().getBlockAt(x+2, y + 1, z).setType(fillMaterial);
                overWorld.getCBWorld().getBlockAt(x+2, y + 2, z).setType(fillMaterial);
                overWorld.getCBWorld().getBlockAt(x+2, y + 3, z).setType(fillMaterial);
            //*cries*
            //MVC portal linking
            PortalManager pm = plugin.pm.getPortalManager();
            if (pm.isPortal("team" + i + "out")) {
                pm.removePortal("team" + i + "out", true);
            }
            PortalLocation pl = new PortalLocation();
            Vector bottomLeft = new Vector(x, y, z);
            Vector topRight = new Vector(x + 3, y + 4, z);
            pl.setLocation(bottomLeft, topRight, overWorld);
            pm.addPortal(overWorld, "team" + i + "out", "", pl);
            pm.getPortal("team" + i + "out").setDestination("p:team" + i + "home");
            pm.getPortal("team" + i + "home").setDestination("p:team" + i + "out");
            Bukkit.getServer().broadcastMessage("The " + teamName + "' portal has relocated!");
        }

    }
        Main.sb.onPortalScatter();
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
            overWorld.getWorldBorder().setSize((plugin.getConfig().getInt("border-radius", 2000))*2);
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
                MultiverseWorld world = plugin.wm.getMVWorld("Throne" + i);
                World cbWorld = world.getCBWorld();
                cbWorld.getWorldBorder().setCenter(14 ,-4);
                cbWorld.getWorldBorder().setSize(300);
            }
            // Send players to thrones (set spawn points and kill all players). Setup portals
            for (int i = 0; i < totalTeams; i++){
                List<String> teamPlayers = teamsDB.getStringList("team" + i +".members");

                //Set home portal
                MultiverseWorld world = wm.getMVWorld(teamsDB.getString("team" + i + ".WorldName"));
                PortalLocation pl = new PortalLocation();
                Vector bottomLeft = new Vector(26, 52, -2);
                Vector topRight = new Vector(26, 57, 2);
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
            //Start Forges
            new BukkitRunnable() {
                @Override
                public void run() {
                    new Essence(plugin).toBeExecutedEvery5Ticks();
                }
            }.runTaskTimer(plugin, 40, 40);
            Main.sb.generateScoreboard();
            Bukkit.getServer().broadcastMessage("Throne Worlds created!");
            qm.CreateQueens();
            worldState.set("GameState", 2);
            onBorderUpdate();
            portalScatter();
            return true;
        }else{
        out("Unable to find Overworld Template!", sender);
        return  false;
        }
    }

    //Manage incoming border change
    public void onBorderUpdate(){
        //Calculate ticks until the border changes
        long changeTime = plugin.getConfig().getLong("next-change",0);
        int radius = plugin.getConfig().getInt("border-radius",1000);

        //Create random new time between 3 and 20 minutes, half radius for subsequent shrink.
        int newRadius = radius/2;

        int newSecondsToChange = ThreadLocalRandom.current().nextInt(180, 1200);
        long newMilSecsUntilChange = newSecondsToChange*1000L;
        plugin.getConfig().set("next-change", (newMilSecsUntilChange + System.currentTimeMillis()));
        Bukkit.getServer().broadcastMessage("OI LOOK " + newSecondsToChange+ " AND " +radius);
        //Set task to execute border change and scatter
        new BukkitRunnable() {
            @Override
            public void run() {
                //
                World world = plugin.wm.getMVWorld("Overworld").getCBWorld();
                world.getWorldBorder().setSize((radius*2), 60);
                plugin.getConfig().set("border-radius", newRadius);
                portalScatter();
                Bukkit.getServer().broadcastMessage("§l§cPlay area now shrinking to a radius of " + radius);
                Bukkit.getServer().broadcastMessage("§l§cThe play area will shrink to a radius of "+ newRadius + " in " + newSecondsToChange/60 + " minutes");
                onBorderUpdate();
            }
        }.runTaskLater(plugin, newMilSecsUntilChange/50L);
    }
}
