package net.igneouscraft.plugin.icctf.arena;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import net.igneouscraft.plugin.icctf.ICCTF;

/**
 * Models a lobby waiting for an arena to start
 * @author bl4ckscor3
 */
public class Lobby
{
	public static final ArrayList<Lobby> lobbies = new ArrayList<Lobby>();
	private String arenaName;
	private ArrayList<String> players = new ArrayList<String>();
	private ArrayList<String> lobbyBounds= new ArrayList<String>();
	private Location spawn;
	private Arena arena;
	
	/**
	 * @param aN The name of the arena this lobby corresponds to
	 */
	public Lobby(String aN)
	{
		File folder = new File(ICCTF.i().getDataFolder(), "arenas");
		File f = new File(ICCTF.i().getDataFolder(), "arenas/" + aN +".yml");
		
		if(!folder.exists() || !f.exists())
			arena = null;

		YamlConfiguration yaml = YamlConfiguration.loadConfiguration(f);
		
		arena = new Arena(yaml);
		//TODO: write data from yaml info variables
		arenaName = aN;
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
	
	public void addPlayer(Player p)
	{
		//TODO: Add player to lobby and tp
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
}
