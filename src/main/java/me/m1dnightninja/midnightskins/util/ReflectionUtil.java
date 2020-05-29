package me.m1dnightninja.midnightskins.util;

import com.mojang.authlib.*;
import me.m1dnightninja.midnightskins.MidnightSkins;
import org.bukkit.*;
import org.bukkit.entity.*;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.logging.Level;

public class ReflectionUtil {

    static Class<?> craftPlayer = ReflectionUtil.getCBClass("entity.CraftPlayer");
    static Class<?> entityPlayer = ReflectionUtil.getNMSClass("EntityPlayer");

    public static String getAPIVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().replace(".",",").split(",")[3];
    }

    public static int getMajorVersion() {
        return Integer.parseInt(getAPIVersion().split("_")[1]);
    }

    public static Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server." + getAPIVersion() + "." + name);
        } catch(ClassNotFoundException ex) {
            MidnightSkins.log("No NMS Class named " + name, Level.WARNING);
            return null;
        }
    }

    public static Class<?> getNMSArrayClass(String name) {
        try {
            return Class.forName("[Lnet.minecraft.server." + getAPIVersion() + "." + name + ";");
        } catch(ClassNotFoundException ex) {
            MidnightSkins.log("No NMS Class named " + name, Level.WARNING);
            return null;
        }
    }

    public static Class<?> getCBClass(String name) {
        try {
            return Class.forName("org.bukkit.craftbukkit." + getAPIVersion() + "." + name);
        } catch(ClassNotFoundException ex) {
            MidnightSkins.log("No CraftBukkit Class named " + name, Level.WARNING);
            return null;
        }
    }

    public static Class<?> getClass(String name) {
        try {
            return Class.forName(name);
        } catch(ClassNotFoundException ex) {
            return null;
        }
    }

    public static Constructor<?> getConstructor(Class<?> c, Class<?>... args) {
        try {
            return c.getConstructor(args);
        } catch(NoSuchMethodException ex) {
            StringBuilder arg = new StringBuilder();
            for(int i = 0 ; i < args.length ; i++) {
                Class<?> cl = args[i];
                arg.append(cl.getName());
                if(i+1 < args.length) {
                    arg.append(", ");
                }
            }
            Bukkit.getLogger().warning("No constructor exists for class " + c.getName() + " with args " + arg.toString());
            return null;
        }
    }

    public static Object construct(Constructor<?> c, Object... args) {
        try {
            return c.newInstance(args);
        } catch(InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            return null;
        }
    }

    public static Field getField(Class<?> c, String name) {
        try {
            return c.getDeclaredField(name);
        } catch(NoSuchFieldException ex) {
            return null;
        }
    }

    public static Field getFieldByType(Class<?> c, Class<?> type) {
        for(Field f : c.getDeclaredFields()) {
            if(f.getType().equals(type)) {
                return f;
            }
        }
        return null;
    }

    public static void setFieldValue(Object o, Field f, Object value) {
        try {
            f.setAccessible(true);
            f.set(o, value);
        } catch(IllegalAccessException ex) {
            MidnightSkins.log("Unable to set field " + f.getName() + " accessible!");
        }
    }

    public static Object getFieldValue(Object o, Field f) {
        try {
            f.setAccessible(true);
            return f.get(o);
        } catch(IllegalAccessException ex) {
            return null;
        }
    }

    public static Object callMethod(Object o, Method m, Object... args) {
        try {
            return m.invoke(o, args);
        } catch(InvocationTargetException | IllegalAccessException ex) {
            return null;
        }
    }

    public static Object callMethod(Object o, Method m) {
        try {
            return m.invoke(o);
        } catch(InvocationTargetException | IllegalAccessException ex) {
            return null;
        }
    }

    public static Method getMethod(Class<?> c, String name, Class<?>... args) {
        try {
            return c.getMethod(name,args);
        } catch(NoSuchMethodException ex) {
            MidnightSkins.log("No method called " + name + " in class " + c.getName() + " with provided arguments", Level.WARNING);
            return null;
        }
    }

    public static Method getMethod(Class<?> c, String name) {
        try {
            return c.getMethod(name);
        } catch(NoSuchMethodException ex) {
            MidnightSkins.log("No method called " + name + " in class " + c.getName(), Level.WARNING);
            return null;
        }
    }

    public static Method getMethodByType(Class<?> c, Class<?> ret) {
        for(Method m : c.getMethods()) {
            if(m.getReturnType().equals(ret)) {
                return m;
            }
        }
        return null;
    }

    public static Method getMethodByType(Class<?> c, Class<?> ret, Class<?>... args) {
        for(Method m : c.getMethods()) {
            if(m.getReturnType().equals(ret) && Arrays.equals(m.getParameterTypes(), args)) {
                return m;
            }
        }
        return null;
    }

    public static GameProfile getPlayerProfile(Player p) {
        try {
            Object cp = craftPlayer.cast(p);
            Object ep = craftPlayer.getMethod("getHandle").invoke(cp);
            return (GameProfile) entityPlayer.getMethod("getProfile").invoke(ep);
        } catch(NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
            return null;
        }
    }

    public static Object getEnumValue(Class<?> e, String s) {
        if(e.isEnum()) {
            Object[] vs = e.getEnumConstants();
            for(Object v : vs) {
                if(v.toString().equals(s)) {
                    return v;
                }
            }
        }
        return null;
    }

    public static Object castTo(Object o, Class<?> c) {
        if(o != null) {
            if(c.isAssignableFrom(o.getClass())) {
                return c.cast(o);
            }
        }
        return null;
    }
}
