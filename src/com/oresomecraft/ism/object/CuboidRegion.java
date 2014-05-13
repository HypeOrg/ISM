package com.oresomecraft.ism.object;

import com.oresomecraft.ism.Storage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


//Cuboid code taken from DarkLord12's paintwar plugin, all credit goes to DarkLord12.

public class CuboidRegion implements Iterable<Block> {
    public int x1, y1, z1;
    public int x2, y2, z2;

    public CuboidRegion(int x1, int y1, int z1, int x2, int y2, int z2) {
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
    }

    public boolean isInside(Location loc) {
        if (!loc.getWorld().getName().equals(Storage.roundID + ""))
            return false;
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        return x >= x1 && x <= x2 && y >= y1 && y <= y2 && z >= z1 && z <= z2;
    }

    public List<Block> getBlocks() {
        World world = getWorld();
        List<Block> blocks = new ArrayList<Block>();
        for (int x = x1; x <= x2; x++) {
            for (int z = z1; z <= z2; z++) {
                for (int y = y1; y <= y2; y++) {
                    blocks.add(world.getBlockAt(x, y, z));
                }
            }
        }
        return blocks;
    }

    public int getSizeX() {
        return (x2 - x1) + 1;
    }

    public int getSizeY() {
        return (y2 - y1) + 1;
    }

    public int getSizeZ() {
        return (z2 - z1) + 1;
    }

    public int getVolume() {
        return getSizeX() * getSizeY() * getSizeZ();
    }

    public Location getLowerNE() {
        return new Location(this.getWorld(), this.x1, this.y1, this.z1);
    }

    public Location getUpperSW() {
        return new Location(this.getWorld(), this.x2, this.y2, this.z2);
    }

    public World getWorld() {
        World world = Bukkit.getWorld(Storage.roundIDToAString());
        if (world == null)
            throw new IllegalStateException("World is not loaded");
        return world;
    }

    public Location getHorizontalMirrorLocation(Location loc) {
        int x = loc.getBlockX();
        int z = loc.getBlockZ();
        boolean xBorder = x == x1 || x == x2;
        boolean x1Border = xBorder ? x == x1 : false;
        boolean zBorder = z == z1 || z == z2;
        boolean z1Border = zBorder ? z == z1 : false;
        if (xBorder)
            loc.setX(x1Border ? x2 : x1 + 1);
        if (zBorder)
            loc.setZ(z1Border ? z2 : z1 + 1);
        return loc;
    }

    @Override
    public Iterator<Block> iterator() {
        return getBlocks().iterator();
    }
}