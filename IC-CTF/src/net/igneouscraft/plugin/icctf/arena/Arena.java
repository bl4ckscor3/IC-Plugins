package net.igneouscraft.plugin.icctf.arena;

import java.util.ArrayList;

import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import net.igneouscraft.plugin.icctf.RoomBounds;

/**
 * Models a playable arena
 * @author bl4ckscor3
 */
public class Arena
{
	public World world;
	public String name;
	public RoomBounds arenaBounds;
	public RoomBounds blueSpawnBounds;
	public ArrayList<String> blueSpawns;
	public RoomBounds redSpawnBounds;
	public ArrayList<String> redSpawns;
	
	public Arena(YamlConfiguration yaml)
	{
		//TODO: write data from yaml into variables
	}
	
	/**
	 * @return The name of the arena
	 */
	public String getName()
	{
		return name;
	}
}
