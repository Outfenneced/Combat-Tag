package com.WildAmazing.marinating.CombatTag;

import java.util.ArrayList;

import net.minecraft.server.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerCombatClass {

	private String taggedBy; //who has tagged you
	private ArrayList<String> taggedPlayers = new ArrayList<String>(); //who you tagged
	private long tagExpirationTime; //time when you will no longer be tagged 
	private long gracePeriod; //time avalable for relog
	private String playerName; //PlayerName
	private ArrayList<ItemStack> ITEMS = new ArrayList<ItemStack>();//items in players inventory
	private boolean scheduledtask;
	private int Tasknumber;
	private boolean pvplogged;
	
	public PlayerCombatClass(Player pl)//initializes the class
	{
		setPvplogged(false);
		tagExpirationTime = 0;
		gracePeriod = 0;
		playerName = pl.getName();
		taggedBy = null;
		ITEMS = null;
		setTasknumber(0);
		setScheduledtask(false);
	}
	
	//Who this player is tagged by
	public void setTaggedBy(String PlayerName)//Sets the player that has currently tagged this player
	{
		taggedBy = PlayerName;
	}
	public String getTaggedBy()//Returns who has currently tagged this player
	{
		return taggedBy;
	}
	public boolean isTagged()
	{
		if(taggedBy == null)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	public void removeTaggedBy()//Set's tagged by to null
	{
		taggedBy = null;
	}

	//Who this player has tagged
	public void addToTaggedPlayers(String player)//Adds player to current tagged list
	{	
		taggedPlayers.add(player);
		return;
	}	
	public void removeFromTaggedPlayers(String player)//Removes player from currently tagged list
	{
		taggedPlayers.remove(player);
		return;
	}
	public ArrayList<String> getTaggedPlayers()
	{
		return taggedPlayers;
	}
	public boolean hasTaggedPlayer()
	{
		return (!taggedPlayers.isEmpty());
	}
	public boolean hasTaggedPlayer(String player)//Checks to see if the player has tagged said player
	{
		return taggedPlayers.contains(player);
	}
	public void removeAllTaggedPlayers()// Removes all players from the tagged list
	{
		taggedPlayers.clear();
		return;
	}
	public String TaggedList()//Return tagged list in string form
	{
		if(taggedPlayers.isEmpty())
		{
			return "none";
		}
		else
		{
			String tmplist = taggedPlayers.get(0);
			for(int i = 1; taggedPlayers.size() > i; i++)
			{
				tmplist = tmplist + ", " + taggedPlayers.get(i);
			}
			return tmplist;
		}
		
	}
	
	//This players name
	public void setPlayerName(String PlayerName)//Sets the Players Name
	{
		playerName = PlayerName;
	}
	public String getPlayerName()//Returns the players name
	{
		return playerName;
	}		

	//This players items
	public void setItems(Player p)// Retrieves a copy of items from a players inventory
	{	
		ArrayList<ItemStack> NewCont = new ArrayList<ItemStack>();
		ItemStack Temp[] = p.getInventory().getContents();
		for(int i = 0;p.getInventory().getSize() > i; i++)
		{
			if(!(Temp[i] == null) && !(Temp[i].getType().equals(Material.AIR)))
			{
				NewCont.add(Temp[i]);
			}
		}
		ITEMS = NewCont;
	}
	public ArrayList<ItemStack> getItems()//Returns a copy of items from the players inventory
	{
		return ITEMS;
	}
	public void clearItems()//Sets items to null
	{
		ITEMS.clear();
		return;
	}

	//This players grace period (graceperiod + systemtime)
	public void setGracePeriod(long stdGracePeriod)//Sets the grace period for a player (time avalable for relog)
	{
		gracePeriod = System.currentTimeMillis() + stdGracePeriod;
		return;
	}
	public boolean gracePeriodExpired()//If grace period is up returns true otherwise returns false
	{
		if((gracePeriod <= System.currentTimeMillis()))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	public long getGracePeriod()//returns the graceperiod remaining
	{
		return gracePeriod - System.currentTimeMillis();
	}
	public String gracePeriodInSeconds()
	{
		if(gracePeriod - System.currentTimeMillis() < 0)
		{
			return "0";
		}
		else
		{
			return String.valueOf((gracePeriod - System.currentTimeMillis())/1000);
		}
			
	}
	
	public void setGraceAndTagPast()//Sets the graceperiod and tagexpiration time in the past
	{
		gracePeriod = System.currentTimeMillis() -1;
		tagExpirationTime = System.currentTimeMillis() -2;
	}
	
	//This players tagtime (tagduration + systemtime)
	public void setTagExpiration(long stdTagPeriod)//Sets the time for tag Expiration
	{
		tagExpirationTime = stdTagPeriod + System.currentTimeMillis();
	}
	public boolean tagExpired()// If tag is expired returns true else returns false
	{
		if(tagExpirationTime <= System.currentTimeMillis())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	public String tagPeriodInSeconds()
	{
		if((tagExpirationTime - System.currentTimeMillis()) < 0)
		{
			return "Something went wrong";
		}
		else
		{
			return String.valueOf(((tagExpirationTime - System.currentTimeMillis())/1000));
		}
	}

	//This players scheduled task
	public void setScheduledtask(boolean hastask)// Set true if task is scheduled false otherwise
	{
		scheduledtask = hastask;
	}
	public boolean hasScheduledtask()// Returns true if a scheduled task is set. false otherwise  
	{
		return scheduledtask;
	}
	public void setTasknumber(int tasknumber)// Set's the scheduled task number for Bukkit
	{
		Tasknumber = tasknumber;
	}
	public int getTasknumber()// Returns the scheduled task number for Bukkit 
	{
		return Tasknumber;
	}

	//If this player has pvp logged
	public void setPvplogged(boolean boolPvPLogged)// Set true if the player has logged in pvp
	{
		pvplogged = boolPvPLogged;
	}
	public boolean hasPvplogged()// Returns true if player has pvp logged
	{
		return pvplogged;
	}

}
