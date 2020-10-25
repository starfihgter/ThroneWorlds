package net.stardevelopments.throneworlds.weapons;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;

public class KnockbackShield implements Listener {

    public static ItemStack getKnockbackShield(){

        ItemStack knockbackShield = new ItemStack(Material.SHIELD);

        ItemMeta knockbackShieldMeta = knockbackShield.getItemMeta();
        knockbackShieldMeta.setDisplayName("Knockback Shield");

        ArrayList<String> lore = new ArrayList<>();
        lore.add("Blocking damage knockbacks attacker");
        knockbackShieldMeta.setLore(lore);

        knockbackShield.setItemMeta(knockbackShieldMeta);

        return knockbackShield;
    }

    @EventHandler
    public void onBlock(EntityDamageByEntityEvent e){
        Entity entity = (Entity) e.getEntity();
        Entity entity1 = (Entity) e.getDamager();

        if(entity instanceof Player){

            Player player = (Player) entity;

            if(player.isBlocking()){

                entity1.setVelocity(player.getLocation().getDirection().multiply(2));

            }

        }
    }
}
