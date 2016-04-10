package net.igneouscraft.plugin.icctf.arenamode;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import net.igneouscraft.plugin.icctf.ICCTF;

/**
 * A class that handles setting up an arena
 * @author bl4ckscor3
 */
public class ArenaMode implements Listener
{
	private static final HashMap<Player,ArenaSave> players = new HashMap<Player,ArenaSave>();
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) throws IOException
	{
		Player p = event.getPlayer();

		if(active(p))
		{
			ArenaSave data = players.get(p);
			YamlConfiguration f = data.getYamlFile();
			Location l = event.getBlock().getLocation();
			
			event.setCancelled(true);
			
			switch(data.getState())
			{
				case ARENA1:
					f.set("arena1.x", l.getX());
					f.set("arena1.y", l.getY());
					f.set("arena1.z", l.getZ());
					f.save(data.getFile());
					data.setState(AMS.ARENA2);
					p.sendMessage(ICCTF.prefix + "Please select the second corner of the arena.");
					break;
				case ARENA2:
					f.set("arena2.x", l.getX());
					f.set("arena2.y", l.getY());
					f.set("arena2.z", l.getZ());
					f.save(data.getFile());
					data.setState(AMS.BLUEFLAG1);
					p.sendMessage(ICCTF.prefix + "Please select the first corner of the blue team's flag.");
					break;
				case BLUEFLAG1:
					f.set("blueflag1.x", l.getX());
					f.set("blueflag1.y", l.getY());
					f.set("blueflag1.z", l.getZ());
					f.save(data.getFile());
					data.setState(AMS.BLUEFLAG2);
					p.sendMessage(ICCTF.prefix + "Please select the second corner of the blue team's flag.");
					break;
				case BLUEFLAG2:
					f.set("blueflag2.x", l.getX());
					f.set("blueflag2.y", l.getY());
					f.set("blueflag2.z", l.getZ());
					f.save(data.getFile());
					data.setState(AMS.BLUESPAWNPOINTS);
					p.sendMessage(ICCTF.prefix + "Please select the spawnpoints of the blue team. Type " + ChatColor.AQUA + "/ctf continue" + ChatColor.WHITE + " to finish.");
					break;
				case BLUESPAWNPOINTS:
					data.increaseBlue();
					f.set("bluespawns." + data.blueSpawns() + ".x", l.getX());
					f.set("bluespawns." + data.blueSpawns() + ".y", l.getY());
					f.set("bluespawns." + data.blueSpawns() + ".z", l.getZ());
					f.save(data.getFile());
					p.sendMessage(ICCTF.prefix + "Spawnpoint added.");
					break;
				case REDFLAG1:
					f.set("redflag1.x", l.getX());
					f.set("redflag1.y", l.getY());
					f.set("redflag1.z", l.getZ());
					f.save(data.getFile());
					data.setState(AMS.REDFLAG2);
					p.sendMessage(ICCTF.prefix + "Please select the second corner of the red team's flag.");
					break;
				case REDFLAG2:
					f.set("redflag1.x", l.getX());
					f.set("redflag1.y", l.getY());
					f.set("redflag1.z", l.getZ());
					f.save(data.getFile());
					data.setState(AMS.REDSPAWNPOINTS);
					p.sendMessage(ICCTF.prefix + "Please select the spawnpoints of the red team. Type " + ChatColor.AQUA + "/ctf continue" + ChatColor.WHITE + " to finish.");
					break;
				case REDSPAWNPOINTS:
					data.increaseRed();
					f.set("redspawns." + data.redSpawns() + ".x", l.getX());
					f.set("redspawns." + data.redSpawns() + ".y", l.getY());
					f.set("redspawns." + data.redSpawns() + ".z", l.getZ());
					f.save(data.getFile());
					p.sendMessage(ICCTF.prefix + "Spawnpoint added.");
					break;
				default:
					break; 
			}
		}
	}
	
	/**
	 * Checks wether a player is currently in arena mode or not
	 * @param p The player to check
	 * @return true if the player is in arena mode, false otherwise
	 */
	public static boolean active(Player p)
	{
		return players.containsKey(p);
	}
	
	/**
	 * Activates arena mode for the specified player
	 * @param p The player to activate arena mode for
	 * @param name The name of the arena
	 */
	public static void activateFor(Player p, String name) throws IOException
	{
		if(!active(p))
			players.put(p, new ArenaSave(p, name));
	}
	
	/**
	 * Deactivates arena mode for the specified player
	 * @param p The player to deactivate arena mode for
	 */
	public static void deactivateFor(Player p)
	{
		if(active(p))
			players.remove(p);
	}
	
	/**
	 * @param p The player to get the state from
	 * @return The state of the player if they are currently in arena mode, null otherwise
	 */
	public static AMS getState(Player p)
	{
		if(active(p))
			return players.get(p).getState();
		return null;
	}
	
	/**
	 * Sets the state of a player
	 * @param p The player to set the state of
	 * @param state The state to set the player to
	 */
	public static void setState(Player p, AMS state)
	{
		if(active(p))
			players.get(p).setState(state);
	}
	
	/**
	 * @return How many spawnpoints there are for the blue team
	 */
	public static int blueSpawns(Player p)
	{
		return players.get(p).blueSpawns();
	}
	
	/**
	 * @return How many spawnpoints there are for the red team
	 */
	public static int redSpawns(Player p)
	{
		return players.get(p).redSpawns();
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
}
