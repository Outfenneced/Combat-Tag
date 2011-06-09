package com.WildAmazing.marinating.CombatTag;

import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.*;

import com.WildAmazing.marinating.CombatTag.CombatTagPlayerListener.CombatTagRunnable;



/**
 * Handle events for all Player related events
 * @author <Your Name>
 */
public class CombatTagPlayerListener extends PlayerListener {
    private final CombatTag plugin;

    public CombatTagPlayerListener(CombatTag instance) {
        plugin = instance;
    }
	@Override
    public void onPlayerJoin(PlayerJoinEvent e){
    	Player p = e.getPlayer();
    	if (!plugin.isinPlayerList(p.getName()))
    	{
    		plugin.logit("player added to combat tag.");
    		plugin.addtoPCC(p);//Add new player to Playerlist
    	}
    	
    	PlayerCombatClass PlrComClass = plugin.getPCC(p.getName());
    	if(PlrComClass.hasPvplogged())//Check to see if the player has pvp logged on current session
    	{
    		plugin.killAndClean(p);//Kill player and clear inventory
    		if(PlrComClass.isTagged())
    		{
    			PlayerCombatClass tagger = plugin.getPCC(PlrComClass.getTaggedBy());
    			plugin.getServer().broadcastMessage(ChatColor.LIGHT_PURPLE+"[CombatTag] "+ChatColor.RED+p.getName()+ChatColor.GOLD+" was executed for logging off" +
    			" while in combat with " + ChatColor.RED + PlrComClass.getTaggedBy());
    			tagger.removeFromTaggedPlayers(PlrComClass.getPlayerName());
    		}
    		else
    		{
    			plugin.getServer().broadcastMessage(ChatColor.LIGHT_PURPLE+"[CombatTag] "+ChatColor.RED+p.getName()+ChatColor.GOLD+" was executed for logging off" +
    			" while in combat.");
    		}
    		PlrComClass.removeTaggedBy();//Removes player from all tagged lists
   			PlrComClass.setPvplogged(false);
    	}
    	else if(plugin.checkpvplogger(p.getName()))// Check to see if the player has pvp logged after reload or restart (from file)
    	{
    		plugin.killAndClean(p);
    		plugin.logit("plugins have been reloaded and " + p.getName() + "is in pvploggers file");
			plugin.removepvplogger(p.getName());
			plugin.getServer().broadcastMessage(ChatColor.LIGHT_PURPLE+"[CombatTag] "+ChatColor.RED+p.getName()+ChatColor.GOLD+" was executed for logging off" +
			" while in combat.");
    	}		    		
    	else
    	{
    		//Player has done nothing wrong, ignore them.
    	}
    	return;
	}
	
