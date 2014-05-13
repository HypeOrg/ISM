package com.oresomecraft.ism.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerLeaveEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public PlayerLeaveEvent(Player player) {
        this.player = player;
    }

    private Player player = null;

    public Player getPlayer() {
        return player;
    }
}
