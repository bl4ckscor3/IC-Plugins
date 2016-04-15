package net.igneouscraft.plugin.icctf;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.block.Sign;

import net.igneouscraft.plugin.icctf.arena.Arena;
import net.igneouscraft.plugin.icctf.arena.Lobby;

public class Data
{
	public static final ArrayList<Lobby> lobbies = new ArrayList<Lobby>();
	private static final ArrayList<Arena> arenas = new ArrayList<Arena>();
	public static final HashMap<String,ArrayList<Sign>> signs = new HashMap<String,ArrayList<Sign>>();
	
	/**
	 * Adds a lobby to the lobby list
	 * @param a The lobby to add
	 * @return true if the lobby got added, false otherwise
	 */
	public static boolean addLobby(Lobby l)
	{
		return lobbies.add(l);
	}
	
	/**
	 * Removes a lobby from the lobby list
	 * @param a The lobby to remove
	 * @return true if the lobby got removed, false otherwise
	 */
	public static boolean removeLobby(Lobby l)
	{
		return lobbies.remove(l);
	}
	
	/**
	 * @return All lobbies
	 */
	public static ArrayList<Lobby> getLobbies()
	{
		return lobbies;
	}
	
	/**
	 * Adds an arena to the arenas list
	 * @param a The arena to add
	 * @return true if the arena got added, false otherwise
	 */
	public static boolean addArena(Arena a)
	{
		return arenas.add(a);
	}
	
	/**
	 * Removes an arena from the arena list
	 * @param a The arena to remove
	 * @return true if the arena got removed, false otherwise
	 */
	public static boolean removeArena(Arena a)
	{
		return arenas.remove(a);
	}
	
	/**
	 * @return All arenas
	 */
	public static ArrayList<Arena> getArenas()
	{
		return arenas;
	}
	
	/**
	 * Links a sign to the specified arena in the signs map
	 * @param a The arena this sign is associated with
	 * @param s The sign to assign the arena to
	 * @return The previous sign list before adding this sign, null if none existed
	 */
	public static ArrayList<Sign> addSign(String a, Sign s)
	{
		ArrayList<Sign> al = signs.containsKey(a) ? signs.get(a) : new ArrayList<Sign>();

		if(!al.add(s))
			return al;
		return signs.put(a, al);
	}
	
	/**
	 * Removes a sign from the signs list
	 * @param a The arena this sign is associated with
	 * @param s The sign to remove
	 * @return The previous sign list before removing this sign, null if none existed
	 */
	public static ArrayList<Sign> removeSign(String a, Sign s)
	{
		ArrayList<Sign> al = signs.containsKey(a) ? signs.get(a) : new ArrayList<Sign>();
		
		if(!al.remove(s))
			return al;
		return signs.remove(a);
	}
	
	/**
	 * @return All signs associated with their arenas
	 */
	public static HashMap<String,ArrayList<Sign>> getSigns()
	{
		return signs;
	}
}
