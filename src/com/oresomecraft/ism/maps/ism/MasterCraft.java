package com.oresomecraft.ism.maps.ism;

import com.oresomecraft.ism.ISM;
import com.oresomecraft.ism.Storage;
import com.oresomecraft.ism.event.GlobalRoundFinishEvent;
import com.oresomecraft.ism.event.PlayerLeaveEvent;
import com.oresomecraft.ism.event.RoundBeginEvent;
import com.oresomecraft.ism.maps.Map;
import com.oresomecraft.ism.object.iPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class MasterCraft extends Map implements Listener {
    public MasterCraft(String map) {
        super(map);
    }

    public Map config = super.config;
    public boolean pass = false;

    int count = 312;

    @EventHandler
    public void start(RoundBeginEvent event) {
        start();
    }

    private void start() {
        if (pass) return;
        RoundBeginEvent.getHandlerList().unregister(this);
        doChef();
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

    public HashMap<Integer, List<Block>> benches = new HashMap<Integer, List<Block>>();
    public HashMap<Block, Integer> signs = new HashMap<Block, Integer>();

    {
        benches.put(1, Arrays.asList(Bukkit.getWorld(Storage.roundIDToAString()).getBlockAt(-35, 75, 66),
                Bukkit.getWorld(Storage.roundIDToAString()).getBlockAt(-35, 75, 67),
                Bukkit.getWorld(Storage.roundIDToAString()).getBlockAt(-35, 75, 70),
                Bukkit.getWorld(Storage.roundIDToAString()).getBlockAt(-35, 74, 69)));
        benches.put(2, Arrays.asList(Bukkit.getWorld(Storage.roundIDToAString()).getBlockAt(-35, 75, 74),
                Bukkit.getWorld(Storage.roundIDToAString()).getBlockAt(-35, 75, 75),
                Bukkit.getWorld(Storage.roundIDToAString()).getBlockAt(-35, 75, 78),
                Bukkit.getWorld(Storage.roundIDToAString()).getBlockAt(-35, 74, 77)));
        benches.put(3, Arrays.asList(Bukkit.getWorld(Storage.roundIDToAString()).getBlockAt(-35, 75, 57),
                Bukkit.getWorld(Storage.roundIDToAString()).getBlockAt(-35, 75, 56),
                Bukkit.getWorld(Storage.roundIDToAString()).getBlockAt(-35, 75, 53),
                Bukkit.getWorld(Storage.roundIDToAString()).getBlockAt(-35, 74, 54)));
        benches.put(4, Arrays.asList(Bukkit.getWorld(Storage.roundIDToAString()).getBlockAt(-35, 75, 49),
                Bukkit.getWorld(Storage.roundIDToAString()).getBlockAt(-35, 75, 48),
                Bukkit.getWorld(Storage.roundIDToAString()).getBlockAt(-35, 75, 45),
                Bukkit.getWorld(Storage.roundIDToAString()).getBlockAt(-35, 74, 46)));
    }

    {
        signs.put(Bukkit.getWorld(Storage.roundIDToAString()).getBlockAt(-35, 76, 68), 1);
        signs.put(Bukkit.getWorld(Storage.roundIDToAString()).getBlockAt(-35, 76, 76), 2);
        signs.put(Bukkit.getWorld(Storage.roundIDToAString()).getBlockAt(-35, 76, 55), 3);
        signs.put(Bukkit.getWorld(Storage.roundIDToAString()).getBlockAt(-35, 76, 47), 4);
    }

    public HashMap<ItemStack, List<ItemStack>> ingredients = new HashMap<ItemStack, List<ItemStack>>();
    public List<ItemStack> recipes = new ArrayList<ItemStack>();

    {
        ItemStack recipe1 = new ItemStack(Material.WOOD_SWORD, 1);
        ItemMeta recipe1Meta = recipe1.getItemMeta();
        recipe1Meta.setDisplayName("Shank");
        recipe1.setItemMeta(recipe1Meta);
        recipes.add(recipe1);
        ingredients.put(recipe1, Arrays.asList(new ItemStack(Material.LOG, 1), new ItemStack(Material.EXP_BOTTLE, 16)));

        ItemStack recipe2 = new ItemStack(Material.BEACON, 1);
        recipes.add(recipe2);
        ingredients.put(recipe2, Arrays.asList(new ItemStack(Material.NETHER_STAR), new ItemStack(Material.WOOD, 1, (short) 1),
                new ItemStack(Material.SAND, 1), new ItemStack(Material.OBSIDIAN, 5), new ItemStack(Material.GLASS, 4)));
    }

    public HashMap<String, Integer> benchControl = new HashMap<String, Integer>();

    public HashMap<String, Integer> points = new HashMap<String, Integer>();
    public String leader = "No one";
    private boolean start = false;

    private List<Block> allBlocks() {
        List<Block> blocks = new ArrayList<Block>();
        for (List<Block> blockList : benches.values())
            for (Block block : blockList)
                blocks.add(block);
        return blocks;
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        if (!pass) {
            event.setCancelled(true);
            return;
        }
        if (!start) {
            event.setCancelled(true);
            return;
        }
        if (event.isCancelled()) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (benchControl.containsKey(event.getPlayer().getName())) {
            List<Block> all = allBlocks();
            all.removeAll(benches.get(benchControl.get(event.getPlayer().getName())));
            if (all.contains(event.getClickedBlock())) {
                event.getPlayer().sendMessage(ChatColor.RED + "You can't modify another workspace that isn't yours!");
                event.setCancelled(true);
            }
            return;
        }
        if (!benchControl.containsKey(event.getPlayer().getName())) {
            if (signs.containsKey(event.getClickedBlock())) {
                if (benchControl.values().contains(signs.get(event.getClickedBlock()))) {
                    event.getPlayer().sendMessage(ChatColor.RED + "This workspace is being used!");
                } else {
                    event.getPlayer().sendMessage(ChatColor.GOLD + "You have claimed " + signs.get(event.getClickedBlock()) + "!");
                    benchControl.put(event.getPlayer().getName(), signs.get(event.getClickedBlock()));
                }
            } else {
                List<Block> all = allBlocks();
                if (all.contains(event.getClickedBlock())) {
                    event.getPlayer().sendMessage(ChatColor.RED + "You can't modify another workspace that isn't yours!");
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void quit(PlayerQuitEvent event) {
        if (!pass) return;
        points.remove(event.getPlayer().getName());
        benchControl.remove(event.getPlayer().getName());
        if (leader.equals(event.getPlayer().getName())) leader = "No one";
    }

    @EventHandler
    public void leave(PlayerLeaveEvent event) {
        if (!pass) return;
        points.remove(event.getPlayer().getName());
        benchControl.remove(event.getPlayer().getName());
        if (leader.equals(event.getPlayer().getName())) leader = "No one";
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL) {
            Block block = event.getClickedBlock();

            if (block == null)
                return;

            int blockType = block.getTypeId();

            if (blockType == Material.SOIL.getId()) {
                event.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);
                event.setCancelled(true);

                block.setTypeId(blockType);
                block.setData(block.getData());
            }
        }
    }

    private void doChef() {
        tasks.add(new BukkitRunnable() {
            int count = 11;

            public void run() {
                count--;
                if (count == 0) {
                    start = true;
                    doChefPast();
                    this.cancel();
                    return;
                }
                Bukkit.broadcastMessage(ChatColor.YELLOW + "[" + ChatColor.GOLD + "ISM" + ChatColor.YELLOW + "] " + ChatColor.RED + "MasterCraft starting in " + count + " second(s)!");
            }
        }.runTaskTimer(plugin, 0L, 20L));
    }

    private void doChefPast() {
        tasks.add(new BukkitRunnable() {
            public void run() {
                benchControl.clear();
                for (Block b : allBlocks()) {
                    Material material = b.getType();
                    b.setType(Material.AIR);
                    b.setType(material);
                }
                for (iPlayer player : ISM.getIPlayers().values()) {
                    if (player.inGame) {
                        player.getInventory().clear();
                        player.getInventory().setHelmet(new ItemStack(Material.AIR));
                        player.getInventory().setChestplate(new ItemStack(Material.AIR));
                        player.getInventory().setLeggings(new ItemStack(Material.AIR));
                        player.getInventory().setBoots(new ItemStack(Material.AIR));
                    }
                }
                Bukkit.broadcastMessage(ChatColor.YELLOW + "[" + ChatColor.GOLD + "ISM" + ChatColor.YELLOW + "] " + ChatColor.RED + "Picking new recipe!");
                tasks.add(new BukkitRunnable() {
                    ItemStack target = recipes.get(new Random().nextInt(recipes.size()));

                    public void run() {
                        Bukkit.broadcastMessage(ChatColor.YELLOW + "[" + ChatColor.GOLD + "ISM" + ChatColor.YELLOW + "] " + ChatColor.RED + "The recipe has been chosen!");
                        Bukkit.broadcastMessage(ChatColor.GOLD + "The final item has to be a " + target.getType().toString().replaceAll("_", " ") + "!");
                        if (target.hasItemMeta())
                            Bukkit.broadcastMessage(ChatColor.GOLD + "  It has to be called " + ChatColor.RED + target.getItemMeta().getDisplayName() + ChatColor.GOLD + "!");
                        Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "    GO!");
                        for (iPlayer player : ISM.getIPlayers().values()) {
                            if (player.inGame) {
                                player.getInventory().clear();
                                player.getInventory().setHelmet(new ItemStack(Material.AIR));
                                player.getInventory().setChestplate(new ItemStack(Material.AIR));
                                player.getInventory().setLeggings(new ItemStack(Material.AIR));
                                player.getInventory().setBoots(new ItemStack(Material.AIR));
                                for (ItemStack i : ingredients.get(target)) {
                                    player.getInventory().addItem(i);
                                }
                            }
                        }
                    }
                }.runTaskLater(plugin, 10 * 20L));
            }
        }.runTaskTimer(plugin, 0L, 40 * 20L));
    }
    /*@EventHandler
    public void parachute(PlayerMoveEvent e) {
        if (!pass || !iPlayer.getIPlayer(e.getPlayer()).inGame) return;
        if (e.getPlayer().getVelocity().getY() < 0.3 && e.getPlayer().getLocation().subtract(0, 1, 0).getBlock().getType() == Material.AIR) {
            if (e.getPlayer().getItemInHand().getType().toString().equals("EMPTY_MAP")) {
                Vector velocity;
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
    }*/
}
