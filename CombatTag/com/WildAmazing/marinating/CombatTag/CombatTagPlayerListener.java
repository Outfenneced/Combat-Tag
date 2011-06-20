package com.WildAmazing.marinating.CombatTag;

import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.*;



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
    	PlrComClass.removeDisconnectType();
    	if(PlrComClass.hasPvplogged())//Check to see if the player has pvp logged on current session
    	{
    		plugin.killAndClean(p);//Kill player and clear inventory
    		if(PlrComClass.isTagged())
    		{
    			PlayerCombatClass tagger = plugin.getPCC(PlrComClass.getTaggedBy());
    			plugin.announcePvPLog(PlrComClass.getPlayerName(), PlrComClass.getTaggedBy());
    			tagger.removeFromTaggedPlayers(PlrComClass.getPlayerName());
    		}
    		else
    		{
    			plugin.announcePvPLog(p.getName());
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
    		PlayerCombatClass PlrLogin = plugin.getPCC(p.getName());
    		if(PlrLogin.isTagged())//Player is in combat
    		{
    			if(PlrLogin.hasScheduledtask())//Player is in combat
    			{//Cancel old scheduled task.
    				plugin.getServer().getScheduler().cancelTask(PlrLogin.getTasknumber());
    				PlrLogin.setScheduledtask(false);
    				PlrLogin.setTagExpiration(plugin.getTagTime());
    				p.sendMessage( ChatColor.LIGHT_PURPLE + "[CombatTag]" + ChatColor.GOLD + " Your tag time has been reset to: " + PlrLogin.tagPeriodInSeconds() + " seconds.");
    				p.sendMessage(ChatColor.LIGHT_PURPLE + "[CombatTag]" + ChatColor.GOLD + " You can relog " + new Integer (plugin.getMaxRelog()-PlrLogin.getTimesReloged()).toString() + " time(s) during this tag.");
    			}
    		}
    		
    	}
    	return;
	}
	
	@Override
    public void onPlayerQuit(PlayerQuitEvent e){
    	//try {
    	final Player quitter = e.getPlayer();//Player quitting
    	if(!(quitter.isDead()))
    	{
    		final PlayerCombatClass CCQuitter = plugin.getPCC(quitter.getName());// CombatClass of quitter
    		if(CCQuitter.isTagged())// Checks to see if the player is tagged otherwise does nothing
    		{
    			if(!(CCQuitter.tagExpired()))
    			{
    				plugin.logit(CCQuitter.getPlayerName() + " logged out within the tag period");
    				if(plugin.getPenalty().equals("DEATH"))
    				{
    					CCQuitter.setItems(quitter);//Save items before logout
    				}
    				if(CCQuitter.getTimesReloged() >= plugin.getMaxRelog())
    				{
    					plugin.logit("Penalty is " + plugin.getPenalty() );
    					plugin.logit("Player has exceeded the maximum number of relogs allowed");
    					//Quitter has reloged more than the maximum amount in current tag. Deal with player appropriately.
    					CCQuitter.removeTimesReloged();
    					plugin.dropitemsandclearPCCitems(CCQuitter.getTaggedBy(), CCQuitter.getPlayerName());
    					CCQuitter.setPvplogged(true);
    				}
    				else if(!(CCQuitter.hasScheduledtask()))
    				{
    					plugin.logit("Task scheduled");
    					CCQuitter.setScheduledtask(true);
    					if(CCQuitter.getDisconnectType() == null)
    					{
    						plugin.logit("Disconnect type is null using default period");
    						CCQuitter.setGracePeriod(plugin.getGracePeriod());
    					}
    					else if(CCQuitter.getDisconnectType().equals("disconnect.quitting"))
    					{
    						plugin.logit("Regular disconnect. using regular period");
    						CCQuitter.setGracePeriod(plugin.getGracePeriod());
    					}
    					else if(CCQuitter.getDisconnectType().equals("disconnect.endOfStream"))
    					{
    						plugin.logit("EOS disconnect using extended time");
    						CCQuitter.setGracePeriod(plugin.getExtendedGracePeriod()); //Extended grace period here
    					}
    					else
    					{
    						plugin.logit("Could not detect disconnect type. using default.");
    						CCQuitter.setGracePeriod(plugin.getGracePeriod());
    					}
	    				CCQuitter.incrementTimesReloged();
	    				
	    				if(plugin.isPlrOnline(CCQuitter.getTaggedBy()))
	    				{
	    					plugin.getServer().getPlayer(CCQuitter.getTaggedBy()).sendMessage(ChatColor.LIGHT_PURPLE+ "[CombatTag] "+ ChatColor.RED+CCQuitter.getPlayerName() + ChatColor.GOLD + " has " + (CCQuitter.gracePeriodInSeconds()) + " seconds to relog.");
	    				}
	    				CombatTagRunnable cr =  new CombatTagRunnable(CCQuitter.getPlayerName());
	    				CCQuitter.setTasknumber(plugin.getServer().getScheduler().scheduleSyncDelayedTask(
	                            plugin, cr, (CCQuitter.getGracePeriod()/50))); // ~ 20*((cpi.getGracePeriod())/1000)
	    			}
	    			else
	    			{
	    				plugin.logit(CCQuitter.getPlayerName() +" already has a scheduled task. doing nothing.");
	    			}
	    				
	    		}
	    		else
	    		{
	    			//do nothing the tag is already expired
	    		}
	    	}
	    	else
	    	{
	    		plugin.logit(quitter.getName() + " is not tagged, doing nothing.");
	    		//Player is not tagged. Do nothing.
	    	}
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
			PlrKicked.removeTimesReloged();
			plugin.logit("removing tagger from tagged players");
			Tagger.removeFromTaggedPlayers(PlrKicked.getPlayerName());//Remove PlrKicked from the taggers list of tagged players
			plugin.logit("setpvplogged to false");
			PlrKicked.setPvplogged(false);// Should not make any difference but is there for good measure
			plugin.logit("set grace and tag past");
			//PlrKicked.setGraceAndTagPast();
			
			if(PlrKicked.hasScheduledtask())//Check to see if the player has scheduled task
			{
				plugin.logit("Plrkicked has scheduled task canceling it");
				PlrKicked.setScheduledtask(false);
				plugin.getServer().getScheduler().cancelTask(PlrKicked.getTasknumber());//Cancel the players task
				
			}
			
		}
		else
		{
			plugin.logit("Player is not tagged, do nothing.");
		}
		plugin.logit("PlrKicked.hastaggedPlayer()" + new Boolean(PlrKicked.hasTaggedPlayer()).toString());
		if(PlrKicked.hasTaggedPlayer())
		{
			ArrayList<String> Myarray = PlrKicked.getTaggedPlayers();// Temporary arraylist for deep copy
			plugin.logit("setting up iterator");
			//Make a deep copy of Myarray
		    Iterator<String> itr = Myarray.iterator(); // Setup iterator
		    ArrayList<String> backup = new ArrayList<String>();
		    while (itr.hasNext())//Check to see if there is another element in Myarray
		    {
		    	String temp = itr.next();
		    	plugin.logit(temp + "is in " + PlrKicked.getPlayerName() + "'s tagged list.");
		    	backup.add(temp);	//Copy each element in arraylist
		    }
		    //Deep copy finished
		    Iterator<String> newitr = backup.iterator();//Setup iterator
		    while(newitr.hasNext())// Check to see if there is another element in backup
		    {
		    	
		    	PlayerCombatClass PCCPlr2 = plugin.getPCC(newitr.next());//Player2 is the tagger
							plugin.logit("removing PCCPlr2 from tagged players");
							//Cause of the concurrent modification exception
							PlrKicked.removeFromTaggedPlayers(PCCPlr2.getPlayerName());//Remove Player1 from Player2's tagged list
							plugin.logit("Removing PCCPlr1's tagged by");
							PCCPlr2.removeTaggedBy();//Set Player1's tagged by to null
							PCCPlr2.removeTimesReloged();
		    }
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
    		PlrRespawn.removeTimesReloged();
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
    	 plugin.logit("Task running");
    	 	PlayerCombatClass CCQuitter = plugin.getPCC(name);
			CCQuitter.setScheduledtask(false);
			if(!(plugin.isPlrOnline(CCQuitter.getPlayerName())))//Check to see if the player is offline (continues if ofline)
				{
					plugin.logit(CCQuitter.getPlayerName() + " did not make it back online before the required time");
					if(CCQuitter.isTagged())
					{
						plugin.sendLogToConsole(CCQuitter.getPlayerName());
						plugin.updatepvploggerfile(CCQuitter.getPlayerName());
						CCQuitter.setPvplogged(true);// Set pvp logged to true to indicate player needs to be dealt with on login
						plugin.dropitemsandclearPCCitems(CCQuitter.getTaggedBy(), CCQuitter.getPlayerName());//Drops pvp loggers inventory at the taggers feet.
					}
				}
				else //Player is online
				{
					PlayerCombatClass Playertest = plugin.getPCC(CCQuitter.getTaggedBy());
					CCQuitter.removeTimesReloged();
					CCQuitter.removeTaggedBy();//Removes player that has tagged this player
					Playertest.removeFromTaggedPlayers(CCQuitter.getPlayerName());// Gets tagger and removes CCQuitter from taggers tagged list
				
				}
			}
     }
    }

