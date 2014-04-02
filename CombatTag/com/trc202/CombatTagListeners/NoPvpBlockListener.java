package com.trc202.CombatTagListeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.trc202.CombatTag.CombatTag;

public class NoPvpBlockListener implements Listener{

	CombatTag plugin;
	
	public NoPvpBlockListener(CombatTag combatTag){
		this.plugin = combatTag;
	}
	
	@EventHandler
	public void onBlockBreakEvent(BlockBreakEvent event){
		if(event.isCancelled()){return;}
		Player player = event.getPlayer();
		if(plugin.isInCombat(player.getUniqueId())){
			if(!isBlockEditWhileTagged()){
				player.sendMessage(ChatColor.RED + "[Combat Tag] You can't break blocks while tagged.");
				event.setCancelled(true);
				
			}
		}
	}
	@EventHandler
	public void onBlockPlaceEvent(BlockPlaceEvent event){
		if(event.isCancelled()){return;}
		Player player = event.getPlayer();
		if(plugin.isInCombat(player.getUniqueId())){
			if(!isBlockEditWhileTagged()){
				player.sendMessage(ChatColor.RED + "[Combat Tag] You can't place blocks while tagged.");
				event.setCancelled(true);
			}
		}
	}
	
	public boolean isBlockEditWhileTagged(){
		return plugin.settings.isBlockEditWhileTagged();
	}
}
