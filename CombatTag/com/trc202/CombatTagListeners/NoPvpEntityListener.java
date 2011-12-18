package com.trc202.CombatTagListeners;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityListener;
import com.trc202.CombatTag.CombatTag;
import com.trc202.Containers.PlayerDataContainer;
import com.trc202.Containers.Settings;

public class NoPvpEntityListener extends EntityListener{

	CombatTag plugin;
	
	public NoPvpEntityListener(CombatTag combatTag){
		this.plugin = combatTag;
	}
	
	public void onEntityDamage(EntityDamageEvent EntityDamaged){
		if (EntityDamaged.isCancelled()){//Check if the damage event is canceled
			return;
		}
		if (EntityDamaged.getCause() == DamageCause.ENTITY_ATTACK){
    		if (EntityDamaged instanceof EntityDamageByEntityEvent){
	    		EntityDamageByEntityEvent e = (EntityDamageByEntityEvent)EntityDamaged;
	    		if ((e.getDamager() instanceof Player) && (e.getEntity() instanceof Player)){//Check to see if the damager and damaged are players
	    			Player damager = (Player) e.getDamager();
	    			Player tagged = (Player) e.getEntity();
	    			if(plugin.settings.getCurrentMode() == Settings.SettingsType.NPC){
		    			if(!plugin.npcm.isNPC(e.getEntity())){
			    			PlayerDataContainer taggedData;
			    			if(plugin.hasDataContainer(tagged.getName())){
			    				taggedData = plugin.getPlayerData(tagged.getName());
			    			}
			    			else{
			    				taggedData = plugin.createPlayerData(tagged.getName());
			    			}
			    			if(plugin.isDebugEnabled()){plugin.log.info("[CombatTag] Player tagged another player, setting pvp timeout");}
			    			taggedData.setPvPTimeout(plugin.getTagDuration());
		    			}
	    			}else if(plugin.settings.getCurrentMode() == Settings.SettingsType.OTHER){
	    				
	    			}

	    		}
    		}
		}
	}
		
	public void onEntityDeath(EntityDeathEvent event){
		if(plugin.npcm.isNPC(event.getEntity())){
			if(plugin.hasDataContainer(plugin.getPlayerName(event.getEntity()))){
				plugin.killPlayerEmptyInventory(plugin.getPlayerData(plugin.getPlayerName(event.getEntity())));
			}
		}
		//if Player died with a tag duration, cancel the timeout and remove the data container
		else if(event.getEntity() instanceof Player){
			Player deadPlayer = (Player) event.getEntity();
			if(plugin.hasDataContainer(deadPlayer.getName())){
				PlayerDataContainer deadPlayerData = plugin.getPlayerData(deadPlayer.getName());
				deadPlayerData.setPvPTimeout(0);
				plugin.removeDataContainer(deadPlayer.getName());
			}
		}
	}
	
	
}