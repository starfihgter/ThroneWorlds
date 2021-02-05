package net.stardevelopments.throneworlds.weapons;

import net.stardevelopments.throneworlds.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class TNTBundle extends TWAbility {
    @Override
    public ItemStack getItem() {
        ItemStack TNT = new ItemStack(Material.TNT, 10);
        Main.setItemName(TNT, getName(), Arrays.asList("§f10 blocks of TNT", "§eThis item costs " + getCost() + " essence!"));
        return TNT;
    }

    @Override
    public String getName() {
        return "TNT Bundle";
    }

    @Override
    public int getCost() {
        return Main.plugin.getConfig().getInt("TNTBundle", 4);
    }
}
