package com.trc202.CombatTagApi;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.trc202.CombatTag.CombatTag;

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
	public boolean isInCombat(String name){
		return plugin.isInCombat(name);
	}
	
	/**
	 * Returns the time before the tag is over
	 *  -1 if the tag has expired
	 *  -2 if the player is not in combat
	 * @param name
	 */
	public long getRemainingTagTime(String name){
		if(plugin.isInCombat(name)){
			return plugin.getRemainingTagTime(name);
		}else{
			return -1L;
		}
	}
	
	/**
	 * Returns if the entity is an NPC
	 * @param entity
	 * @return true if the player is an NPC
	 */
	public boolean isNPC(Entity entity){
		if(plugin.npcm.isNPC(entity)){return true;}
		return false;
	}
	
	/**
	 * Tags player
	 * @param player
	 * @return true if the action is successful, false if not
	 */
	public boolean tagPlayer(Player player){
		return plugin.addTagged(player);
	}
	
	
	/**
	 * Untags player
	 * @param player
	 * @return nothing
	 */
	public void untagPlayer(Player player){
		plugin.removeTagged(player.getName());
	}
	
	/**
	 * Returns the value of a configuration option with the specified name
	 * @param Name of config option
	 * @return String value of option
	 */
	public String getConfigOption(String configKey){
		return plugin.getSettingsHelper().getProperty(configKey);
	}
}
