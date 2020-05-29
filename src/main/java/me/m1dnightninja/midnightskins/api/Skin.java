package me.m1dnightninja.midnightskins.api;

import java.util.*;

public class Skin {

    UUID uuid;
    String base64;
    String signedBase64;

    public Skin(UUID uuid, String base64, String signedBase64) {
        this.uuid = uuid;
        this.base64 = base64;
        this.signedBase64 = signedBase64;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getBase64() {
        return base64;
    }

    public String getSignedBase64() {
        return signedBase64;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Skin) {
            Skin s2 = (Skin) obj;
            return s2.getUUID().equals(uuid) && s2.getBase64().equals(base64) && s2.getSignedBase64().equals(signedBase64);
        }
        return false;
    }

    public static Skin deserialize(Map<String, Object> args) {
        if(args.containsKey("uuid") && args.containsKey("base64") && args.containsKey("signedBase64")) {
            UUID uuid;
            try {
                uuid = UUID.fromString(args.get("uuid").toString());
            } catch(IllegalArgumentException ex) {
                return null;
            }
            return new Skin(uuid, args.get("base64").toString(), args.get("signedBase64").toString());
        }
        return null;
    }
}
