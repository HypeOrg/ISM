package com.oresomecraft.ism.maps.ism;

import com.oresomecraft.ism.ISM;
import com.oresomecraft.ism.Storage;
import com.oresomecraft.ism.event.GlobalRoundFinishEvent;
import com.oresomecraft.ism.event.PlayerLeaveEvent;
import com.oresomecraft.ism.event.RoundBeginEvent;
import com.oresomecraft.ism.maps.Map;
import com.oresomecraft.ism.object.CuboidRegion;
import com.oresomecraft.ism.object.iPlayer;
import com.oresomecraft.ism.util.Utility;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class SuperLunge extends Map implements Listener {
    public SuperLunge(String map) {
        super(map);
    }

    public Map config = super.config;
    public boolean pass = false;

    int count = 181;

    @EventHandler
    public void start(RoundBeginEvent event) {
        start();
    }

    private void start() {
        if (pass) return;
        RoundBeginEvent.getHandlerList().unregister(this);
        pass = true;
        BukkitTask task = new BukkitRunnable() {
            public void run() {
                if (count == 0) {
                    Bukkit.broadcastMessage(ChatColor.YELLOW + "[" + ChatColor.GOLD + "ISM" + ChatColor.YELLOW + "] " + ChatColor.RED + " GAME OVER!");
                    Bukkit.broadcastMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "    Congratulations to our winners!");
                    tasks.get(0).cancel();
                    HashMap<String, Integer> temp = new HashMap<String, Integer>(kills);
                    String winner = "No one";
                    try {
                        int maxValueInMap = (Collections.max(temp.values()));
                        for (java.util.Map.Entry<String, Integer> entry : temp.entrySet()) {
                            if (entry.getValue() == maxValueInMap) {
                                winner = entry.getKey();
                                temp.remove(entry.getKey());
                                Bukkit.broadcastMessage(ChatColor.GOLD + " 1st Place: " + ChatColor.AQUA + entry.getKey() +
                                        ChatColor.GREEN + " (" + entry.getValue() + ")");
                                if (iPlayer.getIPlayer(entry.getKey()) != null)
                                    iPlayer.getIPlayer(entry.getKey()).gold++;
                                break;
                            }
                        }
                    } catch (Exception e) {
                        Bukkit.broadcastMessage(ChatColor.GOLD + " 1st Place: " + ChatColor.AQUA + "No one");
                    }
                    try {
                        int maxValueInMap2 = (Collections.max(temp.values()));
                        for (java.util.Map.Entry<String, Integer> entry : temp.entrySet()) {
                            if (entry.getValue() == maxValueInMap2) {
                                Bukkit.broadcastMessage(ChatColor.GRAY + "  2nd Place: " + ChatColor.AQUA + entry.getKey() +
                                        ChatColor.GREEN + " (" + entry.getValue() + ")");
                                temp.remove(entry.getKey());
                                if (iPlayer.getIPlayer(entry.getKey()) != null)
                                    iPlayer.getIPlayer(entry.getKey()).silver++;
                                break;
                            }
                        }
                    } catch (Exception e) {
                        Bukkit.broadcastMessage(ChatColor.GRAY + "  2nd Place: " + ChatColor.AQUA + "No one");
                    }
                    try {
                        int maxValueInMap3 = (Collections.max(temp.values()));
                        for (java.util.Map.Entry<String, Integer> entry : temp.entrySet()) {
                            if (entry.getValue() == maxValueInMap3) {
                                Bukkit.broadcastMessage(ChatColor.DARK_RED + " 3rd Place: " + ChatColor.AQUA + entry.getKey() +
                                        ChatColor.GREEN + " (" + entry.getValue() + ")");
                                temp.remove(entry.getKey());
                                if (iPlayer.getIPlayer(entry.getKey()) != null)
                                    iPlayer.getIPlayer(entry.getKey()).bronze++;
                                break;
                            }
                        }
                    } catch (Exception e) {
                        Bukkit.broadcastMessage(ChatColor.DARK_RED + "   3rd Place: " + ChatColor.AQUA + "No one");
                    }
                    Bukkit.getPluginManager().callEvent(new GlobalRoundFinishEvent(false, Storage.currentRound, winner, Storage.currentGamemode.toString()));
                    return;
                }
                if (count == 180 || count == 120 || count == 60) {
                    if (count == 60) {
                        Bukkit.broadcastMessage(ChatColor.YELLOW + "[" + ChatColor.GOLD + "ISM" + ChatColor.YELLOW + "] " + ChatColor.RED + (count / 60) + " minute remaining!");
                    } else
                        Bukkit.broadcastMessage(ChatColor.YELLOW + "[" + ChatColor.GOLD + "ISM" + ChatColor.YELLOW + "] " + ChatColor.RED + (count / 60) + " minutes remaining!");
                }
                if (count < 6 || count == 10 || count == 15 || count == 30) {
                    if (count == 1) {
                        Bukkit.broadcastMessage(ChatColor.YELLOW + "[" + ChatColor.GOLD + "ISM" + ChatColor.YELLOW + "] " + ChatColor.RED + count + " second remaining!");
                    } else {
                        Bukkit.broadcastMessage(ChatColor.YELLOW + "[" + ChatColor.GOLD + "ISM" + ChatColor.YELLOW + "] " + ChatColor.RED + count + " seconds remaining!");
                    }
                }
                count--;
            }
        }.runTaskTimer(plugin, 0, 20);
        tasks.add(task);
    }

    public HashMap<String, Integer> kills = new HashMap<String, Integer>();
    public HashMap<String, Integer> charge = new HashMap<String, Integer>();
    public String leader = "No one";

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        if (!pass || !iPlayer.getIPlayer(event.getPlayer()).inGame) return;
        if (charge.containsKey(event.getPlayer().getName())) {
            charge.put(event.getPlayer().getName(), charge.get(event.getPlayer().getName()) + 1);
            event.getPlayer().playSound(event.getPlayer().getLocation(),
                    Sound.NOTE_PIANO, charge.get(event.getPlayer().getName()), charge.get(event.getPlayer().getName()));
        }
        if (kills.containsKey(event.getPlayer().getName())) return;
        if (event.getPlayer().getItemInHand().getType().equals(Material.FEATHER)) {
            event.getPlayer().sendMessage(ChatColor.RED + "You decided to try again!");
            Utility.teleportToSpawn(iPlayer.getIPlayer(event.getPlayer()));
            Utility.handKit(iPlayer.getIPlayer(event.getPlayer()));
            kills.remove(event.getPlayer().getName());
            kills.put(event.getPlayer().getName(), 0);
        }
    }

    @EventHandler
    public void move(PlayerMoveEvent event) {
        if (!pass || !iPlayer.getIPlayer(event.getPlayer()).inGame) return;
        if (!charge.containsKey(event.getPlayer().getName()) && event.getFrom().getZ() >= -4 && event.getFrom().getZ() <= -3) {
            event.getPlayer().sendMessage(ChatColor.GOLD + "Charge up your launch by clicking! Go go go!");
            charge.put(event.getPlayer().getName(), 0);
            charge(event.getPlayer().getName());
        }
    }

    @EventHandler
    public void death(PlayerDeathEvent event) {
        if (!pass) return;
        final Player p = event.getEntity();
        kills.remove(event.getEntity().getName());
        kills.put(event.getEntity().getName(), 0);
        event.getEntity().sendMessage(ChatColor.RED + "You died and your score was reset, try again?");
    }

    @EventHandler
    public void quit(PlayerQuitEvent event) {
        if (!pass) return;
        final Player p = event.getPlayer();
        kills.remove(event.getPlayer().getName());
        if (leader.equals(event.getPlayer().getName())) leader = "No one";
    }

    @EventHandler
    public void leave(PlayerLeaveEvent event) {
        if (!pass) return;
        final Player p = event.getPlayer();
        kills.remove(event.getPlayer().getName());
        if (leader.equals(event.getPlayer().getName())) leader = "No one";
    }

    public void charge(final String player) {
        new BukkitRunnable() {
            public void run() {
                int amount = charge.get(player);
                charge.remove(player);
                Bukkit.getPlayer(player).setVelocity(new Vector(0, 2, amount / 2));
            }
        }.runTaskLater(ISM.getInstance(), 5 * 20L);
    }
}
