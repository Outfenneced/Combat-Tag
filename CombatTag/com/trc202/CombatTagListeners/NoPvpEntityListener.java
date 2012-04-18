package com.trc202.CombatTagListeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import com.trc202.CombatTag.CombatTag;
import com.trc202.Containers.PlayerDataContainer;
import com.trc202.Containers.Settings;

public class NoPvpEntityListener implements Listener{

	CombatTag plugin;
	
	public NoPvpEntityListener(CombatTag combatTag){
		this.plugin = combatTag;
	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamage(EntityDamageEvent EntityDamaged){
		if (EntityDamaged.isCancelled() || (EntityDamaged.getDamage() == 0)){return;}
		if (EntityDamaged instanceof EntityDamageByEntityEvent){
    		EntityDamageByEntityEvent e = (EntityDamageByEntityEvent)EntityDamaged;
    		Entity dmgr = e.getDamager();
    		if(dmgr instanceof Projectile)
    		{
    			dmgr = ((Projectile)dmgr).getShooter();
    		}
    		if ((dmgr instanceof Player) && (e.getEntity() instanceof Player)){//Check to see if the damager and damaged are players
    			Player damager = (Player) dmgr;
    			Player tagged = (Player) e.getEntity();
    			if(damager != tagged && damager != null){
    				for(String disallowedWorlds : plugin.settings.getDisallowedWorlds()){
    					if(damager.getWorld().getName().equalsIgnoreCase(disallowedWorlds)){
    						//Skip this tag the world they are in is not to be tracked by combat tag
    						return;
    					}
    				}
    				boolean isInCombatDamager = false;
    				if(plugin.hasDataContainer(damager.getName())){
    					PlayerDataContainer containerDamager = plugin.getPlayerData(damager.getName());
    					isInCombatDamager = !containerDamager.hasPVPtagExpired();
    				}
    				boolean isInCombatTagged = false;
    				if(plugin.hasDataContainer(tagged.getName())){
    					if(plugin.npcm.isNPC(tagged)){
    						isInCombatTagged = true;
    					} else{
    						PlayerDataContainer containerTagged = plugin.getPlayerData(tagged.getName());
    						isInCombatTagged = !containerTagged.hasPVPtagExpired();
    					}
    				}
    				if(plugin.settings.isSendMessageWhenTagged() && !isInCombatTagged && !isInCombatDamager){
    					damager.sendMessage(ChatColor.RED + "[CombatTag] You are now in combat. Type /ct to check your  remaining tag time.");
    					tagged.sendMessage(ChatColor.RED + "[CombatTag] You are now in combat. Type /ct to check your  remaining tag time.");
    				}
    				if(plugin.settings.getCurrentMode() == Settings.SettingsType.NPC){
	    				onPlayerDamageByPlayerNPCMode(damager,tagged);
    				}else if(plugin.settings.getCurrentMode() == Settings.SettingsType.TIMED){
    					//onPlayerDamageByPlayerTimedMode(damager,tagged);
    				}
    			}
    		}
		}
	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDeath(EntityDeathEvent event){
		if(plugin.npcm.isNPC(event.getEntity())){
			onNPCDeath(event.getEntity());
		}
		//if Player died with a tag duration, cancel the timeout and remove the data container
		else if(event.getEntity() instanceof Player){
			Player deadPlayer = (Player) event.getEntity();
			onPlayerDeath(deadPlayer);
		}
	}
	
	public void onNPCDeath(Entity entity){
		if(plugin.hasDataContainer(plugin.getPlayerName(entity))){
			plugin.killPlayerEmptyInventory(plugin.getPlayerData(plugin.getPlayerName(entity)));
		}
	}
	
	private void onPlayerDeath(Player deadPlayer){
		if(plugin.hasDataContainer(deadPlayer.getName())){
			PlayerDataContainer deadPlayerData = plugin.getPlayerData(deadPlayer.getName());
			deadPlayerData.setPvPTimeout(0);
			plugin.removeDataContainer(deadPlayer.getName());
		}
	}
	
	private void onPlayerDamageByPlayerNPCMode(Player damager, Player damaged){
		if(plugin.npcm.isNPC(damaged)){return;} //If the damaged player is an npc do nothing
		PlayerDataContainer damagerData;
		PlayerDataContainer damagedData;
		if(!damager.hasPermission("combattag.ignore")){	
			//Get damager player data container
			if(plugin.hasDataContainer(damager.getName())){damagerData = plugin.getPlayerData(damager.getName());
			}else{damagerData = plugin.createPlayerData(damager.getName());}
			damagerData.setPvPTimeout(plugin.getTagDuration());
		}
		if(!damaged.hasPermission("combattag.ignore")){	
			//Get damaged player data container
			if(plugin.hasDataContainer(damaged.getName())){damagedData = plugin.getPlayerData(damaged.getName());
			}else{damagedData = plugin.createPlayerData(damaged.getName());}
			damagedData.setPvPTimeout(plugin.getTagDuration());
		}
		if(plugin.isDebugEnabled()){plugin.log.info("[CombatTag] Player tagged another player, setting pvp timeout");}
	}
/**	
	private void onPlayerDamageByPlayerTimedMode(Player damager, Player tagged) {
		if(plugin.npcm.isNPC(tagged)){return;} //If the damaged player is an npc do nothing
		PlayerDataContainer damagerData;
		PlayerDataContainer damagedData;
		if(!damager.hasPermission("combattag.ignore")){	
			if(plugin.hasDataContainer(damager.getName())){
				damagerData = plugin.getPlayerData(damager.getName());
			}else{damagerData = plugin.createPlayerData(damager.getName());}
			damagerData.setPvPTimeout(plugin.settings.getTagDuration());
		}
		if(!tagged.hasPermission("combattag.ignore")){
			if(plugin.hasDataContainer(tagged.getName())){
				damagedData = plugin.getPlayerData(tagged.getName());
			}else{damagedData = plugin.createPlayerData(tagged.getName());}
			damagedData.setPvPTimeout(plugin.settings.getTagDuration());
		}
	}
**/
	
}