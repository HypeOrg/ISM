package com.oresomecraft.ism.maps.ism;

import com.oresomecraft.ism.ISM;
import com.oresomecraft.ism.Storage;
import com.oresomecraft.ism.event.GlobalRoundFinishEvent;
import com.oresomecraft.ism.event.PlayerLeaveEvent;
import com.oresomecraft.ism.event.RoundBeginEvent;
import com.oresomecraft.ism.maps.Map;
import com.oresomecraft.ism.object.CuboidRegion;
import com.oresomecraft.ism.object.ManualCuboid;
import com.oresomecraft.ism.object.iPlayer;
import com.oresomecraft.ism.util.LocationUtil;
import com.oresomecraft.ism.util.Utility;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.*;
import org.bukkit.util.Vector;

import java.util.*;

public class TroubledBridges extends Map implements Listener {
    public TroubledBridges(String map) {
        super(map);
    }


    public HashMap<String, ArrayList<Block>> points = new HashMap<String, ArrayList<Block>>();
    public String leader = "No one";

    public Map config = super.config;
    public boolean pass = false;

    int count = 181;

    String cap1 = "None";
    String cap2 = "None";
    String cap3 = "None";

    @EventHandler
    public void start(RoundBeginEvent event) {
        start();
    }

    private void start() {
        if (pass) return;
        for (Player p : Bukkit.getOnlinePlayers()) {
            points.put(p.getName(), new ArrayList<Block>());
            if (p.getLocation().getBlockX() < -2) Utility.teleportToSpawn(iPlayer.getIPlayer(p));
        }
        RoundBeginEvent.getHandlerList().unregister(this);
        pass = true;
        BukkitTask task = new BukkitRunnable() {
            public void run() {
                if (count == 0) {
                    Bukkit.broadcastMessage(ChatColor.YELLOW + "[" + ChatColor.GOLD + "ISM" + ChatColor.YELLOW + "] " + ChatColor.RED + " GAME OVER!");
                    Bukkit.broadcastMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "    Congratulations to our winners!");
                    tasks.get(0).cancel();
                    HashMap<String, ArrayList<Block>> temp = new HashMap<String, ArrayList<Block>>(points);
                    String winner = "No one";
                    try {
                        int max = 0;
                        for (ArrayList<Block> ab : temp.values()) {
                            if (ab.size() > max) max = ab.size();
                        }
                        for (java.util.Map.Entry<String, ArrayList<Block>> entry : temp.entrySet()) {
                            if (entry.getValue().size() == max) {
                                winner = entry.getKey();
                                temp.remove(entry.getKey());
                                Bukkit.broadcastMessage(ChatColor.GOLD + " 1st Place: " + ChatColor.AQUA + entry.getKey() +
                                        ChatColor.GREEN + " (" + entry.getValue().size() + ")");
                                if (iPlayer.getIPlayer(entry.getKey()) != null)
                                    iPlayer.getIPlayer(entry.getKey()).gold++;
                                break;
                            }
                        }
                    } catch (Exception e) {
                        Bukkit.broadcastMessage(ChatColor.GOLD + " 1st Place: " + ChatColor.AQUA + "No one");
                    }
                    try {
                        int max2 = 0;
                        for (ArrayList<Block> ab : temp.values()) {
                            if (ab.size() > max2) max2 = ab.size();
                        }
                        if (temp.values().size() == 0) throw new Exception("Not enough players");
                        for (java.util.Map.Entry<String, ArrayList<Block>> entry : temp.entrySet()) {
                            if (entry.getValue().size() == max2) {
                                Bukkit.broadcastMessage(ChatColor.GRAY + "  2nd Place: " + ChatColor.AQUA + entry.getKey() +
                                        ChatColor.GREEN + " (" + entry.getValue().size() + ")");
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
                        int max3 = 0;
                        for (ArrayList<Block> ab : temp.values()) {
                            if (ab.size() > max3) max3 = ab.size();
                        }
                        if (temp.values().size() == 0) throw new Exception("Not enough players");
                        for (java.util.Map.Entry<String, ArrayList<Block>> entry : temp.entrySet()) {
                            if (entry.getValue().size() == max3) {
                                Bukkit.broadcastMessage(ChatColor.DARK_RED + " 3rd Place: " + ChatColor.AQUA + entry.getKey() +
                                        ChatColor.GREEN + " (" + entry.getValue().size() + ")");
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
                if (count == 180 || count == 120 || count == 60 || count == 150 || count == 90 || count == 30) {
                    int count = 3;
                    while (count > 0) {
                        count--;
                        CuboidRegion cu = Storage.regions.get(Storage.currentRound);
                        int x = grpnv(cu.x1, cu.x2);
                        int z = grpnv(cu.z1, cu.z2);
                        try {
                            Bukkit.getWorld(Storage.roundID + "").spawnEntity(LocationUtil.getSafeDestination(
                                    new Location(Bukkit.getWorld(Storage.roundID + ""), x, 200, z)).add(0, 19, 0), EntityType.ZOMBIE);
                        } catch (IllegalArgumentException ex) {
                            count++;
                        } catch (NullPointerException ex) {
                            count++;
                        }
                    }
                }
                if (count == 165 || count == 105 || count == 45 || count == 135 || count == 75 || count == 15) {
                    CuboidRegion cu = Storage.regions.get(Storage.currentRound);
                    ManualCuboid mc = new ManualCuboid(new Location(w, cu.x1, cu.y1, cu.z1), new Location(w, cu.x2, cu.y2, cu.z2));
                    ArrayList<Block> bl = new ArrayList<Block>(mc.getBlocks());
                    for (Block b : bl) {
                        double rand = Math.random();
                        if (rand > 0.75) {
                            w.spawnFallingBlock(b.getLocation(), b.getType(), b.getData());
                            b.setType(Material.AIR);
                        }
                    }
                }
                if (count == 150 || count == 90 || count == 30) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.playSound(p.getLocation(), Sound.PORTAL_TRAVEL, 2L, 2L);
                    }
                    if (count == 30) {
                        Bukkit.getWorld(Storage.roundID + "").createExplosion(-44, 75, 25, 6F);
                    }
                    if (count == 90) {
                        Bukkit.getWorld(Storage.roundID + "").createExplosion(-27, 75, 25, 6F);
                    }
                    if (count == 150) {
                        Bukkit.getWorld(Storage.roundID + "").createExplosion(-9, 75, 25, 6F);
                    }
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

    @EventHandler
    public void interact(final PlayerInteractEvent event) {
        if (!pass) return;
        if (event.getClickedBlock() == null) return;
        if (event.getClickedBlock().getType() == Material.DIAMOND_BLOCK) {
            HashMap<String, ArrayList<Block>> temp = new HashMap<String, ArrayList<Block>>(points);
            int max = 0;
            for (ArrayList<Block> ab : temp.values()) {
                if (ab.size() > max) max = ab.size();
            }
            if (cap1.equals("None")) {
                cap1 = event.getPlayer().getName();
                int count = 15;
                while (count > 0) {
                    count--;
                    points.get(event.getPlayer().getName()).add(event.getPlayer().getWorld().getBlockAt(0, 0, 0));
                }
                event.getPlayer().sendMessage(ChatColor.YELLOW + "Capture!" + ChatColor.GOLD + " +15");
                new BukkitRunnable() {
                    int count = 5;

                    public void run() {
                        if (count == 0) {
                            this.cancel();
                            return;
                        }
                        count--;
                        Utility.ffw(event.getPlayer().getLocation(), Color.AQUA);
                    }
                }.runTaskTimer(ISM.getInstance(), 2, 2);
            }
            if (cap2.equals("None") && !(cap1.equals(event.getPlayer().getName()))) {
                cap1 = event.getPlayer().getName();
                int count = 10;
                while (count > 0) {
                    count--;
                    points.get(event.getPlayer().getName()).add(event.getPlayer().getWorld().getBlockAt(0, 0, 0));
                }
                event.getPlayer().sendMessage(ChatColor.YELLOW + "Capture!" + ChatColor.GOLD + " +10");
                new BukkitRunnable() {
                    int count = 5;

                    public void run() {
                        if (count == 0) {
                            this.cancel();
                            return;
                        }
                        count--;
                        Utility.ffw(event.getPlayer().getLocation(), Color.AQUA);
                    }
                }.runTaskTimer(ISM.getInstance(), 2, 2);
            }
            if (cap3.equals("None") && !(cap1.equals(event.getPlayer().getName())) && !(cap2.equals(event.getPlayer().getName()))) {
                cap1 = event.getPlayer().getName();
                int count = 5;
                while (count > 0) {
                    count--;
                    points.get(event.getPlayer().getName()).add(event.getPlayer().getWorld().getBlockAt(0, 0, 0));
                }
                event.getPlayer().sendMessage(ChatColor.YELLOW + "Capture!" + ChatColor.GOLD + " +5");
                new BukkitRunnable() {
                    int count = 5;

                    public void run() {
                        if (count == 0) {
                            this.cancel();
                            return;
                        }
                        count--;
                        Utility.ffw(event.getPlayer().getLocation(), Color.AQUA);
                    }
                }.runTaskTimer(ISM.getInstance(), 2, 2);
            }
            for (java.util.Map.Entry<String, ArrayList<Block>> entry : temp.entrySet()) {
                if (entry.getValue().size() > max && !leader.equals(event.getPlayer().getName())) {
                    leader = event.getPlayer().getName();
                    callLead(event.getPlayer());
                }
            }
        }
    }

    @EventHandler
    public void join(PlayerJoinEvent event) {
        if (pass) {
            if (!points.containsKey(event.getPlayer())) points.put(event.getPlayer().getName(), new ArrayList<Block>());
        }
    }

    @EventHandler
    public void explode(EntityExplodeEvent event) {
        if (!pass) return;
        event.setCancelled(true);
        List<Block> bl = new ArrayList<Block>();
        ArrayList<Block> remove = new ArrayList<Block>();
        bl.addAll(event.blockList());
        for (Block b : bl) {
            if (b.getType() != Material.STONE) {
                remove.add(b);
            }
        }
        bl.removeAll(remove);
        for (Block b : bl) {
            b.setType(Material.AIR);
        }
        event.getLocation().getWorld().playSound(event.getLocation(), Sound.EXPLODE, 4L, 1L);
    }

    @EventHandler
    public void quit(PlayerQuitEvent event) {
        if (!pass) return;
        for (Block b : points.get(event.getPlayer().getName())) {
            b.setType(Material.AIR);
        }
        points.remove(event.getPlayer().getName());
        if (leader.equals(event.getPlayer().getName())) leader = "No one";
    }

    @EventHandler
    public void slide(PlayerMoveEvent event) {
        Location l = event.getPlayer().getLocation();
        if (!pass) {
            if (l.getBlockY() <= 74) {
                Utility.teleportToSpawn(iPlayer.getIPlayer(event.getPlayer()));
            }
        }
        if (contains(l.subtract(0, 1, 0), -53, -1, 75, 75, 30, 30)) {
            event.getPlayer().setVelocity(event.getPlayer().getVelocity().setZ(0.6));
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.DONKEY_DEATH, 1L, 1L);
        }
        if (contains(l, -1, -51, 75, 75, 20, 20)) {
            event.getPlayer().setVelocity(event.getPlayer().getVelocity().setZ(-0.6));
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.DONKEY_DEATH, 1L, 1L);
        }
        if (!pass) return;
        if (event.getTo().getBlockY() <= 74) {
            if (!event.getPlayer().isDead()) {
                event.getPlayer().setLastDamageCause(new EntityDamageEvent(event.getPlayer(), EntityDamageEvent.DamageCause.CUSTOM, (double) 0));
                event.getPlayer().damage(200);
            }
        }
    }

    @EventHandler
    public void death(PlayerDeathEvent event) {
        if (!pass) return;
        for (Block b : points.get(event.getEntity().getName())) {
            b.setType(Material.AIR);
        }
        points.remove(event.getEntity().getName());
        points.put(event.getEntity().getName(), new ArrayList<Block>());
        if (leader.equals(event.getEntity().getName())) leader = "No one";
    }

    @EventHandler
    public void leave(PlayerLeaveEvent event) {
        if (!pass) return;
        for (Block b : points.get(event.getPlayer().getName())) {
            b.setType(Material.AIR);
        }
        points.remove(event.getPlayer().getName());
        points.put(event.getPlayer().getName(), new ArrayList<Block>());
        if (leader.equals(event.getPlayer().getName())) leader = "No one";
    }

    @EventHandler
    public void place(BlockPlaceEvent event) {
        final Player p = event.getPlayer();
        if (!pass) {
            p.sendMessage(ChatColor.RED + "You have to wait until the round starts before building!");
            event.setCancelled(true);
            return;
        }
        if (contains(event.getBlockPlaced().getLocation(), -1, -54, 75, 75, 21, 29)) {
            HashMap<String, ArrayList<Block>> temp = new HashMap<String, ArrayList<Block>>(points);
            int max = 0;
            for (ArrayList<Block> ab : temp.values()) {
                if (ab.size() > max) max = ab.size();
            }
            points.get(event.getPlayer().getName()).add(event.getBlockPlaced());
            for (java.util.Map.Entry<String, ArrayList<Block>> entry : temp.entrySet()) {
                if (entry.getValue().size() > max && !leader.equals(event.getPlayer().getName())) {
                    leader = p.getName();
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
            }
        } else {
            p.sendMessage(ChatColor.RED + "Don't build here, build the bridge!");
            event.setCancelled(true);
        }
    }
}
