package com.trc202.NoPvpLog;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

public class nopvpPlayerListener extends PlayerListener{
	
	private final NoPvpLog plugin;
	
    public nopvpPlayerListener(NoPvpLog instance) {
        plugin = instance;
    }
    
	@Override
    public void onPlayerJoin(PlayerJoinEvent e){
		Player loginPlayer = e.getPlayer();
		if(plugin.hasDataContainer(loginPlayer.getName()))
		{
			PlayerDataContainer loginDataContainer = plugin.getPlayerData(loginPlayer.getName());
			if(loginDataContainer.hasNPC())
			{
				plugin.despawnNPC(loginDataContainer);
			}
			if(loginDataContainer.shouldBePunished())
			{
				loginPlayer.getInventory().setArmorContents(loginDataContainer.getPlayerArmor());
				loginPlayer.getInventory().setContents(loginDataContainer.getPlayerInventory());
				loginPlayer.setHealth(loginDataContainer.getHealth());
				loginPlayer.setLastDamageCause(new EntityDamageEvent(loginPlayer, DamageCause.ENTITY_EXPLOSION, 0));
				loginDataContainer.setShouldBePunished(false);
			}
		}
	}
	
	@Override
	public void onPlayerQuit(PlayerQuitEvent e)
	{
		Player quitPlr = e.getPlayer();
		if(plugin.hasDataContainer(quitPlr.getName()))
		{
			PlayerDataContainer quitDataContainer = plugin.getPlayerData(quitPlr.getName());
			if(!quitDataContainer.hasPVPTimedOut())
			{
				plugin.spawnPlayerNpc(quitPlr);
				quitDataContainer.setShouldBePunished(true);
			}
		}
	}

	

}
