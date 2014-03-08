package com.trc202.CombatTagListeners;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
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
    		if ((dmgr instanceof Player) && (e.getEntity() instanceof Player) && plugin.settings.playerTag()){//Check to see if the damager and damaged are players
    			Player damager = (Player) dmgr;
    			Player tagged = (Player) e.getEntity();
    			if(damager != tagged && damager != null){
    				for(String disallowedWorlds : plugin.settings.getDisallowedWorlds()){
    					if(damager.getWorld().getName().equalsIgnoreCase(disallowedWorlds)){
    						//Skip this tag the world they are in is not to be tracked by combat tag
    						return;
    					}
    				}
	    			onPlayerDamageByPlayerNPCMode(damager,tagged);
    			}
    		} else if ((dmgr instanceof LivingEntity) && (e.getEntity() instanceof Player) && plugin.settings.mobTag()){
    			LivingEntity damager = (LivingEntity) dmgr;
    			Player tagged = (Player) e.getEntity();
    			if(damager != tagged && damager != null){
    				for(String disallowedWorlds : plugin.settings.getDisallowedWorlds()){
    					if(damager.getWorld().getName().equalsIgnoreCase(disallowedWorlds)){
    						//Skip this tag the world they are in is not to be tracked by combat tag
    						return;
    					}
    				}
	    			onPlayerDamageByMobNPCMode(damager,tagged);
    			}
    		}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDeath(EntityDeathEvent event){
		if(plugin.npcm.isNPC(event.getEntity())){
			onNPCDeath(event.getEntity());
		}
		//if Player died with a tag duration, cancel the timeout and remove the data container
		else if(event.getEntity() instanceof Player){
			onPlayerDeath((Player) event.getEntity());
		}
	}
	
	public void onNPCDeath(Entity entity){
		String id = plugin.getPlayerName(entity);
		NPC npc = plugin.npcm.getNPC(id);
		plugin.updatePlayerData(npc, id);
		plugin.removeTagged(id);
	}
	
	public void onPlayerDeath(Player deadPlayer){
		plugin.removeTagged(deadPlayer.getName());
	}
	
	private void onPlayerDamageByPlayerNPCMode(Player damager, Player damaged){
		if(plugin.npcm.isNPC(damaged)){return;} //If the damaged player is an npc do nothing
		
		if(plugin.ctIncompatible.WarArenaHook(damager) && plugin.ctIncompatible.WarArenaHook(damaged)){
			if(!damager.hasPermission("combattag.ignore") && (damager.getGameMode() != GameMode.CREATIVE)){	
				if(plugin.settings.isSendMessageWhenTagged() && !plugin.isInCombat(damager.getName())){
					String tagMessage = plugin.settings.getTagMessageDamager();
					tagMessage = tagMessage.replace("[player]", "" + damaged.getName());
					damager.sendMessage(ChatColor.RED + "[CombatTag] " + tagMessage);
				}
				if(plugin.isDebugEnabled()){
					plugin.log.info("[CombatTag] " + damager.getName() + " tagged " + damaged.getName() + ", setting pvp timeout");
				}
				plugin.addTagged(damager);
			}
			if(!damaged.hasPermission("combattag.ignore") && !plugin.settings.onlyDamagerTagged()){	
				if(!plugin.isInCombat(damaged.getName())){
					if(plugin.settings.isSendMessageWhenTagged()){
						String tagMessage = plugin.settings.getTagMessageDamaged();
						tagMessage = tagMessage.replace("[player]", damager.getName());
						damaged.sendMessage(ChatColor.RED + "[CombatTag] " + tagMessage);
					}
				}
				plugin.addTagged(damaged);
			}
		}
	}
	
	private void onPlayerDamageByMobNPCMode(LivingEntity damager, Player damaged) {
		if(plugin.npcm.isNPC(damaged)){return;} //If the damaged player is an npc do nothing
		if(damager == null){return;}
		if(plugin.ctIncompatible.WarArenaHook(damaged)){
			if(!damaged.hasPermission("combattag.ignoremob")){	
				if(!plugin.isInCombat(damaged.getName())){
					if(plugin.settings.isSendMessageWhenTagged()){
						String tagMessage = plugin.settings.getTagMessageDamaged();
						tagMessage = tagMessage.replace("[player]", damager.getType().name());
						damaged.sendMessage(ChatColor.RED + "[CombatTag] " + tagMessage);
					}
					if(plugin.isDebugEnabled()){
						plugin.log.info("[CombatTag] " + damager.getType().name() + " tagged " + damaged.getName() + ", setting pvp timeout");
					}
				}
				plugin.addTagged(damaged);
			}
		}
	}
}