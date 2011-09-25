package org.martin.bukkit.npclib;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jline.ConsoleReader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.NetworkListenThread;
import net.minecraft.server.PropertyManager;
import net.minecraft.server.ServerConfigurationManager;
import net.minecraft.server.WorldServer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Server hacks for Bukkit
 * @author Kekec852
 */
public class BServer {

    private static BServer ins;
    private MinecraftServer mcServer;
    private CraftServer cServer;
    private Server server;
    private HashMap<String, BWorld> worlds = new HashMap<String, BWorld>();

    private BServer(JavaPlugin plugin) {
        //Getting neede structures
        server = plugin.getServer();
        try {
            cServer = (CraftServer) server;
            mcServer = cServer.getServer();
        } catch (Exception ex) {
            Logger.getLogger("Minecraft").log(Level.SEVERE, null, ex);
        }
        //end
    }

    private BServer(Server server) {
        //Getting neede structures
        this.server = server;
        try {
            cServer = (CraftServer) server;
            mcServer = cServer.getServer();
        } catch (Exception ex) {
            Logger.getLogger("Minecraft").log(Level.SEVERE, null, ex);
        }
        //end
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
        mcServer.a();
    }

    public void sendConsoleCommand(String cmd) {
        if (!mcServer.isStopped && MinecraftServer.isRunning(mcServer)) {
            mcServer.issueCommand(cmd, mcServer);
        }
    }

    public Logger getLogger() {
        return cServer.getLogger();
    }

    public List<WorldServer> getWorldServers() {
        return mcServer.worlds;
    }

    public int getSpawnProtationRadius() {
        return cServer.getSpawnRadius();
    }

    public PropertyManager getPropertyManager() {
        return mcServer.propertyManager;
    }

    public NetworkListenThread getNetworkThread() {
        return mcServer.networkListenThread;
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

    public static BServer getInstance(JavaPlugin pl) {
        if (ins == null) {
            ins = new BServer(pl);
        }
        return ins;
    }

    public static BServer getInstance(Server pl) {
        if (ins == null) {
            ins = new BServer(pl);
        }
        return ins;
    }

    public MinecraftServer getMCServer() {
        return mcServer;
    }
}