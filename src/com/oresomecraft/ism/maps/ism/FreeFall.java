package com.oresomecraft.ism.maps.ism;

import com.oresomecraft.ism.ISM;
import com.oresomecraft.ism.Storage;
import com.oresomecraft.ism.event.GlobalRoundFinishEvent;
import com.oresomecraft.ism.event.PlayerLeaveEvent;
import com.oresomecraft.ism.event.RoundBeginEvent;
import com.oresomecraft.ism.maps.Map;
import com.oresomecraft.ism.object.CuboidRegion;
import com.oresomecraft.ism.object.iPlayer;
import com.oresomecraft.ism.util.LocationUtil;
import com.oresomecraft.ism.util.Utility;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class FreeFall extends Map implements Listener {
    public FreeFall(String map) {
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
    public ArrayList<String> cooldown = new ArrayList<String>();
    public String leader = "No one";

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        if (!pass || !iPlayer.getIPlayer(event.getPlayer()).inGame) return;
        if (event.getPlayer().getItemInHand().getType().equals(Material.EMPTY_MAP)) {
            event.setCancelled(true);
            event.getPlayer().updateInventory();
        }
        if (event.getPlayer().getItemInHand().getType().equals(Material.FEATHER)) {
            event.getPlayer().sendMessage(ChatColor.RED + "You decided to try again!");
            Utility.teleportToSpawn(iPlayer.getIPlayer(event.getPlayer()));
            Utility.handKit(iPlayer.getIPlayer(event.getPlayer()));
            kills.remove(event.getPlayer().getName());
            kills.put(event.getPlayer().getName(), 0);
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

    CuboidRegion[] greenRings = new CuboidRegion[]{new CuboidRegion(2, 240, -24, 4, 244, -25), new CuboidRegion(-4, 208, -93, -2, 212, -94),
            new CuboidRegion(-31, 172, -163, -29, 176, -165),
            new CuboidRegion(-4, 136, -240, -2, 140, -241),
            new CuboidRegion(-32, 113, -274, -30, 114, -270),
            new CuboidRegion(17, 76, -305, 13, 77, -307),
            new CuboidRegion(-29, 42, -362, -33, 43, -360), new CuboidRegion(-21, 30, -373, -17, 31, -375)};

    CuboidRegion[] redRings = new CuboidRegion[]{new CuboidRegion(-13, 238, -33, -9, 242, -35), new CuboidRegion(-14, 198, -93, -10, 202, -94),
            new CuboidRegion(-25, 163, -166, -21, 167, -167), new CuboidRegion(-26, 131, -238, -22, 135, -239),
            new CuboidRegion(1, 108, -274, 5, 109, -278), new CuboidRegion(-9, 74, -303, -5, 75, -307),
            new CuboidRegion(7, 41, -331, 11, 42, -355)};

    CuboidRegion[] yellowRings = new CuboidRegion[]{new CuboidRegion(-22, 238, -28, -20, 242, -29), new CuboidRegion(-29, 203, -81, -23, 209, -82),
            new CuboidRegion(-3, 170, -170, 3, 176, -171), new CuboidRegion(-19, 35, -331, -13, 36, -337),
            new CuboidRegion(-29, 68, -314, -35, 68, -308)};

    @EventHandler
    public void parachute(PlayerMoveEvent e) {
        if (!pass || !iPlayer.getIPlayer(e.getPlayer()).inGame) return;
        if (e.getPlayer().getVelocity().getY() < 0.3 && e.getPlayer().getLocation().subtract(0, 1, 0).getBlock().getType() == Material.AIR) {
            if (e.getPlayer().getItemInHand().getType().toString().equals("EMPTY_MAP")) {
                Vector velocity = new Vector();
                velocity = e.getPlayer().getLocation().getDirection().multiply(1.01);
                velocity.setY(-0.3);
                e.getPlayer().setVelocity(velocity);
                e.getPlayer().setFallDistance(0);
            }
        } else if (e.getPlayer().getLocation().subtract(0, 1, 0).getBlock().getType() == Material.WOOL && e.getPlayer().getLocation().getY() > 16) {
            Vector velocity = new Vector();
            e.getPlayer().setVelocity(e.getPlayer().getVelocity().setZ(-1));
            e.getPlayer().setFallDistance(0);
        }
        final Player p = e.getPlayer();
        int amount = 0;
        for (CuboidRegion c : Arrays.asList(greenRings)) {
            if (contains(e.getPlayer().getLocation(), c.x1, c.x2, c.y1, c.y2, c.z1, c.z2) && !cooldown.contains(p.getName())) {
                p.sendMessage(ChatColor.GREEN + "Green" + ChatColor.YELLOW + " ring! " + ChatColor.GOLD + "+20");
                amount = 20;
                cooldown.add(p.getName());
                new BukkitRunnable() {
                    public void run() {
                        cooldown.remove(p.getName());
                    }
                }.runTaskLater(plugin, 20);
                break;
            }
        }
        for (CuboidRegion c : Arrays.asList(redRings)) {
            if (contains(e.getPlayer().getLocation(), c.x1, c.x2, c.y1, c.y2, c.z1, c.z2) && !cooldown.contains(p.getName())) {
                p.sendMessage(ChatColor.RED + "Red" + ChatColor.YELLOW + " ring! " + ChatColor.GOLD + "+10");
                amount = 10;
                cooldown.add(p.getName());
                new BukkitRunnable() {
                    public void run() {
                        cooldown.remove(p.getName());
                    }
                }.runTaskLater(plugin, 20);
                break;
            }
        }
        for (CuboidRegion c : Arrays.asList(yellowRings)) {
            if (contains(e.getPlayer().getLocation(), c.x1, c.x2, c.y1, c.y2, c.z1, c.z2) && !cooldown.contains(p.getName())) {
                p.sendMessage(ChatColor.YELLOW + "Yellow ring! " + ChatColor.GOLD + "+5");
                amount = 5;
                cooldown.add(p.getName());
                new BukkitRunnable() {
                    public void run() {
                        cooldown.remove(p.getName());
                    }
                }.runTaskLater(plugin, 20);
                break;
            }
        }
        int maxValueInMap;
        try {
            maxValueInMap = (Collections.max(kills.values()));
        } catch (Exception ex) {
            maxValueInMap = 0;
        }
        Block touch = e.getPlayer().getLocation().subtract(0, 1, 0).getBlock();
        Block centre = e.getPlayer().getWorld().getBlockAt(-10, 13, -342);
        if (e.getPlayer().getLocation().getBlockY() <= 25) {
            if (touch.getType() == Material.WOOL && e.getPlayer().getInventory().contains(Material.EMPTY_MAP)) {
                e.getPlayer().getInventory().remove(Material.EMPTY_MAP);
                ItemStack FEATHER = new ItemStack(Material.FEATHER, 1);
                ItemMeta IM = FEATHER.getItemMeta();
                IM.setDisplayName(ChatColor.BLUE + "RIGHT CLICK THIS FEATHER TO RESET SCORE AND GO AGAIN!");
                FEATHER.setItemMeta(IM);
                e.getPlayer().getInventory().addItem(FEATHER);
                new BukkitRunnable() {
                    int count = 5;

                    public void run() {
                        if (count == 0) {
                            Bukkit.getScheduler().cancelTask(this.getTaskId());
                            return;
                        }
                        count--;
                        Utility.ffw(p.getLocation(), Color.BLUE);
                    }
                }.runTaskTimer(ISM.getInstance(), 2, 2);
                double disSqu = centre.getLocation().distanceSquared(touch.getLocation());
                if (disSqu <= 1.5) {
                    p.sendMessage(ChatColor.RED + "BULLSEYE! " + ChatColor.GOLD + "+30");
                    amount = 30;
                    Bukkit.broadcastMessage(p.getDisplayName() + ChatColor.YELLOW + " landed with a score of " + ChatColor.RED + (kills.get(p.getName()) + amount));
                } else {
                    p.sendMessage(ChatColor.RED + "Sketchy landing... " + ChatColor.GOLD + "+5");
                    amount = 5;
                    Bukkit.broadcastMessage(p.getDisplayName() + ChatColor.YELLOW + " landed with a score of " + ChatColor.RED + (kills.get(p.getName()) + amount));
                }
            } else if (touch.getType() == Material.GRASS || touch.getType() == Material.LOG || touch.getType() == Material.LEAVES) {
                if (e.getPlayer().getInventory().contains(Material.EMPTY_MAP)) {
                    e.getPlayer().getInventory().remove(Material.EMPTY_MAP);
                    ItemStack FEATHER = new ItemStack(Material.FEATHER, 1);
                    ItemMeta IM = FEATHER.getItemMeta();
                    IM.setDisplayName(ChatColor.BLUE + "RIGHT CLICK THIS FEATHER TO RESET SCORE AND GO AGAIN!");
                    p.sendMessage(ChatColor.RED + "Terrible landing... " + ChatColor.GOLD + "+0");
                    FEATHER.setItemMeta(IM);
                    e.getPlayer().getInventory().addItem(FEATHER);
                    Bukkit.broadcastMessage(p.getDisplayName() + ChatColor.YELLOW + " landed with a score of " + ChatColor.RED + kills.get(p.getName()));
                }
            }
        }
        if (kills.containsKey(p.getName())) {
            int k = kills.get(p.getName());
            kills.remove(p.getName());
            kills.put(p.getName(), k + amount);
        }
        if (!kills.containsKey(p.getName())) {
            kills.put(p.getName(), amount);
        }
        HashMap<String, Integer> temp = new HashMap<String, Integer>(kills);
        for (java.util.Map.Entry<String, Integer> entry : temp.entrySet()) {
            if (entry.getValue() > maxValueInMap && !leader.equals(p.getName())) {
                leader = p.getName();
                callLead(p);
            }
        }
    }
}
