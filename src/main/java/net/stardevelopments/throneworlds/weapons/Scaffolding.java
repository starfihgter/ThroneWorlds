package net.stardevelopments.throneworlds.weapons;

import net.stardevelopments.throneworlds.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class Scaffolding extends TWAbility {
    @Override
    public ItemStack getItem() {
        ItemStack scaffold = new ItemStack(Material.SCAFFOLDING, 10);
        Main.setItemName(scaffold, getName(), Arrays.asList("Can be placed anywhere!", "This item costs " + getCost() + " essence!"));
        return scaffold;
    }

    @Override
    public String getName() {
        return "Scaffolding";
    }

    @Override
    public int getCost() {
        return 20;
    }
}
