package net.stardevelopments.throneworlds.essence;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Essence implements Listener {
    public ItemStack getEssence(){

        ItemStack essence = new ItemStack(Material.EMERALD);
        ItemMeta essenceMeta = essence.getItemMeta();
        essenceMeta.setDisplayName("Essence");
        essence.setItemMeta(essenceMeta);

        return essence;
    }

    @EventHandler
    public void onDrop(EntityDeathEvent event){
        if (event.getEntity().getKiller() != null) {
            event.getDrops().add(getEssence());
        }
    }

}
