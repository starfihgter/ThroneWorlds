package net.stardevelopments.throneworlds.weapons;

import net.stardevelopments.throneworlds.Main;
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

    public WitherBow() {
        super("Wither Bow");
    }

    public ItemStack getItem(){
        ItemStack tntBow = new ItemStack(Material.BOW);
        tntBow.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 999);

        ItemMeta tntBowMeta = tntBow.getItemMeta();
        tntBowMeta.setDisplayName(name);

        ArrayList<String> lore = new ArrayList<>();
        lore.add("§fSingle use item - Creates a wither where your arrow lands.");
        lore.add("§eThis item costs " + getCost() + " essence!");
        tntBowMeta.setLore(lore);

        tntBow.setItemMeta(tntBowMeta);

        return tntBow;
    }

    @EventHandler
    public void onShot(ProjectileHitEvent e){

        if(e.getEntity().getShooter() instanceof Player){

            Player player = (Player) e.getEntity().getShooter();

            if(player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(getName())){

                Location location = e.getEntity().getLocation();
                location.getWorld().spawnEntity(location, EntityType.WITHER);
                player.getInventory().remove(player.getInventory().getItemInMainHand());
                e.getEntity().remove();
            }


        }

    }
}
