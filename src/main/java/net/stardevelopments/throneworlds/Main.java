package net.stardevelopments.throneworlds;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiversePortals.MultiversePortals;
import net.stardevelopments.throneworlds.commands.BorderCommand;
import net.stardevelopments.throneworlds.commands.ScatterCommand;
import net.stardevelopments.throneworlds.commands.TeamsCommand;
import net.stardevelopments.throneworlds.weapons.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class Main extends JavaPlugin {
    public static FileConfiguration config;
    public static Plugin plugin;
    public static FileLoader teamsDB;
    public static FileLoader worldState;
    public MVWorldManager wm;
    public MultiversePortals pm;
    GameThread gt;
    QueenManager qm;
    static ScoreBoardManager sb;
    PlayerManager playerManager;

    @Override
    public void onEnable() {

        //Save default plugin files
        plugin = this;
        plugin.saveResource("config.yml", false);
        plugin.saveResource("teamsDB.yml", false);
        plugin.saveResource("WorldState.yml", false);
        config = this.getConfig();
        teamsDB = new FileLoader("teamsDB.yml");
        worldState = new FileLoader("WorldState.yml");
        teamsDB.reloadUserRecord();
        worldState.reloadUserRecord();

        //Check for Multiverse Core and Multiverse Portals, and register MVWorldManager (wm) and Portal Manager (pm)
        MultiverseCore mvc = (MultiverseCore) Bukkit.getPluginManager().getPlugin("Multiverse-Core");
        pm = (MultiversePortals) Bukkit.getPluginManager().getPlugin("Multiverse-Portals");
        if (mvc == null || pm == null){
            System.out.println("Multiverse-Core and Multiverse-Portals were not detected. Both are required to run this plugin. Disabling Throneworlds.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        wm = mvc.getMVWorldManager();

        //Create static QueenManager and GameThread Objects
        qm = new QueenManager(this);
        gt = new GameThread(this, qm);
        sb = new ScoreBoardManager(this);
        playerManager = new PlayerManager(this);

        //Command Registration
        getCommand("startgame").setExecutor(gt);
        getCommand("setteam").setExecutor(new TeamsCommand());
        getCommand("scatter").setExecutor(new ScatterCommand(gt));
        getCommand("border").setExecutor(new BorderCommand(this, gt));

        int gameState = worldState.getUserRecord().getInt("GameState", 0);
        System.out.println("Throne worlds has started in state " + gameState);

        //Class Listener Registration
        getServer().getPluginManager().registerEvents(new TntBow(), this);
        //getServer().getPluginManager().registerEvents(new LifeSword(), this); // These classes are bugged, throw exceptions on entity hit.
        //getServer().getPluginManager().registerEvents(new PoisonShank(), this);
        //getServer().getPluginManager().registerEvents(new KnockbackShield(), this);
        getServer().getPluginManager().registerEvents(new FireBallWand(), this);
        getServer().getPluginManager().registerEvents(new WitherBow(), this);
        getServer().getPluginManager().registerEvents(new Essence(this), this);
        getServer().getPluginManager().registerEvents(qm, this);
        getServer().getPluginManager().registerEvents(new BuildingCheck(this), this);
        getServer().getPluginManager().registerEvents(new MagicMirror(), this);
        getServer().getPluginManager().registerEvents(playerManager, this);

    }

    @Override
    public void onDisable() {
        //Save files
        teamsDB.saveCustomConfig();
        plugin.saveConfig();
        worldState.saveCustomConfig();
        System.out.println("Saved files!");
    }

    public static ItemStack setItemName(ItemStack item, String name, List<String> lore){
      
        //Simple method to set item name and lore
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);

        if (lore != null) {
            meta.setLore(lore);
        }

        item.setItemMeta(meta);
        return item;
    }

}
