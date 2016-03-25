package bl4ckscor3.plugin.icrb;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class ICRainbowBeacons extends JavaPlugin implements Listener
{
	private static final HashMap<Location,Integer> activeBeacons = new HashMap<Location,Integer>(); //location of the block, task id
	private static ICRainbowBeacons instance;
	
	@Override
	public void onEnable()
	{
		instance = this;
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	public void onBlockRedstone(BlockRedstoneEvent event)
	{
		if(event.getBlock().getBlockPower() > 0)
		{
			BeaconRunnable r = new BeaconRunnable(event.getBlock());
			
			r.runTaskTimer(this, 0, 5);
			activeBeacons.put(event.getBlock().getLocation(), r.getTaskId());
		}
		else
			getServer().getScheduler().cancelTask(activeBeacons.remove(event.getBlock().getLocation()));
	}
	
	public class BeaconRunnable extends BukkitRunnable implements BukkitTask
	{
		private Block b;
		
		public BeaconRunnable(Block block)
		{
			b = block;
		}
		
		@Override
		public void run()
		{
			
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
}
