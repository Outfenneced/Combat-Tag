package com.trc202.CombatTagApi;

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
		if(plugin.hasDataContainer(player)){
			PlayerDataContainer container = plugin.getPlayerData(player);
			isInCombat = !container.hasPVPtagExpired();
		}
		return isInCombat;
	}
	
	public boolean isInCombat(Player player){
		return isInCombat(player.getName());
	}
}
