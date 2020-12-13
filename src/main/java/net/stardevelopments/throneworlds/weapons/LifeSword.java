package net.stardevelopments.throneworlds.weapons;

import net.stardevelopments.throneworlds.Main;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;

public class LifeSword extends TWAbility implements Listener {

    String name = "Life Stealer";
    int cost = Main.plugin.getConfig().getInt("LifeSword", 4);

    @Override
    public ItemStack getItem(){

        ItemStack lifeSword = new ItemStack(Material.IRON_SWORD);

        ItemMeta lifeSwordMeta = lifeSword.getItemMeta();
        lifeSwordMeta.setDisplayName(name);

        ArrayList<String> lore = new ArrayList<>();
        lore.add("Steal life from your opponent");
        lore.add("This item costs " + cost + " essence!");
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

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getCost() {
        return cost;
    }

}
