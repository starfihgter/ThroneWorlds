package net.stardevelopments.throneworlds.weapons.blocksanditems;

import net.stardevelopments.throneworlds.Main;
import net.stardevelopments.throneworlds.weapons.TWAbility;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class Scaffolding extends TWAbility {
    public Scaffolding() {
        super("Scaffolding",Material.SCAFFOLDING,16,"Can be placed anywhere!");
    }
    //Code for scaffolding building exception is in BuildingCheck.java
}
