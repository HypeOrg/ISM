package com.oresomecraft.ism.handlers;

import com.oresomecraft.ism.ISM;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.sql.SQLException;

public class MiscListener implements Listener {

    ISM plugin;

    public MiscListener(ISM pl) {
        plugin = pl;
        pl.getServer().getPluginManager().registerEvents(this, pl);
    }

    @EventHandler
    public void chatListener(AsyncPlayerChatEvent event) {
        Player p = event.getPlayer();
        event.setFormat("<" + event.getPlayer().getDisplayName() + ChatColor.WHITE + ">: " + ChatColor.translateAlternateColorCodes('&', "%2$s"));
    }

    @EventHandler
    public void hideCertainCommands(PlayerCommandPreprocessEvent event) throws SQLException {
        String placeholderMessage = "Unknown command. Type \"/help\" for help.";

        if (event.getMessage().equalsIgnoreCase("/plugins") && !event.getPlayer().isOp() || event.getMessage().equalsIgnoreCase("/pl") && !event.getPlayer().isOp()) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("Plugins (2): " + ChatColor.GREEN + "ISM-CORE" + ChatColor.WHITE + ", " + ChatColor.GREEN + "OresomeBungeeUtils");
        }
        if (event.getMessage().equalsIgnoreCase("/?") && !event.getPlayer().isOp() || event.getMessage().equalsIgnoreCase("/help") && !event.getPlayer().isOp()) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(placeholderMessage);
        }
    }

    @EventHandler
    public void colorsign(SignChangeEvent s) {
        s.setLine(0, ChatColor.translateAlternateColorCodes('&', s.getLine(0)));
        s.setLine(1, ChatColor.translateAlternateColorCodes('&', s.getLine(1)));
        s.setLine(2, ChatColor.translateAlternateColorCodes('&', s.getLine(2)));
        s.setLine(3, ChatColor.translateAlternateColorCodes('&', s.getLine(3)));
    }
}
