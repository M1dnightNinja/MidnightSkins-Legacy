package me.m1dnightninja.midnightskins.api;

import org.bukkit.entity.*;
import org.bukkit.event.*;

public class PlayerSkinChangeEvent extends Event implements Cancellable {

    private boolean cancelled;
    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private Skin oldSkin;
    private Skin newSkin;

    public PlayerSkinChangeEvent(Player player, Skin oldSkin, Skin newSkin) {
        this.player = player;
        this.oldSkin = oldSkin;
        this.newSkin = newSkin;
    }

    public Player getPlayer() {
        return player;
    }

    public Skin getOldSkin() {
        return oldSkin;
    }

    public Skin getNewSkin() {
        return newSkin;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean c) {
        cancelled = c;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
