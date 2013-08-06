package com.trc202.helpers;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class OtherHelpers {
	
	public static int inventorySize = 36;
	public static int armorSize = 4;
	public static ItemStack[] getEmptyInventory(){
		ItemStack airItem = new ItemStack(Material.AIR);
		ItemStack[] emptyInventoryStack = new ItemStack[inventorySize];
		for(int x = 0; x < emptyInventoryStack.length; x++){
			emptyInventoryStack[x] = airItem;
		}
		return emptyInventoryStack;
	}
}
