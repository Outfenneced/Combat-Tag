package com.trc202.CombatTagListeners;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerListener;

import com.trc202.CombatTag.CombatTag;

public class CombatTagCommandPrevention extends PlayerListener{
	
	CombatTag plugin;
	
	public CombatTagCommandPrevention(CombatTag plugin){
		this.plugin = plugin;
	}
	
	@Override
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
    {
		if(event.isCancelled()) return;
		Player player = event.getPlayer();
		if(plugin.hasDataContainer(player.getName()) && !plugin.getPlayerData(player.getName()).hasPVPtagExpired()){
			for(String disabledCommand : plugin.settings.getDisabledCommands()){
				if(event.getMessage().contains(disabledCommand)){
					player.sendMessage("This command is disabled while in combat");
					event.setCancelled(true);
					break;
				}
			}
		}else if(plugin.hasDataContainer(player.getName()) && plugin.getPlayerData(player.getName()).hasPVPtagExpired()){
			plugin.removeDataContainer(player.getName());
		}
	}

}
