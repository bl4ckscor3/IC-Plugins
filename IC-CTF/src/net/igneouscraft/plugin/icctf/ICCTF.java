package net.igneouscraft.plugin.icctf;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.igneouscraft.plugin.icctf.arenamode.AMS;
import net.igneouscraft.plugin.icctf.arenamode.ArenaMode;

public class ICCTF extends JavaPlugin
{
	public static final String prefix = ChatColor.DARK_PURPLE + "==" + ChatColor.AQUA + "IC-CTF" + ChatColor.DARK_PURPLE + "== " + ChatColor.WHITE;
	private static ICCTF instance;

	@Override
	public void onEnable()
	{
		instance = this;
		getServer().getPluginManager().registerEvents(new ArenaMode(), this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		try
		{
			if(!(sender instanceof Player))
				return true;

			Player p = (Player)sender;

			if(cmd.getName().equals("ctf"))
			{
				if(p.hasPermission("ctf.use"))
				{
					if(args.length == 1)
					{
						if(args[0].equals("continue"))
						{
							if(ArenaMode.active(p))
							{
								if(ArenaMode.getState(p) == AMS.BLUESPAWNPOINTS)
								{
									if(ArenaMode.blueSpawns(p) > 0)
									{
										ArenaMode.setState(p, AMS.REDFLAG1);
										p.sendMessage(ICCTF.prefix + "Please select the first corner of the red team's flag.");
									}
									else
										p.sendMessage(prefix + "You need to set at least one spawn point.");
								}
								else if(ArenaMode.getState(p) == AMS.REDSPAWNPOINTS)
								{
									if(ArenaMode.redSpawns(p) > 0)
									{
										ArenaMode.deactivateFor(p);
										p.sendMessage(prefix + "You are done setting up the arena.");
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
					}
					else if(args.length == 2)
					{
						if(args[0].equals("add"))
						{
							if(!ArenaMode.active(p))
							{
								ArenaMode.activateFor(p, args[1]);
								p.sendMessage(prefix + "You are now in Arena Mode. Please select the first corner of the arena (leftclick a block, similar to a World-Edit selection).");
							}
						}
						else if(args[0].equals("remove"))
						{
							
						}
						else
						{
							//TODO: Help
						}
					}
					else
					{
						//TODO: Help
					}
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

	/**
	 * @return The instance of this plugin
	 */
	public static ICCTF i()
	{
		return instance;
	}
}
