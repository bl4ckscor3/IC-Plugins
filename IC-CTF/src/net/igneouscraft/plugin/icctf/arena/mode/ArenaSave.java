package net.igneouscraft.plugin.icctf.arena.mode;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.igneouscraft.plugin.icctf.ICCTF;

/**
 * Serves as a class to save information about the player who is currently in arena mode
 * @author bl4ckscor3
 */
public class ArenaSave
{
	private Player p;
	private AMS state;
	private String name;
	private Plugin pl;
	private File file;
	private YamlConfiguration yaml;
	private int blueSpawns = 0;
	private int redSpawns = 0;
	
	/**
	 * @param player The player who currently sets up this arena
	 * @param n The name of the arena
	 * @param players The maximum amount of players required to start
	 */
	public ArenaSave(Player player, String n, int players) throws IOException
	{
		p = player;
		name = n;
		state = AMS.ARENA1;
		pl = ICCTF.i();
		
		File f = new File(pl.getDataFolder(), "arenas/" + n +".yml");
		
		if(!f.exists())
			f.createNewFile();
		else
		{
			f.delete();
			f.createNewFile();
		}

		file = f;
		yaml = YamlConfiguration.loadConfiguration(f);
		yaml.set("world", player.getWorld().getName());
		yaml.set("players", players);
		yaml.save(f);
	}
	
	/**
	 * @return The player currently in arena mode
	 */
	public Player getPlayer()
	{
		return p;
	}
	
	/**
	 * @return The name of the arena involved
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * @return The state the player is currently in
	 */
	public AMS getState()
	{
		return state;
	}
	
	/**
	 * @return The file this arena gets saved in
	 */
	public File getFile()
	{
		return file;
	}
	
	/**
	 * @return The file as a YamlConfiguration this arena gets saved in
	 */
	public YamlConfiguration getYamlFile()
	{
		return yaml;
	}
	
	/**
	 * Sets the state the player is currently in
	 * @param s The state to set
	 */
	public void setState(AMS s)
	{
		state = s;
	}

	/**
	 * @return How many spawnpoints there are for the blue team
	 */
	public int blueSpawns()
	{
		return blueSpawns;
	}
	
	/**
	 * Increases the number of blue spawns there are
	 */
	public void increaseBlue()
	{
		blueSpawns++;
	}
	
	/**
	 * @return How many spawnpoints there are for the red team
	 */
	public int redSpawns()
	{
		return redSpawns;
	}

	/**
	 * Increases the number of red spawns there are
	 */
	public void increaseRed()
	{
		redSpawns++;
	}
}