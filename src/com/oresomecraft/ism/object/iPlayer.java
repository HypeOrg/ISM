package com.oresomecraft.ism.object;

import com.oresomecraft.ism.ISM;
import com.oresomecraft.ism.handlers.SQLHandler;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R3.CraftServer;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class iPlayer extends CraftPlayer {

    public iPlayer(final Player p) {
        super((CraftServer) Bukkit.getServer(), ((CraftPlayer) p).getHandle());
        instance = this;
        Bukkit.getScheduler().runTaskAsynchronously(ISM.getInstance(), new Runnable() {
            public void run() {
                try {
                    asyncRefreshPlayerInfo(p);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static iPlayer instance;

    //Personal stuff
    public int gold;
    public int silver;
    public int bronze;
    public String title;

    public boolean inGame = false;

    /**
     * Gets a player's iPlayer object from their username.
     */
    public static iPlayer getIPlayer(String name) {
        return ISM.getIPlayers().get(name);
    }

    /**
     * Gets a player's iPlayer object from their player instance.
     */
    public static iPlayer getIPlayer(Player p) {
        return ISM.getIPlayers().get(p.getName());
    }

    public void asyncRefreshPlayerInfo(final Player p) throws SQLException {
        Bukkit.getScheduler().runTaskAsynchronously(ISM.getInstance(), new Runnable() {
            public void run() {
                try {
                    SQLHandler.refreshStats(instance);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Creates an iPlayer instance for a player.
     *
     * @param p A Player
     */
    public static void craftIPlayer(Player p) {
        iPlayer t = new iPlayer(p);
        ISM.getInstance().getIPlayers().put(p.getName(), t);
    }

    public static void removeIPlayer(final String p) {
        Bukkit.getScheduler().runTaskAsynchronously(ISM.getInstance(), new Runnable() {
            @Override
            public void run() {
                try {
                    SQLHandler.pushStats(instance);
                    ISM.getInstance().getIPlayers().remove(p);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public boolean hasPermisson(String perm) {
        return Bukkit.getPlayer(getName()).hasPermission(perm);
    }


    // *********** Deprecated shit thanks to Bukkit's over-mapped thing ***********

    public double getLastDamage() {
        return super.getLastDamage();
    }

    public double getMaxHealth() {
        return super.getMaxHealth();
    }

    public double getHealth() {
        return super.getHealth();
    }

    @Deprecated
    public void setMaxHealth(int amount) {
        super.setMaxHealth((double) amount);
    }

    @Deprecated
    public void setHealth(int amount) {
        // This is a dud. Use setHealth(double)
    }

    @Deprecated
    public void damage(int amount) {
        super.damage((double) amount);
    }

    @Deprecated
    public void damage(int amount, org.bukkit.entity.Entity entity) {
        super.damage((double) amount, entity);
    }

    @Deprecated
    public void setLastDamage(int amount) {
        super.damage((double) amount);
    }

}