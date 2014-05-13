package com.oresomecraft.ism.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GlobalRoundFinishEvent extends Event {

    public GlobalRoundFinishEvent(boolean forced, String winner, String map, String gamemode) {
        this.forced = forced;
        this.winner = winner;
        this.map = map;
        this.gamemode = gamemode;
    }

    private boolean forced = false;

    private String winner = null;
    private String map = null;
    private String gamemode = null;

    public static HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Checks if the round end was forced.
     *
     * @return Returns false or true to the round being force ended.
     */
    public boolean isForced() {
        return forced;
    }

    public String getWinner() {
        return winner;
    }

    public String getMap() {
        return map;
    }

    public String getGamemode() {
        return gamemode;
    }
}
