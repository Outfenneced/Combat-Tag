package com.trc202.NoPvpLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import org.martin.bukkit.npclib.NPCEntity;
import org.martin.bukkit.npclib.NPCManager;

public class NoPvpLog extends JavaPlugin {
	private Configuration settings;
	public final Logger log = Logger.getLogger("Minecraft");
	public NPCManager npcm;
	private HashMap<String,PlayerDataContainer> playerData;
	private int tagDuration;
	private String mainDirectory;
	private File dataContainerFile;
	private	File settingsFile;
	
	private final nopvpPlayerListener plrListener = new nopvpPlayerListener(this); 
	private final NoPvpEntityListener entityListener = new NoPvpEntityListener(this);

	public NoPvpLog()
	{
		mainDirectory = "plugins/CombatTag";
		tagDuration = 10;
		dataContainerFile = new File(mainDirectory + File.separator + "PlayerDataContainerFile.ser");
		new File(mainDirectory).mkdirs();
		settingsFile =new File(mainDirectory + File.separator + "settings.yml");
		settings = new Configuration(settingsFile);
	}
	
	@Override
	public void onDisable() {
		for(PlayerDataContainer pdc : playerData.values())
		{
			if(pdc.hasNPC())
			{
				despawnNPC(pdc);
			}
		}
		try {
			if(!dataContainerFile.exists())
			{
				dataContainerFile.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(dataContainerFile);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(playerData);
			oos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			log.warning("Combat Tag has encountered an error");
			e.printStackTrace();
		}
 catch (IOException e) {
	 		log.warning("Combat Tag has encountered an error");
			e.printStackTrace();
		}
	}

	@Override
	public void onEnable() {
		loadFiles();
		npcm = new NPCManager(this);
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_JOIN, plrListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, plrListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Event.Priority.Monitor, this);
		pm.registerEvent(Event.Type.ENTITY_DEATH, entityListener, Event.Priority.Monitor, this);
		log.info("["+ getDescription().getName() +"]"+ " has loaded with a tag time of " + tagDuration + " seconds");
	}

	@SuppressWarnings("unchecked")
	private void loadFiles() {
		if (!settingsFile.exists())
		{	
			settings.setProperty("Tag Duration", tagDuration);
			settings.save();
		}
		else
		{
			settings.load();
			tagDuration = (Integer) settings.getProperty("Tag Duration");
		}
		if(!dataContainerFile.exists())
		{
			playerData = new HashMap<String, PlayerDataContainer>();
		}
		else
		{
			try {
				FileInputStream fis = new FileInputStream(dataContainerFile);
				ObjectInputStream ois = new ObjectInputStream(fis);
				Object obj;

				obj = ois.readObject();
				if(obj instanceof HashMap<?,?>)
				{
					playerData = (HashMap<String,PlayerDataContainer>)obj;
				}
			} catch (IOException e) {
				log.warning("[" + getDescription().getName()+ "]"+ " has encountered an error.");
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				log.warning("[" + getDescription().getName()+ "]"+ " has encountered an error.");
				e.printStackTrace();
			}
		}
		
	}

	
	/**
	 * Spawns npc at players current location
	 * @param plr
	 */
	public void spawnPlayerNpc(Player plr) 
	{
		PlayerDataContainer plrData = getPlayerData(plr.getName());
		NPCEntity spawnedNPC = npcm.spawnNPC(plr.getName(), plr.getLocation(), plr.getName());
		spawnedNPC.health = plr.getHealth();
		copyContentsNpc(spawnedNPC, plr);
		plrData.setNPC(true);
		plrData.setNPCId(plr.getName());
	}
	
	/**
	 * Despawns npc and copys all contents from npc to player data
	 * @param plrData 
	 */
	public void despawnNPC(PlayerDataContainer plrData) {
		NPCEntity npc = npcm.getNPC(plrData.getNPCId());
		plrData.setPlayerArmor(npc.getInventory().getArmorContents());
		plrData.setPlayerInventory(npc.getInventory().getContents());
		plrData.setHealth(npc.health);
		npcm.despawn(plrData.getNPCId());
		plrData.setNPCId("");
		plrData.setNPC(false);
	}

	/**
	 * Copys inventory from the Player to the NPC
	 * @param npc Npc
	 * @param plr Player
	 */
	public void copyContentsNpc(NPCEntity npc, Player plr) {
		PlayerInventory npcInv = npc.getInventory();
		PlayerInventory plrInv = plr.getInventory();
		npcInv.setArmorContents(plrInv.getArmorContents());
		npcInv.setContents(plrInv.getContents());
	}
	/**
	 * Copys inventory from the NPC to the player
	 * @param npc
	 * @param plr
	 */
	public void copyContentsPlayer(NPCEntity npc, Player plr) {
		PlayerInventory npcInv = npc.getInventory();
		PlayerInventory plrInv = plr.getInventory();
		plrInv.setArmorContents(npcInv.getArmorContents());
		plrInv.setContents(npcInv.getContents());
	}

	public boolean hasDataContainer(String playerName)
	{
		return playerData.containsKey(playerName);
	}
	public PlayerDataContainer getPlayerData(String playerName)
	{
		return playerData.get(playerName);
	}
	public PlayerDataContainer createPlayerData(String playerName)
	{
		PlayerDataContainer plr = new PlayerDataContainer(playerName);
		playerData.put(playerName, plr);
		return plr;
	}


	/**
	 * 
	 * @return the system tag duration as set by the user
	 */
	public int getTagDuration()
	{
		return tagDuration;
	}

	
	/**
	 * Kills player and sets their inventory to an empty stack
	 * @param deadPlayerData
	 */
	public void killPlayerEmptyInventory(PlayerDataContainer deadPlayerData) {
		ItemStack airItem = new ItemStack(Material.AIR);
		ItemStack[] emptyStack = new ItemStack[36];
		for(int x = 0; x < emptyStack.length; x++)
		{
			emptyStack[x] = airItem;
		}
		ItemStack[] emptyArmorStack = new ItemStack[4];
		for(int x = 0; x < emptyArmorStack.length; x++)
		{
			emptyArmorStack[x] = airItem;
		}
		deadPlayerData.setPlayerArmor(emptyArmorStack);
		deadPlayerData.setPlayerInventory(emptyStack);
		deadPlayerData.setHealth(0);
		deadPlayerData.setNPC(false);
		deadPlayerData.setNPCId("");
		deadPlayerData.setPvPTimeout(0);
		
	}
}
