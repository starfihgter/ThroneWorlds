package net.stardevelopments.throneworlds.Bow;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;

public class TntBow implements Listener {

    public ItemStack getTntBow(){
        ItemStack tntBow = new ItemStack(Material.BOW);
        tntBow.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 999);

        ItemMeta tntBowMeta = tntBow.getItemMeta();
        tntBowMeta.setDisplayName("TNT Bow");

        ArrayList<String> lore = new ArrayList<>();
        lore.add("Arrows detonate on impact");
        tntBowMeta.setLore(lore);

        tntBow.setItemMeta(tntBowMeta);

        return tntBow;
    }

    @EventHandler
    public void onShot(ProjectileHitEvent e){

        if(e.getEntity().getShooter() instanceof Player){

            Player player = (Player) e.getEntity().getShooter();

            ArrayList<String> tntBowLore = new ArrayList<>();
            tntBowLore.add("Arrows detonate on impact");

            if(player.getInventory().getItemInMainHand().getItemMeta().equals(tntBowLore)){

                Location location = e.getEntity().getLocation();
                location.getWorld().createExplosion(location, 3);
                e.getEntity().remove();

            }


        }

    }
}
