package net.stardevelopments.throneworlds.weapons;

import net.stardevelopments.throneworlds.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class Scaffolding extends TWAbility {
    public Scaffolding() {
        super("Scaffolding");
    }

    public ItemStack getItem() {
        ItemStack scaffold = new ItemStack(Material.SCAFFOLDING, 10);
        Main.setItemName(scaffold, name, Arrays.asList("§fCan be placed anywhere!", "§eThis item costs " + getCost() + " essence!"));
        return scaffold;
    }
}
