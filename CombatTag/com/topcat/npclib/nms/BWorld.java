package com.topcat.npclib.nms;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.server.v1_7_R3.WorldServer;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;

/**
 *
 * @author martin
 */
public class BWorld {

	private CraftWorld cWorld;
	private WorldServer wServer;

	public BWorld(World world) {
		try {
			cWorld = (CraftWorld) world;
			wServer = cWorld.getHandle();
		} catch (Exception ex) {
			Logger.getLogger("Minecraft").log(Level.SEVERE, null, ex);
		}
	}

	public WorldServer getWorldServer() {
		return wServer;
	}
}