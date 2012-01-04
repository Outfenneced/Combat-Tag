package com.trc202.CombatTag;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.topcat.npclib.NPCManager;
import com.topcat.npclib.entity.NPC;
import com.trc202.CombatTagListeners.CombatTagCommandPrevention;
import com.trc202.CombatTagListeners.NoPvpEntityListener;
import com.trc202.CombatTagListeners.NoPvpPlayerListener;
import com.trc202.Containers.PlayerDataContainer;
import com.trc202.Containers.PlayerDataManager;
import com.trc202.Containers.Settings;
import com.trc202.helpers.SettingsHelper;

public class CombatTag extends JavaPlugin {
	
	private SettingsHelper settingsHelper;
	private	File settingsFile;
	public Settings settings;
	
	public final Logger log = Logger.getLogger("Minecraft");
	public NPCManager npcm;
	private HashMap<String,PlayerDataContainer> playerData;
	private static String mainDirectory = "plugins/CombatTag";

	private final NoPvpPlayerListener plrListener = new NoPvpPlayerListener(this); 
	private final NoPvpEntityListener entityListener = new NoPvpEntityListener(this);
	private final CombatTagCommandPrevention commandPreventer = new CombatTagCommandPrevention(this);
	
	private int npcNumber;

	public CombatTag(){
		settings = new Settings();
		new File(mainDirectory).mkdirs();
		settingsFile = new File(mainDirectory + File.separator + "settings.prop");
		settingsHelper = new SettingsHelper(settingsFile, "CombatTag");
		npcNumber = 0;
	}
	
	@Override
	public void onDisable() {
		for(PlayerDataContainer pdc : playerData.values()){
			if(pdc.hasSpawnedNPC()){
				despawnNPC(pdc);
			}
			PlayerDataManager.savePlayerData(mainDirectory, pdc);
		}
		//Just in case...
		log.info("[CombatTag] Disabled");
	}

