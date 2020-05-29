package me.m1dnightninja.midnightskins.util;

import me.clip.placeholderapi.*;
import me.m1dnightninja.midnightskins.*;
import org.bukkit.entity.*;

public class PlaceholderUtil extends PlaceholderHook {

    public void register() {
        PlaceholderAPI.registerPlaceholderHook("midnightskins", this);
    }

    @Override
    public String onPlaceholderRequest(Player p, String identifier) {
        switch(identifier) {
            case "current_name":
                return MidnightSkins.getInstance().getCurrentName(p);
            case "original_name":
                return MidnightSkins.getInstance().getOriginalName(p);
            case "current_skin_base64":
                return MidnightSkins.getInstance().getCurrentSkin(p).getBase64();
            case "current_skin_signedBase64":
                return MidnightSkins.getInstance().getCurrentSkin(p).getSignedBase64();
            case "current_skin_uuid":
                return MidnightSkins.getInstance().getCurrentSkin(p).getUUID().toString();
            case "original_skin_base64":
                return MidnightSkins.getInstance().getOriginalSkin(p).getBase64();
            case "original_skin_signedBase64":
                return MidnightSkins.getInstance().getOriginalSkin(p).getSignedBase64();
            case "original_skin_uuid":
                return MidnightSkins.getInstance().getOriginalSkin(p).getUUID().toString();
        }
        return null;
    }

}
