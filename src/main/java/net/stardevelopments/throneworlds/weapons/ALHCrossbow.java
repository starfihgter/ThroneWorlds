package net.stardevelopments.throneworlds.weapons;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class ALHCrossbow implements Listener {

    public static ItemStack getALHCrossbow(){

        ItemStack alhCrossbow = new ItemStack(Material.CROSSBOW);

        ItemMeta alhcrossbowMeta = alhCrossbow.getItemMeta();
        alhcrossbowMeta.setDisplayName("ALH Crossbow");

        ArrayList<String> lore = new ArrayList<>();
        lore.add("Crossbow automatically reload");
        alhcrossbowMeta.getLore();

        alhCrossbow.setItemMeta(alhcrossbowMeta);

        return alhCrossbow;
    }
    @EventHandler
    public void onUnload()


}
