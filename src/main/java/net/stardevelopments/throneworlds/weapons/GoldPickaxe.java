package net.stardevelopments.throneworlds.weapons;

import net.stardevelopments.throneworlds.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class GoldPickaxe extends TWAbility {
    public GoldPickaxe() {
        super("Gold Pickaxe");
    }

    public ItemStack getItem() {
        ItemStack TNT = new ItemStack(Material.GOLDEN_PICKAXE, 1);
        Main.setItemName(TNT, getName(), Arrays.asList("§fQuick but fragile", "§eThis item costs " + getCost() + " essence!"));
        return TNT;
    }
}
