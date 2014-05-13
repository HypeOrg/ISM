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
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;

public class TopSnipe extends Map implements Listener {
    public TopSnipe(String map) {
        super(map);
    }


    public HashMap<String, Integer> points = new HashMap<String, Integer>();
    public String leader = "No one";

    public Map config = super.config;
    public boolean pass = false;

    int count = 241;

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
                    HashMap<String, Integer> temp = new HashMap<String, Integer>(points);
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
                if (count == 240 || count == 180 || count == 120 || count == 60 || count == 270 ||
                        count == 210 || count == 150 || count == 90 || count == 30) {
                    doRingThrow();
                }
                if (count == 235 || count == 175 || count == 115 || count == 55 || count == 265 ||
                        count == 205 || count == 145 || count == 85 || count == 25) {
                    doThrowIn();
                }
                if (count == 230 || count == 170 || count == 110 || count == 50 || count == 260 ||
                        count == 200 || count == 140 || count == 80 || count == 20) {
                    doDiagThrowIn();
                }
                if (count == 225 || count == 165 || count == 105 || count == 45 || count == 255 ||
                        count == 195 || count == 135 || count == 75 || count == 15) {
                    doDiagThrowIn();
                    doThrowIn();
                    doRingThrow();
                }
                if (count == 220 || count == 160 || count == 100 || count == 40 || count == 250 ||
                        count == 190 || count == 130 || count == 70 || count == 10) {
                    doSideThrow();
                    doRingThrow();
                }
                if (count == 215 || count == 155 || count == 95 || count == 35 || count == 245 ||
                        count == 185 || count == 125 || count == 65 || count == 5) {
                    doSnakeThrow();
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
    public void damage2(EntityDamageEvent event) {
        if (event.getEntity() instanceof Spider && event.getCause() == EntityDamageEvent.DamageCause.FALL)
            ((Spider) event.getEntity()).damage(200);
    }

    @EventHandler
    public void damage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Spider)) return;
        Spider s = (Spider) event.getEntity();
        if (!(event.getDamager() instanceof Arrow)) return;
        Arrow a = (Arrow) event.getDamager();
        if (!(a.getShooter() instanceof Player)) return;
        final Player p = (Player) a.getShooter();
        int maxValueInMap;
        try {
            maxValueInMap = (Collections.max(points.values()));
        } catch (Exception ex) {
            maxValueInMap = 0;
        }
        if (points.containsKey(p.getName())) {
            int k = points.get(p.getName());
            points.remove(p.getName());
            points.put(p.getName(), k + 1);
        }
        if (!points.containsKey(p.getName())) {
            points.put(p.getName(), 1);
        }
        p.sendMessage(ChatColor.YELLOW + "Sniped! " + ChatColor.GOLD + "+1");
        Utility.ffw(p.getLocation(), Color.OLIVE);
        HashMap<String, Integer> temp = new HashMap<String, Integer>(points);
        for (java.util.Map.Entry<String, Integer> entry : temp.entrySet()) {
            if (entry.getValue() > maxValueInMap && !leader.equals(p.getName())) {
                leader = p.getName();
                callLead(p);
            }
        }
        s.damage(200);
    }

    @EventHandler
    public void hit(ProjectileHitEvent event) {
        event.getEntity().remove();
    }

    @EventHandler
    public void interact(final PlayerInteractEvent event) {
        if (!pass) return;
        if (event.getPlayer().getItemInHand().getType() == Material.EMERALD) {
            int count = 30;
            while (count > 0) {
                count--;
                double x = Math.random() / 1.2;
                double z = Math.random() / 1.2;

                //Half chance of making it minus; locations go into the minuses too, you know?
                if (Math.random() >= 0.5) x = x - (x * 2);
                if (Math.random() >= 0.5) z = z - (z * 2);
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.COW_WALK, 5L, 5L);
                Arrow a = (Arrow) event.getPlayer().getWorld().spawnEntity(event.getPlayer().getLocation().add(0, 2.2, 0), EntityType.ARROW);
                a.setShooter(event.getPlayer());
                a.setVelocity(new Vector(x, Math.random(), z));
            }
            ItemStack i = event.getPlayer().getItemInHand();
            i.setAmount(i.getAmount() - 1);
            event.getPlayer().getInventory().setItemInHand(i);
            return;
        }
        if (event.getPlayer().getItemInHand().getType() == Material.NETHER_BRICK_ITEM) {
            new BukkitRunnable() {
                int count = 45;

                public void run() {
                    if (count == 0) this.cancel();
                    event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.SHOOT_ARROW, 1L, 1L);
                    event.getPlayer().shootArrow();
                    count--;
                }
            }.runTaskTimer(ISM.getInstance(), 0, 2L);
            ItemStack i = event.getPlayer().getItemInHand();
            i.setAmount(i.getAmount() - 1);
            event.getPlayer().getInventory().setItemInHand(i);
            return;
        }
    }

    @EventHandler
    public void join(PlayerJoinEvent event) {
        if (pass) {
            if (!points.containsKey(event.getPlayer())) points.put(event.getPlayer().getName(), 0);
        }
    }

    @EventHandler
    public void quit(PlayerQuitEvent event) {
        if (!pass) return;
        points.remove(event.getPlayer().getName());
        if (leader.equals(event.getPlayer().getName())) leader = "No one";
    }

    @EventHandler
    public void leave(PlayerLeaveEvent event) {
        if (!pass) return;
        points.remove(event.getPlayer().getName());
        if (leader.equals(event.getPlayer().getName())) leader = "No one";
    }

    private void doRingThrow() {
        new BukkitRunnable() {
            int count = 0;

            public void run() {
                if (count == 7) this.cancel();
                throwRing(count);
                count++;
            }
        }.runTaskTimer(ISM.getInstance(), 0, 5L);
    }

    private void doThrowIn() {
        new BukkitRunnable() {
            int count = 0;

            public void run() {
                if (count == 7) this.cancel();
                throwIn(count);
                count++;
            }
        }.runTaskTimer(ISM.getInstance(), 0, 5L);
    }

    private void doSnakeThrow() {
        doThrowIn();
        doRingThrow();
        new BukkitRunnable() {
            int count = 0;

            public void run() {
                if (count == 4) this.cancel();
                throwIn(0);
                throwIn(1);
                throwIn(2);
                throwIn(3);
                count++;
            }
        }.runTaskTimer(ISM.getInstance(), 0, 2L);
        new BukkitRunnable() {
            public void run() {
                new BukkitRunnable() {
                    int count = 0;

                    public void run() {
                        if (count == 4) this.cancel();
                        throwIn(0);
                        throwIn(1);
                        throwIn(2);
                        throwIn(3);
                        count++;
                    }
                }.runTaskTimer(ISM.getInstance(), 0, 2L);
            }
        }.runTaskLater(ISM.getInstance(), 40L);
    }

    private void doSideThrow() {
        new BukkitRunnable() {
            int count = 0;

            public void run() {
                if (count == 7) this.cancel();
                throwSide(count);
                count++;
            }
        }.runTaskTimer(ISM.getInstance(), 0, 5L);
    }

    private void doDiagThrowIn() {
        new BukkitRunnable() {
            public void run() {
                throwIn(0);
                throwIn(1);
                throwIn(4);
                throwIn(5);
            }
        }.runTaskLater(ISM.getInstance(), 20L);
        new BukkitRunnable() {
            public void run() {
                throwIn(2);
                throwIn(3);
                throwIn(6);
                throwIn(7);
            }
        }.runTaskLater(ISM.getInstance(), 60L);
    }

    HashMap<Integer, CuboidRegion> ring = new HashMap<Integer, CuboidRegion>();

    {
        ring.put(0, new CuboidRegion(10, 63, -30, 10, 63, -30));
        ring.put(1, new CuboidRegion(30, 63, -10, 30, 63, -10));
        ring.put(2, new CuboidRegion(30, 63, 10, 30, 63, 10));
        ring.put(3, new CuboidRegion(10, 63, 30, 10, 63, 30));
        ring.put(4, new CuboidRegion(-10, 63, 30, -10, 63, 30));
        ring.put(5, new CuboidRegion(-30, 63, 10, -30, 63, 10));
        ring.put(6, new CuboidRegion(-30, 63, -10, -30, 63, -10));
        ring.put(7, new CuboidRegion(-10, 63, -30, -10, 64, -30));
    }

    private void throwRing(int count) {
        Location l = ring.get(count).getBlocks().get(0).getLocation();
        final Spider sp = (Spider) l.getWorld().spawnEntity(l, EntityType.SPIDER);
        sp.setFireTicks(200);
        sp.setPassenger(null);
        sp.setVelocity(new Vector(0, 2, 0));
        sp.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 10000 * 20, 10000 * 20));
        new BukkitRunnable() {
            public void run() {
                if (sp.isDead() || sp == null) {
                } else {
                    sp.damage(200);
                }
            }
        }.runTaskLater(ISM.getInstance(), 4 * 20L);
    }

    private void throwIn(int count) {
        Location l = ring.get(count).getBlocks().get(0).getLocation();
        final Spider sp = (Spider) l.getWorld().spawnEntity(l, EntityType.SPIDER);
        sp.setPassenger(null);
        sp.setFireTicks(200);
        sp.setVelocity(towardsMiddle(count));
        sp.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 10000 * 20, 10000 * 20));
        new BukkitRunnable() {
            public void run() {
                if (sp.isDead() || sp == null) {
                } else {
                    sp.damage(200);
                }
            }
        }.runTaskLater(ISM.getInstance(), 4 * 20L);
    }

    private void throwSide(int count) {
        Location l = ring.get(count).getBlocks().get(0).getLocation();
        final Spider sp = (Spider) l.getWorld().spawnEntity(l, EntityType.SPIDER);
        sp.setPassenger(null);
        sp.setFireTicks(200);
        sp.setVelocity(towardsSide(count));
        sp.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 10000 * 20, 10000 * 20));
        new BukkitRunnable() {
            public void run() {
                if (sp.isDead() || sp == null) {
                } else {
                    sp.damage(200);
                }
            }
        }.runTaskLater(ISM.getInstance(), 4 * 20L);
    }

    private Vector towardsMiddle(int count) {
        if (count == 0) return new Vector().setX(-0.4).setZ(1.9).setY(2.1);
        if (count == 1) return new Vector().setX(-1.9).setZ(0.4).setY(2.1);

        if (count == 2) return new Vector().setX(-1.9).setZ(-0.4).setY(2.1);
        if (count == 3) return new Vector().setX(-0.4).setZ(-1.9).setY(2.1);

        if (count == 4) return new Vector().setX(0.4).setZ(-1.9).setY(2.1);
        if (count == 5) return new Vector().setX(1.9).setZ(-0.4).setY(2.1);

        if (count == 6) return new Vector().setX(1.9).setZ(0.4).setY(2.1);
        if (count == 7) return new Vector().setX(0.4).setZ(1.9).setY(2.1);
        return new Vector();
    }

    private Vector towardsSide(int count) {
        if (count == 0) return new Vector().setX(-1.9).setZ(0.4).setY(2.6);
        if (count == 1) return new Vector().setX(-0.4).setZ(1.9).setY(2.6);

        if (count == 2) return new Vector().setX(-0.4).setZ(-1.9).setY(2.6);
        if (count == 3) return new Vector().setX(-1.9).setZ(-0.4).setY(2.6);

        if (count == 4) return new Vector().setX(1.9).setZ(-0.4).setY(2.6);
        if (count == 5) return new Vector().setX(0.4).setZ(-1.9).setY(2.6);

        if (count == 6) return new Vector().setX(0.4).setZ(1.9).setY(2.6);
        if (count == 7) return new Vector().setX(1.9).setZ(0.4).setY(2.6);
        return new Vector();
    }
}