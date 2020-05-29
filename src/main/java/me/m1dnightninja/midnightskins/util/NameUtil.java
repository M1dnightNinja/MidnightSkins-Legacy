package me.m1dnightninja.midnightskins.util;

import com.mojang.authlib.GameProfile;
import org.bukkit.entity.*;

import java.util.*;

public class NameUtil {

    private static Map<Player, String> oldNames = new HashMap<>();

    public static GameProfile setName(GameProfile g, String name) {

        if(name.length() > 16) {
            name = name.substring(0,15);
        }

        GameProfile newProf = new GameProfile(g.getId(), name);
        if(!g.getProperties().isEmpty()) {
            newProf.getProperties().putAll(g.getProperties());
        }

        return newProf;
    }

    public static String getOldName(Player p) {
        if(oldNames.containsKey(p)) {
            return oldNames.get(p);
        } else {
            return p.getName();
        }
    }

}
