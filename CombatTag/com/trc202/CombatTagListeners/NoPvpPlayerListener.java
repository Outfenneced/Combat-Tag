package com.trc202.CombatTagListeners;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.martin.bukkit.npclib.NPCEntity;

import com.trc202.CombatTag.CombatTag;
import com.trc202.Containers.PlayerDataContainer;

public class NoPvpPlayerListener extends PlayerListener{
	
	private final CombatTag plugin;
	public static int explosionDamage = -1;
	
    public NoPvpPlayerListener(CombatTag instance) {
        plugin = instance;
    }
    
	@Override
    public void onPlayerJoin(PlayerJoinEvent e){
		Player loginPlayer = e.getPlayer();
		if(plugin.hasDataContainer(loginPlayer.getName())){
			//Player has a data container and is likely to need some sort of punishment
			PlayerDataContainer loginDataContainer = plugin.getPlayerData(loginPlayer.getName());
			if(loginDataContainer.hasSpawnedNPC()){
				//Player has pvplogged and has not been killed yet
				//despawn the npc and transfer any effects over to the player
				if(plugin.isDebugEnabled()){plugin.log.info("[CombatTag] Player logged in and has npc");}
				plugin.despawnNPC(loginDataContainer);
			}
			if(plugin.isDebugEnabled()){plugin.log.info("[CombatTag] " + loginDataContainer.getPlayerName() +" should be punushed");}
			if(loginDataContainer.shouldBePunished()){
				if(plugin.isDebugEnabled()){plugin.log.info("[CombatTag] Getting info from NPC and putting it back into the player");}
				loginPlayer.setExperience(loginDataContainer.getExp());
				loginPlayer.getInventory().setArmorContents(loginDataContainer.getPlayerArmor());
				loginPlayer.getInventory().setContents(loginDataContainer.getPlayerInventory());
				loginPlayer.setHealth(loginDataContainer.getHealth());
				assert(loginPlayer.getHealth() == loginDataContainer.getHealth());
				loginPlayer.setLastDamageCause(new EntityDamageEvent(loginPlayer, DamageCause.ENTITY_EXPLOSION, 0));
			}
			plugin.removeDataContainer(loginPlayer.getName());
		}
	}
	
	@Override
	public void onPlayerQuit(PlayerQuitEvent e){
		Player quitPlr = e.getPlayer();
		//Player is likely in pvp
		if(plugin.hasDataContainer(quitPlr.getName())){
			PlayerDataContainer quitDataContainer = plugin.getPlayerData(quitPlr.getName());
			if(!quitDataContainer.hasPVPTimedOut()){
				//Player has logged out before the pvp battle is considered over by the plugin
				if(plugin.isDebugEnabled()){plugin.log.info("[CombatTag] Player has logged of during pvp!");}
				if(plugin.settings.isInstaKill()){
					quitPlr.setHealth(0);
					plugin.removeDataContainer(quitPlr.getName());
				}else{
					NPCEntity npc = plugin.spawnNpc(quitPlr.getName(),quitPlr.getLocation());
					plugin.copyContentsNpc(npc, quitPlr);
					npc.health = quitPlr.getHealth();
					quitDataContainer.setSpawnedNPC(true);
					quitDataContainer.setNPCId(quitPlr.getName());
					quitDataContainer.setShouldBePunished(true);
					quitPlr.getWorld().createExplosion(quitPlr.getLocation(), explosionDamage); //Create the smoke effect //
					System.out.println("NPC identifier: "+ quitDataContainer.getNPCId());
				}
			}
		}
	}

	

}
