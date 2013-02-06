package com.trc202.CombatTagApi;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.trc202.CombatTag.CombatTag;
import com.trc202.Containers.PlayerDataContainer;

public class CombatTagApi {
	
	private CombatTag plugin;
	
	public CombatTagApi(CombatTag plugin){
		this.plugin = plugin;
	}

	/**
	 * Checks to see if the player is in combat. The combat time can be configured by the server owner
	 * If the player has died while in combat the player is no longer considered in combat and as such will return false
	 * @param playerName
	 * @return true if player is in combat
	 */
	public boolean isInCombat(String player){
		boolean isInCombat = false;
		if(player.contains("pvpLogger")){return false;}
		if(plugin.hasDataContainer(player)){
			PlayerDataContainer container = plugin.getPlayerData(player);
			isInCombat = !container.hasPVPtagExpired();
		}
		return isInCombat;
	}
	
    /**
	 * Checks to see if the player is in combat. The combat time can be configured by the server owner
	 * If the player has died while in combat the player is no longer considered in combat and as such will return false
	 * @param player
	 * @return true if player is in combat
	 */
	public boolean isInCombat(Player player){
		return isInCombat(player.getName());
	}
	
	/**
	 * Returns the time before the tag is over
	 *  -1 if the tag has expired
	 *  -2 if the player is not in combat
	 * @param player
	 * @return
	 */
	public long getRemainingTagTime(String player){
		if(plugin.hasDataContainer(player)){
			PlayerDataContainer playerDataContainer = plugin.getPlayerData(player);
			if(playerDataContainer.hasPVPtagExpired()){
				return -1;
			}else{
				return playerDataContainer.getRemainingTagTime();
			}
		}else{
			return -2;
		}
	}
	
	/**
	 * Returns if the entity is an NPC
	 * @param player
	 * @return true if the player is an NPC
	 */
	public boolean isNPC(Entity player){
		if(plugin.npcm.isNPC(player)){return true;}
		return false;
	}
	
	/**
	 * Tags player
	 * @param player
	 * @return true if the action is successful, false if not
	 */
	public boolean tagPlayer(Player player){
		if(player.isOnline()){
			PlayerDataContainer playerData;
			if(plugin.hasDataContainer(player.getName())){
				playerData = plugin.getPlayerData(player.getName());
			}else{
				playerData = plugin.createPlayerData(player.getName());
			}
			playerData.setPvPTimeout(plugin.getTagDuration());
			return true;
		}
		return false;
	}
	
	/**
	 * Tags player with name
	 * @param string
	 */
	public void tagPlayer(String playerName){
		PlayerDataContainer playerData;
		if(plugin.hasDataContainer(playerName)){
			playerData = plugin.getPlayerData(playerName);
		}else{
			playerData = plugin.createPlayerData(playerName);
		}
		playerData.setPvPTimeout(plugin.getTagDuration());
	}
}
