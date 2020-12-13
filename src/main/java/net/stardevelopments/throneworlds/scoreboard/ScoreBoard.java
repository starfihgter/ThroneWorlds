package net.stardevelopments.throneworlds.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class ScoreBoard {

    ScoreboardManager manager = Bukkit.getScoreboardManager();
    Scoreboard sb = manager.getNewScoreboard();

    Objective objective = sb.registerNewObjective("throneWorlds", "dummy", ChatColor.BLUE + "THRONE WORLDS");
    objective.setDisplaySlot(DisplaySlot.SIDEBAR)


}
