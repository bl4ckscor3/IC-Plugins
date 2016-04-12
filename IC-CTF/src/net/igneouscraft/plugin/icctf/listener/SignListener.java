package net.igneouscraft.plugin.icctf.listener;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import net.igneouscraft.plugin.icctf.ICCTF;
import net.igneouscraft.plugin.icctf.arena.Arena;
import net.igneouscraft.plugin.icctf.arena.Lobby;

public class SignListener implements Listener
{
	@EventHandler
	public void onSignChange(SignChangeEvent event)
	{
		if(event.getBlock().getType() == Material.WALL_SIGN)
		{
			if(event.getLine(0).equalsIgnoreCase("[ctf]"))
			{
				if(!Arena.isArena(event.getLine(1)))
					event.getPlayer().sendMessage(ICCTF.prefix + "This arena does not exist.");
				else
				{
					event.setLine(0, ICCTF.prefix);
					event.setLine(3, ChatColor.GREEN + "0/1234"); //TODO: add player limit per arena
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			if(event.getClickedBlock().getType() == Material.WALL_SIGN)
			{
				Sign s = (Sign)event.getClickedBlock().getState();

				if(s.getLine(0).trim().equals(ICCTF.prefix.substring(0, ICCTF.prefix.length() - 2).trim()))
				{
					String line1 = s.getLine(1);
					Lobby l;

					if(!Lobby.isLobby(line1))
						l = new Lobby(line1);
					else
						l = Lobby.getLobby(line1);
					
					l.addPlayer(event.getPlayer());
				}
			}
		}
	}
}
