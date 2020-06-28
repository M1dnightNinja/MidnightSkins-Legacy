package me.m1dnightninja.midnightskins;

import me.m1dnightninja.midnightskins.api.*;
import me.m1dnightninja.midnightskins.updater.*;
import me.m1dnightninja.midnightskins.util.*;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.*;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.logging.Level;

public class MidnightSkins {

    private static boolean enabled;

    private static final MidnightSkins INSTANCE = new MidnightSkins();
    private static Updater updater;

    private JavaPlugin plugin;

    private boolean forwarder = false;

    // Constructor
    private MidnightSkins() {

        Class<?> cl = ReflectionUtil.getClass("me.m1dnightninja.midnightskins.standalone.MidnightSkins");
        if(cl != null) {
            log("MidnightSkins standalone jar detected! Using this API as a forwarder!");
            forwarder = true;
            enabled = true;
            return;
        }

        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (this.getClass().getProtectionDomain().getCodeSource().equals(plugin.getClass().getProtectionDomain().getCodeSource())) {
                if(plugin instanceof JavaPlugin) this.plugin = (JavaPlugin) plugin;
            }
        }
    }

    // Enable MidnightSkins, registering PlaceholderAPI placeholders.
    // This should be called by the plugin that's using it.
    public void enable() {
        if(forwarder) {
            StandaloneUtil.enable();
            return;
        }
        if(!enabled) {
            if(ReflectionUtil.getMajorVersion() < 8) {
                // Setting skins was username based before 1.8, this library is only built to support the 1.8+ UUID system
                log("This API cannot support versions lower than 1.8!", Level.WARNING);
            } else {
                if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                    new PlaceholderUtil().register();
                }
                Bukkit.getPluginManager().registerEvents(new MainListener(), plugin);
                String api = ReflectionUtil.getAPIVersion();
                log("Attempting to enable MidnightSkins with API version " + api);

                if(api.equals("v1_8_R1")) {
                    updater = new Updater_v8R1();
                } else {
                    int majorVersion = ReflectionUtil.getMajorVersion();
                    if(majorVersion == 8) {
                        updater = new Updater_v8RX();
                    } else if(majorVersion < 16) {
                        updater = new Updater_v9RX_15RX();
                    } else {
                        try {
                            Class<?> upd = Class.forName("me.m1dnightninja.midnightskins.updater.Updater_v" + majorVersion + api.substring(api.length()-2));
                            Constructor<?> cons = ReflectionUtil.getConstructor(upd);
                            if(cons != null) updater = (Updater) ReflectionUtil.construct(cons);
                        } catch(NullPointerException | ClassCastException ignored) { } catch(ClassNotFoundException ex) {
                            try {
                                Class<?> upd = Class.forName("me.m1dnightninja.midnightskins.updater.Updater_v" + majorVersion + "RX");
                                Constructor<?> cons = ReflectionUtil.getConstructor(upd);
                                if(cons != null) updater = (Updater) ReflectionUtil.construct(cons);
                            } catch (NullPointerException | ClassCastException | ClassNotFoundException ignored) { }
                        }
                    }
                }

                if(updater == null) updater = new Updater_Other();

                if(updater.isLoaded()) {
                    enabled = true;
                    log("MidnightSkins enabled with API version " + api + ", Game version 1." + ReflectionUtil.getMajorVersion());
                } else {
                    log("Could not enable the plugin! This version is not supported!", Level.WARNING);
                }
            }
        }
    }

    // Disable MidnightSkins
    // Calling this will disable the library, making all the functions non-functional
    public void disable() {
        if(forwarder) {
            StandaloneUtil.disable();
            return;
        }
        if(enabled) {
            enabled = false;

            for(Player p : Bukkit.getOnlinePlayers()) {
                resetPlayer(p);
            }
        }
    }

    // Check if the Library is enabled
    public boolean isEnabled() {
        if(forwarder) {
            return StandaloneUtil.isEnabled();
        }
        return enabled;
    }

    // Log something to the server console using the MidnightSkins prefix and the given Log level.
    public static void log(String s, Level lvl) {
        ChatColor color = ChatColor.WHITE;
        if(lvl == Level.WARNING) {
            color = ChatColor.YELLOW;
        } else if(lvl == Level.SEVERE) {
            color = ChatColor.RED;
        }
        Bukkit.getLogger().log(lvl, ChatColor.DARK_PURPLE + "[MidnightSkins] " + color + s);
    }

    public static void log(String s) {
        log(s, Level.INFO);
    }

    // Returns the active instance of the library, so that the methods can be called.
    public static MidnightSkins getInstance() {
        return INSTANCE;
    }

    public JavaPlugin getPlugin() {
        if(forwarder) {
            return StandaloneUtil.getInstance();
        }
        return plugin;
    }

    // Sets the given player's skin to the values stored within the skin object. This method does not update the player's skin for the other players.
    public void setPlayerSkin(Player p, Skin s) {
        if(forwarder) {
            StandaloneUtil.setPlayerSkin(p, s);
            return;
        }
        if(!enabled) return;
        if(Bukkit.isPrimaryThread()) {
            PlayerData.getPlayerData(p).setCustomSkin(s);
        } else {
            new BukkitRunnable() {
                @Override
                public void run() {
                    setPlayerSkin(p,s);
                }
            }.runTask(plugin);
        }
    }

    // Resets the given player's skin to default. This method does not update the player's skin for other players.
    public void resetPlayerSkin(Player p) {
        if(forwarder) {
            StandaloneUtil.resetPlayerSkin(p);
            return;
        }
        if (!enabled) return;
        if(Bukkit.isPrimaryThread()) {
            PlayerData.getPlayerData(p).setCustomSkin(PlayerData.getPlayerData(p).getNormalSkin());
        } else {
            new BukkitRunnable() {
                @Override
                public void run() {
                    resetPlayerSkin(p);
                }
            }.runTask(plugin);
        }
    }

    // Sets a player's name to the given text. This method does not update the player's name for other players.
    public void setPlayerName(Player p, final String name) {
        if(forwarder) {
            StandaloneUtil.setPlayerName(p, name);
            return;
        }
        if(!enabled) return;
        if(Bukkit.isPrimaryThread()) {
            PlayerData.getPlayerData(p).setCustomName(name);

        } else {
            new BukkitRunnable() {
                @Override
                public void run() {
                    setPlayerName(p,name);
                }
            }.runTask(plugin);
        }
    }

    // Resets a player's name to default.
    public void resetPlayerName(Player p) {
        if(forwarder) {
            StandaloneUtil.resetPlayerName(p);
            return;
        }
        if (!enabled) return;
        if (Bukkit.isPrimaryThread()) {
            PlayerData.getPlayerData(p).setCustomName(PlayerData.getPlayerData(p).getNormalName());
        } else {
            new BukkitRunnable() {
                @Override
                public void run() {
                    resetPlayerName(p);
                }
            }.runTask(plugin);
        }
    }

    // Resets a player's name and skin
    public void resetPlayer(Player p) {
        if(forwarder) {
            StandaloneUtil.resetPlayer(p);
            return;
        }
        if(!enabled) return;
        resetPlayerName(p);
        resetPlayerSkin(p);
    }

    // Update a player's skin and name for all other players on the server.
    public void updatePlayer(Player p) {
        if(forwarder) {
            StandaloneUtil.updatePlayer(p);
            return;
        }
        if(!enabled) return;
        if(Bukkit.isPrimaryThread()) {
            updater.updatePlayer(p);

        } else {
            new BukkitRunnable() {
                @Override
                public void run() {
                    updatePlayer(p);
                }
            }.runTask(plugin);
        }
    }

    // Update a player's skin and name for another player on the server.
    public void updatePlayer(Player p, Player o) {
        if(forwarder) {
            StandaloneUtil.updatePlayer(p, o);
            return;
        }
        if(!enabled) return;
        if(Bukkit.isPrimaryThread()) {
            updater.updatePlayerOther(p,o);

        } else {
            new BukkitRunnable() {
                @Override
                public void run() {
                    updatePlayer(p,o);
                }
            }.runTask(plugin);
        }
    }

    // Returns the skin data for a player's currently applied skin.
    public Skin getCurrentSkin(Player p) {
        if(forwarder) {
            return StandaloneUtil.getCurrentSkin(p);
        }
        if(!enabled) return null;
        return PlayerData.getPlayerData(p).getCustomSkin() == null ? PlayerData.getPlayerData(p).getNormalSkin() : PlayerData.getPlayerData(p).getCustomSkin();
    }

    // Returns the skin data for a player's skin they logged in with.
    public Skin getOriginalSkin(Player p) {
        if(forwarder) {
            return StandaloneUtil.getOriginalSkin(p);
        }
        if(!enabled) return null;
        return PlayerData.getPlayerData(p).getNormalSkin();
    }

    // Returns the currently applied name of a player
    public String getCurrentName(Player p) {
        if(forwarder) {
            return StandaloneUtil.getCurrentName(p);
        }
        if(!enabled) return null;
        return PlayerData.getPlayerData(p).getCustomName() == null ? PlayerData.getPlayerData(p).getNormalName() : PlayerData.getPlayerData(p).getCustomName();
    }

    // Returns the name that a player logged in with
    public String getOriginalName(Player p) {
        if(forwarder) {
            return StandaloneUtil.getOriginalName(p);
        }
        if(!enabled) return null;
        return PlayerData.getPlayerData(p).getNormalName();
    }

    // Returns the skin data of a player using Mojang's server
    public Skin getSkinFromWeb(UUID u) {
        if(forwarder) {
            return StandaloneUtil.getSkinFromWeb(u);
        }
        MojangUtil.SkinData d = MojangUtil.getSkin(u);
        if(d != null) {
            return new Skin(d.uuid, d.base64, d.signedBase64);
        }
        return null;
    }
}
