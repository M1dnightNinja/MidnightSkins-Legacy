package me.m1dnightninja.midnightskins.util;

import com.mojang.authlib.*;
import com.mojang.authlib.properties.*;
import me.m1dnightninja.midnightskins.api.*;
import org.bukkit.entity.*;

import java.util.*;

public class SkinUtil {

    public static Skin getPlayerSkin(Player p) {
        GameProfile prof = ReflectionUtil.getPlayerProfile(p);
        if (prof != null) {
            return getSkin(prof);
        }
        return null;
    }

    public static Skin getSkin(GameProfile g) {
        if(g == null) return null;
        return getSkin(g,g.getId());
    }

    public static Skin getSkin(GameProfile g, UUID u) {
        if(g == null) return null;
        if(u == null) u = g.getId();
        PropertyMap properties = g.getProperties();
        if (properties.get("textures").size() > 0) {
            Iterator<Property> it = properties.get("textures").iterator();
            Property skin = it.next();
            return new Skin(u, skin.getValue(), skin.getSignature());
        }
        return null;
    }

    public static void setSkin(GameProfile g, Skin s) {
        if(g == null) return;
        PropertyMap properties = g.getProperties();
        properties.get("textures").clear();
        if(s != null) {
            properties.put("textures", new Property("textures", s.getBase64(), s.getSignedBase64()));
        }
    }

}
