package net.igneouscraft.plugin.icctf.util;

import org.bukkit.Location;
import org.bukkit.World;

/**
 * Models a cuboid
 * @author bl4ckscor3
 */
public class Cuboid
{
	public World world;
	public int xMin;
	public int xMax;
	public int yMin;
	public int yMax;
	public int zMin;
	public int zMax;
	
	/**
	 * @param w The world this cuboid is in
	 * @param l1 The location of the first corner of this cuboid
	 * @param l2 The location of the second corner of this cuboid
	 */
	public Cuboid(Location l1, Location l2)
	{
		world = l1.getWorld();
		xMin = Math.min(l1.getBlockX(), l2.getBlockX());
		xMax = Math.max(l1.getBlockX(), l2.getBlockX());
		yMin = Math.min(l1.getBlockY(), l2.getBlockY());
		yMax = Math.max(l1.getBlockY(), l2.getBlockY());
		zMin = Math.min(l1.getBlockZ(), l2.getBlockZ());
		zMax = Math.max(l1.getBlockZ(), l2.getBlockZ());
	}
	
	/**
	 * Checks if a location is within this cuboid
	 * @param l The location to check
	 * @return true if the location is within this cuboid, false otherwise
	 */
	public boolean contains(Location l)
	{
		return !(l.getWorld() == world ||
				l.getBlockX() < xMin || l.getBlockX() > xMax ||
				l.getBlockY() < yMin || l.getBlockY() > yMax ||
				l.getBlockZ() < zMin || l.getBlockZ() > zMax);
	}
}
