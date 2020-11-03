package net.stardevelopments.throneworlds.commands;

import net.stardevelopments.throneworlds.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TeamsCommand implements CommandExecutor {
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
            if (sender instanceof Player) {
                if (args.length == 2) {
                    switch (args[0]) {
                        case "list": {
                            String team = args[1];
                            String memberList = String.join(", ", teamsDB.getStringList("team" + team + ".members"));
                            sender.sendMessage(memberList);
                            break;
                        }
                        case "join": {
                            String team = args[1];
                            List<String> teamList = teamsDB.getStringList("team" + team + ".members");
                            teamList.add(sender.getName());
                            teamsDB.set("team" + team + ".members", teamList);
                            sender.sendMessage("You have joined team " + team + "!");
                            break;
                        }
                        case "leave": {
                            String team = args[1];
                            List<String> teamList = teamsDB.getStringList("team" + team + ".members");
                            teamList.remove(sender.getName());
                            teamsDB.set("team" + team + ".members", teamList);
                            sender.sendMessage("You have left team " + team + "!");
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
            } else {
                sender.sendMessage("This command can only be used by players!");
                return false;
            }
    }
}
