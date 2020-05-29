package me.m1dnightninja.midnightskins.util;

import me.m1dnightninja.midnightskins.MidnightSkins;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.*;
import org.json.simple.parser.*;

import java.io.*;
import java.net.*;
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
                JSONParser parser = new JSONParser();
                JSONObject o = (JSONObject) parser.parse(res.response);
                String id = (String)o.get("id");
                return UUID.fromString(id.substring(0,8) + "-" + id.substring(8,12) + "-" + id.substring(12,16) + "-" + id.substring(16,20) + "-" + id.substring(20));
            }
        } catch(MalformedURLException | ParseException ex) {}
        return null;
    }

    // Returns a UUID of a Player's name from Mojang's servers. Will return null if nobody has the supplied name. This task is run asynchronously, override callback.callback() to get the data when it returns
    public static void getUUIDAsync(String name, MojangCallback callback) {
        new BukkitRunnable() {
            @Override
            public void run() {
                callback.callback(getUUID(name), null);
            }
        }.runTaskAsynchronously(MidnightSkins.getInstance().plugin);
    }

    // Returns Skin Data of a Player from Mojang's servers. Will return null if Mojang's servers cannot be accessed.
    public static SkinData getSkin(UUID player) {
        try {
            URL prof = new URL(SKIN_URL+player.toString().replace("-","") +"?unsigned=false");

            HttpResponse res = makeHTTPRequest(prof);
            if(res.successful && res.code == 200) {
                String base64 = null;
                String signedBase64 = null;


                JSONParser parser = new JSONParser();
                JSONObject o = (JSONObject) parser.parse(res.response);
                JSONArray properties = (JSONArray) o.get("properties");
                for(JSONObject key  : (List<JSONObject>) properties) {
                    if(key.get("name").equals("textures")) {
                        base64 = (String) key.get("value");
                        signedBase64 = (String) key.get("signature");
                    }
                }
                if(base64 == null) return null;
                JSONObject b64 = (JSONObject) parser.parse(new String(Base64.getDecoder().decode(base64), "UTF-8"));
                String name = (String) b64.get("profileName");
                JSONObject textures = (JSONObject) b64.get("textures");
                String skUrl = null;
                String cpUrl = null;
                if(textures.containsKey("SKIN")) skUrl = (String)((JSONObject)textures.get("SKIN")).get("url");
                if(textures.containsKey("CAPE")) cpUrl = (String)((JSONObject)textures.get("CAPE")).get("url");

                return new SkinData(name,player,base64,signedBase64,skUrl,cpUrl);
            }
        } catch(MalformedURLException | ParseException | UnsupportedEncodingException ex) {}
        return null;
    }

    // Returns Skin Data of a Player from Mojang's servers. Will return null if Mojang's servers cannot be accessed. This task is run asynchronously, override callback.callback() to get the data when it returns
    public static void getSkinAsync(UUID player, MojangCallback callback) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    URL prof = new URL(SKIN_URL+player.toString().replace("-","") +"?unsigned=false");

                    HttpResponse res = makeHTTPRequest(prof);
                    if(res.successful && res.code == 200) {
                        String base64 = null;
                        String signedBase64 = null;


                        JSONParser parser = new JSONParser();
                        JSONObject o = (JSONObject) parser.parse(res.response);
                        JSONArray properties = (JSONArray) o.get("properties");
                        for(JSONObject key  : (List<JSONObject>) properties) {
                            if(key.get("name").equals("textures")) {
                                base64 = (String) key.get("value");
                                signedBase64 = (String) key.get("signature");
                            }
                        }
                        if(base64 == null) return;
                        JSONObject b64 = (JSONObject) parser.parse(new String(Base64.getDecoder().decode(base64), "UTF-8"));
                        String name = (String) b64.get("profileName");
                        JSONObject textures = (JSONObject) b64.get("textures");
                        String skUrl = null;
                        String cpUrl = null;
                        if(textures.containsKey("SKIN")) skUrl = (String)((JSONObject)textures.get("SKIN")).get("url");
                        if(textures.containsKey("CAPE")) cpUrl = (String)((JSONObject)textures.get("CAPE")).get("url");

                        callback.callback(player, new SkinData(name,player,base64,signedBase64,skUrl,cpUrl));
                    }
                } catch(MalformedURLException | ParseException | UnsupportedEncodingException ex) {
                    MidnightSkins.log("Failed to get skin data from Mojang's Servers!", Level.WARNING);
                    return;
                }
            }
        }.runTaskAsynchronously(MidnightSkins.getInstance().plugin);
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
        } catch(IOException ex) { }
        return new HttpResponse(null, 400, false);
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
