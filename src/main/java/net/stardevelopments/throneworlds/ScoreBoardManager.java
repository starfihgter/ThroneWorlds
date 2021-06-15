package net.stardevelopments.throneworlds;

import net.stardevelopments.throneworlds.GameThread;
import net.stardevelopments.throneworlds.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

public class ScoreBoardManager implements Listener {

    //Below is a basic constructor for the class to grab an instance of the main class.
    Main plugin;
    public ScoreBoardManager(Main impPlugin){
        plugin = impPlugin;
    }

    Scoreboard[] boardList = new Scoreboard[4];
    public void generateScoreboard() {
        FileConfiguration teamsDB = Main.teamsDB.getUserRecord();
        int totalTeams = plugin.getConfig().getInt("Teams", 4);
        ScoreboardManager manager = plugin.getServer().getScoreboardManager();
        for (int i = 0; i < totalTeams; i++){
            boardList[i] = manager.getNewScoreboard();

            Objective objective = boardList[i].registerNewObjective("Timer", "dummy", teamsDB.getString("team" + i + ".name"));
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);


            Score timer = objective.getScore("Time until Portal Scatter - " + ChatColor.AQUA + " Unknown");
            timer.setScore(11);

            int x = teamsDB.getInt("team" + i + ".portal.x");
            int z = teamsDB.getInt("team" + i + ".portal.z");
            Score coords = objective.getScore("Portal Coordinates - " + ChatColor.LIGHT_PURPLE + x + "," + z);
            coords.setScore(9);

            for (int b = 0; b < totalTeams; b++){
                Score team = objective.getScore(teamsDB.getString("team" + b + ".name") + ChatColor.GREEN + " - Active");
                team.setScore(7-b);
            }
        }
        //This is just to start the loop. Runs timerUpdate() every second, passing through only the player (for now, idk
        //If you need other parameters.
        new BukkitRunnable() {
            @Override
            public void run() {
                timerUpdate();
            }
        }.runTaskTimer(plugin, 20, 20);
    }

    public void timerUpdate() {
        FileConfiguration teamsDB = Main.teamsDB.getUserRecord();
        int totalTeams = plugin.getConfig().getInt("Teams", 4);

        //This method should update the timer every second. As I set it up, this method is being called once per second
        //for every player. If there's some sort of global scoreboard push thing (see comments below), obviously change my set up
        //for the runnable and do that lol.
        FileConfiguration config = plugin.getConfig();
        //These two variables are the time that the next change occurs, and the RADIUS of the next border from the config.
        //the long for changeTime will be millisecond Unix-time. The long millisecondsUntilChange should be pretty self explanatory.
        //Convert that millisecondsUntilChange into a human readable format and display it. VERY IMPORTANT NOTE!
        //If changeTime = 0, that means there is no change planned. Check for this, and have it display the scoreboard as such.
        //with a message like "awaiting announcement or something like that.
        long changeTime = config.getLong("next-change", 0);
        int borderRadius = config.getInt("border-radius");
        if (changeTime <= 0) {
            for (int i = 0; i < totalTeams; i++) {
                Objective objective = boardList[i].getObjective("Timer");
                for (String entry : boardList[i].getEntries()){
                    if (entry.contains("Time until Portal Scatter")){
                        boardList[i].resetScores(entry);
                    }
                }
                Score timer = objective.getScore("Time until Portal Scatter - " + ChatColor.AQUA + " Unknown");
                timer.setScore(11);
                List<String> teamPlayers = teamsDB.getStringList("team" + i + ".members");
                for (String playerName : teamPlayers) {
                    Player player = Bukkit.getPlayer(playerName);
                    if (player != null) {
                        player.setScoreboard(boardList[i]);
                    }
                }
            }
            return;
        }
            long secondsUntilChange = Math.round((changeTime - System.currentTimeMillis()) / 1000L);
            int input = (int) secondsUntilChange;
            int secondBefore = input - 1;

            int numberOfMinutes = ((input % 86400) % 3600) / 60;
            int numberOfSeconds = ((input % 86400) % 3600) % 60;

        int BnumberOfMinutes = ((secondBefore % 86400) % 3600) / 60;
        int BnumberOfSeconds = ((secondBefore % 86400) % 3600) % 60;

            String countdown = numberOfMinutes + " : " + numberOfSeconds;
            String priorCountdown = BnumberOfMinutes + " : " + BnumberOfSeconds;

            for (int i = 0; i < totalTeams; i++) {
                for (String entry : boardList[i].getEntries()){
                    if (entry.contains("Time until Portal Scatter")){
                        boardList[i].resetScores(entry);
                    }
                }
                Objective objective = boardList[i].getObjective("Timer");
                Score timer = objective.getScore("Time until Portal Scatter - " + ChatColor.AQUA + countdown);
                timer.setScore(11);
                List<String> teamPlayers = teamsDB.getStringList("team" + i + ".members");
                for (String playerName : teamPlayers) {
                    Player player = Bukkit.getPlayer(playerName);
                    if (player != null) {
                        player.setScoreboard(boardList[i]);
                    }
                }
            }
        }

    //While TimerUpdate will occur for every player once per second (at least in the spot I put the BukkitRunnable, generateScoreBoard)
    //These methods will not. I'm not super sure how scoreboards work, but obviously try to avoid looping through every player to update their
    //scoreboards if possible. If that's unavoidable, make sure you do AS MUCH logic as possible OUTSIDE loops or in the top most loop it can
    //be done to avoid lag.

    public void teamEliminated(char Team, int status){
        //To be called when a team's queen is killed
        FileConfiguration worldState = Main.worldState.getUserRecord();
        FileConfiguration teamsDB = Main.teamsDB.getUserRecord();
        int totalTeams = plugin.getConfig().getInt("Teams", 4);
        for (int i = 0; i < totalTeams; i++) {
            Objective objective = boardList[i].getObjective("Timer");
            for (String entry : boardList[i].getEntries()) {
                if (entry.contains(teamsDB.getString("team" + Team + ".name"))) {
                    boardList[i].resetScores(entry);
                }
            }

            if (status == 0){
                Score team = objective.getScore(teamsDB.getString("team" + Team + ".name") + ChatColor.GOLD + " - Queen Eliminated");
                team.setScore(7-Character.getNumericValue(Team));
            }
            if (status == 1){
                Score team = objective.getScore(teamsDB.getString("team" + Team + ".name") + ChatColor.RED + " - Team Eliminated");
                team.setScore(7-Character.getNumericValue(Team));
            }
        }
    }

    public void onPortalScatter(){
        FileConfiguration worldState = Main.worldState.getUserRecord();
        FileConfiguration teamsDB = Main.teamsDB.getUserRecord();
        //To be called on a portal scatter (GameThread.portalScatter()).
        //This should update the location of the player's portal.
        if (worldState.getInt("GameState") == 2) {
            int totalTeams = plugin.getConfig().getInt("Teams", 4);
            for (int i = 0; i < totalTeams; i++) {
                Objective objective = boardList[i].getObjective("Timer");
                for (String entry : boardList[i].getEntries()) {
                    if (entry.contains("Portal Coordinates")) {
                        boardList[i].resetScores(entry);
                    }
                }
                int x = teamsDB.getInt("team" + i + ".portal.x");
                int z = teamsDB.getInt("team" + i + ".portal.z");
                Score coords = objective.getScore("Portal Coordinates - " + ChatColor.LIGHT_PURPLE + x + "," + z);
                coords.setScore(9);
            }
        }
    }
}
