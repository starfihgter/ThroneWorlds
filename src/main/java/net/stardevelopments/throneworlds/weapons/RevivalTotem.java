package net.stardevelopments.throneworlds.weapons;

import net.stardevelopments.throneworlds.Main;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class RevivalTotem extends TWAbility implements Listener {

    public RevivalTotem() {
        super("Revival Totem", Material.TOTEM_OF_UNDYING,1,"Utter the phrase idk this is a placeholder");
    }

    @Override
    public ItemStack getItem() {
        return null;
    }

}
