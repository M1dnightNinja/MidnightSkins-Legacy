package me.m1dnightninja.midnightskins.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.m1dnightninja.midnightskins.MidnightSkins;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;

public class MojangUtil {

    private static final String UUID_URL = "https://api.mojang.com/users/profiles/minecraft/";
    private static final String SKIN_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";

    // Returns a UUID of a Player's name from Mojang's servers. Will return null if nobody has the supplied name.
    public static UUID getUUID(String name) {
        try {
            URL prof = new URL(UUID_URL+name);

            HttpResponse res = makeHTTPRequest(prof);
            if(res.successful && res.code == 200) {
                JsonParser parser = new JsonParser();
                JsonObject object = parser.parse(res.response).getAsJsonObject();
                
                String id = object.get("id").getAsString();
                
                return UUID.fromString(id.substring(0,8) + "-" + id.substring(8,12) + "-" + id.substring(12,16) + "-" + id.substring(16,20) + "-" + id.substring(20));
            }
        } catch(MalformedURLException | IllegalStateException ignored) {
            // Invalid URL
        }
        return null;
    }

    // Returns a UUID of a Player's name from Mojang's servers. Will return null if nobody has the supplied name. This task is run asynchronously, override callback.callback() to get the data when it returns
    public static void getUUIDAsync(String name, MojangCallback callback) {
        new BukkitRunnable() {
            @Override
            public void run() {
                callback.callback(getUUID(name), null);
            }
        }.runTaskAsynchronously(MidnightSkins.getInstance().getPlugin());
    }

    // Returns Skin Data of a Player from Mojang's servers. Will return null if Mojang's servers cannot be accessed.
    public static SkinData getSkin(UUID player) {
        try {
            URL prof = new URL(SKIN_URL+player.toString().replace("-","") +"?unsigned=false");

            HttpResponse res = makeHTTPRequest(prof);
            return getSkinData(res, player);

        } catch(MalformedURLException ex) {
            MidnightSkins.log("Failed to get skin data from Mojang's Servers!", Level.WARNING);
        }
        return null;
    }

    // Returns Skin Data of a Player from Mojang's servers. Will return null if Mojang's servers cannot be accessed. This task is run asynchronously, override callback.callback() to get the data when it returns
    public static void getSkinAsync(UUID player, MojangCallback callback) {
        new BukkitRunnable() {
            @Override
            public void run() {

                SkinData data = getSkin(player);
                if(data == null) return;

                callback.callback(player, data);
            }
        }.runTaskAsynchronously(MidnightSkins.getInstance().getPlugin());
    }

    // Makes an HTTP Request to the given URL
    public static HttpResponse makeHTTPRequest(URL u) {
        try {
            StringBuilder res = new StringBuilder();
            HttpURLConnection con = (HttpURLConnection) u.openConnection();
            con.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            while((line = reader.readLine()) != null) {
                res.append(line);
            }
            return new HttpResponse(res.toString(), con.getResponseCode(), true);
        } catch(IOException ignored) { }
        return new HttpResponse(null, 400, false);
    }

    private static SkinData getSkinData(HttpResponse response, UUID uuid) {
        if(!response.successful || response.code != 200) return null;
        try {
            String base64 = null;
            String signedBase64 = null;

            JsonParser parser = new JsonParser();
            JsonObject o = parser.parse(response.response).getAsJsonObject();

            JsonArray properties = o.getAsJsonArray("properties");

            for (int i = 0; i < properties.size(); i++) {
                JsonElement element = properties.get(i);
                try {
                    JsonObject object = element.getAsJsonObject();
                    if (object.get("name").getAsString().equals("textures")) {
                        base64 = object.get("value").getAsString();
                        signedBase64 = object.get("signature").getAsString();
                    }
                } catch (IllegalStateException ignored) {
                }
            }

            if (base64 == null) return null;

            JsonObject b64 = parser.parse(new String(Base64.getDecoder().decode(base64), StandardCharsets.UTF_8)).getAsJsonObject();
            String name = b64.get("profileName").getAsString();

            JsonObject textures = b64.get("textures").getAsJsonObject();
            String skUrl = null;
            String cpUrl = null;

            JsonElement skin = textures.get("SKIN");
            if (skin != null) skUrl = skin.getAsJsonObject().get("url").getAsString();

            JsonElement cape = textures.get("CAPE");
            if (cape != null) cpUrl = cape.getAsJsonObject().get("url").getAsString();

            return new SkinData(name, uuid, base64, signedBase64, skUrl, cpUrl);

        } catch(IllegalStateException ignored) { }

        return null;
    }

    // Class that contains data of a response to an HTTP Request
    private static class HttpResponse {
        public String response;
        public int code;
        public boolean successful;

        public HttpResponse(String response, int code, boolean successful) {
            this.response = response;
            this.code = code;
            this.successful = successful;
        }
    }

    // Class that contains data of a player's skin, including cape data.
    public static class SkinData {

        public String name;
        public UUID uuid;
        public String base64;
        public String signedBase64;
        public String skinURL;
        public String capeURL;

        public SkinData(String name, UUID uuid, String base64, String signedBase64, String skinURL, String capeURL) {
            this.name = name;
            this.uuid = uuid;
            this.base64 = base64;
            this.signedBase64 = signedBase64;
            this.skinURL = skinURL;
            this.capeURL = capeURL;
        }
    }

    // Class that contains callback data
    public static class MojangCallback {

        public MojangCallback() { }

        public void callback(UUID uuid, SkinData skin) {

        }
    }

}
