package com.oresomecraft.ism.maps;

import com.oresomecraft.ism.ISM;
import com.oresomecraft.ism.Storage;
import com.oresomecraft.ism.event.GlobalRoundFinishEvent;
import com.oresomecraft.ism.util.Utility;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;

public abstract class Map implements Listener {

    public ISM plugin = ISM.getInstance();
    public Map config;

    public Map(String name) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        map = name;
    }

    public String map;
    public ArrayList<BukkitTask> tasks = new ArrayList<BukkitTask>();
    public World w = Bukkit.getWorld(Storage.roundID + "");

    /**
     * Gets the map's name.
     *
     * @return Returns the map's name.
     */
    public final String getName() {
        return map;
    }

    /**
     * Completely unregisters the map listeners, tasks and attributes on round end.
     *
     * @param event An event called by ISM.
     */
    @EventHandler
    public void end(GlobalRoundFinishEvent event) {
        HandlerList.unregisterAll(this);
        plugin = null;
        config = null;
        for (BukkitTask task : tasks) {
            task.cancel();
        }
    }

    /**
     * Disables breaking on the map if disabled by the config
     *
     * @param event An event called by the server
     */
    @EventHandler
    public void lBreak(BlockBreakEvent event) {
        if (Storage.noBreak.contains(map)) {
            event.setCancelled(true);
        }
    }

    /**
     * Disables hunger on the map if disabled by the config
     *
     * @param event An event called by the server
     */
    @EventHandler
    public void lPvP(EntityDamageByEntityEvent event) {
        if (Storage.noPvP.contains(map)) {
            if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
                event.setCancelled(true);
            }
            if (event.getDamager() instanceof Arrow) {
                if (((Arrow) event.getDamager()).getShooter() instanceof Player) {
                    event.setCancelled(true);
                }
            }
        }
    }

    /**
     * Disables hunger on the map if disabled by the config
     *
     * @param event An event called by the server
     */
    @EventHandler
    public void lHunger(FoodLevelChangeEvent event) {
        if (Storage.noHunger.contains(map)) {
            event.setCancelled(true);
        }
    }

    /**
     * Disables placing on the map if disabled by the config
     *
     * @param event An event called by the server
     */
    @EventHandler
    public void lPlace(BlockPlaceEvent event) {
        if (Storage.noPlace.contains(map)) {
            event.setCancelled(true);
        }
    }

    /**
     * Disables drops on the map if disabled by the config
     *
     * @param event An event called by the server
     */
    @EventHandler
    public void lDrops(PlayerDeathEvent event) {
        if (Storage.noDrops.contains(map)) {
            event.getDrops().clear();
        }
    }

    /**
     * Broadcasts a lead takeover
     *
     * @param p A player
     */
    public void callLead(final Player p) {
        Bukkit.broadcastMessage(ChatColor.YELLOW + "[" + ChatColor.GOLD + "ISM" + ChatColor.YELLOW + "] " + p.getDisplayName() + ChatColor.RED + " takes the lead!");
        new BukkitRunnable() {
            int count = 5;

            public void run() {
                if (count == 0) {
                    Bukkit.getScheduler().cancelTask(this.getTaskId());
                    return;
                }
                count--;
                Utility.ffw(p.getLocation(), Color.YELLOW);
            }
        }.runTaskTimer(ISM.getInstance(), 2, 2);
    }

    public boolean contains(Location loc, int x1, int x2, int y1, int y2, int z1, int z2) {
        int bottomCornerX = x1 < x2 ? x1 : x2;
        int bottomCornerZ = z1 < z2 ? z1 : z2;
        int topCornerX = x1 > x2 ? x1 : x2;
        int topCornerZ = z1 > z2 ? z1 : z2;
        int bottomCornerY = y1 < y2 ? y1 : y2;
        int topCornerY = y1 > y2 ? y1 : y2;
        if (loc.getBlockX() >= bottomCornerX && loc.getBlockX() <= topCornerX) {
            if (loc.getBlockZ() >= bottomCornerZ && loc.getBlockZ() <= topCornerZ) {
                if (loc.getBlockY() >= bottomCornerY && loc.getBlockY() <= topCornerY) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int grpnv(int p1, int p2) {
        return Math.min(p1, p2) + (int) Math.round(-0.5f + (1 + Math.abs(p1 - p2)) * Math.random());
    }
}
