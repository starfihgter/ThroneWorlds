package net.stardevelopments.throneworlds.weapons;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class WitherBow extends TWAbility implements Listener {

    String name = "Wither Bow";
    int cost = 10;

    public ItemStack getItem(){
        ItemStack tntBow = new ItemStack(Material.BOW);
        tntBow.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 999);

        ItemMeta tntBowMeta = tntBow.getItemMeta();
        tntBowMeta.setDisplayName(name);

        ArrayList<String> lore = new ArrayList<>();
        lore.add("You're a terrible person.");
        tntBowMeta.setLore(lore);

        tntBow.setItemMeta(tntBowMeta);

        return tntBow;
    }

    @EventHandler
    public void onShot(ProjectileHitEvent e){

        if(e.getEntity().getShooter() instanceof Player){

            Player player = (Player) e.getEntity().getShooter();

            ArrayList<String> tntBowLore = new ArrayList<>();
            tntBowLore.add("You're a terrible person.");

            if(player.getInventory().getItemInMainHand().getItemMeta().getLore().equals(tntBowLore)){

                Location location = e.getEntity().getLocation();
                location.getWorld().spawnEntity(location, EntityType.WITHER);
                player.getInventory().remove(player.getInventory().getItemInMainHand());
                e.getEntity().remove();
            }


        }

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getCost() {
        return cost;
    }
}