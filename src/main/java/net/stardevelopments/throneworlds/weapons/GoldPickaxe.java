package net.stardevelopments.throneworlds.weapons;

import net.stardevelopments.throneworlds.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class GoldPickaxe extends TWAbility {
    @Override
    public ItemStack getItem() {
        ItemStack TNT = new ItemStack(Material.GOLDEN_PICKAXE, 1);
        Main.setItemName(TNT, getName(), Arrays.asList("§fQuick but fragile", "§eThis item costs " + getCost() + " essence!"));
        return TNT;
    }

    @Override
    public String getName() {
        return "Golden Pickaxe";
    }

    @Override
    public int getCost() {
        return Main.plugin.getConfig().getInt("GP", 4);
    }
}
