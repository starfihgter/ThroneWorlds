package net.stardevelopments.throneworlds.weapons;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class PoisonShank implements Listener {

    public static ItemStack getPoisonShank(){

        ItemStack poisonShank = new ItemStack(Material.STICK);

        ItemMeta poisonShankMeta = poisonShank.getItemMeta();
        poisonShankMeta.setDisplayName("Poison Shank");

        ArrayList<String> lore = new ArrayList<>();
        lore.add("Poisons your opponent");
        poisonShankMeta.setLore(lore);

        poisonShank.setItemMeta(poisonShankMeta);

        return poisonShank;
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e){
        if(e instanceof Player) {
            Player player = (Player) e.getDamager();
            Player hit = (Player) e;
            if (e.getDamager() instanceof Player) {

                if (player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("Poison Shank")) {

                    hit.addPotionEffect(new PotionEffect(PotionEffectType.POISON,10,1));
                    player.getInventory().remove(player.getInventory().getItemInMainHand());

                }
            }
        }

    }
}
