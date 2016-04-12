package net.igneouscraft.plugin.icctf;

import org.bukkit.Location;

/**
 * Contains two locations sorted by size modelling a room
 * @author bl4ckscor3
 */
public class RoomBounds
{
	public Location min;
	public Location max;
	
	public RoomBounds(Location n, Location x)
	{
		//TODO: sort locations correctly
	}

	/**
	 * @return The smaller location of both
	 */
	public Location getMin()
	{
		return min;
	}
	
	/**
	 * @return The bigger location of both
	 */
	public Location getMax()
	{
		return max;
	}
}
