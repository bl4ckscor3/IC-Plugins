package net.igneouscraft.plugin.icctf.arena;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import net.igneouscraft.plugin.icctf.Data;
import net.igneouscraft.plugin.icctf.ICCTF;
import net.igneouscraft.plugin.icctf.util.Cuboid;

/**
 * Models a lobby waiting for an arena to start
 * @author bl4ckscor3
 */
public class Lobby
{
	private ArrayList<String> players = new ArrayList<String>();
	private Cuboid bounds;
	private Location spawn;
	private Arena arena;

	/**
	 * @param yaml The YamlConfiguration of the arena this lobby corresponds to
	 * @param name The name of the arena
	 */
	public Lobby(YamlConfiguration yaml, String name)
	{
		World w = ICCTF.i().getServer().getWorld(yaml.getString("world"));

		arena = new Arena(yaml, name, w);
		bounds = new Cuboid(
				new Location(w, yaml.getInt("lobby.1.x"), yaml.getInt("lobby.1.y"), yaml.getInt("lobby.1.z")),
				new Location(w, yaml.getInt("lobby.2.x"), yaml.getInt("lobby.2.y"), yaml.getInt("lobby.2.z")));
		spawn = new Location(w, yaml.getDouble("lobby.spawn.x"), yaml.getDouble("lobby.spawn.y"), yaml.getDouble("lobby.spawn.z"));
		Data.addLobby(this);
	}

	/**
	 * @return The arena this lobby corresponds to
	 */
	public Arena getArena()
	{
		return arena;
	}

	/**
	 * @return All the players that are currently in the lobby
	 */
	public ArrayList<String> getPlayers()
	{
		return players;
	}

	/**
	 * @return The bounds of this lobby as a Cuboid
	 */
	public Cuboid getBounds()
	{
		return bounds;
	}

	/**
	 * @return The spawn of this lobby as a Location
	 */
	public Location getSpawn()
	{
		return spawn;
	}

	/**
	 * Adds and teleports a player to a lobby
	 * @param p The player to add
	 */
	public void addPlayer(Player p)
	{
		players.add(p.getName());
		p.teleport(spawn);
		p.sendMessage(ICCTF.prefix + "You have been teleported to the lobby. Please select your team, or let the server randomly decide for you.");
	}
	
	/**
	 * Removes a player from the lobby and removes the lobby if no players are in it
	 * @param p The player to remove
	 */
	public void removePlayer(Player p)
	{
		players.remove(p.getName());
		//TODO: tp player to initial location aka world spawn he was in previously
		p.sendMessage(ICCTF.prefix + "You are no longer in the lobby.");
	}

	/**
	 * Checks if a player is in a specific lobby
	 * @param p The player to check
	 * @return true if they are in this lobby, false otherwise
	 */
	public boolean isInThisLobby(Player p)
	{
		for(String s : getPlayers())
		{
			if(s.equals(p.getName()))
				return true;
		}

		return false;
	}

	/**
	 * Checks if a lobby is active
	 * @param lobby The name of the arena corresponding to the lobby
	 * @return true if the lobby is active, false otherwise
	 */
	public static boolean isLobby(String lobby)
	{
		for(Lobby l : Data.getLobbies())
		{
			if(l.getArena().getName().equals(lobby))
				return true;
		}

		return false;
	}

	/**
	 * @param lobby The name of the arena corresponding to the lobby
	 * @return The lobby if it exists, null otherwise
	 */
	public static Lobby getLobby(String lobby)
	{
		for(Lobby l : Data.getLobbies())
		{
			if(l.getArena().getName().equals(lobby))
				return l;
		}

		return null;
	}

	/**
	 * @param lobby Gets the lobby a player is in
	 * @param p The player to check
	 * @return The lobby if it exists, null otherwise
	 */
	public static Lobby getLobby(Player p)
	{
		for(Lobby l : Data.getLobbies())
		{
			for(String s : l.getPlayers())
			{
				if(s.equals(p.getName()))
					return l;
			}
		}

		return null;
	}
	
	/**
	 * Checks if a player is in a lobby
	 * @param p The player to check
	 * @return true if they are in a lobby, false otherwise
	 */
	public static boolean isInLobby(Player p)
	{
		for(Lobby l : Data.getLobbies())
		{
			for(String s : l.getPlayers())
			{
				if(s.equals(p.getName()))
					return true;
			}
		}

		return false;
	}
}	
