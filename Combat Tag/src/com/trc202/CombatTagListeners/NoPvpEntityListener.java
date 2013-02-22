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

import com.topcat.npclib.entity.NPC;
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
    				if(plugin.settings.getCurrentMode() == Settings.SettingsType.NPC){
	    				onPlayerDamageByPlayerNPCMode(damager,tagged);
    				}else if(plugin.settings.getCurrentMode() == Settings.SettingsType.TIMED){
    					//onPlayerDamageByPlayerTimedMode(damager,tagged);
    				}
    			}
    		}
		}
	}
	@EventHandler(priority = EventPriority.LOWEST)
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
			String id = plugin.getPlayerName(entity);
			NPC npc = plugin.npcm.getNPC(id);
			plugin.updatePlayerData(npc, id);
		}
	}
	
	public void onPlayerDeath(Player deadPlayer){
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
		
		if(plugin.ctIncompatible.notInArena(damaged,damager)){
			if(!damager.hasPermission("combattag.ignore")){	
				//Get damager player data container
				if(plugin.hasDataContainer(damager.getName())){
					damagerData = plugin.getPlayerData(damager.getName());
				}else{
					damagerData = plugin.createPlayerData(damager.getName());
				}
				if(plugin.settings.isSendMessageWhenTagged() && !plugin.npcm.isNPC(damaged) && damagerData.hasPVPtagExpired()){
					String tagMessage = plugin.settings.getTagMessageDamager();
					tagMessage = tagMessage.replace("[player]", "" + damaged.getName());
					damager.sendMessage(ChatColor.RED + "[CombatTag] " + tagMessage);
				}
				damagerData.setPvPTimeout(plugin.getTagDuration());
			}
			if(!damaged.hasPermission("combattag.ignore") && !plugin.settings.onlyDamagerTagged()){	
				//Get damaged player data container
				if(plugin.hasDataContainer(damaged.getName())){
					damagedData = plugin.getPlayerData(damaged.getName());
				}else{
					damagedData = plugin.createPlayerData(damaged.getName());
				}
				if(plugin.settings.isSendMessageWhenTagged() && !plugin.npcm.isNPC(damaged) && damagedData.hasPVPtagExpired()){
					String tagMessage = plugin.settings.getTagMessageDamaged();
					tagMessage = tagMessage.replace("[player]", damager.getName());
					damaged.sendMessage(ChatColor.RED + "[CombatTag] " + tagMessage);
				}
				if(plugin.isDebugEnabled() && !plugin.npcm.isNPC(damaged) && damagedData.hasPVPtagExpired()){
					plugin.log.info("[CombatTag] " + damager.getName() + " tagged " + damaged.getName() + ", setting pvp timeout");
				}
				damagedData.setPvPTimeout(plugin.getTagDuration());
			}
		}
	}
}