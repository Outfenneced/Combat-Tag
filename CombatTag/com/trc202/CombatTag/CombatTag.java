package com.trc202.CombatTag;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

import net.minecraft.server.v1_7_R1.EntityHuman;
import net.minecraft.server.v1_7_R1.EntityPlayer;
import net.minecraft.server.v1_7_R1.MinecraftServer;
import net.minecraft.server.v1_7_R1.PlayerInteractManager;
import net.minecraft.util.com.mojang.authlib.GameProfile;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R1.CraftServer;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftHumanEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.topcat.npclib.NPCManager;
import com.topcat.npclib.entity.NPC;
import com.trc202.CombatTagListeners.CombatTagCommandPrevention;
import com.trc202.CombatTagListeners.NoPvpBlockListener;
import com.trc202.CombatTagListeners.NoPvpEntityListener;
import com.trc202.CombatTagListeners.NoPvpPlayerListener;
import com.trc202.Containers.Settings;
import com.trc202.helpers.SettingsHelper;

public class CombatTag extends JavaPlugin {
    private SettingsHelper settingsHelper;
    private File settingsFile;
    public Settings settings;
    public final Logger log = Logger.getLogger("Minecraft");
    public NPCManager npcm;
    private HashMap<String, Long> tagged;
    private static String mainDirectory = "plugins/CombatTag";
    
    public final CombatTagIncompatibles ctIncompatible = new CombatTagIncompatibles(this);
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
     * Change SL in NPCManager to:
     *
     * private class SL implements Listener { 
     * 	   @SuppressWarnings("unused") 
     * 	   public void disableNPCLib() { 
     *     		despawnAll();
     *     		Bukkit.getServer().getScheduler().cancelTask(taskid); 
     *     } 
     * }
     */
    
    /**
     * Change NullSocket to:
     * 
     * class NullSocket extends Socket
	 *	{
	 *		private final byte[] buffer = new byte[50];
	 *
	 *		@Override
	 *		public InputStream getInputStream()
	 *		{
	 *			return new ByteArrayInputStream(this.buffer);
	 *		}
	 *	
	 *		@Override
	 *		public OutputStream getOutputStream()
	 *		{
	 *			return new ByteArrayOutputStream(10);
	 *		}
	 *	}
     */
    @Override
    public void onDisable() {
        for (String pdc : tagged.keySet()) {
            NPC npc = npcm.getNPC(pdc);
            if (npc != null) {
                if (isDebugEnabled()) {
                    log.info("[CombatTag] Disable npc for: " + pdc + " !");
                }
                updatePlayerData(npc, pdc);
                despawnNPC(pdc);
            }
        }
        //Just in case...
        log.info("[CombatTag] Disabled");
    }

    @Override
    public void onEnable() {
        tagged = new HashMap<String, Long>();
        settings = new SettingsLoader().loadSettings(settingsHelper, this.getDescription().getVersion());
        npcm = new NPCManager(this);
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(plrListener, this);
        pm.registerEvents(entityListener, this);
        pm.registerEvents(commandPreventer, this);
        pm.registerEvents(blockListener, this);
        log.info("[" + getDescription().getName() + "]" + " has loaded with a tag time of " + settings.getTagDuration() + " seconds");
    }
    
    public long getRemainingTagTime(String name){
    	if(tagged.get(name) == null){return -1;}
    	return (tagged.get(name) - System.currentTimeMillis());
	}
    
    public boolean addTagged(Player player){
    	if(player.isOnline()){
    		tagged.remove(player.getName());
    		tagged.put(player.getName(), PvPTimeout(getTagDuration()));
    		return true;
    	}
    	return false;
    }
    
    public boolean inTagged(String name){
    	return tagged.containsKey(name);
    }
    
    public long removeTagged(String name){
    	if(inTagged(name)){
    		return tagged.remove(name);
    	}
    	return -1;
    }
    
    public long PvPTimeout(int seconds){
		return System.currentTimeMillis() + (seconds * 1000);
	}
    
