package net.stardevelopments.throneworlds.scoreboard;

import net.stardevelopments.throneworlds.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

public class ScoreBoard {

    //Below is a basic constructor for the class to grab an instance of the main class.
    Main plugin;
    public ScoreBoard(Main impPlugin){
        plugin = impPlugin;

    }
    //All I have done is outline methods and notes on overall design, plus some code snippets to help. LMK if you need help
    //With any of it, but this should help you on the right track I hope. The design should be event-based, you call to
    //methods in this when events occur elsewhere in the plugin, by passing around an instance of this class, like I do with
    //GameThread and QueenManager (see BorderCommand's usage of GameThread for an example). The only thing that should be on
    //a timer is the clock, which I've written out some code for you already. You might need to move the bukkit runnable (the
    //code inside Run() is executed every 20 ticks), that snippet of code just starts the loop. Don't really need a way to stop it.
    //More notes are below. PLLLLLLLLLLLLLLLEEEEEEEEEEEEEEEEEEEEAAAAAAAAAAAAAASSSSSSSSSSSSSSSEEEEEEEEEEEEEEEEEEEEE have this done by the time
    //I get back from Fish Creek on the 27th. I want to hopefully playtest the weekend I get back (6/7 feb). Text me any questions, should
    //still be checking my phone. Remember, I've already coded the actual border changing and all that, you just gotta display it to the player.
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        generateScoreboard(e.getPlayer());
    }

    public void generateScoreboard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard sb = manager.getNewScoreboard();

        Objective objective = sb.registerNewObjective("throneWorlds", "dummy", ChatColor.BLUE + "THRONE WORLDS");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score xyz = objective.getScore(ChatColor.RED + "xyz");
        xyz.setScore(2);

        Score power = objective.getScore(ChatColor.GREEN + "POWER");
        power.setScore(1);


        //This is just to start the loop. Runs timerUpdate() every second, passing through only the player (for now, idk
        //If you need other parameters.
        new BukkitRunnable() {
            @Override
            public void run() {
                timerUpdate(player);
            }
        }.runTaskTimer(plugin, 20, 20);
    }

    public void timerUpdate(Player player){

        //This method should update the timer every second. As I set it up, this method is being called once per second
        //for every player. If there's some sort of global scoreboard push thing (see comments below), obviously change my set up
        //for the runnable and do that lol.
        FileConfiguration config = plugin.getConfig();
        //These two variables are the time that the next change occurs, and the RADIUS of the next border from the config.
        //the long for changeTime will be millisecond Unix-time. The long millisecondsUntilChange should be pretty self explanatory.
        //Convert that millisecondsUntilChange into a human readable format and display it. VERY IMPORTANT NOTE!
        //If changeTime = 0, that means there is no change planned. Check for this, and have it display the scoreboard as such.
        //with a message like "awaiting announcement or something like that.
        long changeTime = config.getLong("next-change");
        int borderRadius = config.getInt("border-radius");
        if (changeTime == 0){
            //Display alternate scoreboard for no upcoming border change
            return;
        }
        long millisecondsUntilChange = changeTime - System.currentTimeMillis();
    }

    //While TimerUpdate will occur for every player once per second (at least in the spot I put the BukkitRunnable, generateScoreBoard)
    //These methods will not. I'm not super sure how scoreboards work, but obviously try to avoid looping through every player to update their
    //scoreboards if possible. If that's unavoidable, make sure you do AS MUCH logic as possible OUTSIDE loops or in the top most loop it can
    //be done to avoid lag.

    public void teamEliminated(int Team){
        //To be called when a team's queen is killed
    }
    public void playerEliminated(Player player, int Team){
        //To be called when a player is eliminated (The code for this is in QueenManager.onPlayerDeath())

        //This will also need to contain logic to determine when a team is completely out (No players left). You can get a full
        //list of team members from teamsDB. See other classes for usage.
    }

    public void onPortalScatter(){
        //To be called on a portal scatter (GameThread.portalScatter()).
        //This should update the location of the player's portal.
    }
}
