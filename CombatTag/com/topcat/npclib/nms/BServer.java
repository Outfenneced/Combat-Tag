package com.topcat.npclib.nms;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.server.v1_4_5.DedicatedServer;
import net.minecraft.server.v1_4_5.MinecraftServer;
import net.minecraft.server.v1_4_5.PropertyManager;
import net.minecraft.server.v1_4_5.ServerConfigurationManager;
import net.minecraft.server.v1_4_5.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_4_5.CraftServer;
import org.bukkit.craftbukkit.libs.jline.console.ConsoleReader;

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
	private HashMap<String, BWorld> worlds = new HashMap<String, BWorld>();

	private BServer() {
		server = Bukkit.getServer();
		try {
			cServer = (CraftServer) server;
			mcServer = cServer.getServer();
		} catch (Exception ex) {
			Logger.getLogger("Minecraft").log(Level.SEVERE, null, ex);
		}
	}

	public void disablePlugins() {
		cServer.disablePlugins();
	}

	public void dispatchCommand(CommandSender sender, String msg) {
		cServer.dispatchCommand(sender, msg);
	}

	public ServerConfigurationManager getHandle() {
		return cServer.getHandle();
	}

	public ConsoleReader getReader() {
		return cServer.getReader();
	}

	public void loadPlugins() {
		cServer.loadPlugins();
	}

	public void stop() {
		mcServer.safeShutdown();
	}

	public void sendConsoleCommand(String cmd) {
		if (mcServer.isRunning()) {
			((DedicatedServer) mcServer).issueCommand(cmd, mcServer);
		}
	}

	public Logger getLogger() {
		return cServer.getLogger();
	}

	public List<WorldServer> getWorldServers() {
		return mcServer.worlds;
	}

	public int getSpawnProtationRadius() {
		return mcServer.server.getSpawnRadius();
	}

	public PropertyManager getPropertyManager() {
		return mcServer.getPropertyManager();
	}

	public Server getServer() {
		return server;
	}

	public BWorld getWorld(String worldName) {
		if (worlds.containsKey(worldName)) {
			return worlds.get(worldName);
		}
		BWorld w = new BWorld(this, worldName);
		worlds.put(worldName, w);
		return w;
	}

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