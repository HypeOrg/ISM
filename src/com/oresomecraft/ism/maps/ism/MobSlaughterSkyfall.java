package com.oresomecraft.ism.maps.ism;

import com.oresomecraft.ism.ISM;
import com.oresomecraft.ism.Storage;
import com.oresomecraft.ism.event.GlobalRoundFinishEvent;
import com.oresomecraft.ism.event.PlayerLeaveEvent;
import com.oresomecraft.ism.event.RoundBeginEvent;
import com.oresomecraft.ism.handlers.RoundHandler;
import com.oresomecraft.ism.maps.Map;
import com.oresomecraft.ism.object.CuboidRegion;
import com.oresomecraft.ism.object.iPlayer;
import com.oresomecraft.ism.util.LocationUtil;
import com.oresomecraft.ism.util.Utility;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.*;

public class MobSlaughterSkyfall extends Map implements Listener {
    public MobSlaughterSkyfall(String map) {
        super(map);
    }

    public Map config = super.config;
    public boolean pass = false;

    int count = 301;

    @EventHandler
    public void start(RoundBeginEvent event) {
        start();
    }

    EntityType[] mobs = new EntityType[]{EntityType.PIG, EntityType.COW, EntityType.SHEEP};

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
                if (count == 299 || count == 240 || count == 180 || count == 120 || count == 60 || count == 270 || count == 210 || count == 150 || count == 90 || count == 30) {
                    int count = 20;
                    while (count > 0) {
                        count--;
                        CuboidRegion cu = Storage.regions.get(Storage.currentRound);
                        int x = grpnv(cu.x1, cu.x2);
                        int z = grpnv(cu.z1, cu.z2);
                        try {
                            Bukkit.getWorld(Storage.roundID + "").spawnEntity(LocationUtil.getSafeDestination(
                                    new Location(Bukkit.getWorld(Storage.roundID + ""), x, 200, z)).add(0, 10, 0), Arrays.asList(mobs).get(new Random().nextInt(3)));
                        } catch (IllegalArgumentException ex) {
                            count++;
                        } catch (NullPointerException ex) {
                            count++;
                        }
                    }
                }
                if (count == 300 || count == 240 || count == 180 || count == 120 || count == 60) {
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
    public String leader = "No one";

    @EventHandler
    public void quit(PlayerQuitEvent event) {
        if (!pass) return;
        kills.remove(event.getPlayer().getName());
        if (leader.equals(event.getPlayer().getName())) leader = "No one";
    }

    @EventHandler
    public void leave(PlayerLeaveEvent event) {
        if (!pass) return;
        kills.remove(event.getPlayer().getName());
        if (leader.equals(event.getPlayer().getName())) leader = "No one";
    }

    Material[] allowed = new Material[]{Material.PORK, Material.LEATHER, Material.RAW_BEEF, Material.WOOL};

    @EventHandler
    public void pickup(PlayerPickupItemEvent event) {
        if (Arrays.asList(allowed).contains(event.getItem().getItemStack().getType())) {
            final Player p = event.getPlayer();
            event.setCancelled(true);
            event.getItem().remove();
            event.getPlayer().getWorld().playSound(p.getLocation(), Sound.ITEM_PICKUP, 1L, 1L);
            if (!pass) return;
            int maxValueInMap;
            int amount = event.getItem().getItemStack().getAmount();
            try {
                maxValueInMap = (Collections.max(kills.values()));
            } catch (Exception e) {
                maxValueInMap = 0;
            }
            if (kills.containsKey(p.getName())) {
                int k = kills.get(p.getName());
                kills.remove(p.getName());
                kills.put(p.getName(), k + amount);
            }
            if (!kills.containsKey(p.getName())) {
                kills.put(p.getName(), amount);
            }
            p.sendMessage(ChatColor.RED + event.getItem().getItemStack().getType().toString().replaceAll("_", " ").replaceAll("ORE", "").toLowerCase() + ChatColor.YELLOW + " was obtained! " + ChatColor.GOLD + "+" + amount);
            HashMap<String, Integer> temp = new HashMap<String, Integer>(kills);
            for (java.util.Map.Entry<String, Integer> entry : temp.entrySet()) {
                if (entry.getValue() > maxValueInMap && !leader.equals(p.getName())) {
                    leader = p.getName();
                    callLead(p);
                }
            }
        }
    }

    @EventHandler
    public void kill(EntityDeathEvent event) {
        if (!pass) return;
        if (Arrays.asList(mobs).contains(event.getEntityType()) && event.getEntity().getKiller() != null) {
            final Player p = event.getEntity().getKiller();
            int maxValueInMap;
            event.getEntity().getKiller().getWorld().playSound(p.getLocation(), Sound.ANVIL_LAND, 1L, 1L);
            try {
                maxValueInMap = (Collections.max(kills.values()));
            } catch (Exception e) {
                maxValueInMap = 0;
            }
            if (kills.containsKey(p.getName())) {
                int k = kills.get(p.getName());
                kills.remove(p.getName());
                kills.put(p.getName(), k + 1);
            }
            if (!kills.containsKey(p.getName())) {
                kills.put(p.getName(), 1);
            }
            p.sendMessage(ChatColor.RED + event.getEntity().getType().toString().toLowerCase().replaceAll("_", " ") + ChatColor.YELLOW + " was killed! " + ChatColor.GOLD + "+1");
            HashMap<String, Integer> temp = new HashMap<String, Integer>(kills);
            for (java.util.Map.Entry<String, Integer> entry : temp.entrySet()) {
                if (entry.getValue() > maxValueInMap && !leader.equals(p.getName())) {
                    leader = p.getName();
                    callLead(p);
                }
            }
        }
    }
}
