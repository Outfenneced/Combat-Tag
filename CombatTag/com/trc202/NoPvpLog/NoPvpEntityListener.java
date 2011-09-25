package com.trc202.NoPvpLog;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityListener;

public class NoPvpEntityListener extends EntityListener{

	NoPvpLog plugin;
	
	public NoPvpEntityListener(NoPvpLog plugin)
	{
		this.plugin = plugin;
	}
	
	public void onEntityDamage(EntityDamageEvent EntityDamaged)
	{
		if (EntityDamaged.isCancelled())//Check if the damage event is canceled
		{
			return;
		}
		if (EntityDamaged.getCause() == DamageCause.ENTITY_ATTACK)
		{
    		if (EntityDamaged instanceof EntityDamageByEntityEvent)
    		{
	    		EntityDamageByEntityEvent e = (EntityDamageByEntityEvent)EntityDamaged;
	    		if ((e.getDamager() instanceof Player) && (e.getEntity() instanceof Player))//Check to see if the damager and damaged are players
	    		{
	    			Player tagged = (Player) e.getEntity();
	    			PlayerDataContainer taggedData;
	    			if(plugin.hasDataContainer(tagged.getName()))
	    			{
	    				taggedData = plugin.getPlayerData(tagged.getName());
	    			}
	    			else
	    			{
	    				taggedData = plugin.createPlayerData(tagged.getName());
	    			}
	    			taggedData.setPvPTimeout(plugin.getTagDuration());
	    		}
    		}
		}
	}
		
	public void onEntityDeath(EntityDeathEvent event)
	{
		if(plugin.npcm.isNPC(event.getEntity()))
		{
			Player deadNPC = (Player) event.getEntity();
			if(plugin.hasDataContainer(deadNPC.getName()))
			{
				plugin.killPlayerEmptyInventory(plugin.getPlayerData(deadNPC.getName()));
			}
		}
		else if(event.getEntity() instanceof Player)
		{
			Player deadPlayer = (Player) event.getEntity();
			if(plugin.hasDataContainer(deadPlayer.getName()))
			{
				PlayerDataContainer deadPlayerData = plugin.getPlayerData(deadPlayer.getName());
				deadPlayerData.setPvPTimeout(0);
			}
		}
	}
}
