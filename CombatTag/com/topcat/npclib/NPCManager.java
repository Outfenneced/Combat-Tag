package com.topcat.npclib;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import net.minecraft.server.Entity;
import net.minecraft.server.ItemInWorldManager;
import net.minecraft.server.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.topcat.npclib.entity.HumanNPC;
import com.topcat.npclib.entity.NPC;
import com.topcat.npclib.nms.BServer;
import com.topcat.npclib.nms.BWorld;
import com.topcat.npclib.nms.NPCEntity;
import com.topcat.npclib.nms.NPCNetworkManager;

/**
 *
 * @author martin
 */
public class NPCManager {

	private HashMap<String, NPC> npcs = new HashMap<String, NPC>();
	private BServer server;
	private int taskid;
	private Map<World, BWorld> bworlds = new HashMap<World, BWorld>();
	private NPCNetworkManager npcNetworkManager;
	public static JavaPlugin plugin;

	public NPCManager(JavaPlugin plugin) {
		server = BServer.getInstance();

		npcNetworkManager = new NPCNetworkManager();
		NPCManager.plugin = plugin;
		taskid = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				HashSet<String> toRemove = new HashSet<String>();
				for (String i : npcs.keySet()) {
					Entity j = npcs.get(i).getEntity();
					j.az();
					if (j.dead) {
						toRemove.add(i);
					}
				}
				for (String n : toRemove) {
					npcs.remove(n);
				}
			}
		}, 1L, 1L);
		Bukkit.getServer().getPluginManager().registerEvents(new SL(), plugin);
		Bukkit.getServer().getPluginManager().registerEvents(new WL(), plugin);
	}

	public BWorld getBWorld(World world) {
		BWorld bworld = bworlds.get(world);
		if (bworld != null) {
			return bworld;
		}
		bworld = new BWorld(world);
		bworlds.put(world, bworld);
		return bworld;
	}

	private class SL implements Listener {
		@SuppressWarnings("unused")
		@EventHandler
		public void onPluginDisable(PluginDisableEvent event) {
			if (event.getPlugin() == plugin) {
				despawnAll();
				Bukkit.getServer().getScheduler().cancelTask(taskid);
			}
		}
	}

	private class WL implements Listener {
		@SuppressWarnings("unused")
		@EventHandler
		public void onChunkLoad(ChunkLoadEvent event) {
			for (NPC npc : npcs.values()) {
				if (npc != null && event.getChunk() == npc.getBukkitEntity().getLocation().getBlock().getChunk()) {
					BWorld world = getBWorld(event.getWorld());
					world.getWorldServer().addEntity(npc.getEntity());
				}
			}
		}
	}

	public NPC spawnHumanNPC(String name, Location l) {
		int i = 0;
		String id = name;
		while (npcs.containsKey(id)) {
			id = name + i;
			i++;
		}
		return spawnHumanNPC(name, l, id);
	}

	public NPC spawnHumanNPC(String name, Location l, String id) {
		if (npcs.containsKey(id)) {
			server.getLogger().log(Level.WARNING, "NPC with that id already exists, existing NPC returned");
			return npcs.get(id);
		} else {
			if (name.length() > 16) { // Check and nag if name is too long, spawn NPC anyway with shortened name.
				String tmp = name.substring(0, 16);
				server.getLogger().log(Level.WARNING, "NPCs can't have names longer than 16 characters,");
				server.getLogger().log(Level.WARNING, name + " has been shortened to " + tmp);
				name = tmp;
			}
			BWorld world = getBWorld(l.getWorld());
			NPCEntity npcEntity = new NPCEntity(this, world, name, new ItemInWorldManager(world.getWorldServer()));
			npcEntity.setPositionRotation(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
			world.getWorldServer().addEntity(npcEntity); //the right way
			NPC npc = new HumanNPC(npcEntity);
			npcs.put(id, npc);
			return npc;
		}
	}

	public void despawnById(String id) {
		NPC npc = npcs.get(id);
		if (npc != null) {
			npcs.remove(id);
			npc.removeFromWorld();
		}
	}

	public void despawnHumanByName(String npcName) {
		if (npcName.length() > 16) {
			npcName = npcName.substring(0, 16); //Ensure you can still despawn
		}
		HashSet<String> toRemove = new HashSet<String>();
		for (String n : npcs.keySet()) {
			NPC npc = npcs.get(n);
			if (npc instanceof HumanNPC) {
				if (npc != null && ((HumanNPC) npc).getName().equals(npcName)) {
					toRemove.add(n);
					npc.removeFromWorld();
				}
			}
		}
		for (String n : toRemove) {
			npcs.remove(n);
		}
	}

	public void despawnAll() {
		for (NPC npc : npcs.values()) {
			if (npc != null) {
				npc.removeFromWorld();
			}
		}
		npcs.clear();
	}

	public NPC getNPC(String id) {
		return npcs.get(id);
	}

	public boolean isNPC(org.bukkit.entity.Entity e) {
		return ((CraftEntity) e).getHandle() instanceof NPCEntity;
	}

	public List<NPC> getHumanNPCByName(String name) {
		List<NPC> ret = new ArrayList<NPC>();
		Collection<NPC> i = npcs.values();
		for (NPC e : i) {
			if (e instanceof HumanNPC) {
				if (((HumanNPC) e).getName().equalsIgnoreCase(name)) {
					ret.add(e);
				}
			}
		}
		return ret;
	}

	public List<NPC> getNPCs() {
		return new ArrayList<NPC>(npcs.values());
	}

	public String getNPCIdFromEntity(org.bukkit.entity.Entity e) {
		if (e instanceof HumanEntity) {
			for (String i : npcs.keySet()) {
				if (npcs.get(i).getBukkitEntity().getEntityId() == ((HumanEntity) e).getEntityId()) {
					return i;
				}
			}
		}
		return null;
	}

	public void rename(String id, String name) {
		if (name.length() > 16) { // Check and nag if name is too long, spawn NPC anyway with shortened name.
			String tmp = name.substring(0, 16);
			server.getLogger().log(Level.WARNING, "NPCs can't have names longer than 16 characters,");
			server.getLogger().log(Level.WARNING, name + " has been shortened to " + tmp);
			name = tmp;
		}
		HumanNPC npc = (HumanNPC) getNPC(id);
		npc.setName(name);
		BWorld b = getBWorld(npc.getBukkitEntity().getLocation().getWorld());
		WorldServer s = b.getWorldServer();
		try {
			Method m = s.getClass().getDeclaredMethod("d", new Class[] {Entity.class});
			m.setAccessible(true);
			m.invoke(s, npc.getEntity());
			m = s.getClass().getDeclaredMethod("c", new Class[] {Entity.class});
			m.setAccessible(true);
			m.invoke(s, npc.getEntity());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		s.everyoneSleeping();
	}

	public BServer getServer() {
		return server;
	}

	public NPCNetworkManager getNPCNetworkManager() {
		return npcNetworkManager;
	}

}