	@Override
    public void onPlayerQuit(PlayerQuitEvent e){
    	//try {
    	final Player quitter = e.getPlayer();//Player quitting
    	final PlayerCombatClass CCQuitter = plugin.getPCC(quitter.getName());// CombatClass of quitter
    	if(CCQuitter.isTagged())// Checks to see if the player is tagged otherwise does nothing
    	{
    		if(!(CCQuitter.tagExpired()))
    		{
    			plugin.logit(CCQuitter.getPlayerName() + " logged out within the tag period");
    			CCQuitter.setItems(quitter);//Save items before logout
    			if(!(CCQuitter.hasScheduledtask()))
    			{
    				plugin.logit("Task scheduled");
    				CCQuitter.setScheduledtask(true);
    				//To be implemented
    				/*
    				if(e.getQuitMessage() == "disconnect.quitting")//Player intended to logout use standard graceperiod
    				{
    					plugin.logit("Regular disconnect using regular period");
    					CCQuitter.setGracePeriod(plugin.getGracePeriod());
    				}
    				else if(e.getQuitMessage() == "disconnect.endOfStream")//Player did not intend to logout extend graceperiod
    				{
    					plugin.logit("EOS disconnect using extended time");
    					CCQuitter.setGracePeriod(plugin.getExtendedGracePeriod());
    				}
    				*/
    				CCQuitter.setGracePeriod(plugin.getGracePeriod());
    				if(!((plugin.getServer().getPlayer(CCQuitter.getTaggedBy())) == null))
    				{
    					plugin.getServer().getPlayer(CCQuitter.getTaggedBy()).sendMessage(ChatColor.LIGHT_PURPLE+ "[CombatTag] "+ ChatColor.RED+CCQuitter.getPlayerName() + ChatColor.GOLD + " has " + (plugin.getGracePeriod()/1000) + " seconds to relog.");
    				}
    				
    				CCQuitter.setTasknumber(plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,Runnable r = new CombatTagRunnable(CCQuitter.getTaggedBy()));
    				{
    					r.run();
    					
    				}, (CCQuitter.getGracePeriod()/50)));
    			}
    			else
    			{
    				plugin.logit(CCQuitter.getPlayerName() +" already has a scheduled task. doing nothing.");
    			}
    				
    		}
    		else
    		{
    			//do nothing player still has time to logout
    		}
    	}
    	else
    	{
    		plugin.logit(quitter.getName() + " is not tagged, doing nothing.");
    		//Player is not tagged. Do nothing.
    	}
    	}

	
	@Override
    public void onPlayerKick(PlayerKickEvent e)//Remove player from tagged list and cancel scheduled task 
	{
		plugin.logit("Start of Player kick");
		Player Plr = e.getPlayer();
		plugin.logit("Got player");
		PlayerCombatClass PlrKicked = plugin.getPCC(Plr.getName());
		if(PlrKicked.isTagged())//Check to see if PlrKicked has been tagged, Otherwise do nothing
		{
			plugin.logit("getting tagger");
			PlayerCombatClass Tagger = plugin.getPCC(PlrKicked.getTaggedBy());
			plugin.logit("set plrkicked.taggedby to null");
			PlrKicked.removeTaggedBy();//Set tagged by to null
			plugin.logit("removing tagger from tagged players");
			Tagger.removeFromTaggedPlayers(PlrKicked.getPlayerName());//Remove PlrKicked from the taggers list of tagged players
			plugin.logit("setpvplogged to false");
			PlrKicked.setPvplogged(false);// Should not make any difference but is there for good measure
			plugin.logit("set grace and tag past");
			PlrKicked.setGraceAndTagPast();
			
			if(PlrKicked.hasScheduledtask())//Check to see if the player has scheduled task
			{
				plugin.logit("Plrkicked has scheduled task");
				PlrKicked.setScheduledtask(false);
				plugin.getServer().getScheduler().cancelTask(PlrKicked.getTasknumber());//Cancel the players task
				
			}
		}
		else
		{
			plugin.logit("Player is not tagged, do nothing.");
		}
		return;
	}
	
	
    @Override
    public void onPlayerRespawn(PlayerRespawnEvent e)
    {
    	Player p = e.getPlayer();
    	PlayerCombatClass PlrRespawn = plugin.getPCC(p.getName());
    	if(PlrRespawn.isTagged())
    	{
    		PlayerCombatClass Tagger = plugin.getPCC(PlrRespawn.getTaggedBy());
    		Tagger.removeFromTaggedPlayers(PlrRespawn.getPlayerName()); //Remove PlrRespawn from the tagger
    		PlrRespawn.removeTaggedBy();//Removes tagger from this player
    		PlrRespawn.removeAllTaggedPlayers();// Removes all players from the respawning players tagged list
    		PlrRespawn.setGraceAndTagPast();//Sets the time of expiration for grace and tag -1 and -2 millis in the past respectively 
    		if(PlrRespawn.hasScheduledtask())//Check to see if the player has a scheduled task
    		{
    			PlrRespawn.setScheduledtask(false);
    			plugin.getServer().getScheduler().cancelTask(PlrRespawn.getTasknumber()); //Cancels the scheduled task for said player
    		}
    	}
    	return;
    }
    
    
    public class CombatTagRunnable implements Runnable
    {
     protected String name;
     public CombatTagRunnable(String Playername) {
		name = Playername;
	}
     @Override
     public void run()
     {
    	 	PlayerCombatClass CCQuitter = plugin.getPCC(name);
			CCQuitter.setScheduledtask(false);				
			if (plugin.getServer().getPlayer(CCQuitter.getPlayerName()) == null)//if not back by the time the grace period has ended
			{
				plugin.logit(CCQuitter.getPlayerName() + " did not make it back online before the required time");
				if(CCQuitter.isTagged())
				{
					CCQuitter.setPvplogged(true);// Set pvp logged to true to indicate player needs to be dealt with on login
					if (plugin.getPenalty().equals("DEATH"))// Checks penalty for pvp logging
					{
						if (plugin.getInventoryClear())// Checks to see if winner receives items
						{
							plugin.dropitemsandclearPCCitems(CCQuitter.getTaggedBy(), CCQuitter.getPlayerName());//Drops pvp loggers inventory at the taggers feet.
						}
					}
				}
			}
			else//Player is online
				//Nagging feeling I'm missing something here
			{
				CCQuitter.removeTaggedBy();//Removes player that has tagged this player
				plugin.getPCC(CCQuitter.getTaggedBy()).removeFromTaggedPlayers(CCQuitter.getPlayerName());// Gets tagger and removes CCQuitter from taggers tagged list
			}
		}
    }
}

