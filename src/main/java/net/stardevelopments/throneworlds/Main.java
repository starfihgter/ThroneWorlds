package net.stardevelopments.throneworlds;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import net.stardevelopments.throneworlds.Bow.TntBow;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    public static FileConfiguration config;
    public static Plugin plugin;
    public static FileLoader teamsDB;
    public static FileLoader worldState;
    public MVWorldManager wm;

    @Override
    public void onEnable() {
        plugin = this;
        config = this.getConfig();
        teamsDB = new FileLoader("teamsDB.yml");
        worldState = new FileLoader("WorldState.yml");
        teamsDB.reloadUserRecord();
        worldState.reloadUserRecord();

        MultiverseCore mvc = (MultiverseCore) Bukkit.getPluginManager().getPlugin("MultiverseCore");
        if (mvc == null){
            System.out.println("MultiverseCore was not detected. Disabling Throneworlds.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        wm = mvc.getMVWorldManager();

        getCommand("startgame").setExecutor(new GameStart(this));
        getCommand("teams").setExecutor(new TeamsCommand());

        int gameState = worldState.getUserRecord().getInt("GameState", 0);
        System.out.println("Throne worlds has started in state " + gameState);

        getServer().getPluginManager().registerEvents(new TntBow(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

}
