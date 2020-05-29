package me.m1dnightninja.midnightskins.util;

import org.bukkit.entity.*;

import java.lang.reflect.*;

public class PacketUtil {

    private static final Class craftPlayer = ReflectionUtil.getCBClass("entity.CraftPlayer");
    private static final Class entityPlayer = ReflectionUtil.getNMSClass("EntityPlayer");
    private static final Field playerConnection = ReflectionUtil.getField(entityPlayer, "playerConnection");

    public static void sendPacket(Player p, Constructor c, Object... params) {

        Object packet = ReflectionUtil.construct(c, params);
        sendPacket(p,packet);

    }

    public static void sendPacket(Player p, Object packet) {
        Object cp = ReflectionUtil.castTo(p, craftPlayer);
        Object ep = ReflectionUtil.callMethod(cp, ReflectionUtil.getMethod(craftPlayer, "getHandle"));
        Object connection = ReflectionUtil.getFieldValue(ep, playerConnection);

        ReflectionUtil.callMethod(connection, ReflectionUtil.getMethod(playerConnection.getType(), "sendPacket", ReflectionUtil.getNMSClass("Packet")), packet);
    }
}
