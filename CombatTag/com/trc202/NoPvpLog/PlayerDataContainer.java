package com.trc202.NoPvpLog;

import java.io.Serializable;

import org.bukkit.inventory.ItemStack;

public class PlayerDataContainer implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2729996254249787972L;
	
	private boolean hasNPC;
	private String npcId;
	private String playerName;
	private long pvpTimeOut; //Time at which the current tag is over
	private boolean shouldBePunished;
	private int health;
	private SerializableItemStack[] playerInventory;
	private SerializableItemStack[] playerArmor;
	
	PlayerDataContainer(String playerName)
	{
		this.playerName = playerName;
		hasNPC = false;
		npcId = "";
		pvpTimeOut = 0L;
		shouldBePunished = false;
		setHealth(0);
		
	}
	
	public void setNPC(boolean hasNPC)
	{
		this.hasNPC = hasNPC;
	}
	public boolean hasNPC()
	{
		return hasNPC;
	}
	
	public void setNPCId(String npcId)
	{
		this.npcId = npcId;
	}
	public String getNPCId()
	{
		return npcId;
	}
	
	public void setPvPTimeout(int seconds)
	{
		pvpTimeOut = System.currentTimeMillis() + (seconds * 1000);
	}
	public boolean hasPVPTimedOut()
	{
		if(pvpTimeOut <= System.currentTimeMillis())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public String getPlayerName()
	{
		return playerName;
	}

	
	public void setShouldBePunished(boolean shouldBePunished) {
		this.shouldBePunished = shouldBePunished;
	}

	public boolean shouldBePunished() {
		return shouldBePunished;
	}



	public void setPlayerInventory(ItemStack[] playerInventory)
	{
		
		this.playerInventory = convertToSerializableItemStack(playerInventory);
	}
	public ItemStack[] getPlayerInventory()
	{
		return convertToItemStack(playerInventory);
	}
	public void setPlayerArmor(ItemStack[] armor)
	{
		this.playerArmor = convertToSerializableItemStack(armor);
	}
	public ItemStack[] getPlayerArmor()
	{
		return convertToItemStack(playerArmor);
	}

	public void setHealth(int health) {
		this.health = health;	
	}
	public int getHealth()
	{
		return health;
	}

	public SerializableItemStack[] convertToSerializableItemStack(ItemStack[] items)
	{
		SerializableItemStack[] output = new SerializableItemStack[items.length];
		for(int i = 0; i < items.length; i++)
		{
			if(items[i] == null)
			{
				output[i] = null;
			}
			else
			{
				output[i] = new SerializableItemStack(items[i]);
			}
		}
		return output;
	}
	public ItemStack[] convertToItemStack(SerializableItemStack[] items)
	{
		ItemStack[] output = new ItemStack[items.length];
		for(int i = 0; i < items.length; i++)
		{
			if(items[i] == null)
			{
				output[i] = null;
			}
			else
			{
				output[i] = items[i].getItemStack();
			}
		}
		return output;
	}
}
