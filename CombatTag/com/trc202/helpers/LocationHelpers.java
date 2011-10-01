package com.trc202.helpers;

import org.bukkit.Chunk;
import org.bukkit.Location;

public class LocationHelpers {

	public static boolean locationHasChanged(Location from, Location to) {
		if(from.getBlockX() != to.getBlockX())
		{
			return true;
		}
		else if(from.getBlockY() != to.getBlockY())
		{
			return true;
		}
		else if(from.getBlockZ() != to.getBlockZ())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public static boolean chunkhasChanged(Location from, Location to)
	{
		Chunk fromChunk = from.getWorld().getChunkAt(from);
		Chunk toChunk = to.getWorld().getChunkAt(to);
		if((fromChunk.getX() != toChunk.getX()) || (fromChunk.getZ() != toChunk.getZ()))
		{
			return true;
		}
		return false;
	}
}
