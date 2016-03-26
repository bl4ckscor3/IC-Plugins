package bl4ckscor3.plugin.icrb;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
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

	@EventHandler
	public void onBlockRedstone(BlockRedstoneEvent event)
	{
		Block b = event.getBlock();
		Block beacon;
		
		if((beacon = b.getRelative(0, 2, 0)).getType() == Material.BEACON)
		{
			if(event.getNewCurrent() > 0)
			{
				BeaconRunnable r = new BeaconRunnable(beacon);

				r.runTaskTimer(this, 0, 1);
				activeBeacons.put(beacon.getLocation(), r.getTaskId());
			}
			else if(event.getOldCurrent() > 0)
			{
				getServer().getScheduler().cancelTask(activeBeacons.remove(beacon.getLocation()));
				setBlockAbove(beacon, 1, 0);
				setBlockAbove(beacon, 2, 0);
				setBlockAbove(beacon, 3, 0);
			}
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
}
