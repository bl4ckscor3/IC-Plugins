package net.igneouscraft.plugin.icctf;

import java.util.ArrayList;

import net.igneouscraft.plugin.icctf.arena.Arena;
import net.igneouscraft.plugin.icctf.arena.Lobby;

public class Data
{
	public static final ArrayList<Lobby> lobbies = new ArrayList<Lobby>();
	private static final ArrayList<Arena> arenas = new ArrayList<Arena>();
	
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
	 * @return All arenas
	 */
	public static ArrayList<Arena> getArenas()
	{
		return arenas;
	}
}
