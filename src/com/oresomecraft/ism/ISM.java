package com.oresomecraft.ism;

import com.oresomecraft.ism.db.MySQL;
import com.oresomecraft.ism.handlers.*;
import com.oresomecraft.ism.maps.ism.MobSlaughterSkyfall;
import com.oresomecraft.ism.util.Utility;
import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import com.oresomecraft.ism.object.iPlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.Random;
import java.util.logging.Logger;

public class ISM extends JavaPlugin {

    public static Logger logger = Logger.getLogger("Minecraft");
    public static HashMap<String, iPlayer> iPlayers = new HashMap<String, iPlayer>();
    public static ArrayList<String> Input = new ArrayList<String>();
    public static ISM plugin;


    //SQL stuff
    public static int storagePort = 0;
    public static String storageHostname = null;
    public static String storageUsername = null;
    public static String storagePassword = null;
    public static String storageDatabase = null;

    public static boolean offlineMode = true;

    public void onEnable() {
        plugin = this;

        plugin.saveDefaultConfig();
        plugin.reloadConfig();

        // SQL stuff, before anything.
        storagePort = getConfig().getInt("database.port");
        storageHostname = getConfig().getString("database.hostname");
        storageUsername = getConfig().getString("database.username");
        storagePassword = getConfig().getString("database.password");
        storageDatabase = getConfig().getString("database.database");

        logger.info("Loading instances...");

        registerCommands();
        Storage.registerMaps();
        runAnnounce();

        new MiscListener(this);
        new AsyncPlayerSQLListener(this);
        new GlobalListener(this);
        new RoundHandler(this);

        MySQL mysql = new MySQL(ISM.logger,
                "[ISM-DB] ",
                ISM.storageHostname,
                ISM.storagePort,
                ISM.storageDatabase,
                ISM.storageUsername,
                ISM.storagePassword);
        if (mysql.open()) {
            offlineMode = false;
            System.out.println("[ISM] We connected to the SQL successfully!");
        } else {
            System.out.println("[ISM] We couldn't connect to the SQL!");
        }

        logger.info("Checking SQL tables...");
        SQLHandler.createTables();

        final long ID = Utility.generateID();
        String map = Storage.maps.get(new Random().nextInt(Storage.maps.size()));
        if (Storage.natural.contains(map)) {
            MapHandler.loadMap(map, ID + "", true);
        } else {
            MapHandler.loadMap(map, ID + "", false);
        }

        RoundHandler.start(map, ID);

        if (!offlineMode)
            cacheTimer();

        for (final Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(ChatColor.GREEN + "iPlayer object created!");
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
            Utility.teleportToSpawn(iPlayer.getIPlayer(p));
            Utility.handKit(iPlayer.getIPlayer(p));
        }
    }

    public void onDisable() {
        MapHandler.restoreMap(Storage.roundID + "");
    }

    public void createNullWorld(String s) {
        WorldCreator wc = new WorldCreator(s);
        wc.generator(new NullChunkGenerator());
        Bukkit.createWorld(wc);
    }

    public static ISM getInstance() {
        return plugin;
    }

    public static HashMap<String, iPlayer> getIPlayers() {
        return iPlayers;
    }

    public static void registerEvents(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, ISM.getInstance());
    }

    // This will push all cached input every 10 seconds
    public synchronized void cacheTimer() {
        final MySQL mysql = new MySQL(ISM.logger,
                "[ISM-DB] ",
                ISM.storageHostname,
                ISM.storagePort,
                ISM.storageDatabase,
                ISM.storageUsername,
                ISM.storagePassword);
        new BukkitRunnable() {
            public synchronized void run() {
                if (Input.size() <= 0) return;
                mysql.open();
                while (Input.size() > 0) {
                    String s = Input.get(0);
                    try {
                        mysql.query(s);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    Input.remove(0);
                }
                mysql.close();
            }
        }.runTaskTimerAsynchronously(plugin, 0, 20 * 20);
    }

    public void runAnnounce() {
        new BukkitRunnable() {
            public void run() {
                if (!Storage.roundStatus.equals("Cycling"))
                    Bukkit.broadcastMessage(ChatColor.YELLOW + "[" + ChatColor.GOLD + "ISM" + ChatColor.YELLOW + "]" +
                            " The current round is " + ChatColor.RED + Storage.currentRound + ChatColor.YELLOW +
                            " by " + ChatColor.GOLD + Utility.sentenceFormat(Storage.currentCreators));
            }
        }.runTaskTimer(this, 0, 60 * 20L);
    }

    /**
     * *******************************************************************
     * Code to use for sk89q's command framework goes below this comment! *
     * ********************************************************************
     */

    private CommandsManager<CommandSender> commands;
    private boolean opPermissions;

    private void registerCommands() {
        final ISM plugin = this;
        // Register the commands that we want to use
        commands = new CommandsManager<CommandSender>() {
            @Override
            public boolean hasPermission(CommandSender player, String perm) {
                return plugin.hasPermission(player, perm);
            }
        };
        commands.setInjector(new SimpleInjector(this));
        final CommandsManagerRegistration cmdRegister = new CommandsManagerRegistration(this, commands);

        cmdRegister.register(Commands.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            commands.execute(cmd.getName(), args, sender, sender);
        } catch (CommandPermissionsException e) {
            sender.sendMessage(ChatColor.RED + "You don't have permission.");
        } catch (MissingNestedCommandException e) {
            sender.sendMessage(ChatColor.RED + e.getUsage());
        } catch (CommandUsageException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
            sender.sendMessage(ChatColor.RED + e.getUsage());
        } catch (WrappedCommandException e) {
            if (e.getCause() instanceof NumberFormatException) {
                sender.sendMessage(ChatColor.RED + "You need to enter a number!");
            } else {
                sender.sendMessage(ChatColor.RED + "Error occurred, contact developer.");
                sender.sendMessage(ChatColor.RED + "Message: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (CommandException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
        }

        return true;
    }

    public boolean hasPermission(CommandSender sender, String perm) {
        if (!(sender instanceof Player)) {
            if (sender.hasPermission(perm)) {
                return ((sender.isOp() && (opPermissions || sender instanceof ConsoleCommandSender)));
            }
        }
        return hasPermission(sender, ((Player) sender).getWorld(), perm);
    }

    public boolean hasPermission(CommandSender sender, World world, String perm) {
        if ((sender.isOp() && opPermissions) || sender instanceof ConsoleCommandSender || sender.hasPermission(perm)) {
            return true;
        }
        return false;
    }
}
