package net.stardevelopments.throneworlds.weapons;

import net.stardevelopments.throneworlds.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class TWAbility {
    String name;
    Material material;
    int num;
    String lore;

    public TWAbility(String name, Material material, int num, String lore){
        this.name = name;
        this.material = material;
        this.num = num;
        this.lore = lore;
    }

    //Abstracted stack return (some classes will override)
    public ItemStack getItem(){
        ItemStack item = new ItemStack(material, num);
        Main.setItemName(item, getName(), Arrays.asList("§f" + lore, "§eThis item costs " + getCost() + " essence!"));
        return item;

    }

    //abstracted name return
    public String getName(){return name;};

    //abstracted cost return
    public int getCost(){
        String configName = name.replace(" ","-");
        return Main.plugin.getConfig().getInt(configName, -1);
    };
}
