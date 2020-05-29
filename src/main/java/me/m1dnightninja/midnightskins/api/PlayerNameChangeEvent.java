package me.m1dnightninja.midnightskins.api;

import org.bukkit.entity.*;
import org.bukkit.event.*;

public class PlayerNameChangeEvent extends Event implements Cancellable {

    private boolean cancelled;

    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private String oldName;
    private String newName;

    public PlayerNameChangeEvent(Player player, String oldName, String newName) {
        this.player = player;
        this.oldName = oldName;
        this.newName = newName;
    }

    public Player getPlayer() {
        return player;
    }

    public String getOldName() {
        return oldName;
    }

    public String getNewName() {
        return newName;
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
