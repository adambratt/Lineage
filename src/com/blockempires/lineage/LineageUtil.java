package com.blockempires.lineage;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class LineageUtil {
	public static Location getSafeDestination(Location loc)
	{
		World world = loc.getWorld();
		int x = loc.getBlockX();
		int y = loc.getBlockY();
		int z = loc.getBlockZ();
		while (isBlockAboveAir(world, x, y, z))
		{
			y -= 1;
			if (y < 0)
			{
				break;
			}
		}
		while (isBlockUnsafe(world, x, y, z))
		{
			y += 1;
			if (y >= 127)
			{
				x += 1;
				break;
			}
		}
		while (isBlockUnsafe(world, x, y, z))
		{
			y -= 1;
			if (y <= 1)
			{
				y = 127;
				x += 1;
			}
		}
		return new Location(world, x + 0.5D, y, z + 0.5D, loc.getYaw(), loc.getPitch());
	}

	private static boolean isBlockAboveAir(final World world, final int x, final int y, final int z)
	{
		return world.getBlockAt(x, y - 1, z).getType() == Material.AIR;
	}

	public static boolean isBlockUnsafe(final World world, final int x, final int y, final int z)
	{
		final Block below = world.getBlockAt(x, y - 1, z);
		if (below.getType() == Material.LAVA || below.getType() == Material.STATIONARY_LAVA)
		{
			return true;
		}
		if (below.getType() == Material.FIRE)
		{
			return true;
		}
		if ((world.getBlockAt(x, y, z).getType() != Material.AIR)
			|| (world.getBlockAt(x, y + 1, z).getType() != Material.AIR))
		{
			return true;
		}
		return isBlockAboveAir(world, x, y, z);
	}
}
