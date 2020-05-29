package me.m1dnightninja.midnightskins.updater;

import org.bukkit.entity.Player;

public interface Updater {

    boolean isLoaded();
    void updatePlayer(Player player);
    void updatePlayerSelf(Player player);
    void updatePlayerOther(Player player, Player other);
    void updatePlayerOther(Player player, Player other, String oldName);

}
