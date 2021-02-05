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

    String name = "Poison Shank";
    int cost = Main.plugin.getConfig().getInt("PoisonShank", 4);

    @Override
    public ItemStack getItem(){

        ItemStack poisonShank = new ItemStack(Material.STICK);

        ItemMeta poisonShankMeta = poisonShank.getItemMeta();
        poisonShankMeta.setDisplayName(name);

        ArrayList<String> lore = new ArrayList<>();
        lore.add("§fPoisons your opponent");
        lore.add("§eThis item costs " + cost + " essence!");
        poisonShankMeta.setLore(lore);

        poisonShank.setItemMeta(poisonShankMeta);

        return poisonShank;
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

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getCost() {
        return cost;
    }
}
