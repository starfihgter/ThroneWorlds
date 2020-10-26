package net.stardevelopments.throneworlds.weapons;

import org.bukkit.Material;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Objects;

public class FireBallWand implements Listener {

    public static ItemStack getFireballStaff(){

        ItemStack fireballStaff = new ItemStack(Material.BLAZE_ROD);

        ItemMeta fireballStaffMeta = fireballStaff.getItemMeta();
        fireballStaffMeta.setDisplayName("Fireball Staff");

        ArrayList<String> lore = new ArrayList<>();
        lore.add("Shoots fireballs");
        fireballStaffMeta.setLore(lore);

        fireballStaff.setItemMeta(fireballStaffMeta);

        return fireballStaff;
    }

    @EventHandler
    public void onUse(PlayerInteractEvent e) {
        Player player = (Player) e.getPlayer();
        ItemStack staff = e.getItem();

        try{
            staff.getType();
        }catch (NullPointerException excep){
            return;
        }

        if (staff.getType() == Material.getMaterial("BLAZE_ROD")){
            if (Objects.requireNonNull(e.getItem()).getType() == Material.getMaterial("BLAZE_ROD")) {
                if (player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("Fireball Staff")) {

                    player.launchProjectile(Fireball.class);

                }
            }
        }
    }
}
