package net.stardevelopments.throneworlds.weapons;

import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiversePortals.MVPortal;
import com.onarandombox.MultiversePortals.MultiversePortals;
import com.onarandombox.MultiversePortals.utils.PortalManager;
import net.stardevelopments.throneworlds.GameThread;
import net.stardevelopments.throneworlds.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class PortalShutdownItem extends TWAbility implements Listener {
    Main mainPlugin;
    public PortalShutdownItem(Main passedPM) {
        super("Ascendant Disruptor", Material.IRON_BARS, 1, "Seals your portal for 1 minute!");
        this.mainPlugin = passedPM;
    }

    @EventHandler
    public void onUse(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack item = e.getItem();

        try{
            item.getType();
        }catch (NullPointerException exception){
            return;
        }


        if (item.getType() == Material.IRON_BARS){
            FileConfiguration teamsDB = Main.teamsDB.getUserRecord();
            MultiversePortals MVP = mainPlugin.pm;
            PortalManager pm = MVP.getPortalManager();
            int team = GameThread.getPlayerTeam(player);
            int x = teamsDB.getInt("team" + team + ".portal.x");
            int y = teamsDB.getInt("team" + team + ".portal.y");
            int z = teamsDB.getInt("team" + team + ".portal.z");
            //Replace portal blocks with filler for 1 minute, announce and set timer to undo in 1 minute.
            World overWorld = Bukkit.getWorld("Overworld");
            Material fillMaterial = Material.BEDROCK;
            overWorld.getBlockAt(x+1, y + 1, z).setType(fillMaterial);
            overWorld.getBlockAt(x+1, y + 2, z).setType(fillMaterial);
            overWorld.getBlockAt(x+1, y + 3, z).setType(fillMaterial);
            overWorld.getBlockAt(x+2, y + 1, z).setType(fillMaterial);
            overWorld.getBlockAt(x+2, y + 2, z).setType(fillMaterial);
            overWorld.getBlockAt(x+2, y + 3, z).setType(fillMaterial);
            //Announce
            String teamName = teamsDB.getString("team" + team + ".name");
            Bukkit.getServer().broadcastMessage(teamName + " have sealed their portal for 1 minute!");
            player.getInventory().remove(getItem());

            new BukkitRunnable() {
                @Override
                public void run() {
                    int x = teamsDB.getInt("team" + team + ".portal.x");
                    int y = teamsDB.getInt("team" + team + ".portal.y");
                    int z = teamsDB.getInt("team" + team + ".portal.z");
                    Material fillMaterial = Material.END_GATEWAY;
                    overWorld.getBlockAt(x+1, y + 1, z).setType(fillMaterial);
                    overWorld.getBlockAt(x+1, y + 2, z).setType(fillMaterial);
                    overWorld.getBlockAt(x+1, y + 3, z).setType(fillMaterial);
                    overWorld.getBlockAt(x+2, y + 1, z).setType(fillMaterial);
                    overWorld.getBlockAt(x+2, y + 2, z).setType(fillMaterial);
                    overWorld.getBlockAt(x+2, y + 3, z).setType(fillMaterial);
                    Bukkit.getServer().broadcastMessage(teamName + "'s portal is now active again!");
                }
            }.runTaskLater(Main.plugin, 400);//change time later
        }
    }
}
