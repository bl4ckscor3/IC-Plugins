package net.igneouscraft.plugin.icctf;

import java.io.File;

/**
 * Utility methods
 * @author bl4ckscor3
 */
public class Util
{
	/**
	 * Checks if an arena exists
	 * @param name The name of the arena to check
	 * @return
	 */
	public static boolean isArena(String name)
	{
		File folder = new File(ICCTF.i().getDataFolder(), "arenas");
		
		if(!folder.exists())
			folder.mkdirs();
		
		for(File f : folder.listFiles())
		{
			if(f.getName().split(".yml")[0].equals(name))
				return true;
		}
		
		return false;
	}
}
