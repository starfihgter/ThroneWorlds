package net.stardevelopments.throneworlds.commands;

import net.stardevelopments.throneworlds.GameThread;
import net.stardevelopments.throneworlds.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TeamsCommand implements CommandExecutor {
    //I'm tired, and this code is self-explanatory and easy to read.
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration teamsDB = Main.teamsDB.getUserRecord();
        FileConfiguration worldState = Main.worldState.getUserRecord();

        int gameState = worldState.getInt("GameState", 0);
        if (gameState != 0) {
            sender.sendMessage("The game has already started!");
        }

        if (args.length < 1) {
            return false;
        }
                if (args.length == 2 || args.length == 3) {
                    boolean specificPlayer;
                    String playerName;
                    Player player;
                    String teamName = teamsDB.getString("team" + args[1] + ".name");
                    if (args.length == 3) {
                        playerName = args[2];
                        player = Bukkit.getPlayer(playerName);
                        if (player == null){
                            sender.sendMessage("Unable to find that player!");
                            return false;
                        }
                    }else {
                        playerName = sender.getName();
                        player = ((Player) sender).getPlayer();}

                    switch (args[0]) {
                        case "list": {
                            String team = args[1];
                            String memberList = String.join(", ", teamsDB.getStringList("team" + team + ".members"));
                            sender.sendMessage(memberList);
                            break;
                        }
                        case "join": {
                            String team = args[1];
                            //Check that player is not already assigned to a team.
                            if (GameThread.getPlayerTeam(player) == -1) {
                                List<String> teamList = teamsDB.getStringList("team" + team + ".members");
                                teamList.add(playerName);
                                teamsDB.set("team" + team + ".members", teamList);
                                sender.sendMessage(playerName + " has joined the " + teamName + "!");
                                player.sendMessage("You have joined the "+ teamName);
                            } else{
                                sender.sendMessage("That player is already on a team!");
                                player.sendMessage("You are already on a team!");
                            }
                            break;
                        }
                        case "leave": {
                            String team = args[1];
                            List<String> teamList = teamsDB.getStringList("team" + team + ".members");
                            teamList.remove(playerName);
                            teamsDB.set("team" + team + ".members", teamList);
                            sender.sendMessage("That player has left team " + team + "!");
                            player.sendMessage("You have left the "+ teamName);
                            break;
                        }
                        default:
                            sender.sendMessage("Unknown command!");
                            return false;
                    }
                    return true;
                } else{
                    sender.sendMessage("Please choose a subcommand!");
                    return false;
                }
    }
}
