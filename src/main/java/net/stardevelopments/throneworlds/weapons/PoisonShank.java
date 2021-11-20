package net.stardevelopments.throneworlds.weapons;

import net.stardevelopments.throneworlds.Main;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class PoisonShank extends TWAbility implements Listener {
    public PoisonShank() {
        super("Poison Shank",Material.STICK,1,"Inflict poison upon thy enemies.");
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e){
        Entity entity = (Entity) e.getEntity();
        if(entity instanceof Player) {
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
