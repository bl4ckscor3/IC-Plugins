package net.igneouscraft.plugin.icctf.arena.mode;

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
					f.set("arena.1.x", l.getBlockX());
					f.set("arena.1.y", l.getBlockY());
					f.set("arena.1.z", l.getBlockZ());
					f.save(data.getFile());
					data.setState(AMS.ARENA2);
					p.sendMessage(ICCTF.prefix + "Please select the second corner of the arena.");
					break;
				case ARENA2:
					f.set("arena.2.x", l.getBlockX());
					f.set("arena.2.y", l.getBlockY());
					f.set("arena.2.z", l.getBlockZ());
					f.save(data.getFile());
					data.setState(AMS.BLUEFLAG1);
					p.sendMessage(ICCTF.prefix + "Please select the first corner of the blue team's flag.");
					break;
				case BLUEFLAG1:
					f.set("blue.flag.1.x", l.getBlockX());
					f.set("blue.flag.1.y", l.getBlockY());
					f.set("blue.flag.1.z", l.getBlockZ());
					f.save(data.getFile());
					data.setState(AMS.BLUEFLAG2);
					p.sendMessage(ICCTF.prefix + "Please select the second corner of the blue team's flag.");
					break;
				case BLUEFLAG2:
					f.set("blue.flag.2.x", l.getBlockX());
					f.set("blue.flag.2.y", l.getBlockY());
					f.set("blue.flag.2.z", l.getBlockZ());
					f.save(data.getFile());
					data.setState(AMS.BLUESPAWNPOINTS);
					p.sendMessage(ICCTF.prefix + "Please select the spawnpoints of the blue team. Type " + ChatColor.AQUA + "/ctf continue" + ChatColor.WHITE + " to finish.");
					break;
				case BLUESPAWNPOINTS:
					data.increaseBlue();
					f.set("blue.spawns." + data.blueSpawns() + ".x", l.getBlockX() + 0.5D);
					f.set("blue.spawns." + data.blueSpawns() + ".y", l.getBlockY() + 1.0D);
					f.set("blue.spawns." + data.blueSpawns() + ".z", l.getBlockZ() + 0.5D);
					f.save(data.getFile());
					p.sendMessage(ICCTF.prefix + "Spawnpoint added.");
					break;
				case REDFLAG1:
					f.set("red.flag.1.x", l.getBlockX());
					f.set("red.flag.1.y", l.getBlockY());
					f.set("red.flag.1.z", l.getBlockZ());
					f.save(data.getFile());
					data.setState(AMS.REDFLAG2);
					p.sendMessage(ICCTF.prefix + "Please select the second corner of the red team's flag.");
					break;
				case REDFLAG2:
					f.set("red.flag.1.x", l.getBlockX());
					f.set("red.flag.1.y", l.getBlockY());
					f.set("red.flag.1.z", l.getBlockZ());
					f.save(data.getFile());
					data.setState(AMS.REDSPAWNPOINTS);
					p.sendMessage(ICCTF.prefix + "Please select the spawnpoints of the red team. Type " + ChatColor.AQUA + "/ctf continue" + ChatColor.WHITE + " to finish.");
					break;
				case REDSPAWNPOINTS:
					data.increaseRed();
					f.set("red.spawns." + data.redSpawns() + ".x", l.getBlockX() + 0.5D);
					f.set("red.spawns." + data.redSpawns() + ".y", l.getBlockY() + 1.0D);
					f.set("red.spawns." + data.redSpawns() + ".z", l.getBlockZ() + 0.5D);
					f.save(data.getFile());
					p.sendMessage(ICCTF.prefix + "Spawnpoint added.");
					break;
				case LOBBY1:
					f.set("lobby.1.x", l.getBlockX());
					f.set("lobby.1.y", l.getBlockY());
					f.set("lobby.1.z", l.getBlockZ());
					f.save(data.getFile());
					data.setState(AMS.LOBBY2);
					p.sendMessage(ICCTF.prefix + "Please select the second lobby corner.");
					break;
				case LOBBY2:
					f.set("lobby.2.x", l.getBlockX());
					f.set("lobby.2.y", l.getBlockY());
					f.set("lobby.2.z", l.getBlockZ());
					f.save(data.getFile());
					data.setState(AMS.LOBBYSPAWN);
					p.sendMessage(ICCTF.prefix + "Please select the lobby spawn, where players will come to when they wait for the start of the game.");
					break;
				case LOBBYSPAWN:
					f.set("lobby.spawn.x", l.getBlockX() + 0.5D);
					f.set("lobby.spawn.y", l.getBlockY() + 1.0D);
					f.set("lobby.spawn.z", l.getBlockZ() + 0.5D);
					f.save(data.getFile());
					ArenaMode.deactivateFor(p);
					p.sendMessage(ICCTF.prefix + "You are done setting up the arena.");
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
	public static void activateFor(Player p, String name, String maxPlayers) throws IOException
	{
		if(!active(p))
		{
			try
			{
				int i = Integer.parseInt(maxPlayers);
				int min = ICCTF.i().getConfig().getInt("minimumPlayers");
				int max = ICCTF.i().getConfig().getInt("maximumPlayers");
				
				if(i < min || i > max || i % 2 != 0)
				{
					p.sendMessage(ICCTF.prefix + "Please specify an even number between " + min + " and " + max + ".");
					return;
				}
				
				players.put(p, new ArenaSave(p, name, i));
				p.sendMessage(ICCTF.prefix + "You are now in Arena Mode. Please select the first corner of the arena (leftclick a block, similar to a World-Edit selection).");
			}
			catch(NumberFormatException e)
			{
				p.sendMessage(ICCTF.prefix + "\"" + maxPlayers + "\" is not a number.");
			}
		}
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
}
