package net.igneouscraft.plugin.icctf.listener;

import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import net.igneouscraft.plugin.icctf.ICCTF;
import net.igneouscraft.plugin.icctf.Util;
import net.igneouscraft.plugin.icctf.arena.Lobby;

public class SignListener implements Listener
{
	@EventHandler
	public void onSignChange(SignChangeEvent event)
	{
		Sign s = (Sign)event.getBlock();
		
		if(s.getLine(0).equalsIgnoreCase("[ctf]"))
		{
			String line1 = s.getLine(1);
			
			if(Util.isArena(line1))
			{
				Lobby l;
				
				if(!Lobby.isLobby(line1))
					l = Lobby.addLobby(new Lobby(line1));
				else
					l = Lobby.getLobby(line1);
				
				if(l.getArena() == null)
				{
					Lobby.lobbies.clear();
					event.getPlayer().sendMessage(ICCTF.prefix + "This arena does not exist.");
				}
			}
			else
				event.getPlayer().sendMessage(ICCTF.prefix + "This arena does not exist.");
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
//		if(event.getAction() == Action.RIGHT_CLICK_BLOCK)
//		{
//			if(event.getClickedBlock().getType() == Material.WALL_SIGN)
//			{
//				Sign s = (Sign)event.getClickedBlock();
//				
//				if(s.getLine(0).equals("[CTF]"))
//				{
//				}
//			}
//		}
	}
}
