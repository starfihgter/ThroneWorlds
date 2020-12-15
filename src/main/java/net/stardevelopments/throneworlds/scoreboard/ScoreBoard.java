package net.stardevelopments.throneworlds.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.*;

public class ScoreBoard {

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

    }

}
