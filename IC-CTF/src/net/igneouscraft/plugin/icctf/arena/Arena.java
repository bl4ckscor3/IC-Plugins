package net.igneouscraft.plugin.icctf.arena;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import net.igneouscraft.plugin.icctf.ICCTF;
import net.igneouscraft.plugin.icctf.util.Cuboid;

/**
 * Models a playable arena
 * @author bl4ckscor3
 */
public class Arena
{
	private World world;
	private String name;
	private Cuboid arenaBounds;
	private Cuboid blueFlagBounds;
	private ArrayList<Location> blueSpawns;
	private Cuboid redFlagBounds;
	private ArrayList<Location> redSpawns;

	/**
	 * @param yaml The file this arena is saved in
	 * @param n The name of the arena
	 * @param w The world this arena is in
	 */
	public Arena(YamlConfiguration yaml, String n, World w)
	{
		world = w;
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
			int x = yaml.getInt("red.spawns." + i + ".x");

			if(x == 0)
				break;

			try
			{
				redSpawns.add(new Location(w, yaml.getInt("red.spawns." + i + ".x"), yaml.getInt("red.spawns." + i + ".y"), yaml.getInt("red.spawns." + i + ".z")));
			}
			catch(Exception e)
			{
				break forLoop;
			}
		}
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
	 * Checks if an arena exists
	 * @param name The name of the arena to check
	 * @return
	 */
	public static boolean isArena(String name)
	{
		File folder = new File(ICCTF.i().getDataFolder(), "arenas");

		if(!folder.exists())
			folder.mkdirs();

		for(File f : folder.listFiles())
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
		File folder = new File(ICCTF.i().getDataFolder(), "arenas");

		if(!folder.exists())
			folder.mkdirs();

		for(File f : folder.listFiles())
		{
			if(f.getName().split(".yml")[0].equals(name))
			{
				YamlConfiguration yaml = YamlConfiguration.loadConfiguration(f);

				return new Arena(yaml, name, ICCTF.i().getServer().getWorld(yaml.getString("world")));
			}
		}

		return null;
	}
}
