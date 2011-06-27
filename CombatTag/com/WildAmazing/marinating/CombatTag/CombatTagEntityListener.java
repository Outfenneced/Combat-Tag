package com.WildAmazing.marinating.CombatTag;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityListener;

public class CombatTagEntityListener extends EntityListener {
	    private final CombatTag plugin;

	    public CombatTagEntityListener(CombatTag instance) {
	        plugin = instance;
	    }
		public void onEntityDamage(EntityDamageEvent EntityDamaged)
		{
	    	if (EntityDamaged.getCause() == DamageCause.ENTITY_ATTACK){
	    		EntityDamageByEntityEvent e = (EntityDamageByEntityEvent)EntityDamaged;
	    		if (e.getDamager() instanceof Player && e.getEntity() instanceof Player)//Check to see if the damager and damaged are players
	    		{
	    			if (!e.isCancelled())//Check if the damage event is canceled
	    			{
	    				plugin.logit("Damage done by player to player.");
	    				Player damager = (Player)e.getDamager();
				    	Player damaged = (Player)e.getEntity();
				    	plugin.logit("damager is " + damager.getName());
				    	plugin.logit("damaged is " + damaged.getName());
				    	// This should not be needed but it appears data has been lost elsewhere in the program. I have yet to be able to reproduce the bug
				    	if (!plugin.isinPlayerList(damager.getName()))
				    	{
				    		plugin.logit("player added to combat tag in entity listener.");
				    		plugin.addtoPCC(damager);//Add new player to Playerlist
				    	}
				    	if (!plugin.isinPlayerList(damaged.getName()))
				    	{
				    		plugin.logit("player added to combat tag in entity listener.");
				    		plugin.addtoPCC(damaged);//Add new player to Playerlist
				    	}
				    	if(!(damager.getName() == damaged.getName()))//Check to make sure the player did not tag themself
				    	{
				    		PlayerCombatClass PCCdamager = plugin.getPCC(damager.getName());//retrieve the Player combat class for damager
				    		PlayerCombatClass PCCdamaged = plugin.getPCC(damaged.getName());//retrieve the Player combat class for damaged
				    		if(PCCdamaged.isTagged())//Check to see if damaged is tagged
				    		{
				    			if(!(PCCdamaged.getTaggedBy() == PCCdamager.getPlayerName()))//Check to see if the damaged is already tagged by damager 
				    			{
				    				PlayerCombatClass otherplr = plugin.getPCC(PCCdamaged.getTaggedBy());//Get previous tagger
				    				otherplr.removeFromTaggedPlayers(PCCdamaged.getPlayerName());//Only one player can tag damaged at a time. Remove from other players tagged list
				    				if(PCCdamaged.hasScheduledtask())// If PCCdamaged has a scheduled task cancel it (should not be possible now as scheduled task are removed on login. (need to double check that before I remove this)
				    				{
				    					PCCdamaged.setScheduledtask(false);
				    					plugin.getServer().getScheduler().cancelTask(PCCdamaged.getTasknumber());				    								    					
				    				}
				    				plugin.configureTaggerAndTagged(PCCdamager, PCCdamaged);//Sets up damager and damaged appropriately 
				    				plugin.sendmessagetoDamagerandDamaged(damager, damaged);

				    			}
				    			else //Player has already been tagged by this person silently reset tag time
				    			{
				    				plugin.configureTaggerAndTagged(PCCdamager, PCCdamaged);//Reset the graceperiod and tagduration	
				    			}
				    		
				    		}
				    		else//Player has been tagged. setup appropately (restart tag time)
				    		{
				    			plugin.configureTaggerAndTagged(PCCdamager, PCCdamaged);//Sets up damager and damaged appropriately 
				    			plugin.sendmessagetoDamagerandDamaged(damager, damaged);
				    		}
				    	}
				    }
				    else
				    {
				    	plugin.logit("Do nothing Damage event is cancled");
				    }
	    		}
	    	}
	    	return;
	    }
		}
