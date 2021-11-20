package net.stardevelopments.throneworlds.weapons;

import net.stardevelopments.throneworlds.Main;
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

public class TntBow extends TWAbility implements Listener {

    public TntBow() {
        super("TNT Bow",Material.BOW,1,"Arrows detonate on impact.");
    }

    @EventHandler
    public void onShot(ProjectileHitEvent e){

        if(e.getEntity().getShooter() instanceof Player){

            Player player = (Player) e.getEntity().getShooter();

            if(player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(getName())){

                Location location = e.getEntity().getLocation();
                location.getWorld().createExplosion(location, 3);
                e.getEntity().remove();

            }


        }

    }
}
