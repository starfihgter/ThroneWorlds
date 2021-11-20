package net.stardevelopments.throneworlds.weapons;

import net.stardevelopments.throneworlds.Main;
import org.bukkit.inventory.ItemStack;

public abstract class TWAbility {
    String name;

    public TWAbility(String name){
        this.name = name;
    }

    public abstract ItemStack getItem();

    //abstracted name return
    public String getName(){return name;};

    //abstracted cost return
    public int getCost(){
        String configName = name.replace(" ","-");
        return Main.plugin.getConfig().getInt(configName, -1);
    };
}
