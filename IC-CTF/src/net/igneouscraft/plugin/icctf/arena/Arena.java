package net.igneouscraft.plugin.icctf.arena;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import net.igneouscraft.plugin.icctf.Data;
import net.igneouscraft.plugin.icctf.ICCTF;
import net.igneouscraft.plugin.icctf.util.Cuboid;

/**
 * Models a playable arena
 * @author bl4ckscor3
 */
public class Arena
{
	private World world;
	private int players;
	private String name;
	private Cuboid arenaBounds;
	private Cuboid blueFlagBounds;
	private ArrayList<Location> blueSpawns = new ArrayList<Location>();
	private Cuboid redFlagBounds;
	private ArrayList<Location> redSpawns = new ArrayList<Location>();
	
	/**
	 * @param yaml The file this arena is saved in
	 * @param n The name of the arena
	 * @param w The world this arena is in
	 */
	public Arena(YamlConfiguration yaml, String n, World w)
	{
		world = w;
		players = yaml.getInt("players");
		name = n;
		arenaBounds = new Cuboid(
				new Location(w, yaml.getInt("arena.1.x"), yaml.getInt("arena.1.y"), yaml.getInt("arena.1.z")),
				new Location(w, yaml.getInt("arena.2.x"), yaml.getInt("arena.2.y"), yaml.getInt("arena.2.z")));
		blueFlagBounds = new Cuboid(
				new Location(w, yaml.getInt("blue.flag.1.x"), yaml.getInt("blue.flag.1.y"), yaml.getInt("blue.flag.1.z")),
				new Location(w, yaml.getInt("blue.flag.2.x"), yaml.getInt("blue.flag.2.y"), yaml.getInt("blue.flag.2.z")));

		forLoop:
		for(int i = 1; i <= 50; i++) //i don't think someone will add more than 50 spawns
		{
			try
			{
				blueSpawns.add(new Location(w, yaml.getInt("blue.spawns." + i + ".x"), yaml.getInt("blue.spawns." + i + ".y"), yaml.getInt("blue.spawns." + i + ".z")));
			}
			catch(Exception e)
			{
				break forLoop;
			}
		}

		redFlagBounds = new Cuboid(
				new Location(w, yaml.getInt("red.flag.1.x"), yaml.getInt("red.flag.1.y"), yaml.getInt("red.flag.1.z")),
				new Location(w, yaml.getInt("red.flag.2.x"), yaml.getInt("red.flag.2.y"), yaml.getInt("red.flag.2.z")));

		forLoop:
		for(int i = 1; i <= 50; i++) //i don't think someone will add more than 50 spawns
		{
			try
			{
				redSpawns.add(new Location(w, yaml.getInt("red.spawns." + i + ".x"), yaml.getInt("red.spawns." + i + ".y"), yaml.getInt("red.spawns." + i + ".z")));
			}
			catch(Exception e)
			{
				break forLoop;
			}
		}
		
		Data.addArena(this);
	}

	/**
	 * @return The world this arena is in
	 */
	public World getWorld()
	{
		return world;
	}

	/**
	 * @return The name of the arena
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @return The bounds of this arena as a Cuboid
	 */
	public Cuboid getArenaBounds()
	{
		return arenaBounds;
	}

	/**
	 * @return The bounds of the blue team's flag as a Cuboid
	 */
	public Cuboid getBlueFlagBounds()
	{
		return blueFlagBounds;
	}

	/**
	 * @return All locations of the blue team's spawns
	 */
	public ArrayList<Location> getBlueSpawns()
	{
		return blueSpawns;
	}

	/**
	 * @return The bounds of the red team's flag as a Cuboid
	 */
	public Cuboid getRedFlagBounds()
	{
		return redFlagBounds;
	}

	/**
	 * @return All locations of the red team's spawns
	 */
	public ArrayList<Location> getRedSpawns()
	{
		return redSpawns;
	}

	/**
	 * @return The maximum amount of players required to start
	 */
	public int getPlayers()
	{
		return players;
	}

	/**
	 * Checks if an arena exists
	 * @param name The name of the arena to check
	 * @return
	 */
	public static boolean isArena(String name)
	{
		for(File f : new File(ICCTF.i().getDataFolder(), "arenas").listFiles())
		{
			if(f.getName().split(".yml")[0].equals(name))
				return true;
		}

		return false;
	}

	/**
	 * @param name The name of the arena to get
	 * @return The Arena, null if none has been found
	 */
	public static Arena getArena(String name)
	{
		for(Arena a : Data.getArenas())
		{
			if(a.getName().equals(name))
				return a;
		}

		return null;
	}

	/**
	 * Gets the maximum of players the given arena can be joined by
	 * @param name The name of the arena
	 * @return The maximum amount of players, 0 if none given
	 */
	public static int getPlayerMaximum(String name)
	{
		return !isArena(name) ? 0 : YamlConfiguration.loadConfiguration(new File(ICCTF.i().getDataFolder(), "arenas/" + name + ".yml")).getInt("players");
	}
}
