package bl4ckscor3.plugin.icrb;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class ICRainbowBeacons extends JavaPlugin implements Listener
{
	private static final String prefix = ChatColor.GREEN + "[" + ChatColor.GOLD + "RainbowBeacons" + ChatColor.GREEN + "] " + ChatColor.WHITE;
	private static final HashMap<Integer,Block> activeBeacons = new HashMap<Integer,Block>(); //beacon, task id
	private static final HashMap<Player,String> waiting = new HashMap<Player,String>(); //player, adding or removing a beacon
	private static boolean running = false;
	private static ICRainbowBeacons instance;

	@Override
	public void onEnable()
	{
		instance = this;
		getServer().getPluginManager().registerEvents(this, this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		try
		{
			if(!(sender instanceof Player))
			{
				sender.sendMessage(prefix + "You can only use this plugin ingame.");
				return true;
			}

			Player p = (Player)sender;

			if(cmd.getName().equals("rb"))
			{
				if(args.length == 1)
				{
					switch(args[0])
					{
						case "add":
							waiting.put(p, "add");
							p.sendMessage(prefix + "Please rightclick the beacon you want to add.");
							break;
						case "remove":
							waiting.put(p, "remove");
							p.sendMessage(prefix + "Please rightclick the beacon you want to remove.");
							break;
						case "abort":
							waiting.remove(p);
							p.sendMessage(prefix + "You are no longer modifying the beacons.");
							break;
						case "start":
							if(!running)
							{
								File folder = new File(getDataFolder(), "storage");
								File f = new File(getDataFolder(), "storage/beacons.txt");
								
								if(!folder.exists())
									folder.mkdirs();

								if(!f.exists())
									f.createNewFile();
								
								List<String> content = FileUtils.readLines(f);

								if(content.size() == 0)
								{
									p.sendMessage(prefix + "There are no rainbow beacons.");
									return true;
								}
								
								for(String s : content)
								{
									Location l = getSavedAsLocation(s);
									BeaconRunnable r = new BeaconRunnable(l.getWorld().getBlockAt(l));

									r.runTaskTimer(this, 0, 1);
									activeBeacons.put(r.getTaskId(), r.b);
								}

								running = true;
								p.sendMessage(prefix + "The rainbow beacons have been started.");
							}
							else
								p.sendMessage(prefix + "The rainbow beacons are already running.");
							
							break;
						case "stop":
							if(running)
							{
								for(int i : activeBeacons.keySet())
								{
									getServer().getScheduler().cancelTask(i);
									setBlockAbove(activeBeacons.get(i), 1, 0);
									setBlockAbove(activeBeacons.get(i), 2, 0);
									setBlockAbove(activeBeacons.get(i), 3, 0);
								}

								running = false;
								p.sendMessage(prefix + "The rainbow beacons have been stopped.");
							}
							else
								p.sendMessage(prefix + "The rainbow beacons aren't running.");
							
							break;
						case "restart":
							if(running)
							{
								for(int i : activeBeacons.keySet())
								{
									getServer().getScheduler().cancelTask(i);
									setBlockAbove(activeBeacons.get(i), 1, 0);
									setBlockAbove(activeBeacons.get(i), 2, 0);
									setBlockAbove(activeBeacons.get(i), 3, 0);
								}

								File folder = new File(getDataFolder(), "storage");
								File f = new File(getDataFolder(), "storage/beacons.txt");
								
								if(!folder.exists())
									folder.mkdirs();

								if(!f.exists())
									f.createNewFile();

								List<String> content = FileUtils.readLines(f);

								for(String s : content)
								{
									Location l = getSavedAsLocation(s);
									BeaconRunnable r = new BeaconRunnable(l.getWorld().getBlockAt(l));

									r.runTaskTimer(this, 0, 1);
									activeBeacons.put(r.getTaskId(), r.b);
								}

								p.sendMessage(prefix + "The rainbow beacons have been restarted.");
							}
							else
								p.sendMessage(prefix + "The rainbow beacons aren't running.");
							
							break;
						default:
							p.sendMessage(prefix + "Correct usage: /rb <add|abort|remove|start|stop|restart>");
					}
				}
				else
					p.sendMessage(prefix + "Correct usage: /rb <add|abort|remove|start|stop|restart>");
			}
		}
		catch(Exception e){}

		return true;
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		try
		{
			if(event.getAction() == Action.RIGHT_CLICK_BLOCK)
			{
				if(waiting.containsKey(event.getPlayer()))
				{
					Block b = event.getClickedBlock();
					Player p = event.getPlayer();

					if(b.getType() == Material.BEACON)
					{
						File folder = new File(getDataFolder(), "storage");
						File f = new File(getDataFolder(), "storage/beacons.txt");
						
						if(!folder.exists())
							folder.mkdirs();

						if(!f.exists())
							f.createNewFile();

						List<String> content = FileUtils.readLines(f);
						String loc = getLocationAsSaved(b.getLocation());

						switch(waiting.get(p))
						{
							case "add":
								if(content.contains(loc))
								{
									p.sendMessage(prefix + "This beacon is already a rainbow beacon.");
									event.setCancelled(true);
									return;
								}

								content.add(loc);
								FileUtils.writeLines(f, content);
								p.sendMessage(prefix + "Beacon added. Use " + ChatColor.AQUA + "/rb " + (running ? "restart" : "start") + ChatColor.WHITE + " to activate it.");
								break;
							case "remove":
								if(!content.contains(loc))
								{
									p.sendMessage(prefix + "This beacon isn't a rainbow beacon.");
									event.setCancelled(true);
									return;
								}

								content.remove(loc);
								FileUtils.writeLines(f, content);
								p.sendMessage(prefix + "Beacon removed." + (running ? " Use " + ChatColor.AQUA + "/rb restart" + ChatColor.WHITE + " to deactivate it." : ""));
								break;
						}

						waiting.remove(p);
					}
					else
						p.sendMessage(prefix + "This is not a beacon.");

					event.setCancelled(true);
				}
			}
		}
		catch(Exception e){}
	}

	//starting the beacons once the first player joins
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) throws IOException, InterruptedException
	{
		if(getServer().getOnlinePlayers().size() == 1 && !running)
		{
			File folder = new File(getDataFolder(), "storage");
			File f = new File(getDataFolder(), "storage/beacons.txt");
			
			if(!folder.exists())
				folder.mkdirs();

			if(!f.exists())
				f.createNewFile();
			
			List<String> content = FileUtils.readLines(f);

			if(content.size() == 0)
				return;
			
			for(String s : content)
			{
				Location l = getSavedAsLocation(s);
				BeaconRunnable r = new BeaconRunnable(l.getWorld().getBlockAt(l));

				r.runTaskTimer(this, 0, 5);
				activeBeacons.put(r.getTaskId(), r.b);
			}

			running = true;
		}
	}
	
	//stopping the beacons once the last player leaves
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) throws IOException
	{
		if(getServer().getOnlinePlayers().size() == 0 && running)
		{
			for(int i : activeBeacons.keySet())
			{
				getServer().getScheduler().cancelTask(i);
				activeBeacons.remove(i);
				setBlockAbove(activeBeacons.get(i), 1, 0);
				setBlockAbove(activeBeacons.get(i), 2, 0);
				setBlockAbove(activeBeacons.get(i), 3, 0);
			}

			running = false;
		}
	}
	
	public class BeaconRunnable extends BukkitRunnable implements BukkitTask
	{
		private Block b;
		private int state = 0;

		public BeaconRunnable(Block block)
		{
			b = block;
		}

		@Override
		public void run()
		{
			switch(state)
			{
				case 0:
					setBlockAbove(b, 1, 14);
					setBlockAbove(b, 2, 0);
					setBlockAbove(b, 3, 0);
					break;
				case 1:
					setBlockAbove(b, 1, 14);
					setBlockAbove(b, 2, 1);
					setBlockAbove(b, 3, 14);
					break;
				case 2:
					setBlockAbove(b, 1, 14);
					setBlockAbove(b, 2, 1);
					setBlockAbove(b, 3, 0);
					break;
				case 3:
					setBlockAbove(b, 1, 1);
					setBlockAbove(b, 2, 14);
					setBlockAbove(b, 3, 1);
					break;
				case 4:
					setBlockAbove(b, 1, 1);
					setBlockAbove(b, 2, 0);
					setBlockAbove(b, 3, 0);
					break;
				case 5:
					setBlockAbove(b, 1, 1);
					setBlockAbove(b, 2, 4);
					setBlockAbove(b, 3, 1);
					break;
				case 6:
					setBlockAbove(b, 1, 1);
					setBlockAbove(b, 2, 4);
					setBlockAbove(b, 3, 0);
					break;
				case 7:
					setBlockAbove(b, 1, 4);
					setBlockAbove(b, 2, 1);
					setBlockAbove(b, 3, 4);
					break;
				case 8:
					setBlockAbove(b, 1, 4);
					setBlockAbove(b, 2, 0);
					setBlockAbove(b, 3, 0);
					break;
				case 9:
					setBlockAbove(b, 1, 4);
					setBlockAbove(b, 2, 13);
					setBlockAbove(b, 3, 4);
					break;
				case 10:
					setBlockAbove(b, 1, 4);
					setBlockAbove(b, 2, 13);
					setBlockAbove(b, 3, 0);
					break;
				case 11:
					setBlockAbove(b, 1, 13);
					setBlockAbove(b, 2, 4);
					setBlockAbove(b, 3, 13);
					break;
				case 12:
					setBlockAbove(b, 1, 13);
					setBlockAbove(b, 2, 0);
					setBlockAbove(b, 3, 0);
					break;
				case 13:
					setBlockAbove(b, 1, 13);
					setBlockAbove(b, 2, 11);
					setBlockAbove(b, 3, 13);
					break;
				case 14:
					setBlockAbove(b, 1, 13);
					setBlockAbove(b, 2, 11);
					setBlockAbove(b, 3, 0);
					break;
				case 15:
					setBlockAbove(b, 1, 11);
					setBlockAbove(b, 2, 13);
					setBlockAbove(b, 3, 11);
					break;
				case 16:
					setBlockAbove(b, 1, 11);
					setBlockAbove(b, 2, 0);
					setBlockAbove(b, 3, 0);
					break;
				case 17:
					setBlockAbove(b, 1, 11);
					setBlockAbove(b, 2, 10);
					setBlockAbove(b, 3, 11);
					break;
				case 18:
					setBlockAbove(b, 1, 11);
					setBlockAbove(b, 2, 10);
					setBlockAbove(b, 3, 0);
					break;
				case 19:
					setBlockAbove(b, 1, 10);
					setBlockAbove(b, 2, 11);
					setBlockAbove(b, 3, 10);
					break;
				case 20:
					setBlockAbove(b, 1, 10);
					setBlockAbove(b, 2, 0);
					setBlockAbove(b, 3, 0);
					break;
				case 21:
					setBlockAbove(b, 1, 10);
					setBlockAbove(b, 2, 14);
					setBlockAbove(b, 3, 10);
					break;
				case 22:
					setBlockAbove(b, 1, 10);
					setBlockAbove(b, 2, 14);
					setBlockAbove(b, 3, 0);
					break;
				case 23:
					setBlockAbove(b, 1, 14);
					setBlockAbove(b, 2, 10);
					setBlockAbove(b, 3, 14);
					break;
			}

			if(state == 23)
				state = 0;
			else
				state++;
		}

		@Override
		public Plugin getOwner()
		{
			return instance;
		}

		@Override
		public boolean isSync()
		{
			return false;
		}
	}

	/**
	 * Sets a block above the beacon
	 * @param block The block to set the blocks relative from
	 * @param above How many blocks above to set the block at
	 * @param data Which glass metadata to set
	 */
	@SuppressWarnings("deprecation")
	private void setBlockAbove(Block b, int above, int data)
	{
		if(data == 0)
		{
			b.getRelative(0, above, 0).setType(Material.AIR);
			return;
		}

		b.getRelative(0, above, 0).setType(Material.STAINED_GLASS);
		b.getRelative(0, above, 0).setData((byte)data);
	}

	/**
	 * Returns a formatted location to save in a file and check for
	 * @param l The location to format
	 * @return The formatted location
	 */
	private String getLocationAsSaved(Location l)
	{
		return String.format("%s,%s,%s,%s", l.getWorld().getName(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
	}

	/**
	 * Returns a location from a formatted location
	 * @param l The formatted location
	 * @return The new location
	 */
	private Location getSavedAsLocation(String s)
	{
		String[] split = s.split(",");

		return new Location(getServer().getWorld(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
	}
}
