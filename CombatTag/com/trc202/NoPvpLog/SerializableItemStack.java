package com.trc202.NoPvpLog;

import java.io.Serializable;

import org.bukkit.inventory.ItemStack;

public class SerializableItemStack implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2403272907336050993L;
	private int itemTypeId;
	private int amount;
	private short durability;
	public SerializableItemStack(ItemStack item)
	{
		itemTypeId = item.getTypeId();
		amount = item.getAmount();
		item.getData();
		durability = item.getDurability();
	}
	public ItemStack getItemStack()
	{
		ItemStack item = new ItemStack(itemTypeId, amount, durability);
		return item;
	}

}
