package net.stardevelopments.throneworlds.weapons;

import net.stardevelopments.throneworlds.Main;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Objects;

public class MagicMirror extends TWAbility implements Listener {

    String name = "Magic Mirror";
    int cost = Main.plugin.getConfig().getInt("MagicMirror", 4);

    @Override
    public ItemStack getItem(){

        ItemStack magicMirror = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);

        ItemMeta magicMirrorMeta = magicMirror.getItemMeta();
        magicMirrorMeta.setDisplayName(name);

        ArrayList<String> lore = new ArrayList<>();
        lore.add("Returns you to your throne world");
        magicMirrorMeta.setLore(lore);

        magicMirror.setItemMeta(magicMirrorMeta);

        return magicMirror;

    }
    @EventHandler
    public void onUse(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack mirror = e.getItem();

        try{
            mirror.getType();
        }catch (NullPointerException exception){
            return;
        }

        if (mirror.getType() == Material.WHITE_STAINED_GLASS_PANE){
                if (player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("Magic Mirror")) {

                    player.teleport(player.getBedSpawnLocation());

                }
        }
    }

    @Override
    public String getName() { return name; }

    @Override
    public int getCost() { return cost;}
}
