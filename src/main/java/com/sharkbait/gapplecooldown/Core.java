package com.sharkbait.gapplecooldown;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public final class Core extends JavaPlugin implements Listener {

    private HashMap<Player, Integer> cooldownTime;
    private HashMap<Player, BukkitRunnable> cooldownTask;
    
    private HashMap<Player, Integer> normalAppleTime;
    private HashMap<Player, BukkitRunnable> normalAppleTask;

    @Override
    public void onEnable() {
        cooldownTime = new HashMap<>();
        cooldownTask = new HashMap<>();
        
        normalAppleTime = new HashMap<>();
        normalAppleTask = new HashMap<>();

        getConfig().options().copyDefaults(true);
        saveConfig();

        getServer().getPluginManager().registerEvents(this, this);

        Bukkit.getConsoleSender().sendMessage("§aGappleCooldown enabled !");
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("§cGappleCooldown disabled !");
    }

    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent event){
        Player player = event.getPlayer();

        if(event.getItem().equals(new ItemStack(Material.getMaterial(322), 1, (short) 1))){
            if(cooldownTime.containsKey(player)){
                event.setCancelled(true);
                player.sendMessage(getConfig().getString("mustWaitGodGapple").replace("&", "§").replace("{time}", String.valueOf(cooldownTime.get(player))));
            }
            else{
                event.setCancelled(false);
                cooldownTime.put(player, getConfig().getInt("cooldownTime"));
                cooldownTask.put(player, new BukkitRunnable() {
                    public void run() {
                        cooldownTime.put(player, cooldownTime.get(player) - 1);
                        if (cooldownTime.get(player) == 0) {
                            cooldownTime.remove(player);
                            cooldownTask.remove(player);
                            cancel();
                        }
                    }
                });

                cooldownTask.get(player).runTaskTimer(this, 20, 20);
            }
        }
        else if(event.getItem().getType()==Material.GOLDEN_APPLE){
            if(normalAppleTime.containsKey(player)){
                event.setCancelled(true);
                player.sendMessage(getConfig().getString("mustWaitNormalGapple").replace("&", "§").replace("{time}", String.valueOf(normalAppleTime.get(player))));
            }
            else{
                event.setCancelled(false);
                normalAppleTime.put(player, getConfig().getInt("normalGappleCooldownTime"));
                normalAppleTask.put(player, new BukkitRunnable() {
                    public void run() {
                        normalAppleTime.put(player, normalAppleTime.get(player) - 1);
                        if (normalAppleTime.get(player) == 0) {
                            normalAppleTime.remove(player);
                            normalAppleTask.remove(player);
                            cancel();
                        }
                    }
                });

                normalAppleTask.get(player).runTaskTimer(this, 20, 20);
            }
        }
        
        
    }
}
