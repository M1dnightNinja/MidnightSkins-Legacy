package me.m1dnightninja.midnightskins;

import com.mojang.authlib.*;
import me.m1dnightninja.midnightskins.api.PlayerNameChangeEvent;
import me.m1dnightninja.midnightskins.api.PlayerSkinChangeEvent;
import me.m1dnightninja.midnightskins.api.Skin;
import me.m1dnightninja.midnightskins.util.*;
import org.bukkit.*;
import org.bukkit.entity.*;

import java.util.*;

public class PlayerData {

    private final Player player;
    private final GameProfile originalProfile;
    private GameProfile customProfile;

    private UUID customSkinUUID = null;

    private static final List<PlayerData> data = new ArrayList<>();

    PlayerData(Player player) {
        this.player = player;

        GameProfile oldProf = ReflectionUtil.getPlayerProfile(player);

        if(oldProf == null) {
            originalProfile = new GameProfile(player.getUniqueId(), player.getName());
        } else {
            originalProfile = new GameProfile(oldProf.getId(), oldProf.getName());
            originalProfile.getProperties().putAll(oldProf.getProperties());
        }

        data.add(this);
    }

    Player getPlayer() {
        return player;
    }

    public Skin getNormalSkin() {
        return SkinUtil.getSkin(originalProfile);
    }

    public Skin getCustomSkin() {
        return SkinUtil.getSkin(customProfile, customSkinUUID);
    }

    public String getNormalName() {
        return originalProfile.getName();
    }

    public String getCustomName() {
        return customProfile == null ? null : customProfile.getName();
    }

    void setCustomSkin(Skin customSkin) {
        PlayerSkinChangeEvent event = new PlayerSkinChangeEvent(player, MidnightSkins.getInstance().getCurrentSkin(player), customSkin);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            if (customProfile == null) {
                customProfile = new GameProfile(player.getUniqueId(), getGameProfile().getName());
            }
            SkinUtil.setSkin(customProfile, customSkin);
            if(customSkin == null) {
                customSkinUUID = null;
            } else {
                customSkinUUID = customSkin.getUUID();
            }
        }
    }

    void setCustomName(String customName) {
        PlayerNameChangeEvent event = new PlayerNameChangeEvent(player, MidnightSkins.getInstance().getCurrentName(player), customName);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            if (customName == null) customName = originalProfile.getName();
            customProfile = NameUtil.setName(getGameProfile(), customName);
        }
    }

    public GameProfile getGameProfile() {
        if(customProfile != null) {
            return customProfile;
        } else {
            return originalProfile;
        }
    }

    static void purge(Player p) {
        PlayerData dt = null;
        for(PlayerData d : data) {
            if(d.getPlayer().equals(p)) {
                dt = getPlayerData(p);
            }
        }
        if(dt != null) {
            data.remove(dt);
        }
    }

    public static PlayerData getPlayerData(Player p) {
        for(PlayerData d : data) {
            if(d.getPlayer().equals(p)) {
                return d;
            }
        }
        return new PlayerData(p);
    }
}
