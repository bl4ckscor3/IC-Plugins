package net.igneouscraft.plugin.icctf;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.igneouscraft.plugin.icctf.arena.Arena;
import net.igneouscraft.plugin.icctf.arena.Lobby;
import net.igneouscraft.plugin.icctf.arena.mode.AMS;
import net.igneouscraft.plugin.icctf.arena.mode.ArenaMode;
import net.igneouscraft.plugin.icctf.listener.SignListener;

/**
 * Main class for this plugin
 * @author bl4ckscor3
 */
public class ICCTF extends JavaPlugin
{
	public static final String prefix = ChatColor.DARK_PURPLE + "==" + ChatColor.AQUA + "IC-CTF" + ChatColor.DARK_PURPLE + "== " + ChatColor.WHITE;
	private static ICCTF instance;

	@Override
	public void onEnable()
	{
		instance = this;
		getServer().getPluginManager().registerEvents(new ArenaMode(), this);
		getServer().getPluginManager().registerEvents(new SignListener(), this);
		reloadConfig();
		getConfig().addDefault("minimumPlayers", 10);
		getConfig().addDefault("maximumPlayers", 20);
		getConfig().options().copyDefaults(true);
		saveConfig();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		try
		{
			if(cmd.getName().equals("ctf"))
			{
				if(!(sender instanceof Player))
				{
					sender.sendMessage(prefix + "You can only use this plugin as a player.");
					return true;
				}

				Player p = (Player)sender;
				
				if(p.hasPermission("ctf.use"))
				{
					if(args.length == 1) //only one argument
					{
						if(args[0].equals("continue"))
							cmdContinue(p);
						else if(args[0].equals("leave"))
							cmdLeave(p);
						else
							sendHelp(p);
					}
					else if(args.length == 2) //two arguments
					{
						if(args[0].equals("remove"))
							cmdRemove(p, args);
						else
							sendHelp(p);
					}
					else if(args.length == 3) //three arguments
					{
						if(args[0].equals("add"))
							cmdAdd(p, args);
						else
							sendHelp(p);
					}
					else
						sendHelp(p);
				}
				else
					p.sendMessage(prefix + "You do not have permission to execute this command.");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return true;
	}

	/*---------------FOLLOWING METHODS ARE JUST TO MAKE THE COMMAND CODE MORE CLEAR---------------*/
	
	/**
	 * Defines what happens when executing /ctf continue
	 * @param p The player who executed the command
	 */
	private void cmdContinue(Player p)
	{
		if(ArenaMode.active(p))
		{
			if(ArenaMode.getState(p) == AMS.BLUESPAWNPOINTS)
			{
				if(ArenaMode.blueSpawns(p) > 0)
				{
					ArenaMode.setState(p, AMS.REDFLAG1);
					p.sendMessage(prefix + "Please select the first corner of the red team's flag.");
				}
				else
					p.sendMessage(prefix + "You need to set at least one spawn point.");
			}
			else if(ArenaMode.getState(p) == AMS.REDSPAWNPOINTS)
			{
				if(ArenaMode.redSpawns(p) > 0)
				{
					ArenaMode.setState(p, AMS.LOBBY1);
					p.sendMessage(prefix + "Please select the first lobby corner.");
				}
				else
					p.sendMessage(prefix + "You need to set at least one spawn point.");
			}
			else
				p.sendMessage(prefix + ChatColor.AQUA + "/ctf continue" + ChatColor.AQUA + " is not allowed for this state.");
		}
		else
			p.sendMessage(prefix + "You are not in arena mode.");
	}

	/**
	 * Defines what happens when executing /ctf remove
	 * @param p The player who executed the command
	 * @param args The arguments that got sent with the command
	 */
	private void cmdRemove(Player p, String[] args)
	{
		File folder = new File(getDataFolder(), "arenas");

		if(!folder.exists())
			folder.mkdirs();

		for(File f : folder.listFiles())
		{
			if(f.getName().split(".yml")[0].equals(args[1]))
			{
				f.delete();
				p.sendMessage(prefix + "Arena removed.");
				return;
			}
		}

		p.sendMessage(prefix + "There is no arena with this name.");
	}
	
	/**
	 * Defines what happens when executing /ctf add
	 * @param p The player who executed the command
	 * @param args The arguments that got sent with the command
	 */
	private void cmdAdd(Player p, String[] args) throws IOException
	{
		if(!ArenaMode.active(p))
		{
			if(!Arena.isArena(args[1]))
				ArenaMode.activateFor(p, args[1], args[2]);
			else
				p.sendMessage(prefix + "This arena already exists.");
		}
		else
			p.sendMessage(prefix + "You are already in arena mode.");
	}
	
	/**
	 * Defines what happens when executing /ctf leave
	 * @param p The player who executed the command
	 */
	private void cmdLeave(Player p)
	{
		if(Lobby.isInLobby(p))
			Lobby.getLobby(p).removePlayer(p);
		else
			p.sendMessage(prefix + "You are not in a lobby.");
	}
	
	/**
	 * Sends the help menu to a CommandSender
	 * @param sender The instance to send the message to
	 */
	private void sendHelp(CommandSender sender)
	{
		sender.sendMessage(prefix + "-----------Help for IC-CTF-----------");
		sender.sendMessage(prefix + ChatColor.RED + "/ctf add <name>" + ChatColor.WHITE + " - Sets you into arena mode so you can create a new arena with the given name.");
		sender.sendMessage(prefix + ChatColor.RED + "/ctf remove <name>" + ChatColor.WHITE + " - Removes the arena with the given name.");
		sender.sendMessage(prefix + ChatColor.RED + "/ctf continue" + ChatColor.WHITE + " - Allows you to continue setting up the arena after you are done setting up the spawnpoints.");
		sender.sendMessage(prefix + "-----------------------------------");
	}
	
	/**
	 * @return The instance of this plugin
	 */
	public static ICCTF i()
	{
		return instance;
	}
}
