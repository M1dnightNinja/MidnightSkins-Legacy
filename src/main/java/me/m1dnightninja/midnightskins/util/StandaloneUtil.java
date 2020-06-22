package me.m1dnightninja.midnightskins.util;


import me.m1dnightninja.midnightskins.api.Skin;
import me.m1dnightninja.midnightskins.standalone.MidnightSkins;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class StandaloneUtil {

    private static MidnightSkins skins = MidnightSkins.getInstance();

    public static void enable() {
        skins.enable();
    }

    public static void disable() {
        skins.disable();
    }

    public static JavaPlugin getInstance() {
        return skins;
    }

    public static boolean isEnabled() {
        return skins.isLibraryEnabled();
    }

    public static void setPlayerSkin(Player p, Skin s) {
        skins.setPlayerSkin(p, s);
    }

    // Resets the given player's skin to default. This method does not update the player's skin for other players.
    public static void resetPlayerSkin(Player p) {
        skins.resetPlayerSkin(p);
    }

    // Sets a player's name to the given text. This method does not update the player's name for other players.
    public static void setPlayerName(Player p, final String name) {
        skins.setPlayerName(p, name);
    }

    // Resets a player's name to default.
    public static void resetPlayerName(Player p) {
        skins.resetPlayerName(p);
    }

    // Resets a player's name and skin
    public static void resetPlayer(Player p) {
        skins.resetPlayer(p);
    }

    // Update a player's skin and name for all other players on the server.
    public static void updatePlayer(Player p) {
        skins.updatePlayer(p);
    }

    // Update a player's skin and name for another player on the server.
    public static void updatePlayer(Player p, Player o) {
        skins.updatePlayer(p, o);
    }

    // Returns the skin data for a player's currently applied skin.
    public static Skin getCurrentSkin(Player p) {
        return skins.getCurrentSkin(p);
    }

    // Returns the skin data for a player's skin they logged in with.
    public static Skin getOriginalSkin(Player p) {
        return skins.getOriginalSkin(p);
    }

    // Returns the currently applied name of a player
    public static String getCurrentName(Player p) {
        return skins.getCurrentName(p);
    }

    // Returns the name that a player logged in with
    public static String getOriginalName(Player p) {
        return skins.getOriginalName(p);
    }

    // Returns the skin data of a player using Mojang's server
    public static Skin getSkinFromWeb(UUID u) {
        return skins.getSkinFromWeb(u);
    }

}
