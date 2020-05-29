package me.m1dnightninja.midnightskins.api;

import org.bukkit.entity.*;
import org.bukkit.event.*;

public class PlayerAppearanceUpdatedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final String name;
    private final Skin skin;

    public PlayerAppearanceUpdatedEvent(Player player, String name, Skin skin) {
        this.player = player;
        this.name = name;
        this.skin = skin;
    }

    public Player getPlayer() {
        return player;
    }

    public String getName() {
        return name;
    }

    public Skin getSkin() {
        return skin;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
