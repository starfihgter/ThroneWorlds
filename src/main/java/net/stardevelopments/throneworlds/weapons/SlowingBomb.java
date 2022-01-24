package net.stardevelopments.throneworlds.weapons;


import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SlowingBomb extends TWAbility implements Listener {
    public SlowingBomb() {
        super("Curse of Chains", Material.CHAIN, 1, "Apply a slowing effect to all nearby players.");
    }

    @EventHandler
    public void onUse(PlayerInteractEvent e){
        Player player = e.getPlayer();
        ItemStack item = e.getItem();

        try{
            item.getType();
        }catch (NullPointerException exception){
            return;
        }


        if (item.getType() == material && item.getItemMeta().getDisplayName().equals(name)){
            player.getInventory().remove(getItem());
            for (Player aboutToBeSlowedPlayer : player.getWorld().getPlayers()){
                if (!aboutToBeSlowedPlayer.getName().equals(player.getDisplayName())){
                    aboutToBeSlowedPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 600, 3));
                    aboutToBeSlowedPlayer.sendTitle("§4Curse of Chains","§cYou have been slowed for 30 seconds by " + player.getDisplayName());
                }
            }
        }
    }
}
