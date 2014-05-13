package com.oresomecraft.ism.handlers;

import com.oresomecraft.ism.ISM;
import com.oresomecraft.ism.Storage;
import com.oresomecraft.ism.object.iPlayer;
import com.oresomecraft.ism.util.Utility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;

public class AsyncPlayerSQLListener implements Listener {

    ISM plugin;

    public AsyncPlayerSQLListener(ISM pl) {
        plugin = pl;
        pl.getServer().getPluginManager().registerEvents(this, pl);
    }

    @EventHandler
    public void ping(ServerListPingEvent e) {
        e.setMotd(ChatColor.YELLOW + "[" + ChatColor.GOLD + "ISM" + ChatColor.YELLOW + "] " + Storage.currentRound);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent e) {
        final Player p = e.getPlayer();

        //ASYNCHRONOUSLY refresh user data.
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            public void run() {
                try {
                    //This will create new user's data if it doesn't exist.
                    SQLHandler.createNewUser(p.getName());
                } catch (SQLException ex) {
                    System.out.println("ERROR IN SQL!");
                    ex.printStackTrace(System.err);
                }
            }
        });

        iPlayer.craftIPlayer(p);
        Utility.updateDisplayName(iPlayer.getIPlayer(p));
        e.setJoinMessage(p.getDisplayName() + ChatColor.DARK_AQUA + " connected");
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) throws SQLException {
        Player p = e.getPlayer();

        e.setQuitMessage(p.getDisplayName() + ChatColor.DARK_AQUA + " disconnected");
        //Remove instances and references
        iPlayer.removeIPlayer(p.getName());
    }

    private boolean contains(Location loc, int x1, int x2, int y1, int y2, int z1, int z2) {
        int bottomCornerX = x1 < x2 ? x1 : x2;
        int bottomCornerZ = z1 < z2 ? z1 : z2;
        int topCornerX = x1 > x2 ? x1 : x2;
        int topCornerZ = z1 > z2 ? z1 : z2;
        int bottomCornerY = y1 < y2 ? y1 : y2;
        int topCornerY = y1 > y2 ? y1 : y2;
        if (loc.getX() >= bottomCornerX && loc.getX() <= topCornerX) {
            if (loc.getZ() >= bottomCornerZ && loc.getZ() <= topCornerZ) {
                if (loc.getY() >= bottomCornerY && loc.getY() <= topCornerY) {
                    return true;
                }
            }
        }
        return false;
    }
}
