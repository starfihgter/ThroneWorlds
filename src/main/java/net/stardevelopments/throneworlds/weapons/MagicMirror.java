package net.stardevelopments.throneworlds.weapons;

import net.stardevelopments.throneworlds.GameThread;
import net.stardevelopments.throneworlds.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Objects;

public class MagicMirror extends TWAbility implements Listener {

    public MagicMirror() {
        super("Magic Mirror");
    }



    public ItemStack getItem(){

        ItemStack magicMirror = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);

        ItemMeta magicMirrorMeta = magicMirror.getItemMeta();
        magicMirrorMeta.setDisplayName(name);

        ArrayList<String> lore = new ArrayList<>();
        lore.add("§fReturns you to your throne world");
        lore.add("§eThis item costs " + getCost() + " essence!");
        magicMirrorMeta.setLore(lore);

        magicMirror.setItemMeta(magicMirrorMeta);

        return magicMirror;

    }

    @EventHandler
    public void onUse(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack mirror = e.getItem();

        try{
            mirror.getType();
        }catch (NullPointerException exception){
            return;
        }


        if (mirror.getType() == Material.WHITE_STAINED_GLASS_PANE){
            if (player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(name)) {
                player.sendMessage("The seam between realities grows weaker...");
                player.sendMessage("You will return to your Throne World in 20 Seconds");
                mirror.setAmount(mirror.getAmount() - 1);
                e.setCancelled(true);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.sendMessage("You were returned to your Throne World!");
                        try{player.teleport(player.getBedSpawnLocation());}catch(NullPointerException e){player.sendMessage("Unable to find Throne World signal");}
                    }
                }.runTaskLater(Main.plugin, 400);
            }
        }
    }
}
