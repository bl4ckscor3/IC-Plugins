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
	private static final int tickSpeed = 2; //how fast the beacon's color changes
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
							sendMessage(p, "Please rightclick the beacon you want to add.");
							break;
						case "remove":
							waiting.put(p, "remove");
							sendMessage(p, "Please rightclick the beacon you want to remove.");
							break;
						case "abort":
							waiting.remove(p);
							sendMessage(p, "You are no longer modifying the beacons.");
							break;
						case "start":
							if(!running)
							{
								List<String> content = getContent();

								if(content.size() == 0)
								{
									sendMessage(p, "There are no rainbow beacons.");
									return true;
								}

								for(String s : content)
								{
									Location l = getSavedAsLocation(s);
									Block b = l.getWorld().getBlockAt(l);

									if(!isBeacon(b, p))
										continue;

									start(b);
								}

								running = true;
								sendMessage(p, "The rainbow beacons have been started.");
							}
							else
								sendMessage(p, "The rainbow beacons are already running.");

							break;
						case "stop":
							if(running)
							{
								stopBeacons();
								running = false;
								sendMessage(p, "The rainbow beacons have been stopped.");
							}
							else
								sendMessage(p, "The rainbow beacons aren't running.");

							break;
						case "restart":
							if(running)
							{
								stopBeacons();
								
								List<String> content = getContent();

								for(String s : content)
								{
									Location l = getSavedAsLocation(s);
									Block b = l.getWorld().getBlockAt(l);

									if(!isBeacon(b, p))
										continue;

									start(b);
								}

								sendMessage(p, "The rainbow beacons have been restarted.");
							}
							else
								sendMessage(p, "The rainbow beacons aren't running.");

							break;
						default:
							sendMessage(p, "Correct usage: /rb <add|abort|remove|start|stop|restart>");
					}
				}
				else
					sendMessage(p, "Correct usage: /rb <add|abort|remove|start|stop|restart>");
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
						List<String> content = getContent();
						String loc = getLocationAsSaved(b.getLocation());

						switch(waiting.get(p))
						{
							case "add":
								if(content.contains(loc))
								{
									sendMessage(p, "This beacon is already a rainbow beacon.");
									event.setCancelled(true);
									return;
								}

								content.add(loc);
								FileUtils.writeLines(getContentFile(), content);
								sendMessage(p, "Beacon added. Use " + ChatColor.AQUA + "/rb " + (running ? "restart" : "start") + ChatColor.WHITE + " to activate it.");
								break;
							case "remove":
								if(!content.contains(loc))
								{
									sendMessage(p, "This beacon isn't a rainbow beacon.");
									event.setCancelled(true);
									return;
								}

								content.remove(loc);
								FileUtils.writeLines(getContentFile(), content);
								sendMessage(p, "Beacon removed." + (running ? " Use " + ChatColor.AQUA + "/rb restart" + ChatColor.WHITE + " to deactivate it." : ""));
								break;
						}

						waiting.remove(p);
					}
					else
						sendMessage(p, "This is not a beacon.");

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
			List<String> content = getContent();

			if(content.size() == 0)
				return;

			for(String s : content)
			{
				Location l = getSavedAsLocation(s);
				Block b = l.getWorld().getBlockAt(l);

				if(b.getType() != Material.BEACON)
					continue;

				start(b);
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
			stopBeacons();
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

	/**
	 * Starts a beacon
	 * @param b The beacon to start. Should be checked if it's a beacon before calling this method
	 */
	private void start(Block b)
	{
		BeaconRunnable r = new BeaconRunnable(b);

		r.runTaskTimer(this, 0, tickSpeed);
		activeBeacons.put(r.getTaskId(), r.b);
	}

	/**
	 * Stops all beacons
	 */
	private void stopBeacons()
	{
		for(int i : activeBeacons.keySet())
		{
			getServer().getScheduler().cancelTask(i);
			setBlockAbove(activeBeacons.get(i), 1, 0);
			setBlockAbove(activeBeacons.get(i), 2, 0);
			setBlockAbove(activeBeacons.get(i), 3, 0);
			activeBeacons.remove(i);
		}
	}

	/**
	 * Checks if the given block is a beacon and sends a message if not
	 * @param b The block to check
	 * @param p The player to send the message to
	 * @return true if it's a beacon, false if not
	 */
	private boolean isBeacon(Block b, Player p)
	{
		if(b.getType() != Material.BEACON)
		{
			String[] split = getLocationAsSaved(b.getLocation()).split(",");

			sendMessage(p, "The beacon at" + ChatColor.AQUA + " X:" + split[1] + " Y:" + split[2] + " Z:" + split[3] + ChatColor.WHITE + " does not exist anymore.");
			return false;
		}

		return true;
	}

	/**
	 * Gets the content of the beacons.txt file
	 * @return The content
	 */
	private List<String> getContent() throws IOException
	{
		File folder = new File(getDataFolder(), "storage");
		File f = getContentFile();

		if(!folder.exists())
			folder.mkdirs();

		if(!f.exists())
			f.createNewFile();

		return FileUtils.readLines(f);
	}

	/**
	 * Gets the beacons.txt file
	 */
	private File getContentFile()
	{
		return new File(getDataFolder(), "storage/beacons.txt");
	}
	
	/**
	 * Sends a prefixed message to the given player
	 * @param p The player to send the message to
	 * @param msg
	 */
	private void sendMessage(Player p, String msg)
	{
		p.sendMessage(prefix + msg);
	}
}
