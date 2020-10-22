package net.stardevelopments.throneworlds;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiversePortals.MultiversePortals;
import net.stardevelopments.throneworlds.weapons.TntBow;
import net.stardevelopments.throneworlds.essence.Essence;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    public static FileConfiguration config;
    public static Plugin plugin;
    public static FileLoader teamsDB;
    public static FileLoader worldState;
    public MVWorldManager wm;
    public MultiversePortals pm;
    GameThread gt;
    QueenManager qm;

    @Override
    public void onEnable() {
        plugin = this;
        config = this.getConfig();
        teamsDB = new FileLoader("teamsDB.yml");
        worldState = new FileLoader("WorldState.yml");
        teamsDB.reloadUserRecord();
        worldState.reloadUserRecord();

        MultiverseCore mvc = (MultiverseCore) Bukkit.getPluginManager().getPlugin("Multiverse-Core");
        pm = (MultiversePortals) Bukkit.getPluginManager().getPlugin("Multiverse-Portals");
        if (mvc == null || pm == null){
            System.out.println("Multiverse-Core and Multiverse-Portals were not detected. Both are required to run this plugin. Disabling Throneworlds.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        wm = mvc.getMVWorldManager();


        qm = new QueenManager();
        gt = new GameThread(this, qm);

        getCommand("startgame").setExecutor(gt);
        getCommand("teams").setExecutor(new TeamsCommand());

        int gameState = worldState.getUserRecord().getInt("GameState", 0);
        System.out.println("Throne worlds has started in state " + gameState);

        getServer().getPluginManager().registerEvents(new TntBow(), this);
        getServer().getPluginManager().registerEvents(new Essence(), this);
        getServer().getPluginManager().registerEvents(qm, this);
    }

    @Override
    public void onDisable() {
        teamsDB.saveCustomConfig();
        plugin.saveConfig();
        worldState.saveCustomConfig();
        System.out.println("Saved files!");
    }

    public static ItemStack setItemName(ItemStack item, String name){
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

}
