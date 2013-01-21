package com.trc202.CombatTagListeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;


import com.trc202.CombatTag.CombatTag;

public class CombatTagCommandPrevention implements Listener{
	
	CombatTag plugin;
	
	public CombatTagCommandPrevention(CombatTag plugin){
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
    {
		Player player = event.getPlayer();
		if(plugin.hasDataContainer(player.getName()) && !plugin.getPlayerData(player.getName()).hasPVPtagExpired()){
			String command = event.getMessage();
			if(command.indexOf(" ")>=0)
				command = command.substring(0, command.indexOf(" "));
			for(String disabledCommand : plugin.settings.getDisabledCommands()){
				if(command.equalsIgnoreCase(disabledCommand)){
					if(plugin.isDebugEnabled()){plugin.log.info("[CombatTag] Combat tag has blocked the command: " + disabledCommand + " .");}
					player.sendMessage("This command is disabled while in combat");
					event.setCancelled(true);
					return;
				}
			}
		}else if(plugin.hasDataContainer(player.getName()) && plugin.getPlayerData(player.getName()).hasPVPtagExpired()){
			plugin.removeDataContainer(player.getName());
		}
	}
}
