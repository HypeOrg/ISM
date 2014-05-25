package com.oresomecraft.ism.util;

import com.oresomecraft.ism.Storage;
import com.oresomecraft.ism.event.RoundBeginEvent;
import com.oresomecraft.ism.object.iPlayer;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.*;

public class Utility {

    public static void handKit(iPlayer p, String map) {
        p.getInventory().clear();
        p.setGameMode(GameMode.SURVIVAL);
        p.getInventory().setHelmet(new ItemStack(Material.AIR));
        p.getInventory().setChestplate(new ItemStack(Material.AIR));
        p.getInventory().setLeggings(new ItemStack(Material.AIR));
        p.getInventory().setBoots(new ItemStack(Material.AIR));
        p.setFoodLevel(20);
        p.setHealth(20);
        HashMap<Integer, ItemStack> temp = Storage.kits.get(map);
        Iterator iterator = temp.entrySet().iterator();
        /* iterate through the hashmap to set the stuff to their slots */
        while (iterator.hasNext()) {
            Map.Entry<Integer, ItemStack> it = (Map.Entry<Integer, ItemStack>) iterator.next();
            ItemStack i = it.getValue();
            int slot = it.getKey();
            if (slot == -1) {
                p.getInventory().setHelmet(i);
            } else if (slot == -2) {
                p.getInventory().setChestplate(i);
            } else if (slot == -3) {
                p.getInventory().setLeggings(i);
            } else if (slot == -4) {
                p.getInventory().setBoots(i);
            } else {
                p.getInventory().setItem(slot, i);
            }
        }
        p.updateInventory();
    }

    public static void handKit(iPlayer p) {
        if (p.inGame) {
            handKit(p, Storage.currentRound);
        } else {
            p.setGameMode(GameMode.CREATIVE);
            p.getInventory().clear();
            p.updateInventory();
        }
    }

    public static String sentenceFormat(ArrayList<String> array) {
        String format = "";
        if (array.size() == 1) return array.get(0);
        int i = 1;
        while (i <= array.size()) {
            if (i == array.size()) {
                format = format + " and " + array.get(i - 1);
            } else if (i == 1) {
                format = array.get(0);
            } else {
                format = format + ", " + array.get(i - 1);
            }
            i++;
        }
        return format;
    }

    public static long generateID() {
        return new Random().nextInt(90000) + 10000;
    }

    public static void respawn() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            try {
                Object nmsPlayer = p.getClass().getMethod("getHandle").invoke(p);
                Object packet = Class.forName(nmsPlayer.getClass().getPackage().getName() + ".PacketPlayInClientCommand").newInstance();
                Class<?> enumClass = Class.forName(nmsPlayer.getClass().getPackage().getName() + ".EnumClientCommand");

                for (Object ob : enumClass.getEnumConstants()) {
                    if (ob.toString().equals("PERFORM_RESPAWN")) {
                        packet = packet.getClass().getConstructor(enumClass).newInstance(ob);
                    }
                }

                Object con = nmsPlayer.getClass().getField("playerConnection").get(nmsPlayer);
                con.getClass().getMethod("a", packet.getClass()).invoke(con, packet);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    public static void ffw(Location l, Color c) {
        Firework fw = (Firework) l.getWorld().spawnEntity(l, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
        FireworkEffect few = FireworkEffect.builder().flicker(true).trail(true).withColor(c).withFade(c).build();
        fwm.addEffect(few);
        fw.setFireworkMeta(fwm);
    }

    public static void teleportToSpawn(iPlayer p) {
        p.teleport(getSpawn(p));
    }

    public static Location getSpawn(iPlayer p) {
        if (!p.inGame) {
            if (Storage.roundStatus.equals("Cycling")) {
                Location l = Storage.lobbies.get(Storage.currentRound);
                int x = l.getBlockX();
                int y = l.getBlockY();
                int z = l.getBlockZ();
                return LocationUtil.getSafeDestination(new Location(Bukkit.getWorld(Storage.roundID + ""), x, y, z));
            } else {
                Location l = Storage.lobbies.get(Storage.currentRound);
                int x = l.getBlockX();
                int y = l.getBlockY();
                int z = l.getBlockZ();
                return LocationUtil.getSafeDestination(new Location(Bukkit.getWorld(Storage.roundID + ""), x, y, z));
            }
        } else {
            if (!(Storage.natural.contains(Storage.currentRound))) {
                if (Storage.roundStatus.equals("Started")) {
                    Location l = Storage.spawns.get(Storage.currentRound);
                    int x = l.getBlockX();
                    int y = l.getBlockY();
                    int z = l.getBlockZ();
                    return (new Location(Bukkit.getWorld(Storage.roundID + ""), x, y, z));
                }
            } else {
                return (Utility.randomLocation(Bukkit.getWorld(Storage.roundID + "")));
            }
        }
        return null;
    }

    public static void callBegin() {
        Bukkit.getPluginManager().callEvent(new RoundBeginEvent());
    }

    public static Location randomLocation(World w) {

        //Random number from 0-40
        int x = new Random().nextInt(40);
        int z = new Random().nextInt(40);

        //Half chance of making it minus; locations go into the minuses too, you know?
        if (Math.random() >= 0.5) x = x - (x * 2);
        if (Math.random() >= 0.5) z = z - (z * 2);

        //Get the safest Y block to spawn on
        int y = 100;

        return LocationUtil.getSafeDestination(new Location(w, x, y, z));
    }

    public static void updateDisplayName(iPlayer p) {
        String prefix = p.getName();

        ChatColor n = ChatColor.AQUA;
        if (iPlayer.getIPlayer(p).inGame) n = ChatColor.GREEN;

        prefix = n + prefix;

        if (p.getName().length() > 14) {
            p.setPlayerListName((n + p.getName()).substring(14));
        } else {
            p.setPlayerListName(n + p.getName());
        }

        if (p.hasPermission("ISM.rank.admin")) {
            prefix = ChatColor.GOLD + "@" + n + prefix;
            if (Storage.currentCreators.contains(p.getName())) prefix = ChatColor.DARK_RED + "#" + n + prefix;
            p.setDisplayName(prefix);
            return;
        }

        if (p.hasPermission("ISM.rank.mod")) {
            prefix = ChatColor.DARK_PURPLE + "@" + n + prefix;
        }
        if (p.hasPermission("ISM.rank.donator")) {
            prefix = ChatColor.GREEN + "#" + n + prefix;
        }
        if (Storage.currentCreators.contains(p.getName())) prefix = ChatColor.DARK_RED + "#" + n + prefix;

        p.setDisplayName(prefix);
    }
}
