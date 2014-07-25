package com.oresomecraft.ism.maps.ism;

import com.oresomecraft.ism.ISM;
import com.oresomecraft.ism.Storage;
import com.oresomecraft.ism.event.GlobalRoundFinishEvent;
import com.oresomecraft.ism.event.PlayerLeaveEvent;
import com.oresomecraft.ism.event.RoundBeginEvent;
import com.oresomecraft.ism.maps.Map;
import com.oresomecraft.ism.object.CuboidRegion;
import com.oresomecraft.ism.object.iPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;

public class BombDrop extends Map implements Listener {
    public BombDrop(String map) {
        super(map);
    }

    public Map config = super.config;
    public boolean pass = false;

    int count = 180;

    @EventHandler
    public void start(RoundBeginEvent event) {
        start();
    }

    private void start() {
        if (pass) return;
        RoundBeginEvent.getHandlerList().unregister(this);
        pass = true;
        runTask();
        doSecond();
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
                if (count == 240 || count == 180 || count == 120 || count == 60) {
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

    public HashMap<String, Integer> points = new HashMap<String, Integer>();
    public HashMap<String, Integer> streak = new HashMap<String, Integer>();

    @EventHandler
    public void death(PlayerDeathEvent event) {
        if (!pass) return;
        streak.remove(event.getEntity().getName());
        streak.put(event.getEntity().getName(), 0);
        event.getEntity().sendMessage(ChatColor.RED + "You died and your survival streak was reset!");
    }

    @EventHandler
    public void quit(PlayerQuitEvent event) {
        if (!pass) return;
        points.remove(event.getPlayer().getName());
    }

    @EventHandler
    public void quit(PlayerJoinEvent event) {
        if (!pass) return;
        points.put(event.getPlayer().getName(), 0);
        streak.put(event.getPlayer().getName(), 0);
    }

    @EventHandler
    public void leave(PlayerLeaveEvent event) {
        if (!pass) return;
        points.remove(event.getPlayer().getName());
    }

    @EventHandler
    public void explode(EntityExplodeEvent event) {
        if (!pass) return;
        List<Block> bl = new ArrayList<Block>();
        ArrayList<Block> remove = new ArrayList<Block>();
        bl.addAll(event.blockList());
        for (Block b : bl) {
            if (b.getType() != Material.SANDSTONE && b.getType() != Material.WOOL) {
                remove.add(b);
            }
        }
        event.blockList().clear();
        event.blockList().addAll(remove);
    }

    public void doSecond() {
        tasks.add(new BukkitRunnable() {
            public void run() {
                for (iPlayer player : ISM.getIPlayers().values()) {
                    if (player.inGame && player != null) {
                        if (!points.containsKey(player.getName()) || !streak.containsKey(player.getName())) {
                            points.put(player.getName(), 0);
                            streak.put(player.getName(), 0);
                        } else {
                            points.put(player.getName(), streak.get(player.getName()) + 1);
                            streak.put(player.getName(), streak.get(player.getName()) + 1);
                        }
                    }
                    assert player != null;
                    player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 100 * 100, 0));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 100 * 100, 0));
                }
            }
        }.runTaskTimer(plugin, 0L, 20L));
    }

    public transient int bombs = 0;

    public void runTask() {
        tasks.add(new BukkitRunnable() {
            public void run() {

                Random rdom = new Random();

                bombs++;

                List<EntityType> type = new ArrayList<EntityType>();

                type.add(EntityType.CREEPER);
                if (bombs > 10) type.add(EntityType.COW);
                if (bombs > 20) type.add(EntityType.PIG);
                if (bombs > 45) {
                    type.add(EntityType.SPIDER);
                }
                if (bombs > 50) {
                    type.add(EntityType.SILVERFISH);
                    type.add(EntityType.CAVE_SPIDER);
                }
                if (bombs > 55) {

                    type.add(EntityType.VILLAGER);
                }
                if (bombs > 60) {
                    type.add(EntityType.MUSHROOM_COW);
                    type.add(EntityType.SLIME);
                }
                if (bombs > 70) type.add(EntityType.SKELETON);

                EntityType T = type.get(rdom.nextInt(type.size()));

                CuboidRegion cu = Storage.regions.get(Storage.currentRound);
                int x = grpnv(cu.x1, cu.x2);
                int z = grpnv(cu.z1, cu.z2);
                Bukkit.getWorld(Storage.roundID + "").spawnEntity(new Location(Bukkit.getWorld(Storage.roundID + ""), x, cu.y1, z), T);
            }
        }.runTaskTimer(plugin, 20 * 2L, 20 * 2L));
    }

    @EventHandler
    public void onDrop(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) return;

        final Entity e = event.getEntity();
        EntityType et = e.getType();
        final Location loc = e.getLocation();
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            if (et == EntityType.CREEPER) {
                Bukkit.getScheduler().runTaskLater(ISM.getInstance(), new Runnable() {
                    public void run() {
                        loc.getWorld().createExplosion(loc, (float) 4 + (bombs / 30));
                    }
                }, 30);
            } else if (et == EntityType.COW) {
                Random rdom = new Random();

                for (int i = 0; i <= 6; i++) {
                    loc.getWorld().spawnEntity(loc, EntityType.PRIMED_TNT).setVelocity(new Vector(rdom.nextDouble() - 0.3, 1.5, rdom.nextDouble() - 0.3));
                }

            } else if (et == EntityType.PIG) {
                Random rdom = new Random();
                loc.getWorld().spawnEntity(loc, EntityType.PIG_ZOMBIE).setVelocity(new Vector(rdom.nextDouble(), 2, rdom.nextDouble()));

                for (int i = 0; i <= 2; i++) {
                    loc.getWorld().spawnEntity(loc, EntityType.PRIMED_TNT).setVelocity(new Vector(rdom.nextDouble() - 0.3, 1.5, rdom.nextDouble() - 0.3));
                }
            } else if (et == EntityType.PIG_ZOMBIE) {
                Bukkit.getScheduler().runTaskLater(ISM.getInstance(), new Runnable() {
                    public void run() {
                        loc.getWorld().createExplosion(loc, (float) 6);
                    }
                }, 30);
            } else if (et == EntityType.SQUID) {
                Random rdom = new Random();
                loc.getWorld().spawnFallingBlock(loc, Material.WATER, (byte) 0).setVelocity(new Vector(rdom.nextDouble() - 0.5, 1, rdom.nextDouble() - 0.5));
            } else if (et == EntityType.SPIDER) {
                Random rdom = new Random();

                for (int i = 0; i <= 4; i++) {
                    loc.getWorld().spawnFallingBlock(loc, Material.WEB, (byte) 0).setVelocity(new Vector(rdom.nextDouble() - 0.5, 1, rdom.nextDouble() - 0.5));
                    loc.getWorld().spawnEntity(loc, EntityType.PRIMED_TNT).setVelocity(new Vector(rdom.nextDouble() - 0.3, 1.5, rdom.nextDouble() - 0.3));
                }
            } else if (et == EntityType.SKELETON) {
                Random rdom = new Random();

                for (int i = 0; i <= 4; i++) {
                    loc.getWorld().spawnEntity(loc, EntityType.ZOMBIE).setVelocity(new Vector(rdom.nextDouble() - 0.5, 3, rdom.nextDouble() - 0.5));
                }
                loc.getWorld().spawnFallingBlock(loc, Material.LAVA, (byte) 0).setVelocity(new Vector(rdom.nextDouble() - 0.5, 1, rdom.nextDouble() - 0.5));

            } else if (et == EntityType.SILVERFISH) {
                Random rdom = new Random();
                loc.getWorld().spawnFallingBlock(loc, Material.MONSTER_EGGS, (byte) 0).setVelocity(new Vector(rdom.nextDouble() - 0.5, 3, rdom.nextDouble() - 0.5));
                Bukkit.getScheduler().runTaskLater(ISM.getInstance(), new Runnable() {
                    public void run() {
                        loc.getWorld().createExplosion(loc, (float) 2);
                    }
                }, 30);
            } else if (et == EntityType.CAVE_SPIDER) {
                Random rdom = new Random();
                for (int i = 0; i <= 2; i++) {
                    loc.getWorld().spawnEntity(loc, EntityType.SPIDER).setVelocity(new Vector(rdom.nextDouble() - 0.5, 3, rdom.nextDouble() - 0.5));
                }
            } else if (et == EntityType.ZOMBIE) {
                Bukkit.getScheduler().runTaskLater(ISM.getInstance(), new Runnable() {
                    public void run() {
                        loc.getWorld().strikeLightning(loc);
                    }
                }, 30);
            } else if (et == EntityType.IRON_GOLEM) {
                Random rdom = new Random();
                for (int i = 0; i <= 3; i++) {
                    loc.getWorld().spawnEntity(loc, EntityType.VILLAGER).setVelocity(new Vector(rdom.nextDouble() - 0.5, 3, rdom.nextDouble() - 0.5));
                }
            } else if (et == EntityType.VILLAGER) {
                Random rdom = new Random();

                for (int i = 0; i <= 5; i++) {
                    loc.getWorld().spawnFallingBlock(loc, Material.ANVIL, (byte) 0).setVelocity(new Vector(rdom.nextDouble() - 0.5, 1.5, rdom.nextDouble() - 0.5));
                }
            } else if (et == EntityType.MUSHROOM_COW) {
                Random rdom = new Random();
                for (int i = 0; i <= 13; i++) {
                    loc.getWorld().spawnEntity(loc, EntityType.PRIMED_TNT).setVelocity(new Vector(rdom.nextDouble() - 0.3, 1.5, rdom.nextDouble() - 0.3));
                }
            } else if (et == EntityType.SLIME) {
                Bukkit.getScheduler().runTaskLater(ISM.getInstance(), new Runnable() {
                    public void run() {
                        loc.getWorld().createExplosion(loc, (float) 6);
                    }
                }, 30);
            }
        }

    }
}
