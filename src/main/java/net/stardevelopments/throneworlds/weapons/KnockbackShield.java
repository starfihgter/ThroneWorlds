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
import java.util.ArrayList;

public class KnockbackShield extends TWAbility implements Listener {

    String name = "Knockback Shield";
    int cost = Main.plugin.getConfig().getInt("KBShield", 4);

    @Override
    public ItemStack getItem(){

        ItemStack knockbackShield = new ItemStack(Material.SHIELD);

        ItemMeta knockbackShieldMeta = knockbackShield.getItemMeta();
        knockbackShieldMeta.setDisplayName(name);

        ArrayList<String> lore = new ArrayList<>();
        lore.add("§fBlocking damage knockbacks attacker");
        lore.add("§eThis item costs " + cost + " essence!");
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

            if(player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(name)){

                if(player.isBlocking()){

                    entity1.setVelocity(player.getLocation().getDirection().multiply(2));

                }
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