    public boolean isInCombat(String name){
    	if(getRemainingTagTime(name) < 0){
    		tagged.remove(name);
    		return false;
    	} else {
    		return true;
    	}
    }

    /**
     * Spawns an npc with the name PvPLogger at the given location, sets the npc
     * id to be the players name
     *
     * @param plr
     * @param location
     * @return
     */
    public NPC spawnNpc(Player plr, Location location) {
        NPC spawnedNPC = null;
        if (isDebugEnabled()) {
            log.info("[CombatTag] Spawning NPC for " + plr.getName());
        }
        spawnedNPC = npcm.spawnHumanNPC(getNpcName(plr.getName()), location, plr.getName());
        if (spawnedNPC.getBukkitEntity() instanceof HumanEntity) {
            HumanEntity p = (HumanEntity) spawnedNPC.getBukkitEntity();
            p.setNoDamageTicks(1);
            p.setMetadata("NPC", new FixedMetadataValue(this, "NPC"));
        }
        return spawnedNPC;
    }

    public String getNpcName(String plr) {
        String npcName = settings.getNpcName();
        if (!(npcName.contains("player") || npcName.contains("number"))) {
            npcName = npcName + getNpcNumber();
        }
        if (npcName.contains("player")) {
            npcName = npcName.replace("player", plr);
        }
        if (npcName.contains("number")) {
            npcName = npcName.replace("number", "" + getNpcNumber());
        }
        return npcName;
    }

    /**
     * Despawns npc and copys all contents from npc to player data
     *
     * @param plrData
     */
    public void despawnNPC(String playerName) {
        if (isDebugEnabled()) {
            log.info("[CombatTag] Despawning NPC for " + playerName);
        }
        NPC npc = npcm.getNPC(playerName);
        if (npc != null) {
            updatePlayerData(npc, playerName);
            npcm.despawnById(playerName);
        }
    }

    public String getPlayerName(Entity entity) {
        if (npcm.isNPC(entity)) {
            return npcm.getNPCIdFromEntity(entity);
        }
        return "entity match failure";
    }

    /**
     * Copys inventory from the Player to the NPC
     *
     * @param npc Npc
     * @param plr Player
     */
    public void copyContentsNpc(NPC npc, Player plr) {
        if (npc.getBukkitEntity() instanceof Player) {
            Player playerNPC = (Player) npc.getBukkitEntity();
            copyTo(playerNPC, plr);
        }
    }

    /**
     *
     * @return the system tag duration as set by the user
     */
    public int getTagDuration() {
        return settings.getTagDuration();
    }

    public boolean isDebugEnabled() {
        return settings.isDebugEnabled();
    }

