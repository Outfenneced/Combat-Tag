package com.WildAmazing.marinating.CombatTag;
import org.bukkit.ChatColor;
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
				    	if(!(damager.getName() == damaged.getName()))//Check to make sure the player did not tag themselves
				    	{
				    		PlayerCombatClass PCCdamager = plugin.getPCC(damager.getName());//retrieve the Player combat class for damager
				    		PlayerCombatClass PCCdamaged = plugin.getPCC(damaged.getName());//retrieve the Player combat class for damaged
				    		if(PCCdamaged.isTagged())//Check to see if damaged is tagged
				    		{
				    			if(!(PCCdamaged.getTaggedBy() == PCCdamager.getPlayerName()))//Check to see if the damaged is already tagged by damager if damaged is do nothing
				    			{
				    				PlayerCombatClass otherplr = plugin.getPCC(PCCdamaged.getTaggedBy());//Get previous tagger
				    				otherplr.removeFromTaggedPlayers(PCCdamaged.getPlayerName());//Only one player can tag damaged at a time. Remove from other players tagged list
				    				if(PCCdamaged.hasScheduledtask())// If PCCdamaged has a scheduled task cancel it
				    				{
				    					PCCdamaged.setScheduledtask(false);
				    					plugin.getServer().getScheduler().cancelTask(PCCdamaged.getTasknumber());				    								    					
				    				}
				    				plugin.configureTaggerAndTagged(PCCdamager, PCCdamaged);//Sets up damager and damaged appropriately 
				    				damager.sendMessage(ChatColor.LIGHT_PURPLE +"[CombatTag] " +ChatColor.GOLD + "Tagged: " + ChatColor.RED +PCCdamaged.getPlayerName());
				    				damaged.sendMessage(ChatColor.LIGHT_PURPLE +"[CombatTag] " + ChatColor.GOLD + "Tagged by : " + ChatColor.RED + PCCdamager.getPlayerName());
				    				damaged.sendMessage(ChatColor.GOLD + "StdGracePeriod: " + plugin.getGracePeriod()/1000 + " seconds" );
				    				damaged.sendMessage(ChatColor.GOLD + "/ct for more info.");
				    			}
				    			else
				    			{
				    				plugin.configureTaggerAndTagged(PCCdamager, PCCdamaged);//Reset the graceperiod and tagduration	
				    			}
				    		
				    		}
				    		else//Player has been tagged. setup appropately 
				    		{
				    			plugin.configureTaggerAndTagged(PCCdamager, PCCdamaged);//Sets up damager and damaged appropriately 
				    			damager.sendMessage(ChatColor.LIGHT_PURPLE +"[CombatTag] " +ChatColor.GOLD + "Tagged: " + ChatColor.RED +PCCdamaged.getPlayerName());
			    				damaged.sendMessage(ChatColor.LIGHT_PURPLE +"[CombatTag] " + ChatColor.GOLD + "Tagged by : " + ChatColor.RED + PCCdamager.getPlayerName());
			    				damaged.sendMessage(ChatColor.GOLD + "StdGracePeriod: " + plugin.getGracePeriod()/1000 + " seconds" );
			    				damaged.sendMessage(ChatColor.GOLD + "/ct for more info.");
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
