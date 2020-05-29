package me.m1dnightninja.midnightskins;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;

public class MainListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if(PlayerData.getPlayerData(event.getPlayer()).getCustomSkin() != null) {
                    MidnightSkins.getInstance().updatePlayer(event.getPlayer());
                }
                for(Player p : Bukkit.getOnlinePlayers()) {
                    MidnightSkins.getInstance().updatePlayer(p, event.getPlayer());
                }
            }
        }.runTask(MidnightSkins.getInstance().plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLeave(PlayerQuitEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                PlayerData.purge(event.getPlayer());
            }
        }.runTask(MidnightSkins.getInstance().plugin);
    }
}
