package net.stardevelopments.throneworlds.weapons;

import net.stardevelopments.throneworlds.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;

public class DeathTotem extends TWAbility implements Listener {

    public DeathTotem() {
        super("Temporal Recaster", Material.WITHER_SKELETON_SKULL, 1, "Returns you to the location of your last death");
    }


    @Override
    public ItemStack getItem(){
        ItemStack item = new ItemStack(material, num);
        Main.setItemName(item, getName(), Arrays.asList("§f" + lore,"§dBlank Memory", "§eThis item costs " + getCost() + " essence!"));
        return item;
    }

    public void onPlayerDeath(Player player, Location deathLoc){
        Inventory inventory = player.getInventory();
        if (inventory.contains(Material.WITHER_SKELETON_SKULL)){
            inventory.remove(Material.WITHER_SKELETON_SKULL);
            ItemStack newTotem = new ItemStack(material, num);
            Main.setItemName(newTotem, getName(), Arrays.asList("§f" + lore,"§d x:" + Math.round(deathLoc.getX()) + ", y:" + Math.round(deathLoc.getY()) + ", z:" + Math.round(deathLoc.getZ()),"§d" + deathLoc.getWorld().getName()));
            inventory.addItem(newTotem);
            player.sendMessage("§dYour " + name + " has been updated!");
        }
    }

    @EventHandler
    public void onUse(PlayerInteractEvent e){
        //Check for totem and use
        if (e.getItem().getItemMeta().getDisplayName().equals(name)){
            e.setCancelled(true);
            String memory = e.getItem().getItemMeta().getLore().get(1);
            //Get location, extract proper location, and teleport
            if (memory.contains("x:")){
                memory = memory.replace("§d x:","");
                memory = memory.replace(" y:","");
                memory = memory.replace(" z:","");
                String[] coords = memory.split(",");
                int[] coordInts = new int[]{Integer.parseInt(coords[0]),Integer.parseInt(coords[1]),Integer.parseInt(coords[2])};
                Location destination = Bukkit.getServer()
                        .getWorld(e.getItem().getItemMeta().getLore().get(2))
                        .getBlockAt(coordInts[0],coordInts[1],coordInts[2])
                        .getLocation();
                Location destinationButHigher = Bukkit.getServer()
                        .getWorld(e.getItem().getItemMeta().getLore().get(2))
                        .getBlockAt(coordInts[0],coordInts[1],coordInts[2]+1)
                        .getLocation();
                //Start time for teleport.
                //Start particle effects
                Player player = e.getPlayer();
                player.sendMessage("Memory verified. You will return in 8 seconds.");
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.teleport(destination);
                        player.sendMessage("Memory Transition successful");
                    }
                }.runTaskLater(Main.plugin, 2400); //ADJUST THE TIMING
            }else {
                //If no memory is stored
                e.getPlayer().sendMessage("Your " + name + " has a blank memory! It will capture a memory upon death.");
            }
        }
        return;
    }
}