	@Override
	public void onEnable() {
		playerData = new HashMap<String,PlayerDataContainer>();
		settings = new SettingsLoader().loadSettings(settingsHelper, this.getDescription().getVersion());
		npcm = new NPCManager(this);
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_JOIN, plrListener, Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, plrListener, Event.Priority.Lowest, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Event.Priority.Monitor, this);
		pm.registerEvent(Event.Type.ENTITY_DEATH, entityListener, Event.Priority.Monitor, this);
		pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, commandPreventer, Event.Priority.Normal, this);
		log.info("["+ getDescription().getName() +"]"+ " has loaded with a tag time of " + settings.getTagDuration() + " seconds");
	}
	
	/**
	 * Spawns an npc with the name PvPLogger at the given location, sets the npc id to be the players name
	 * @param plr
	 * @param location
	 * @return
	 */
	public NPC spawnNpc(String plr,Location location){
		if(isDebugEnabled()){log.info("[CombatTag] Spawning NPC");}
		NPC spawnedNPC = npcm.spawnHumanNPC("PvPLogger" + getNpcNumber(), location , plr);
		return spawnedNPC;
	}
	
	/**
	 * Despawns npc and copys all contents from npc to player data
	 * @param plrData 
	 */
	public void despawnNPC(PlayerDataContainer plrData) {
		if(isDebugEnabled()){log.info("[CombatTag] Despawning NPC");}
		Entity anNPC = npcm.getNPC(plrData.getNPCId()).getBukkitEntity();
		if(anNPC instanceof Player){
			Player npc = (Player) anNPC;
			plrData.setPlayerArmor(npc.getInventory().getArmorContents());
			plrData.setPlayerInventory(npc.getInventory().getContents());
			plrData.setHealth(npc.getHealth());
			plrData.setExp(npc.getExp());
			npcm.despawnById(plrData.getNPCId());
			plrData.setNPCId("");
			plrData.setSpawnedNPC(false);
		}
	}

	public String getPlayerName(Entity entity){
		if(npcm.isNPC(entity))return npcm.getNPCIdFromEntity(entity);
		return "entity match failure";
	}
	
	/**
	 * Copys inventory from the Player to the NPC
	 * @param npc Npc
	 * @param plr Player
	 */
	public void copyContentsNpc(NPC npc, Player plr) {
		if(npc.getBukkitEntity() instanceof Player){
			Player playerNPC = (Player) npc.getBukkitEntity();
			PlayerInventory npcInv = playerNPC.getInventory();
			PlayerInventory plrInv = plr.getInventory();
			npcInv.setArmorContents(plrInv.getArmorContents());
			playerNPC.setExp(plr.getExp());
			npcInv.setContents(plrInv.getContents());
		}
	}
	/**
	 * Copys inventory from the NPC to the player
	 * @param npc
	 * @param plr
	 */
	public void copyContentsPlayer(NPC npc, Player plr) {
		if(npc.getBukkitEntity() instanceof Player){
			Player playerNPC = (Player) npc.getBukkitEntity();
			PlayerInventory npcInv = playerNPC.getInventory();
			PlayerInventory plrInv = plr.getInventory();
			plrInv.setArmorContents(npcInv.getArmorContents());
			plrInv.setContents(npcInv.getContents());
			plr.setExp(playerNPC.getExp());
		}
	}

	public boolean hasDataContainer(String playerName){
		if(playerData.containsKey(playerName)){
			return playerData.containsKey(playerName);
		}
		else{
			return PlayerDataManager.hasPlayerDataFile(mainDirectory, playerName);
		}
	}
	public PlayerDataContainer getPlayerData(String playerName){
		if(!playerData.containsKey(playerName)){
			playerData.put(playerName,PlayerDataManager.loadPlayerData(mainDirectory, playerName));
			PlayerDataManager.deletePlayerData(mainDirectory,playerName);
		}
		return playerData.get(playerName);
	}
	
	
	public PlayerDataContainer createPlayerData(String playerName){
		PlayerDataContainer plr = new PlayerDataContainer(playerName);
		playerData.put(playerName, plr);
		return plr;
	}


	/**
	 * 
	 * @return the system tag duration as set by the user
	 */
	public int getTagDuration(){
		return settings.getTagDuration();
	}

	public boolean isDebugEnabled(){
		return settings.isDebugEnabled();
	}
	
	/**
	 * Kills player and sets their inventory to an empty stack
	 * @param deadPlayerData
	 */
	public void killPlayerEmptyInventory(PlayerDataContainer deadPlayerData) {
		deadPlayerData.setExp(0);
		ItemStack airItem = new ItemStack(Material.AIR);
		ItemStack[] emptyStack = new ItemStack[36];
		for(int x = 0; x < emptyStack.length; x++){
			emptyStack[x] = airItem;
		}
		ItemStack[] emptyArmorStack = new ItemStack[4];
		for(int x = 0; x < emptyArmorStack.length; x++){
			emptyArmorStack[x] = airItem;
		}
		deadPlayerData.setPlayerArmor(emptyArmorStack);
		deadPlayerData.setPlayerInventory(emptyStack);
		deadPlayerData.setHealth(0);
		deadPlayerData.setSpawnedNPC(false);
		deadPlayerData.setNPCId("");
		deadPlayerData.setPvPTimeout(0);
	}
	
	public void removeDataContainer(String playerName){
		playerData.remove(playerName);
	}

	public int getNpcNumber() {
		npcNumber = npcNumber + 1;
		return npcNumber;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args)
	{
		if(command.getName().equalsIgnoreCase("ct") || (command.getName().equalsIgnoreCase("combattag"))){
			if(sender instanceof Player){
				Player player = (Player) sender;
				if(hasDataContainer(player.getName())){
					PlayerDataContainer playerDataContainer = getPlayerData(player.getName());
					player.sendMessage("You are in combat for " + playerDataContainer.getRemainingTagTime()/1000 + " seconds");
				}else{
					player.sendMessage("You are not currently in combat!");
				}
				return true;
			}
		}else{
			log.info("[CombatTag] Combat Tag can only be used by a player");
			return true;
		}
		return false;
		
	}
}
