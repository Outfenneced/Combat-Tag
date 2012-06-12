package com.trc202.CombatTag;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Logger;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.ItemInWorldManager;
import net.minecraft.server.MinecraftServer;
import net.slipcor.pvparena.api.PVPArenaAPI;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.topcat.npclib.NPCManager;
import com.topcat.npclib.entity.NPC;
import com.trc202.CombatTagListeners.CombatTagCommandPrevention;
import com.trc202.CombatTagListeners.NoPvpBlockListener;
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
	public final NoPvpEntityListener entityListener = new NoPvpEntityListener(this);
	private final NoPvpBlockListener blockListener = new NoPvpBlockListener(this);
	private final CombatTagCommandPrevention commandPreventer = new CombatTagCommandPrevention(this);
	
	private int npcNumber;
	
	public CombatTag() {
		settings = new Settings();
		new File(mainDirectory).mkdirs();
		settingsFile = new File(mainDirectory + File.separator + "settings.prop");
		settingsHelper = new SettingsHelper(settingsFile, "CombatTag");
		npcNumber = 0;
	}
	
	/**
	 * Change NPCManager to:
	 * 
	 * private class SL implements Listener {
	 *	@SuppressWarnings("unused")
	 *	public void disableNPCLib() {
	 *		despawnAll();
	 *		Bukkit.getServer().getScheduler().cancelTask(taskid);
	 *	}
	 *}
	 */
	@Override
	public void onDisable() {
		for(PlayerDataContainer pdc : playerData.values()){
			NPC npc = npcm.getNPC(pdc.getPlayerName());
			if(npc != null){
				if(isDebugEnabled()){log.info("[CombatTag] Disable npc for: " + pdc.getPlayerName() + " !");}
				updatePlayerData(npc, pdc.getPlayerName());
				despawnNPC(pdc);
			}
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
		pm.registerEvents(plrListener, this);
		pm.registerEvents(entityListener, this);
		pm.registerEvents(commandPreventer, this);
		pm.registerEvents(blockListener, this);
		log.info("["+ getDescription().getName() +"]"+ " has loaded with a tag time of " + settings.getTagDuration() + " seconds");
	}
	
	/**
	 * Spawns an npc with the name PvPLogger at the given location, sets the npc id to be the players name
	 * @param plr
	 * @param location
	 * @return
	 */
	public NPC spawnNpc(String plr,Location location){
		NPC spawnedNPC = null;
		if(isDebugEnabled()){log.info("[CombatTag] Spawning NPC for " + plr);}
		spawnedNPC = npcm.spawnHumanNPC(getNpcNumber() + "", location , plr);
		if(spawnedNPC.getBukkitEntity() instanceof HumanEntity){
			HumanEntity p = (HumanEntity) spawnedNPC.getBukkitEntity();
			p.setTicksLived(80);
			p.setNoDamageTicks(1);
		}
		return spawnedNPC;
	}
	
	public String getNpcName(String plr) {
		String npcName = settings.getNpcName();
		if(!(npcName.contains("player") || npcName.contains("number")))
		{
			npcName = npcName + getNpcNumber();
		}
		if(npcName.contains("player")){
			npcName = npcName.replace("player", plr);
		}
		if(npcName.contains("number")) {
			npcName = npcName.replace("number", "" + getNpcNumber());
		}
		return npcName;
	}

	/**
	 * Despawns npc and copys all contents from npc to player data
	 * @param plrData 
	 */
	public void despawnNPC(PlayerDataContainer plrData) {
		if(isDebugEnabled()){log.info("[CombatTag] Despawning NPC for " + plrData.getPlayerName());}
		NPC npc = npcm.getNPC(plrData.getNPCId());
		if(npc != null){			
			updatePlayerData(npc, plrData.getPlayerName());
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
		if (isDebugEnabled()) {log.info("[CombatTag] " + deadPlayerData.getPlayerName() + " has been killed by Combat Tag and their inventory has been emptied.");}
	}
	
	public void emptyInventory(Player target) {
		PlayerInventory targetInv = target.getInventory();
		targetInv.clear();
		if (isDebugEnabled()) {log.info("[CombatTag] " + target.getName() + " has been killed by Combat Tag and their inventory has been emptied.");}
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
				if(hasDataContainer(player.getName()) && !getPlayerData(player.getName()).hasPVPtagExpired()){
					PlayerDataContainer playerDataContainer = getPlayerData(player.getName());
					String message = settings.getCommandMessageTagged();
					message = message.replace("[time]", "" + (playerDataContainer.getRemainingTagTime()/1000));
					player.sendMessage(message);
				}else{
					removeDataContainer(player.getName());
					player.sendMessage(settings.getCommandMessageNotTagged());
				}
				return true;
			}
		}else{
			log.info("[CombatTag] Combat Tag can only be used by a player");
			return true;
		}
		return false;	
	}

	
	public void scheduleDelayedKill(final NPC npc, final PlayerDataContainer plrData) {
		long despawnTicks = settings.getNpcDespawnTime() * 20L;
		final boolean kill = settings.isNpcDieAfterTime();
    	final Player plrNpc = (Player) npc.getBukkitEntity();
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			@Override
			public void run() {
				if((kill == false) && (kill == true)){
					if(npcm.getNPC(plrData.getNPCId()) != null){
						plrNpc.setHealth(0);
						updatePlayerData(npc, plrData.getPlayerName());
					}
				} else {
					despawnNPC(plrData);
				}
			}
		}, despawnTicks);
	}
	
	public boolean PvPArenaHook(Player damager, Player damaged){
		PVPArenaAPI pvpArenaApi = null;
		boolean bothNotInArena = true;
		if(getServer().getPluginManager().getPlugin("pvparena") != null){
			pvpArenaApi = new PVPArenaAPI(); 
		}
		if(pvpArenaApi != null)
			bothNotInArena = PVPArenaAPI.getArenaName(damager) == "" && PVPArenaAPI.getArenaName(damaged) == "";
		return bothNotInArena;
	}
	
    /**
     * Loads the player data using bukkit and moves the data from the npc to the offline players file
     * @param npc
     * @param playerName
     */
    public void updatePlayerData(NPC npc, String playerName){
    	Player target = this.getServer().getPlayer(playerName); //Could return the player or null
    	if(target == null){ //If player is offline
    		if(isDebugEnabled()){log.info("[CombatTag] Update player data for " + playerName + " !");}
    		//Create an entity to load the player data
    		MinecraftServer server = ((CraftServer)this.getServer()).getServer();
    		EntityPlayer entity = new EntityPlayer(server, server.getWorldServer(0), playerName, new ItemInWorldManager(server.getWorldServer(0)));
    		target = (entity == null) ? null : (Player) entity.getBukkitEntity();
            //Equivalent to
            /*
            if(entity == null){
                target = null;
            }else{
                target = entity.getBukkitEntity();
            }
            */
            if(target != null){
                target.loadData();
            }
    	}
    	if(target instanceof CraftHumanEntity && npc.getBukkitEntity() instanceof CraftHumanEntity){
    		EntityHuman humanTarget = ((CraftHumanEntity) target).getHandle();
    		EntityHuman humanNpc = ((CraftHumanEntity) npc.getBukkitEntity()).getHandle();
    		humanTarget.copyTo(humanNpc); //Actually means copy from
    		if(humanNpc.getHealth() <= 0){
    			emptyInventory(target);
    			ItemStack airItem = new ItemStack(Material.AIR);
    			ItemStack[] emptyArmorStack = new ItemStack[4];
    			for(int x = 0; x < emptyArmorStack.length; x++){
    				emptyArmorStack[x] = airItem;
    			}
    			target.getInventory().setArmorContents(emptyArmorStack);
    		}
    	} else {
    		log.info("[" + this.getDescription().getName() + "] Something went wrong!");
    		log.info("[" + this.getDescription().getName() + "] The target or source of copyTo is not a Human Entity");
    	}
    	target.saveData();
    }
}
