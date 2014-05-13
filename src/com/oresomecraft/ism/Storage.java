package com.oresomecraft.ism;

import com.oresomecraft.ism.object.CuboidRegion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Storage {

    /* Round Attributes */
    public static String currentRound = "None";
    public static ArrayList<String> currentCreators = new ArrayList<String>();
    public static long roundID = 0;
    public static String roundStatus = "None";
    public static Gamemode currentGamemode = Gamemode.NONE;
    public static String gTo = "None";
    /*------------------*/

    /* Map Attributes */
    public static ArrayList<String> noBreak = new ArrayList<String>();
    public static ArrayList<String> noPlace = new ArrayList<String>();
    public static ArrayList<String> noDrops = new ArrayList<String>();
    public static ArrayList<String> noPvP = new ArrayList<String>();
    public static ArrayList<String> noHunger = new ArrayList<String>();
    public static ArrayList<String> natural = new ArrayList<String>();
    /*------------------*/


    //Map config attributes
    public static ArrayList<String> maps = new ArrayList<String>();
    public static HashMap<String, String> descs = new HashMap<String, String>();
    public static HashMap<String, Gamemode> gameTypes = new HashMap<String, Gamemode>();
    public static HashMap<String, Location> spawns = new HashMap<String, Location>();
    public static HashMap<String, Location> lobbies = new HashMap<String, Location>();
    public static HashMap<String, CuboidRegion> regions = new HashMap<String, CuboidRegion>();
    public static HashMap<String, HashMap<Integer, ItemStack>> kits = new HashMap<String, HashMap<Integer, ItemStack>>();
    public static HashMap<String, ArrayList<String>> creators = new HashMap<String, ArrayList<String>>();

    public static void registerMaps() {
        for (File f : new File("config/").listFiles()) {
            if (f.getName().contains(".xml")) {
                try {
                    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                    Document doc = dBuilder.parse(f);
                    doc.getDocumentElement().normalize();

                    String rootNode = doc.getDocumentElement().getNodeName();
                    NodeList rootList = doc.getElementsByTagName(rootNode);
                    String mapName = ((Element) rootList.item(0)).getElementsByTagName("name").item(0).getChildNodes().item(0).getNodeValue();

                    String desc = ((Element) rootList.item(0)).getElementsByTagName("description").item(0).getChildNodes().item(0).getNodeValue();

                    String nbreak = "";
                    try {
                        nbreak = ((Element) rootList.item(0)).getElementsByTagName("break").item(0).getChildNodes().item(0).getNodeValue();
                    } catch (NullPointerException ex) {
                        //Couldn't find the line in the config.
                    }
                    String nplace = "";
                    try {
                        nplace = ((Element) rootList.item(0)).getElementsByTagName("place").item(0).getChildNodes().item(0).getNodeValue();
                    } catch (NullPointerException ex) {
                        //Couldn't find the line in the config.
                    }
                    String ndrop = "";
                    try {
                        ndrop = ((Element) rootList.item(0)).getElementsByTagName("drops").item(0).getChildNodes().item(0).getNodeValue();
                    } catch (NullPointerException ex) {
                        //Couldn't find the line in the config.
                    }
                    String npvp = "";
                    try {
                        npvp = ((Element) rootList.item(0)).getElementsByTagName("pvp").item(0).getChildNodes().item(0).getNodeValue();
                    } catch (NullPointerException ex) {
                        //Couldn't find the line in the config.
                    }
                    String nhunger = "";
                    try {
                        nhunger = ((Element) rootList.item(0)).getElementsByTagName("hunger").item(0).getChildNodes().item(0).getNodeValue();
                    } catch (NullPointerException ex) {
                        //Couldn't find the line in the config.
                    }
                    String nnatural = "";
                    try {
                        nnatural = ((Element) rootList.item(0)).getElementsByTagName("natural").item(0).getChildNodes().item(0).getNodeValue();
                    } catch (NullPointerException ex) {
                        //Couldn't find the line in the config.
                    }

                    if (ndrop.equalsIgnoreCase("false")) noDrops.add(mapName);
                    if (nbreak.equalsIgnoreCase("false")) noBreak.add(mapName);
                    if (nplace.equalsIgnoreCase("false")) noPlace.add(mapName);
                    if (npvp.equalsIgnoreCase("false")) noPvP.add(mapName);
                    if (nhunger.equalsIgnoreCase("false")) noHunger.add(mapName);
                    if (nnatural.equalsIgnoreCase("true")) natural.add(mapName);

                    NodeList nSpawn = doc.getElementsByTagName("spawn");
                    spawns.put(mapName, new Location(Bukkit.getWorld(mapName), Integer.parseInt(nSpawn.item(0).getAttributes().getNamedItem("x").getTextContent()),
                            Integer.parseInt(nSpawn.item(0).getAttributes().getNamedItem("y").getTextContent()),
                            Integer.parseInt(nSpawn.item(0).getAttributes().getNamedItem("z").getTextContent()),
                            Integer.parseInt(nSpawn.item(0).getAttributes().getNamedItem("pitch").getTextContent()),
                            Integer.parseInt(nSpawn.item(0).getAttributes().getNamedItem("yaw").getTextContent())));

                    NodeList nLobby = doc.getElementsByTagName("lobby");
                    lobbies.put(mapName, new Location(Bukkit.getWorld(mapName), Integer.parseInt(nLobby.item(0).getAttributes().getNamedItem("x").getTextContent()),
                            Integer.parseInt(nLobby.item(0).getAttributes().getNamedItem("y").getTextContent()),
                            Integer.parseInt(nLobby.item(0).getAttributes().getNamedItem("z").getTextContent()),
                            Integer.parseInt(nLobby.item(0).getAttributes().getNamedItem("pitch").getTextContent()),
                            Integer.parseInt(nLobby.item(0).getAttributes().getNamedItem("yaw").getTextContent())));

                    NodeList nRegion = doc.getElementsByTagName("region");
                    regions.put(mapName, new CuboidRegion(
                            Integer.parseInt(nRegion.item(0).getAttributes().getNamedItem("x1").getTextContent()),
                            Integer.parseInt(nRegion.item(0).getAttributes().getNamedItem("y1").getTextContent()),
                            Integer.parseInt(nRegion.item(0).getAttributes().getNamedItem("z1").getTextContent()),
                            Integer.parseInt(nRegion.item(0).getAttributes().getNamedItem("x2").getTextContent()),
                            Integer.parseInt(nRegion.item(0).getAttributes().getNamedItem("y2").getTextContent()),
                            Integer.parseInt(nRegion.item(0).getAttributes().getNamedItem("z2").getTextContent())));

                    gameTypes.put(mapName, Gamemode.valueOf(((Element) rootList.item(0)).getElementsByTagName("objective").item(0).getChildNodes().item(0).getNodeValue()));
                    maps.add(mapName);
                    descs.put(mapName, desc);

                    NodeList creator = doc.getElementsByTagName("creators");
                    ArrayList<String> tempC = new ArrayList<String>();

                    for (int i = 0; i < creator.getLength(); i++) {
                        Node node = creator.item(i);
                        for (int j = 0; j < node.getChildNodes().getLength(); j++) {

                            Node child = node.getChildNodes().item(j);

                            if (!child.getNodeName().equals("#text")) {
                                NamedNodeMap attributes = child.getAttributes();
                                tempC.add(attributes.getNamedItem("name").getTextContent());
                            }
                        }
                    }
                    creators.put(mapName, tempC);
                    NodeList nKit = doc.getElementsByTagName("kit");
                    HashMap<Integer, ItemStack> kit = new HashMap<Integer, ItemStack>();

                    for (int i = 0; i < nKit.getLength(); i++) {
                        Node node = nKit.item(i);
                        for (int j = 0; j < node.getChildNodes().getLength(); j++) {

                            Node child = node.getChildNodes().item(j);

                            if (!child.getNodeName().equals("#text")) {
                                NamedNodeMap attributes = child.getAttributes();

                                if (child.getNodeName().equals("item")) {
                                    ItemStack it = new ItemStack(Material.matchMaterial(attributes.getNamedItem("type").getTextContent()),
                                            Integer.parseInt(attributes.getNamedItem("amount").getTextContent()));
                                    ItemMeta im = it.getItemMeta();
                                    try {
                                        im.setDisplayName(ChatColor.BLUE + attributes.getNamedItem("name").getTextContent());
                                    } catch (Exception ex) {
                                    }
                                    it.setItemMeta(im);
                                    kit.put(Integer.parseInt(attributes.getNamedItem("slot").getTextContent()), it);
                                }
                                if (child.getNodeName().equalsIgnoreCase("helmet")) {
                                    kit.put(-1, new ItemStack(Material.matchMaterial(attributes.getNamedItem("type").getTextContent()), 1));
                                }
                                if (child.getNodeName().equalsIgnoreCase("chestplate")) {
                                    kit.put(-2, new ItemStack(Material.matchMaterial(attributes.getNamedItem("type").getTextContent()), 1));
                                }
                                if (child.getNodeName().equalsIgnoreCase("leggings")) {
                                    kit.put(-3, new ItemStack(Material.matchMaterial(attributes.getNamedItem("type").getTextContent()), 1));
                                }
                                if (child.getNodeName().equalsIgnoreCase("boots")) {
                                    kit.put(-4, new ItemStack(Material.matchMaterial(attributes.getNamedItem("type").getTextContent()), 1));
                                }
                            }
                        }
                    }
                    kits.put(mapName, kit);
                } catch (NullPointerException e) {
                    System.out.println("[ISM] Something went wrong whilst adding " + f.getName() + "!");
                    e.printStackTrace();
                    System.out.println("If message was 'null', then something was missing in the config, please double check??");
                } catch (Exception e) {
                    //Don't care if didn't parse.
                }
            }
        }
    }

    public static String roundIDToAString() {
        return String.valueOf(Storage.roundID);
    }
}