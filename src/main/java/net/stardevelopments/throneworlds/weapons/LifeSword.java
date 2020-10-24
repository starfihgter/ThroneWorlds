package net.stardevelopments.throneworlds.weapons;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;

public class LifeSword implements Listener {

    public static ItemStack getLifeSword(){

        ItemStack lifeSword = new ItemStack(Material.IRON_SWORD);

        ItemMeta lifeSwordMeta = lifeSword.getItemMeta();
        lifeSwordMeta.setDisplayName("Life Stealer");

        ArrayList<String> lore = new ArrayList<>();
        lore.add("Steal life from your opponent");
        lifeSwordMeta.setLore(lore);

        lifeSword.setItemMeta(lifeSwordMeta);

        return lifeSword;
    }

    @EventHandler
    public void onshot(EntityDamageByEntityEvent e){

        if(e.getDamager() instanceof Player){

            Player player = (Player) e.getDamager();

            if(player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("Life Stealer")){

                player.setHealth(player.getHealth() + 2);

            }
        }
    }

}
