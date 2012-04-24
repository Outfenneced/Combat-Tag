package com.trc202.CombatTagListeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.topcat.npclib.NPCManager;
import com.topcat.npclib.entity.NPC;
import com.trc202.CombatTag.CombatTag;
import com.trc202.Containers.PlayerDataContainer;

import java.util.HashMap;

public class NoPvpPlayerListener implements Listener{
	
	private final CombatTag plugin;
	public static int explosionDamage = -1;
	public NPCManager npcm;
	public NoPvpEntityListener entityListener;
	private HashMap<String, Long> bannedPlayers;
	
    public NoPvpPlayerListener(CombatTag instance) {
    	plugin = instance;
        bannedPlayers = new HashMap<String, Long>();
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event){
        Player loginPlayer = event.getPlayer();
        kickIfTempBanned(loginPlayer);
        onPlayerJoinNPCMode(loginPlayer);
	}
	
    @EventHandler(ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitEvent e){
		Player quitPlr = e.getPlayer();
		tempBanIfPvP(quitPlr);
		if(plugin.settings.getNpcDespawnTime() <= -1){
			onPlayerQuitNPCMode(quitPlr);
		}else if(plugin.settings.getNpcDespawnTime() > 0){
			onPlayerQuitTimedMode(quitPlr);
		}else{
			plugin.log.info("[CombatTag] Invalid npcDespawnTime");
		}
	}
	
    @EventHandler(ignoreCancelled = true)
    public void onPlayerKick(PlayerKickEvent event){
    	Player player = event.getPlayer();
    	if (plugin.hasDataContainer(player.getName())) {
    		PlayerDataContainer kickDataContainer = plugin.getPlayerData(player.getName());
    		if (!kickDataContainer.hasPVPtagExpired()) {
    			if (plugin.settings.dropTagOnKick()) {
    				if (plugin.isDebugEnabled()) {plugin.log.info("[CombatTag] Player tag dropped for being kicked.");}
    				kickDataContainer.setPvPTimeout(0);
    				plugin.removeDataContainer(player.getName());
    			}
    		}
    	}
    } 
    
	private void onPlayerQuitTimedMode(Player quitPlr){
		if(plugin.hasDataContainer(quitPlr.getName())){
			PlayerDataContainer quitDataContainer = plugin.getPlayerData(quitPlr.getName());
			if(!quitDataContainer.hasPVPtagExpired()){
				//if(plugin.isDebugEnabled()){plugin.log.info("[CombatTag] Player has logged of during pvp!");}
				if(plugin.settings.isInstaKill()){
					quitPlr.setHealth(0);
					plugin.removeDataContainer(quitPlr.getName());
				}else{
					final NPC npc = plugin.spawnNpc(quitPlr.getName(),quitPlr.getLocation());
					if(npc.getBukkitEntity() instanceof Player){
						Player npcPlayer = (Player) npc.getBukkitEntity();
						plugin.copyContentsNpc(npc, quitPlr);
						String plrName = quitPlr.getName(); //tempfix
						plugin.npcm.rename(plrName, plugin.getNpcName(plrName)); //tempfix
						npcPlayer.setHealth(quitPlr.getHealth());
						quitDataContainer.setSpawnedNPC(true);
						quitDataContainer.setNPCId(quitPlr.getName());
						quitDataContainer.setShouldBePunished(true);
						quitPlr.getWorld().createExplosion(quitPlr.getLocation(), explosionDamage); //Create the smoke effect //
						plugin.scheduleDelayedKill(npc, quitDataContainer);	
					}
				}
			}
		}
	}
	
    public void tempBanIfPvP(Player player){
        // this happens when the user quits
        // and it only will do anything if the player has a data container
        // which means he has been damaged in the past
        if (plugin.hasDataContainer(player.getName())) {
            // if the data container signals us that the PvP timer has expired
            // then we do not ban them temporarily
            PlayerDataContainer dataContainer = plugin.getPlayerData(player.getName());
            if (dataContainer.hasPVPtagExpired()) { return; }
            long tempBanSeconds = plugin.settings.getTempBanSeconds();
            long deadline = (tempBanSeconds * 1000) + System.currentTimeMillis();
            bannedPlayers.put(player.getName(), deadline);
        }
    }

