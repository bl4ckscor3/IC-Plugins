package net.igneouscraft.plugin.icctf.arena;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import net.igneouscraft.plugin.icctf.ICCTF;
import net.igneouscraft.plugin.icctf.util.Cuboid;

/**
 * Models a lobby waiting for an arena to start
 * @author bl4ckscor3
 */
public class Lobby
{
	public static final ArrayList<Lobby> lobbies = new ArrayList<Lobby>();
	private ArrayList<Player> players = new ArrayList<Player>();
	private Cuboid bounds;
	private Location spawn;
	private Arena arena;

	/**
	 * @param aN The name of the arena this lobby corresponds to
	 */
	public Lobby(String aN)
	{
		File f = new File(ICCTF.i().getDataFolder(), "arenas/" + aN +".yml");
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration(f);
		World w = ICCTF.i().getServer().getWorld(yaml.getString("world"));

		arena = new Arena(yaml, aN, w);
		bounds = new Cuboid(
				new Location(w, yaml.getInt("lobby.1.x"), yaml.getInt("lobby.1.y"), yaml.getInt("lobby.1.z")),
				new Location(w, yaml.getInt("lobby.2.x"), yaml.getInt("lobby.2.y"), yaml.getInt("lobby.2.z")));
		spawn = new Location(w, yaml.getInt("lobby.spawn.x"), yaml.getInt("lobby.spawn.y"), yaml.getInt("lobby.spawn.z"));
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
	public ArrayList<Player> getPlayers()
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
		players.add(p);
		p.teleport(spawn);
		p.sendMessage(ICCTF.prefix + "You have been teleported to the lobby. Please select your team, or let the server randomly decide for you.");
	}
	
	/**
	 * Removes a player from the lobby and removes the lobby if no players are in it
	 * @param p The player to remove
	 */
	public void removePlayer(Player p)
	{
		players.remove(p);
		//TODO: tp player to initial location
		
		if(players.size() == 0)
			lobbies.remove(this);
	}

	/**
	 * Checks if a player is in a specific lobby
	 * @param p The player to check
	 * @return true if they are in this lobby, false otherwise
	 */
	public boolean isInThisLobby(Player p)
	{
		for(Player pl : getPlayers())
		{
			if(pl.equals(p))
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
		for(Lobby l : lobbies)
		{
			if(l.getArena().getName().equals(lobby))
				return true;
		}

		return false;
	}

	/**
	 * Adds a lobby to the list
	 * @param l The lobby to add
	 * @return The lobby that got added
	 */
	public static Lobby addLobby(Lobby l)
	{
		lobbies.add(l);
		return l;
	}

	/**
	 * @param lobby The name of the arena corresponding to the lobby
	 * @return The lobby if it exists, null otherwise
	 */
	public static Lobby getLobby(String lobby)
	{
		for(Lobby l : lobbies)
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
		for(Lobby l : lobbies)
		{
			for(Player pl : l.getPlayers())
			{
				if(pl.equals(p))
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
		for(Lobby l : lobbies)
		{
			for(Player pl : l.getPlayers())
			{
				if(pl.equals(p))
					return true;
			}
		}

		return false;
	}
}	
