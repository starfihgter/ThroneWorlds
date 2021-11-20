package net.stardevelopments.throneworlds.weapons;

import net.stardevelopments.throneworlds.Main;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class ALHCrossbow extends TWAbility implements Listener {

    String name = "ALH Crossbow";
    int cost = Main.plugin.getConfig().getInt("ALHCrossbow", 4);

    @Override
    public ItemStack getItem(){

        ItemStack alhCrossbow = new ItemStack(Material.CROSSBOW);

        ItemMeta alhcrossbowMeta = alhCrossbow.getItemMeta();
        alhcrossbowMeta.setDisplayName(name);

        ArrayList<String> lore = new ArrayList<>();
        lore.add("§fCrossbow automatically reload");
        lore.add("§eThis item costs " + cost + " essence!");
        alhcrossbowMeta.getLore();

        alhCrossbow.setItemMeta(alhcrossbowMeta);

        return alhCrossbow;
    }
    @EventHandler
    public void onUnload(){}

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getCost() {
        return cost;
    }


}
