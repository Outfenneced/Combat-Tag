package com.trc202.helpers;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.martin.bukkit.npclib.NPCManager;

public class NPCHelper {
	
	NPCManager npcManager;
	
	public NPCHelper(JavaPlugin plugin){
		npcManager = new NPCManager(plugin);
	}
	public void spawnNpc(String name, Location location){
		npcManager.spawnNPC(name, location);
	}

}
