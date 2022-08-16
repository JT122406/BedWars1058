package com.andrei1058.bedwars.gensplit;

import com.andrei1058.bedwars.api.events.player.PlayerGeneratorCollectEvent;
import com.andrei1058.bedwars.arena.Arena;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GenSplit implements Listener {
    @EventHandler
    public void onIngotPickup(PlayerGeneratorCollectEvent e) {
        if (!e.isCancelled() && e.getItem() != null && e.getItemStack().getType() == Material.GOLD_INGOT && e.getPlayer() != null) {
            Location pl = e.getPlayer().getLocation();
            Player p = e.getPlayer();  //Player Picking up the gold
            List<Entity> nearbyEntities2 = (List<Entity>)pl.getWorld().getNearbyEntities(pl, 3.0D, 3.0D, 3.0D);
            for (Player en : pl.getWorld().getPlayers()) {
                if (nearbyEntities2.contains(en)  && en.getUniqueId() != p.getUniqueId()) {
                    if (Arena.getArenaByPlayer(en).getTeam(p) == Arena.getArenaByPlayer(en).getTeam(en)) {
                        ItemStack gold = new ItemStack(e.getItemStack().getType());
                        gold.setAmount(e.getItemStack().getAmount());
                        en.getInventory().addItem(gold);
                        //Use Version Support here
                        if (Bukkit.getServer().getClass().getPackage().getName().contains("v1_8"))
                            en.playSound(en.getLocation(), Sound.valueOf("ITEM_PICKUP"), 0.8F, 1.0F);
                        else
                            en.playSound(en.getLocation(), Sound.valueOf("ENTITY_ITEM_PICKUP"), 0.8F, 1.0F);
                    }
                }
            }
        } else if (!e.isCancelled() && e.getItem() != null && e.getItemStack().getType() == Material.IRON_INGOT && e.getPlayer() != null) {
            Location pl = e.getPlayer().getLocation();
            Player p = e.getPlayer(); //Player Picking up the iron
            List<Entity> nearbyEntities2 = (List<Entity>)pl.getWorld().getNearbyEntities(pl, 3.0D, 3.0D, 3.0D);
            for (Player en : pl.getWorld().getPlayers()) {
                if (nearbyEntities2.contains(en) && en.getUniqueId() != p.getUniqueId()) {
                    if (Arena.getArenaByPlayer(en).getTeam(p) == Arena.getArenaByPlayer(en).getTeam(en)) {
                        ItemStack iron = new ItemStack(e.getItemStack().getType());
                        iron.setAmount(e.getItemStack().getAmount());
                        en.getInventory().addItem(iron);
                        //Use Version Support here
                        if (Bukkit.getServer().getClass().getPackage().getName().contains("v1_8"))
                            en.playSound(en.getLocation(), Sound.valueOf("ITEM_PICKUP"), 0.8F, 1.0F);
                        else
                            en.playSound(en.getLocation(), Sound.valueOf("ENTITY_ITEM_PICKUP"), 0.8F, 1.0F);
                    }
                }
            }
        }
    }
}

