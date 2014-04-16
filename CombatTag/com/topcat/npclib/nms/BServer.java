package com.topcat.npclib.nms;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.server.v1_7_R3.MinecraftServer;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_7_R3.CraftServer;

/**
 * Server hacks for Bukkit
 *
 * @author Kekec852
 */
public class BServer {

	private static BServer ins;
	private MinecraftServer mcServer;
	private CraftServer cServer;
	private Server server;

	private BServer() {
		server = Bukkit.getServer();
		try {
			cServer = (CraftServer) server;
			mcServer = cServer.getServer();
		} catch (Exception ex) {
			Logger.getLogger("Minecraft").log(Level.SEVERE, null, ex);
		}
	}

	public Logger getLogger() {
		return cServer.getLogger();
	}
/*
	public List<WorldServer> getWorldServers() {
		return mcServer.worlds;
	}

	public Server getServer() {
		return server;
	}
*/
	public static BServer getInstance() {
		if (ins == null) {
			ins = new BServer();
		}
		return ins;
	}

	public MinecraftServer getMCServer() {
		return mcServer;
	}
	
}