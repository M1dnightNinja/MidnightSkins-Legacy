package me.m1dnightninja.midnightskins.updater;

import com.mojang.authlib.GameProfile;
import me.m1dnightninja.midnightskins.MidnightSkins;
import me.m1dnightninja.midnightskins.PlayerData;
import me.m1dnightninja.midnightskins.api.PlayerAppearanceUpdatedEvent;
import me.m1dnightninja.midnightskins.util.NameUtil;
import me.m1dnightninja.midnightskins.util.PacketUtil;
import me.m1dnightninja.midnightskins.util.ReflectionUtil;
import me.m1dnightninja.midnightskins.util.SkinUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Updater_v1_16_R1 implements Updater {

    // CraftBukkit classes
    private final Class<?> craftPlayer;
    private final Class<?> craftItemStack;
    private final Class<?> craftWorld;
    private final Class<?> craftScoreboard;

    // NMS Classes
    private final Class<?> entityHuman;
    private final Class<?> entity;

    private final Class<?> worldServer;
    private final Class<?> dataWatcher;

    // NMS Enums
    private final Class<?> enumGamemode;
    private final Class<?> enumItemSlot;
    private final Class<?> enumPlayerInfoAction;

    // NMS Packet Classes
    private final Class<?> namedEntitySpawnPacket;
    private final Class<?> positionPacket;
    private final Class<?> heldItemPacket;
    private final Class<?> entityMetaPacket;
    private final Class<?> entityStatusPacket;
    private final Class<?> headRotationPacket;

    // Methods

    private Method getPlayerId;
    private Method getPlayerHandle;
    private Method getHeadRotation;

    private Method getWorldHandle;
    private Method getWorldData;
    private Method getDimensionManager;
    private Method getWorldType;
    private Method getDataWatcher;

    private Method updateAbilities;
    private Method triggerHealthUpdate;
    private Method updateInventory;

    private Method asNMSCopy;

    private Method getScoreboardHandle;
    private Method getTeam;

    // Fields
    private Field getWorldProvider;
    private Field defaultContainer;
    private Field action;
    private Field list;
    private Field ids;

    // Constructors
    private Constructor<?> playerRespawnConstructor;
    private Constructor<?> playerInfoConstructor;
    private Constructor<?> infoDataConstructor;
    private Constructor<?> chatComponentConstructor;
    private Constructor<?> entityDestroyConstructor;
    private Constructor<?> entityEquipmentConstructor;
    private Constructor<?> scoreboardConstructor;

    private boolean loaded = false;

    public Updater_v1_16_R1() {

        craftPlayer = ReflectionUtil.getCBClass("entity.CraftPlayer");
        craftItemStack = ReflectionUtil.getCBClass("inventory.CraftItemStack");
        craftWorld = ReflectionUtil.getCBClass("CraftWorld");
        craftScoreboard = ReflectionUtil.getCBClass("scoreboard.CraftScoreboard");

        entityHuman = ReflectionUtil.getNMSClass("EntityHuman");
        entity = ReflectionUtil.getNMSClass("Entity");

        Class<?> worldType = ReflectionUtil.getNMSClass("WorldType");
        worldServer = ReflectionUtil.getNMSClass("WorldServer");
        dataWatcher = ReflectionUtil.getNMSClass("DataWatcher");
        Class<?> dimensionManager = ReflectionUtil.getNMSClass("DimensionManager");

        enumGamemode = ReflectionUtil.getNMSClass("EnumGamemode");
        enumItemSlot = ReflectionUtil.getNMSClass("EnumItemSlot");
        enumPlayerInfoAction = ReflectionUtil.getNMSClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction");

        // Packets
        namedEntitySpawnPacket = ReflectionUtil.getNMSClass("PacketPlayOutNamedEntitySpawn");
        Class<?> respawnPacket = ReflectionUtil.getNMSClass("PacketPlayOutRespawn");
        positionPacket = ReflectionUtil.getNMSClass("PacketPlayOutPosition");
        heldItemPacket = ReflectionUtil.getNMSClass("PacketPlayOutHeldItemSlot");
        entityMetaPacket = ReflectionUtil.getNMSClass("PacketPlayOutEntityMetadata");
        entityStatusPacket = ReflectionUtil.getNMSClass("PacketPlayOutEntityStatus");
        headRotationPacket = ReflectionUtil.getNMSClass("PacketPlayOutEntityHeadRotation");
        Class<?> playerInfoPacket = ReflectionUtil.getNMSClass("PacketPlayOutPlayerInfo");
        Class<?> entityDestroyPacket = ReflectionUtil.getNMSClass("PacketPlayOutEntityDestroy");
        Class<?> entityEquipmentPacket = ReflectionUtil.getNMSClass("PacketPlayOutEntityEquipment");
        Class<?> scoreboardPacket = ReflectionUtil.getNMSClass("PacketPlayOutScoreboardTeam");

        Class<?> itemStack = ReflectionUtil.getNMSClass("ItemStack");
        Class<?> playerInfoData = ReflectionUtil.getNMSClass("PacketPlayOutPlayerInfo$PlayerInfoData");
        Class<?> container = ReflectionUtil.getNMSClass("Container");
        Class<?> iChatBaseComponent = ReflectionUtil.getNMSClass("IChatBaseComponent");
        Class<?> chatComponent = ReflectionUtil.getNMSClass("ChatComponentText");
        Class<?> scoreboard = ReflectionUtil.getNMSClass("Scoreboard");
        Class<?> scoreboardTeam = ReflectionUtil.getNMSClass("ScoreboardTeam");
        Class<?> world = ReflectionUtil.getNMSClass("World");
        Class<?> worldProvider = ReflectionUtil.getNMSClass("WorldProvider");
        Class<?> worldData = ReflectionUtil.getNMSClass("WorldData");
        Class<?> entityLiving = ReflectionUtil.getNMSClass("EntityLiving");
        Class<?> entityPlayer = ReflectionUtil.getNMSClass("EntityPlayer");

        if(craftPlayer == null || craftItemStack == null || craftWorld == null || craftScoreboard == null || entityPlayer == null || entityHuman == null || entityLiving == null || entity == null || itemStack == null || playerInfoData == null || container == null
            || iChatBaseComponent == null || chatComponent == null || scoreboard == null || scoreboardTeam == null || world == null || worldData == null || worldType == null || worldServer == null || dataWatcher == null || worldProvider == null || dimensionManager == null
            || enumGamemode == null || enumItemSlot == null || enumPlayerInfoAction == null || playerInfoPacket == null || entityDestroyPacket == null || namedEntitySpawnPacket == null || respawnPacket == null || positionPacket == null || heldItemPacket == null
            || entityMetaPacket == null || entityStatusPacket == null || entityEquipmentPacket == null || scoreboardPacket == null) {
            return;
        }

        getPlayerId = ReflectionUtil.getMethod(entityPlayer, "getId");
        getPlayerHandle = ReflectionUtil.getMethod(craftPlayer, "getHandle");

        getWorldHandle = ReflectionUtil.getMethod(craftWorld, "getHandle");
        getWorldData = ReflectionUtil.getMethod(world, "getWorldData");

        getDimensionManager = ReflectionUtil.getMethod(worldProvider, "getDimensionManager");
        getWorldType = ReflectionUtil.getMethod(worldData, "getType");

        getHeadRotation = ReflectionUtil.getMethod(entityLiving, "getHeadRotation");

        getDataWatcher = ReflectionUtil.getMethod(entity, "getDataWatcher");

        updateAbilities = ReflectionUtil.getMethod(entityPlayer, "updateAbilities");
        triggerHealthUpdate = ReflectionUtil.getMethod(entityPlayer, "triggerHealthUpdate");
        updateInventory = ReflectionUtil.getMethod(entityPlayer, "updateInventory", container);

        getScoreboardHandle = ReflectionUtil.getMethod(craftScoreboard, "getHandle");
        getTeam = ReflectionUtil.getMethod(scoreboard, "getTeam", String.class);
        asNMSCopy = ReflectionUtil.getMethod(craftItemStack, "asNMSCopy", ItemStack.class);

        playerRespawnConstructor = ReflectionUtil.getConstructor(respawnPacket, dimensionManager, long.class, worldType, enumGamemode);
        playerInfoConstructor = ReflectionUtil.getConstructor(playerInfoPacket);
        infoDataConstructor = ReflectionUtil.getConstructor(playerInfoData, playerInfoPacket, GameProfile.class, int.class, enumGamemode, iChatBaseComponent);
        chatComponentConstructor = ReflectionUtil.getConstructor(chatComponent, String.class);
        entityDestroyConstructor = ReflectionUtil.getConstructor(entityDestroyPacket);
        scoreboardConstructor = ReflectionUtil.getConstructor(scoreboardPacket, scoreboardTeam, Collection.class, int.class);
        entityEquipmentConstructor = ReflectionUtil.getConstructor(entityEquipmentPacket, int.class, enumItemSlot, itemStack);

        action = ReflectionUtil.getFieldByType(playerInfoPacket, enumPlayerInfoAction);
        list = ReflectionUtil.getFieldByType(playerInfoPacket, List.class);
        ids = ReflectionUtil.getFieldByType(entityDestroyPacket, int[].class);
        getWorldProvider = ReflectionUtil.getField(world,"worldProvider");
        defaultContainer = ReflectionUtil.getField(entityHuman, "defaultContainer");

        if(getPlayerId == null || getPlayerHandle == null || getWorldHandle == null || getWorldData == null || getDimensionManager == null || getWorldType == null || getHeadRotation == null || getDataWatcher == null || updateAbilities == null || triggerHealthUpdate == null
            || updateInventory == null || getScoreboardHandle == null || getTeam == null || asNMSCopy == null || playerRespawnConstructor == null || playerInfoConstructor == null || infoDataConstructor == null || chatComponentConstructor == null || entityDestroyConstructor == null
            || scoreboardConstructor == null || entityEquipmentConstructor == null || action == null || list == null || ids == null || getWorldProvider == null || defaultContainer == null) {
            return;
        }

        loaded = true;
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    @Override
    public void updatePlayer(Player p) {

        if(!loaded) return;

        updatePlayerSelf(p);

        for (Player observer : Bukkit.getOnlinePlayers()) {
            if(observer.getUniqueId() != p.getUniqueId()) {
                updatePlayerOther(p, observer);
            }
        }

        PlayerAppearanceUpdatedEvent event = new PlayerAppearanceUpdatedEvent(p, p.getName(), SkinUtil.getPlayerSkin(p));
        if(Bukkit.isPrimaryThread()) {
            Bukkit.getPluginManager().callEvent(event);
        } else {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.getPluginManager().callEvent(event);
                }
            }.runTask(MidnightSkins.getInstance().getPlugin());
        }

    }

    @Override
    public void updatePlayerSelf(Player p) {

        if(!loaded) return;

        updatePlayerOther(p,p);

        Object ep = ReflectionUtil.callMethod(ReflectionUtil.castTo(p, craftPlayer), getPlayerHandle);
        if(ep == null) return;

        int id = (int) ReflectionUtil.callMethod(ep, getPlayerId);

        World w = Bukkit.getWorlds().get(0);
        if(p.getWorld().getEnvironment() != World.Environment.NORMAL) {
            for(World ws : Bukkit.getWorlds()) {
                if(ws.getEnvironment() == p.getWorld().getEnvironment()) {
                    w = ws;
                }
            }
        }

        Object cw = ReflectionUtil.castTo(w, craftWorld);
        Object wserver = ReflectionUtil.castTo(ReflectionUtil.callMethod(cw, getWorldHandle), worldServer);
        Object wprovider = ReflectionUtil.getFieldValue(wserver, getWorldProvider);
        Object wdata = ReflectionUtil.callMethod(wserver, getWorldData);

        if(ReflectionUtil.getMajorVersion() >= 15) {
            long hash = 0L;

            try {
                MessageDigest d = MessageDigest.getInstance("SHA-256");
                byte[] enc = d.digest((""+w.getSeed()).getBytes(StandardCharsets.UTF_8));
                for(int i = 0 ; i < 8 ; i++) {
                    hash = (hash << 8) + (enc[i] & 0xff);
                }
            } catch(NoSuchAlgorithmException ex) {
                ex.printStackTrace();
            }
            PacketUtil.sendPacket(p, playerRespawnConstructor, ReflectionUtil.callMethod(wprovider, getDimensionManager), hash, ReflectionUtil.callMethod(wdata, getWorldType), ReflectionUtil.getEnumValue(enumGamemode, p.getGameMode().name()));
        } else {
            PacketUtil.sendPacket(p, playerRespawnConstructor, ReflectionUtil.callMethod(wprovider, getDimensionManager), ReflectionUtil.callMethod(wdata, getWorldType), ReflectionUtil.getEnumValue(enumGamemode, p.getGameMode().name()));
        }
        PacketUtil.sendPacket(p, ReflectionUtil.getConstructor(positionPacket, double.class, double.class, double.class, float.class, float.class, Set.class, int.class), p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), p.getLocation().getYaw(), p.getLocation().getPitch(), new HashSet<>(), 0);
        PacketUtil.sendPacket(p, ReflectionUtil.getConstructor(heldItemPacket, int.class), p.getInventory().getHeldItemSlot());
        PacketUtil.sendPacket(p, ReflectionUtil.getConstructor(entityMetaPacket, int.class, dataWatcher, boolean.class), id, ReflectionUtil.callMethod(ep, getDataWatcher), true);
        PacketUtil.sendPacket(p, ReflectionUtil.getConstructor(entityStatusPacket, entity, byte.class), ep, (byte) 28);

        ReflectionUtil.callMethod(ep, updateAbilities);
        ReflectionUtil.callMethod(ep, triggerHealthUpdate);
        ReflectionUtil.callMethod(ep, updateInventory, ReflectionUtil.getFieldValue(ep,defaultContainer));

        p.recalculatePermissions();
    }

    @Override
    public void updatePlayerOther(Player p, Player o) {
        updatePlayerOther(p,o, NameUtil.getOldName(p));
    }

    @Override
    public void updatePlayerOther(Player p, Player o, String oldName) {

        PlayerData d = PlayerData.getPlayerData(p);

        Object ep = ReflectionUtil.callMethod(ReflectionUtil.castTo(p, craftPlayer), getPlayerHandle);
        if(ep == null) return;

        int id = (int) ReflectionUtil.callMethod(ep, getPlayerId);

        Object remove = ReflectionUtil.getEnumValue(enumPlayerInfoAction, "REMOVE_PLAYER");
        Object add = ReflectionUtil.getEnumValue(enumPlayerInfoAction, "ADD_PLAYER");
        Object removePacket = ReflectionUtil.construct(playerInfoConstructor);
        ReflectionUtil.setFieldValue(removePacket, action, remove);

        Object addPacket = ReflectionUtil.construct(playerInfoConstructor);
        ReflectionUtil.setFieldValue(addPacket, action, add);
        String newName = p.getName();
        if (d.getCustomName() != null) {
            newName = d.getCustomName();
        }
        GameProfile prof = d.getGameProfile();
        Object data = ReflectionUtil.construct(infoDataConstructor, addPacket, prof, id, ReflectionUtil.getEnumValue(enumGamemode, p.getGameMode().name()), ReflectionUtil.construct(chatComponentConstructor, newName));

        ReflectionUtil.setFieldValue(addPacket, list, Collections.singletonList(data));

        PacketUtil.sendPacket(o, removePacket);
        PacketUtil.sendPacket(o, addPacket);

        if (p.getUniqueId() != o.getUniqueId() && o.getWorld().equals(p.getWorld())) {
            Object edpacket = ReflectionUtil.construct(entityDestroyConstructor);
            int[] val = {id};

            ReflectionUtil.setFieldValue(edpacket, ids, val);

            PacketUtil.sendPacket(o, edpacket);
            PacketUtil.sendPacket(o, ReflectionUtil.getConstructor(namedEntitySpawnPacket, entityHuman), ep);

            float headRot = (float) ReflectionUtil.callMethod(ep, getHeadRotation);
            int rot = (int) headRot;
            if (headRot < (float) rot) rot -= 1;

            PacketUtil.sendPacket(o, ReflectionUtil.getConstructor(headRotationPacket, entity, byte.class), ep, (byte) ((rot * 256.0F) / 360.0F));
            PacketUtil.sendPacket(o, ReflectionUtil.getConstructor(entityMetaPacket, int.class, dataWatcher, boolean.class), id, ReflectionUtil.callMethod(ep, getDataWatcher), true);
        }

        if (!oldName.equals(newName)) {
            ScoreboardManager mgr = Bukkit.getScoreboardManager();
            if (mgr != null) {
                List<String> teams = new ArrayList<>();
                for (Team t : mgr.getMainScoreboard().getTeams()) {
                    if (t.getEntries().contains(oldName)) {
                        teams.add(t.getName());
                    }
                }
                if (!teams.isEmpty()) {
                    for (String t : teams) {
                        Scoreboard s = Bukkit.getScoreboardManager().getMainScoreboard();
                        Object sb = ReflectionUtil.callMethod(ReflectionUtil.castTo(s, craftScoreboard), getScoreboardHandle);
                        Object team = ReflectionUtil.callMethod(sb, getTeam, t);

                        Object scRemovePacket = ReflectionUtil.construct(scoreboardConstructor, team, Collections.singleton(oldName), 4);
                        PacketUtil.sendPacket(o, scRemovePacket);

                        Object scAddPacket = ReflectionUtil.construct(scoreboardConstructor, team, Collections.singleton(newName), 3);
                        PacketUtil.sendPacket(o, scAddPacket);
                    }
                }
            }
        }

        PacketUtil.sendPacket(o, entityEquipmentConstructor, id, ReflectionUtil.getEnumValue(enumItemSlot, "MAINHAND"), ReflectionUtil.callMethod(craftItemStack, asNMSCopy, p.getInventory().getItemInMainHand()));
        PacketUtil.sendPacket(o, entityEquipmentConstructor, id, ReflectionUtil.getEnumValue(enumItemSlot, "OFFHAND"), ReflectionUtil.callMethod(craftItemStack, asNMSCopy, p.getInventory().getItemInOffHand()));
        PacketUtil.sendPacket(o, entityEquipmentConstructor, id, ReflectionUtil.getEnumValue(enumItemSlot, "FEET"), ReflectionUtil.callMethod(craftItemStack, asNMSCopy, p.getInventory().getBoots()));
        PacketUtil.sendPacket(o, entityEquipmentConstructor, id, ReflectionUtil.getEnumValue(enumItemSlot, "LEGS"), ReflectionUtil.callMethod(craftItemStack, asNMSCopy, p.getInventory().getLeggings()));
        PacketUtil.sendPacket(o, entityEquipmentConstructor, id, ReflectionUtil.getEnumValue(enumItemSlot, "CHEST"), ReflectionUtil.callMethod(craftItemStack, asNMSCopy, p.getInventory().getChestplate()));
        PacketUtil.sendPacket(o, entityEquipmentConstructor, id, ReflectionUtil.getEnumValue(enumItemSlot, "HEAD"), ReflectionUtil.callMethod(craftItemStack, asNMSCopy, p.getInventory().getHelmet()));
    }
}
