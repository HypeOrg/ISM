package com.oresomecraft.ism.handlers;

import com.oresomecraft.ism.Gamemode;
import com.oresomecraft.ism.ISM;
import com.oresomecraft.ism.Storage;
import com.oresomecraft.ism.event.RoundBeginEvent;
import com.oresomecraft.ism.event.RoundStartEvent;
import com.oresomecraft.ism.object.iPlayer;
import com.oresomecraft.ism.util.Utility;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Random;

public class RoundHandler implements Listener {

    ISM plugin;

    public RoundHandler(ISM pl) {
        plugin = pl;
        pl.getServer().getPluginManager().registerEvents(this, pl);
    }

    public static BukkitTask currentTask = null;

    public static void start(String map, long ID) {
        Storage.roundStatus = "Starting";
        Storage.currentRound = map;
        Storage.currentCreators.clear();
        Storage.currentCreators.addAll(Storage.creators.get(map));
        Storage.roundID = ID;
        Storage.currentGamemode = Storage.gameTypes.get(map);
        Bukkit.getPluginManager().callEvent(new RoundStartEvent());
    }

    public static void beginStartingTask() {
        if (currentTask != null) return;
        currentTask = new BukkitRunnable() {
            int count = 20;

            public void run() {
                if (count == 20 || count == 15 || count <= 5 && count != 0) {
                    if (count <= 5) for (Player p : Bukkit.getOnlinePlayers()) {
                        p.playSound(p.getLocation(), Sound.NOTE_PIANO, 1L, 1L);
                    }
                    Bukkit.broadcastMessage(ChatColor.YELLOW + "[" + ChatColor.GOLD + "ISM" + ChatColor.YELLOW + "] Round start: " + ChatColor.RED + count);
                }
                if (count == 0) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.playSound(p.getLocation(), Sound.NOTE_PIANO, 3L, 3L);
                    }
                    Utility.callBegin();
                    Bukkit.broadcastMessage(ChatColor.YELLOW + "[" + ChatColor.GOLD + "ISM" + ChatColor.YELLOW + "] The round has begun!");
                    this.cancel();
                    currentTask = null;
                }
                count = count - 1;
            }
        }.runTaskTimer(ISM.getInstance(), 20, 20);
    }

    public static void cycleToNextMap() {
        Storage.roundStatus = "Cycling";
        if (currentTask != null) {
            currentTask.cancel();
            currentTask = null;
        }
        final long previousMapID = Storage.roundID;
        if (Storage.gTo.equals("None")) {
            Storage.gTo = Storage.maps.get(new Random().nextInt(Storage.maps.size()));
        } else {
            //A map was set.
        }
        final long ID = Utility.generateID();
        Storage.roundID = ID;
        Storage.currentRound = Storage.gTo;
        Bukkit.broadcastMessage(ChatColor.YELLOW + "[" + ChatColor.GOLD + "ISM" + ChatColor.YELLOW + "] The round is now over and the next map is about to begin!");
        Bukkit.broadcastMessage(ChatColor.GREEN + "Type " + ChatColor.GOLD + "/g " + Storage.gTo + ChatColor.GREEN + " or wait 15 seconds to be transferred");
        if (Storage.natural.contains(Storage.gTo)) {
            MapHandler.loadMap(Storage.gTo, ID + "", true);
        } else {
            MapHandler.loadMap(Storage.gTo, ID + "", false);
        }
        new BukkitRunnable() {
            int count = 5;

            public void run() {
                if (count == 0) {
                    this.cancel();
                    return;
                }
                if (Bukkit.getWorld(previousMapID + "").getPlayers().size() == 0) {
                    this.cancel();
                    return;
                }
                int count2 = 6;
                while (count2 > 0) {
                    Utility.ffw(Bukkit.getWorld(previousMapID + "").getPlayers().get(
                            new Random().nextInt(Bukkit.getWorld(previousMapID + "").getPlayers().size())).getLocation(), Color.GREEN);
                    count2--;
                }
                count--;
            }
        }.runTaskTimer(ISM.getInstance(), 0, 3 * 20L);
        Bukkit.getScheduler().runTaskLater(ISM.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.getWorld().getName().equals(previousMapID + "")) {
                        Utility.teleportToSpawn(iPlayer.getIPlayer(p));
                        Utility.handKit(iPlayer.getIPlayer(p));
                    }
                }
                start(Storage.gTo, ID);
                MapHandler.restoreMap(previousMapID + "");
                Storage.gTo = "None";
            }
        }, 15 * 20L);
    }

    @EventHandler
    public void start(RoundStartEvent e) {
        Gamemode.startRoundAccordingToType(Storage.gameTypes.get(Storage.currentRound));
        beginStartingTask();
    }

    @EventHandler
    public void begin(RoundBeginEvent e) {
        Storage.roundStatus = "Started";
        if (currentTask != null) currentTask.cancel();
    }
}
