package com.oresomecraft.ism.handlers;

import com.oresomecraft.ism.HeadsUpDisplay;
import com.oresomecraft.ism.ISM;
import com.oresomecraft.ism.Storage;
import com.oresomecraft.ism.event.GlobalRoundFinishEvent;
import com.oresomecraft.ism.object.iPlayer;
import com.oresomecraft.ism.util.Utility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.text.DecimalFormat;

public class GlobalListener implements Listener {
    ISM plugin;

    public GlobalListener(ISM pl) {
        plugin = pl;
        pl.getServer().getPluginManager().registerEvents(this, pl);
    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        Utility.handKit(iPlayer.getIPlayer(e.getPlayer()));
        Utility.teleportToSpawn(iPlayer.getIPlayer(e.getPlayer()));
    }

    @EventHandler
    public void respawn(final PlayerRespawnEvent e) {
        Utility.handKit(iPlayer.getIPlayer(e.getPlayer()));
        Utility.updateDisplayName(iPlayer.getIPlayer(e.getPlayer()));
        e.setRespawnLocation(Utility.getSpawn(iPlayer.getIPlayer(e.getPlayer())));
    }

    @EventHandler
    public void end(GlobalRoundFinishEvent e) {
        SQLHandler.logRound(e.getWinner(), e.getMap(), e.getGamemode());
        for (iPlayer p : ISM.iPlayers.values()) {
            p.inGame = false;
            Utility.updateDisplayName(p);
        }
        new BukkitRunnable() {
            public void run() {
                RoundHandler.cycleToNextMap();
            }
        }.runTaskLater(plugin, 20L);
        Utility.respawn();
    }

    @EventHandler
    public void death(PlayerDeathEvent e) {
        if (Storage.roundStatus.equals("Cycling")) {
            e.getEntity().setHealth(20);
            return;
        }
        Utility.updateDisplayName(iPlayer.getIPlayer(e.getEntity()));
        EntityDamageEvent.DamageCause cause = e.getEntity().getLastDamageCause().getCause();
        if (e.getEntity().getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            if (e.getEntity().getKiller() instanceof Player) {
                e.setDeathMessage(e.getEntity().getDisplayName() + ChatColor.YELLOW + " was slain by " + e.getEntity().getKiller().getDisplayName());
            } else {
                e.setDeathMessage(e.getEntity().getDisplayName() + ChatColor.YELLOW + " was slain by an angry mob");
            }
        } else if (cause == EntityDamageEvent.DamageCause.VOID) {
            e.setDeathMessage(e.getEntity().getDisplayName() + ChatColor.YELLOW + " fell out of the world");
        } else if (cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION || cause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
            e.setDeathMessage(e.getEntity().getDisplayName() + ChatColor.YELLOW + " was killed by an explosion");
        } else if (cause == EntityDamageEvent.DamageCause.DROWNING) {
            e.setDeathMessage(e.getEntity().getDisplayName() + ChatColor.YELLOW + " drowned");
        } else if (cause == EntityDamageEvent.DamageCause.PROJECTILE) {
            e.setDeathMessage(e.getEntity().getDisplayName() + ChatColor.YELLOW + " was shot");
        } else if (cause == EntityDamageEvent.DamageCause.MAGIC) {
            e.setDeathMessage(e.getEntity().getDisplayName() + ChatColor.YELLOW + " was killed by magic. :o");
        } else if (cause == EntityDamageEvent.DamageCause.FALL) {
            e.setDeathMessage(e.getEntity().getDisplayName() + ChatColor.YELLOW + " hit the ground too hard (" + new DecimalFormat("#").format(e.getEntity().getFallDistance()) + " blocks)");
        } else if (cause == EntityDamageEvent.DamageCause.PROJECTILE) {
            e.setDeathMessage(e.getEntity().getDisplayName() + ChatColor.YELLOW + " was shot");
        } else if (cause == EntityDamageEvent.DamageCause.FIRE || cause == EntityDamageEvent.DamageCause.LAVA || cause == EntityDamageEvent.DamageCause.FIRE_TICK) {
            e.setDeathMessage(e.getEntity().getDisplayName() + ChatColor.YELLOW + " burned to death");
        } else if (cause == EntityDamageEvent.DamageCause.STARVATION) {
            e.setDeathMessage(e.getEntity().getDisplayName() + ChatColor.YELLOW + " starved to death");
        } else if (cause == EntityDamageEvent.DamageCause.SUFFOCATION) {
            e.setDeathMessage(e.getEntity().getDisplayName() + ChatColor.YELLOW + " suffocated");
        } else if (cause == EntityDamageEvent.DamageCause.SUICIDE) {
            e.setDeathMessage(e.getEntity().getDisplayName() + ChatColor.YELLOW + " committed suicide");
        } else if (cause == EntityDamageEvent.DamageCause.FALLING_BLOCK) {
            e.setDeathMessage(e.getEntity().getDisplayName() + ChatColor.YELLOW + " was crushed by a falling anvil");
        } else {
            e.setDeathMessage(e.getEntity().getDisplayName() + ChatColor.YELLOW + " died");
        }
    }

    @EventHandler
    public void damage(EntityDamageEvent e) {
        if (Storage.roundStatus.equals("Cycling")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void move(PlayerMoveEvent e) {
        if (Storage.roundStatus.equals("Cycling")) {
            if (!e.getTo().getWorld().getName().equals(Storage.roundID + "")) return;
        }
        if (e.getTo().getBlockY() <= 0) {
            if (Storage.roundStatus.equals("Cycling")) {
                Utility.handKit(iPlayer.getIPlayer(e.getPlayer()));
                Utility.teleportToSpawn(iPlayer.getIPlayer(e.getPlayer()));
            }
        }
    }
}