    public void emptyInventory(Player target) {
        PlayerInventory targetInv = target.getInventory();
        targetInv.clear();
        if (isDebugEnabled()) {
            log.info("[CombatTag] " + target.getName() + " has been killed by Combat Tag and their inventory has been emptied through UpdatePlayerData.");
        }
    }
/*
    public void removeDataContainer(String playerName) {
        playerData.remove(playerName);
    }
*/
    public int getNpcNumber() {
        npcNumber = npcNumber + 1;
        return npcNumber;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
    	if (command.getName().equalsIgnoreCase("ct") || (command.getName().equalsIgnoreCase("combattag"))) {
    		if (args.length == 0) {
    			if (sender instanceof Player) {
    				if(isInCombat(sender.getName())) {
    					String message = settings.getCommandMessageTagged();
    					message = message.replace("[time]", "" + (getRemainingTagTime(sender.getName()) / 1000));
    					sender.sendMessage(message);
    				} else {
    					tagged.remove(sender.getName());
    					sender.sendMessage(settings.getCommandMessageNotTagged());
    				}
    			} else {
    				log.info("[CombatTag] /ct can only be used by a player!");
    			}
    			return true;
    		} else if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("combattag.reload")) {
                    settings = new SettingsLoader().loadSettings(settingsHelper, this.getDescription().getVersion());
                    if (sender instanceof Player) {
                        sender.sendMessage(ChatColor.RED + "[CombatTag] Settings were reloaded!");
                    } else {
                        log.info("[CombatTag] Settings were reloaded!");
                    }
                } else {
                    if (sender instanceof Player) {
                        sender.sendMessage(ChatColor.RED + "[CombatTag] You don't have the permission 'combattag.reload'!");
                    }
                }
                return true;
            } else if (args[0].equalsIgnoreCase("wipe")) {
                if (sender.hasPermission("combattag.wipe")) {
                    int numNPC = 0;
                    //PlayerDataContainer despawn;
                    for (NPC npc : npcm.getNPCs()) {
                        updatePlayerData(npc, npcm.getNPCIdFromEntity(npc.getBukkitEntity()));
                        npcm.despawnById(npcm.getNPCIdFromEntity(npc.getBukkitEntity()));
                        numNPC++;
                    }
                    sender.sendMessage("[CombatTag] Wiped " + numNPC + " pvploggers!");
                }
                return true;
            } else if (args[0].equalsIgnoreCase("command")) {
                if (sender.hasPermission("combattag.command")) {
                    if (args.length > 2) {
                        if (args[1].equalsIgnoreCase("add")) {
                            if (args[2].length() == 0 || !args[2].startsWith("/")) {
                                sender.sendMessage(ChatColor.RED + "[CombatTag] Correct Usage: /ct command add /<command>");
                            } else {
                                String disabledCommands = settingsHelper.getProperty("disabledCommands");
                                if (!disabledCommands.contains(args[2])) {
                                    disabledCommands = disabledCommands.substring(0, disabledCommands.length() - 1) + "," + args[2] + "]";
                                    disabledCommands = disabledCommands.replace("[,", "[");
                                    disabledCommands = disabledCommands.replaceAll(",,", ",");
                                    settingsHelper.setProperty("disabledCommands", disabledCommands);
                                    settingsHelper.saveConfig();
                                    sender.sendMessage(ChatColor.RED + "[CombatTag] Added " + args[2] + " to combat blocked commands.");
                                    settings = new SettingsLoader().loadSettings(settingsHelper, this.getDescription().getVersion());
                                } else {
                                    sender.sendMessage(ChatColor.RED + "[CombatTag] That command is already in the blocked commands list.");
                                }
                            }
                        } else if (args[1].equalsIgnoreCase("remove")) {
                            if (args[2].length() == 0 || !args[2].startsWith("/")) {
                                sender.sendMessage(ChatColor.RED + "[CombatTag] Correct Usage: /ct command remove /<command>");
                            } else {
                                String disabledCommands = settingsHelper.getProperty("disabledCommands");
                                if (disabledCommands.contains(args[2] + ",") || disabledCommands.contains(args[2] + "]")) {
                                    disabledCommands = disabledCommands.replace(args[2] + ",", "");
                                    disabledCommands = disabledCommands.replace(args[2] + "]", "]");
                                    disabledCommands = disabledCommands.replace(",]", "]");
                                    disabledCommands = disabledCommands.replaceAll(",,", ",");
                                    settingsHelper.setProperty("disabledCommands", disabledCommands);
                                    settingsHelper.saveConfig();
                                    sender.sendMessage(ChatColor.RED + "[CombatTag] Removed " + args[2] + " from combat blocked commands.");
                                    settings = new SettingsLoader().loadSettings(settingsHelper, this.getDescription().getVersion());
                                } else {
                                    sender.sendMessage(ChatColor.RED + "[CombatTag] That command is not in the blocked commands list.");
                                }
                            }
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "[CombatTag] Correct Usage: /ct command <add/remove> /<command>");
                    }
                }
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "[CombatTag] That is not a valid command!");
                return true;
            }
        }
        return false;
    }

    public void scheduleDelayedKill(final NPC npc, final String name) {
        long despawnTicks = settings.getNpcDespawnTime() * 20L;
        final boolean kill = settings.isNpcDieAfterTime();
        final Player plrNpc = (Player) npc.getBukkitEntity();
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run() {
            	if(Bukkit.getServer().getPlayerExact(name) == null){
            		if (npcm.getNPC(name) != null) {
            			if (kill == true) {
            				plrNpc.setHealth(0);
            				updatePlayerData(npc, name);
            			} else {
            				despawnNPC(name);
            			}
            		}
            	} else if(!Bukkit.getServer().getPlayerExact(name).isOnline()){
            		if (npcm.getNPC(name) != null) {
            			if (kill == true) {
            				plrNpc.setHealth(0);
            				updatePlayerData(npc, name);
            			} else {
            				despawnNPC(name);
            			}
            		}
            	}
            }
        }, despawnTicks);
    }

    /**
     * Loads the player data using bukkit and moves the data from the npc to the
     * offline players file
     *
     * @param npc
     * @param playerName
     */
    public void updatePlayerData(NPC npc, String playerName) {
        Player target = this.getServer().getPlayerExact(playerName); //Could return the player or null
        if (target == null) { //If player is offline
            if (isDebugEnabled()) {
                log.info("[CombatTag] Update player data for " + playerName + " !");
            }
            //Create an entity to load the player data
            MinecraftServer server = ((CraftServer) this.getServer()).getServer();
            EntityPlayer entity = new EntityPlayer(server, server.getWorldServer(0), setGameProfile(playerName, UUID.randomUUID().toString()), new PlayerInteractManager(server.getWorldServer(0)));
            target = (entity == null) ? null : (Player) entity.getBukkitEntity();
            //Equivalent to
			/*
             * if(entity == null){ target = null; }else{ target =
             * entity.getBukkitEntity(); }
             */
            if (target != null) {
                target.loadData();
            }
        }
        if (target != null && (npcm.getNPC(playerName) == npc) && npc != null) {
            EntityHuman humanTarget = ((CraftHumanEntity) target).getHandle();
            Player source = (Player) npc.getBukkitEntity();
            if (source.getHealth() <= 0) {
                emptyInventory(target);
                ItemStack airItem = new ItemStack(Material.AIR);
                ItemStack[] emptyArmorStack = new ItemStack[4];
                for (int x = 0; x < emptyArmorStack.length; x++) {
                    emptyArmorStack[x] = airItem;
                }
                target.getInventory().setArmorContents(emptyArmorStack);
                humanTarget.setHealth(0);
            } else {
                copyTo(target, source);
            }
        } else {
            log.info("[" + this.getDescription().getName() + "] Something went wrong!");
            log.info("[" + this.getDescription().getName() + "] Please make a ticket with this message as well as what occurred.");
        }
        target.saveData();
    }

	public GameProfile setGameProfile(String name, String id){
		return new GameProfile(id, name);
	}
	
    public void copyTo(Player target, Player source) {
        target.getInventory().setContents(source.getInventory().getContents());
        target.getInventory().setArmorContents(source.getInventory().getArmorContents());
        target.setExp(source.getExp());
        target.setLevel(source.getLevel());
        target.setFoodLevel(source.getFoodLevel());
        target.addPotionEffects(source.getActivePotionEffects());
        target.setRemainingAir(source.getRemainingAir());
        target.setExhaustion(source.getExhaustion());
        target.setSaturation(source.getSaturation());
        target.setFireTicks(source.getFireTicks());
        if (target instanceof CraftHumanEntity) {
            EntityHuman humanTarget = ((CraftHumanEntity) target).getHandle();
            double healthSet = healthCheck(source.getHealth());
            humanTarget.setHealth((float) healthSet);
        } else {
            log.info("[CombatTag] An error has occurred! Target is not a HumanEntity!");
        }
    }

    public double healthCheck(double health) {
        if (health < 0) {
            health = 0;
        }
        if (health > 20) {
            health = 20;
        }
        return health;
    }
}
