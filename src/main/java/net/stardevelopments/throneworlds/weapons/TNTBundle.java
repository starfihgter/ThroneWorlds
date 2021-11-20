package net.stardevelopments.throneworlds.weapons;

import net.stardevelopments.throneworlds.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class TNTBundle extends TWAbility {
    public TNTBundle() {
        super("TNT Bundle");
    }

    @Override
    public ItemStack getItem() {
        ItemStack TNT = new ItemStack(Material.TNT, 10);
        Main.setItemName(TNT, name, Arrays.asList("§f10 blocks of TNT", "§eThis item costs " + getCost() + " essence!"));
        return TNT;
    }
}
