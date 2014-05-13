package com.oresomecraft.ism;

import com.oresomecraft.ism.db.MySQL;
import com.oresomecraft.ism.event.GlobalRoundFinishEvent;
import com.oresomecraft.ism.event.PlayerLeaveEvent;
import com.oresomecraft.ism.event.RoundBeginEvent;
import com.oresomecraft.ism.object.iPlayer;
import com.oresomecraft.ism.util.Utility;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Squid;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

public class Commands {
    ISM plugin;

    public Commands(ISM pl) {
        plugin = pl;
    }

    @Command(aliases = {"test", "testt"},
            desc = "test",
            max = 0)
    public void test(CommandContext args, CommandSender sender) {
        Player p = (Player) sender;
        Squid sq = (Squid) p.getWorld().spawnEntity(p.getLocation(), EntityType.SQUID);
        EnderCrystal ec = (EnderCrystal) p.getWorld().spawnEntity(p.getLocation(), EntityType.ENDER_CRYSTAL);
        sq.setPassenger(ec);
        sq.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 10000 * 20, 10000 * 20));
        sq.setVelocity(new Vector(0, 1, 0));
    }

    @Command(aliases = {"join", "goin"},
            desc = "Join the current round",
            max = 0)
    public void join(CommandContext args, CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) return;
        iPlayer t = iPlayer.getIPlayer(sender.getName());
        if (t.inGame) {
            sender.sendMessage(ChatColor.RED + "You are already in game!");
            return;
        }
        Bukkit.broadcastMessage(t.getDisplayName() + ChatColor.YELLOW + " joined the round");
        t.inGame = true;
        Utility.updateDisplayName(t);
        Utility.teleportToSpawn(t);
        Utility.handKit(t);
    }

    @Command(aliases = {"leave", "quit"},
            desc = "Leave the current round",
            max = 0)
    public void leave(CommandContext args, CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) return;
        iPlayer t = iPlayer.getIPlayer(sender.getName());
        if (!t.inGame) {
            sender.sendMessage(ChatColor.RED + "You are already spectating!");
            return;
        }
        Bukkit.getPluginManager().callEvent(new PlayerLeaveEvent(t));
        Bukkit.broadcastMessage(t.getDisplayName() + ChatColor.YELLOW + " left the round");
        t.inGame = false;
        Utility.updateDisplayName(t);
        Utility.teleportToSpawn(t);
        Utility.handKit(t);
    }

    @Command(aliases = {"endround", "end"},
            desc = "Ends the current round and starts the cycle",
            max = 0)
    @CommandPermissions("ISM.rank.admin")
    public void end(CommandContext args, CommandSender sender) {
        if (Storage.roundStatus.equals("Started")) {
            Bukkit.getPluginManager().callEvent(new GlobalRoundFinishEvent(true, "No one", Storage.currentRound, Storage.currentGamemode.toString()));
        }
    }

    @Command(aliases = {"startnow", "start"},
            desc = "Start the round now",
            max = 0)
    @CommandPermissions("ISM.rank.admin")
    public void start(CommandContext args, CommandSender sender) {
        if (Storage.roundStatus.equals("Starting")) {
            Bukkit.getPluginManager().callEvent(new RoundBeginEvent());
        }
    }

    @Command(aliases = {"debug", "dbg"},
            desc = "Debugging command",
            max = 0)
    @CommandPermissions("ISM.rank.admin")
    public void debug(CommandContext args, CommandSender sender) {
        sender.sendMessage(Storage.roundStatus);
        sender.sendMessage(Storage.roundID + " ID");
        sender.sendMessage(Storage.currentRound + " is the current round");
        sender.sendMessage(Storage.gTo + " is the gTo");
        sender.sendMessage(Storage.currentGamemode + " is the gamemode");
        try {
            sender.sendMessage(((Player) sender).getLocation().getY() + " is your Y");
        } catch (Exception e) {

        }
    }

    @Command(aliases = {"setnext", "sn"},
            desc = "Sets the next map",
            usage = "<map>",
            min = 1)
    @CommandPermissions("ISM.rank.admin")
    public void set(CommandContext args, CommandSender sender) {
        String map = args.getString(0);
        if (matchMap(map).equals("None")) {
            sender.sendMessage(ChatColor.RED + "That map doesn't exist!");
            return;
        }
        if (Storage.roundStatus.equals("Cycling")) {
            sender.sendMessage(ChatColor.RED + "You cannot set maps during a cycle!");
            return;
        }
        Bukkit.broadcastMessage(ChatColor.YELLOW + "[" + ChatColor.GOLD + "ISM" + ChatColor.YELLOW + "] " +
                sender.getName() + " set the next map to be " + ChatColor.RED + matchMap(map));
        Storage.gTo = matchMap(map);
    }

    @Command(aliases = {"goto", "g"},
            desc = "Go to the next map",
            usage = "<map>",
            min = 1,
            max = 1)
    public void g(CommandContext args, CommandSender sender) {
        String map = args.getString(0);
        if (Storage.gTo.equals("None")) {
            sender.sendMessage(ChatColor.RED + "You cannot goto a map at the moment.");
            return;
        }
        if (matchMap(map).equals("None")) {
            sender.sendMessage(ChatColor.RED + "That map doesn't exist!");
            return;
        }
        if (!matchMap(map).equals(Storage.gTo)) {
            sender.sendMessage(ChatColor.RED + "That map isn't available at the moment.");
            return;
        }
        Player p = (Player) sender;
        if (p.getWorld().getName().equals(Storage.gTo)) {
            sender.sendMessage(ChatColor.RED + "You've already warped in!");
            return;
        }
        if (!p.getWorld().getName().equals(Storage.roundID + "")) {
            Utility.teleportToSpawn(iPlayer.getIPlayer(p));
            Utility.handKit(iPlayer.getIPlayer(p));
        }
    }

    @Command(aliases = {"ism", "itssimplyoresome", "ismhelp"},
            desc = "View an online player's info and stats",
            usage = "<topic>",
            min = 0)
    public void ism(CommandContext args, CommandSender sender) {
        if (args.argsLength() == 0) {
            sender.sendMessage(ChatColor.GOLD + "[ISM] " + ChatColor.YELLOW + "Hello, how may I help you?");
            return;
        }
        if (args.getString(0).equalsIgnoreCase("help")) {
            sender.sendMessage(ChatColor.GOLD + "[ISM]" + ChatColor.YELLOW + " Available topics-");
            sender.sendMessage(ChatColor.YELLOW + "- Ranks (" + ChatColor.GOLD + "/ism ranks" + ChatColor.YELLOW + ")");
            sender.sendMessage(ChatColor.YELLOW + "- How does it work? (" + ChatColor.GOLD + "/ism gameplay" + ChatColor.YELLOW + ")");
            sender.sendMessage(ChatColor.YELLOW + "- What do you do? (" + ChatColor.GOLD + "/ism overlook" + ChatColor.YELLOW + ")");
            sender.sendMessage(ChatColor.YELLOW + "- Medals and Awards (" + ChatColor.GOLD + "/ism awards" + ChatColor.YELLOW + ")");
            sender.sendMessage(ChatColor.YELLOW + "- Maps and making maps (" + ChatColor.GOLD + "/ism maps" + ChatColor.YELLOW + ")");
            sender.sendMessage(ChatColor.YELLOW + "- Titles and nobility (" + ChatColor.GOLD + "/ism titles" + ChatColor.YELLOW + ")");
            sender.sendMessage(ChatColor.YELLOW + "- Useful commands (" + ChatColor.GOLD + "/ism commands" + ChatColor.YELLOW + ")");
            sender.sendMessage(ChatColor.YELLOW + "- FAQ (" + ChatColor.GOLD + "/ism faq" + ChatColor.YELLOW + ")");
            return;
        }
        if (args.getString(0).equalsIgnoreCase("ranks")) {
            sender.sendMessage(ChatColor.GOLD + "[ISM] " + ChatColor.YELLOW + "Here's what I know about ranks!");
            sender.sendMessage(ChatColor.GOLD + "Ranks" + ChatColor.YELLOW + " are special prefixes which are earned from " +
                    "being a person of interest or from doing certain tasks. I will explain the different types of ranks to you.");
            sender.sendMessage(ChatColor.GOLD + "The Administrator Rank" + ChatColor.YELLOW + " is a very exclusive prefix only shown" +
                    " to those of the title of an admin. In-game, the admin prefix appears as '" + ChatColor.GOLD + "@" + ChatColor.YELLOW + "'");
            sender.sendMessage(ChatColor.GOLD + "The Moderator Rank" + ChatColor.YELLOW + " is shown to an online moderator. Moderators are there to " +
                    "enforce the rules, punish whom shall break them and make sure everyone is having a good time. In-game, the moderator prefix appears as" +
                    " '" + ChatColor.DARK_PURPLE + "@" + ChatColor.YELLOW + "'");
            sender.sendMessage(ChatColor.GOLD + "The Donator Rank" + ChatColor.YELLOW + " is a thank-you prefix in return for a donation of $10 or more" +
                    " to this server. In-game, the donator prefix appears as '" + ChatColor.GREEN + "#" + ChatColor.YELLOW + "'");
            sender.sendMessage(ChatColor.GOLD + "The Map-Creator Rank" + ChatColor.YELLOW + " is a temporary prefix shown to a person who is a creator of" +
                    " the current map being played. In-game, the map-creator prefix appears as '" + ChatColor.DARK_RED + "#" + ChatColor.YELLOW + "'");
            return;
        }
        if (args.getString(0).equalsIgnoreCase("gameplay")) {
            sender.sendMessage(ChatColor.GOLD + "[ISM] " + ChatColor.YELLOW + "I'll explain the gameplay for you!");
            sender.sendMessage(ChatColor.GOLD + " ISM" + ChatColor.YELLOW + " is a different kind of server, " + ChatColor.GOLD + "specialising in the certain " +
                    "skills of minecraft" + ChatColor.YELLOW + " rather than say, PvP or survival. Generally, It's simply Oresome! is an " + ChatColor.GOLD +
                    "overall test of skill" + ChatColor.YELLOW + " ranging from survival skill right over to how good you are at fighting! Instead of just " +
                    "putting you to the test alone, we've rolled it all into one big cycle where you can" + ChatColor.GOLD + "\n compete against other players and show off your skills!" +
                    "\n \n" + ChatColor.GOLD + ChatColor.ITALIC + " Looking for a challenge? ISM is the server for you!");
            return;
        }
        if (args.getString(0).equalsIgnoreCase("overlook")) {
            sender.sendMessage(ChatColor.GOLD + "[ISM] " + ChatColor.YELLOW + "I'll briefly explain ISM for you!");
            sender.sendMessage(ChatColor.GOLD + " ISM" + ChatColor.YELLOW + " is a short (but sweet) cycling map server where you can broadly test all of your " +
                    "skills against other people! ISM was based off of the following statement, \"If we're going to do a cycling server, why not do it all?\" " +
                    "" +
                    "Ever from these words, the ISM development team went straight off and started making the plugin! You now have 2 choices; you can either" +
                    " find out more about ISM with " + ChatColor.GOLD + "/ism gameplay" + ChatColor.YELLOW + " or you can go in there and " + ChatColor.GOLD +
                    ChatColor.BOLD + "SHOW OFF YOUR SKILLS!");
            return;
        }
        if (args.getString(0).equalsIgnoreCase("awards")) {
            sender.sendMessage(ChatColor.GOLD + "[ISM] " + ChatColor.YELLOW + "I'll explain Medals and Awards for you!");
            sender.sendMessage(ChatColor.GOLD + " Medals" + ChatColor.YELLOW + " are handed out each round to 3 players who did the task better than the" +
                    "rest of the players. If you have never watched the olympics, the person who did the best is handed a " + ChatColor.GOLD + "gold medal," +
                    ChatColor.YELLOW + " the 2nd best is handed a " + ChatColor.DARK_GRAY + "silver medal\n" + ChatColor.YELLOW + " and the 3rd best is handed a"
                    + ChatColor.DARK_RED + " bronze medal" + ChatColor.YELLOW + ". Medals can be redeemed in the shops (once per medal) to buy certain perks " +
                    "and powerups to assist you during rounds!");
            return;
        }
        if (args.getString(0).equalsIgnoreCase("commands")) {
            sender.sendMessage(ChatColor.GOLD + "[ISM] " + ChatColor.YELLOW + "Here's a list of useful commands!");
            return;
        }
        sender.sendMessage(ChatColor.GOLD + "[ISM] " + ChatColor.YELLOW + "Unknown topic.");
    }

    @Command(aliases = {"overview", "look", "view"},
            desc = "View an online player's info and stats",
            usage = "<player>",
            min = 0,
            max = 1)
    public void view(CommandContext args, CommandSender sender) {
        if (ISM.offlineMode) {
            sender.sendMessage(ChatColor.RED + "You can't do this command in offline mode!");
            return;
        }
        String bottomFormat = ChatColor.YELLOW + "####################################";
        if (args.argsLength() == 1) {
            if (matchRPlayer(args.getString(0)).equals("None")) {
                sender.sendMessage(ChatColor.RED + "That person is not online!");
                return;
            }
        }
        //Finally, you can now match the nearest player to it's name! For example, /view Anomalo -
        // - would correct to AnomalousRei if I was online and was the first one to be found in the iterator loop

        iPlayer t;
        if (args.argsLength() == 1) {
            t = iPlayer.getIPlayer(Bukkit.getPlayer(args.getString(0)));
        } else if (!(sender instanceof ConsoleCommandSender)) {
            t = iPlayer.getIPlayer(sender.getName());
        } else {
            sender.sendMessage(ChatColor.RED + "You need to be a player to do this!");
            return;
        }
        if (args.argsLength() == 0) {
            t = iPlayer.getIPlayer(sender.getName());
        }
        int tempCheck = t.getName().length();
        while (tempCheck > 0) {
            tempCheck--;
            bottomFormat = bottomFormat + ChatColor.YELLOW + "#";
        }
        sender.sendMessage(ChatColor.YELLOW + "########### " + ChatColor.GOLD + "INFORMATION ON " + t.getName().toUpperCase() + ChatColor.YELLOW + " ###########");
        sender.sendMessage(ChatColor.YELLOW + "Known commonly as '" + ChatColor.RED + t.title + " " + t.getName() + ChatColor.YELLOW + "'");
        sender.sendMessage(ChatColor.GOLD + " " + t.gold + ChatColor.YELLOW + " gold medals");
        sender.sendMessage(ChatColor.DARK_GRAY + " " + t.silver + ChatColor.YELLOW + " silver medals");
        sender.sendMessage(ChatColor.DARK_RED + " " + t.bronze + ChatColor.YELLOW + " bronze medals");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.RED + "   For more help, type /ism help!");
        sender.sendMessage(bottomFormat);
    }

    @Command(aliases = {"maps", "maplist", "whatmaps"},
            desc = "View the maps",
            usage = "<page>",
            min = 0,
            max = 1)
    public void goals(CommandContext args, CommandSender sender) throws SQLException {
        int page = 1;
        if (args.argsLength() == 1) {
            try {
                page = Integer.parseInt(args.getString(0));
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "That is not a number!");
                return;
            }
        }
        int maxPage = page * 10;
        int i = maxPage - 10;
        sender.sendMessage(ChatColor.YELLOW + "[" + ChatColor.GOLD + "ISM" + ChatColor.YELLOW + "] Maps List (Page " + page + ")");
        //10 per page, so if it's page 2 it will check the array-list from 10-20.
        boolean stopCheck = false;
        while (i < maxPage && !stopCheck) {
            try {
                sender.sendMessage(ChatColor.YELLOW + "- " + ChatColor.RED + Storage.maps.get(i));
                i++;
            } catch (IndexOutOfBoundsException e) {
                sender.sendMessage(ChatColor.RED + "No further maps found.");
                i++;
                stopCheck = true;
            }
        }
        sender.sendMessage(ChatColor.YELLOW + "To see next page, type '/maps " + (page + 1) + "'");
    }

    @Command(aliases = {"map", "mapinfo"},
            desc = "View information on a map",
            usage = "<map>",
            min = 0)
    public void map(final CommandContext args, final CommandSender sender) {
        Bukkit.getScheduler().runTaskAsynchronously(ISM.getInstance(), new Runnable() {
            public void run() {
                String map;
                if (args.argsLength() == 1) {
                    map = matchMap(args.getJoinedStrings(0));
                } else {
                    map = Storage.currentRound;
                }
                if (map.equals("None")) {
                    sender.sendMessage(ChatColor.RED + "That map doesn't exist!");
                    return;
                }

                MySQL mysql = new MySQL(plugin.logger,
                        "[ISM-DB] ",
                        plugin.storageHostname,
                        plugin.storagePort,
                        plugin.storageDatabase,
                        plugin.storageUsername,
                        plugin.storagePassword);

                mysql.open();

                ResultSet rsd = null;
                try {
                    rsd = mysql.query("SELECT COUNT(map) FROM " + plugin.storageDatabase + "_rounds WHERE map='" + map + "'");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                String bottomFormat = ChatColor.YELLOW + "#####################";
                int tempCheck = map.length();
                while (tempCheck > 0) {
                    tempCheck--;
                    bottomFormat = bottomFormat + ChatColor.YELLOW + "#";
                }
                try {
                    rsd.first();
                    int playcount = rsd.getInt(1);

                    ArrayList<String> creators = Storage.creators.get(map);
                    sender.sendMessage(ChatColor.YELLOW + "########### " + ChatColor.GOLD + "" + map + ChatColor.YELLOW + " ###########");
                    sender.sendMessage(ChatColor.YELLOW + " Made by: " + ChatColor.GREEN + ChatColor.ITALIC + Utility.sentenceFormat(creators));
                    sender.sendMessage(ChatColor.YELLOW + " Times played: " + ChatColor.GOLD + playcount);
                    sender.sendMessage("");
                    sender.sendMessage(ChatColor.YELLOW + " Description: " + ChatColor.RED + Storage.descs.get(map));
                    sender.sendMessage("");
                    sender.sendMessage(ChatColor.RED + "   For more help, type /ism help!");
                    sender.sendMessage(bottomFormat);
                } catch (SQLException e) {
                    System.out.println("[ISM] Something went wrong in the SQL whilst using /map.");
                    System.out.println(e.getMessage());
                }
            }
        });
    }

    private String matchRPlayer(String map) {
        for (iPlayer s : ISM.getInstance().getIPlayers().values()) {
            String temp = s.getName();
            if (s.getName().toLowerCase().startsWith(map.toLowerCase())) {
                return temp;
            }
        }
        return "None";
    }

    private String matchMap(String map) {
        for (String s : Storage.maps) {
            String temp = s;
            if (s.toLowerCase().startsWith(map.toLowerCase())) {
                return temp;
            }
        }
        return "None";
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

    private ArrayList<String> returnSeperatedStringsAsArray(String chars) {
        ArrayList<String> charsa = new ArrayList<String>();
        ArrayList<Character> cs = new ArrayList<Character>();

        String builder = "";

        for (char c : chars.toCharArray()) cs.add(c);

        Iterator i = cs.iterator();
        while (i.hasNext()) {
            char c = (Character) i.next();
            if (c != ',') {
                builder = builder + c;
            }
            if (c == ',') {
                charsa.add(builder);
                builder = "";
            }
            if (c != ',' && !i.hasNext()) {
                charsa.add(builder);
                builder = "";
            }
        }
        return charsa;
    }
}