    public void kickIfTempBanned(Player player){
        // this happens when the user joins
        // and it only will do anything if the player is in the list of
        // tempbanned players
        if (bannedPlayers.containsKey(player.getName())) {
            long deadline = bannedPlayers.get(player.getName());
            if (deadline >= System.currentTimeMillis()) {
                long duration = ( deadline - System.currentTimeMillis() ) / 1000;
                player.kickPlayer("You cannot log in again for " + duration + " seconds");
            }
            else {
                bannedPlayers.remove(player.getName());
            }
        }
    }

	private void onPlayerQuitNPCMode(Player quitPlr){
		if(plugin.hasDataContainer(quitPlr.getName())){
			//Player is likely in pvp
			PlayerDataContainer quitDataContainer = plugin.getPlayerData(quitPlr.getName());
			if(!quitDataContainer.hasPVPtagExpired()){
				//Player has logged out before the pvp battle is considered over by the plugin
				//if(plugin.isDebugEnabled()){plugin.log.info("[CombatTag] Player has logged of during pvp!");}
				if(plugin.settings.isInstaKill()){
					quitPlr.setHealth(0);
					plugin.removeDataContainer(quitPlr.getName());
				}else{
					NPC npc = plugin.spawnNpc(quitPlr.getName(), quitPlr.getLocation());
					if(npc.getBukkitEntity() instanceof Player){
						Player npcPlayer = (Player) npc.getBukkitEntity();
						plugin.copyContentsNpc(npc, quitPlr);
						plugin.npcm.rename(quitPlr.getName(), plugin.getNpcName(quitPlr.getName()));
						npcPlayer.setHealth(quitPlr.getHealth());
						quitDataContainer.setSpawnedNPC(true);
						quitDataContainer.setNPCId(quitPlr.getName());
						quitDataContainer.setShouldBePunished(true);
						quitPlr.getWorld().createExplosion(quitPlr.getLocation(), explosionDamage); //Create the smoke effect //
					}
				}
			}
		}
	}

	private void onPlayerJoinNPCMode(Player loginPlayer){
		if(plugin.hasDataContainer(loginPlayer.getName())){
			//Player has a data container and is likely to need some sort of punishment
			PlayerDataContainer loginDataContainer = plugin.getPlayerData(loginPlayer.getName());
			if(loginDataContainer.hasSpawnedNPC()){
				//Player has pvplogged and has not been killed yet
				//despawn the npc and transfer any effects over to the player
				//if(plugin.isDebugEnabled()){plugin.log.info("[CombatTag] Player logged in and has npc");}
				plugin.despawnNPC(loginDataContainer);
			}
			if(loginDataContainer.shouldBePunished()){
				loginPlayer.setExp(loginDataContainer.getExp());
				loginPlayer.getInventory().setArmorContents(loginDataContainer.getPlayerArmor());
				loginPlayer.getInventory().setContents(loginDataContainer.getPlayerInventory());
				int healthSet = healthCheck(loginDataContainer.getHealth(), loginDataContainer);
				loginPlayer.setHealth(healthSet);
				assert(loginPlayer.getHealth() == loginDataContainer.getHealth());
				loginPlayer.setLastDamageCause(new EntityDamageEvent(loginPlayer, DamageCause.ENTITY_EXPLOSION, 0));
			}
			plugin.removeDataContainer(loginPlayer.getName());
			plugin.createPlayerData(loginPlayer.getName()).setPvPTimeout(plugin.getTagDuration());
		}
	}
	
	private int healthCheck(int health, PlayerDataContainer loginDataContainer) {
		if(health < 0){
			health = 0;
		}
		if(health > 20){
			health = 20;
		}
		if(health == 0){
			if(plugin.isDebugEnabled()){plugin.log.info("[CombatTag] " + loginDataContainer.getPlayerName() +" has been set a health of 0.");}
		}
		return health;
	}
}
