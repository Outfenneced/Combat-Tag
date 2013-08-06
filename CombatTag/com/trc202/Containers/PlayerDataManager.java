package com.trc202.Containers;

import java.io.File;

import org.bukkit.inventory.ItemStack;

import com.trc202.helpers.SettingsHelper;

public class PlayerDataManager {
	
	private static int armorSlots = 4;
	private static int inventorySlots = 36;
	private static String stringAir = "0:0:0";
	
	/**
	 * Saves the player data container to a particular directory
	 * @param directory to save the file to
	 * @param playerContainer with which to get the data from
	 */
	public static void savePlayerData(String directory,PlayerDataContainer playerContainer){
		File file = new File(directory + File.separator + playerContainer.getPlayerName());
		SettingsHelper settings = new SettingsHelper(file, "CombatTag");
		settings.setProperty("Health", String.valueOf(playerContainer.getHealth()));
		settings.setProperty("Experience", String.valueOf(playerContainer.getExp()));
		settings.setProperty("Should-Be-Punished", String.valueOf(playerContainer.shouldBePunished()));
		String[] items;
		if(playerContainer.getPlayerInventory() == null){
			 items = itemsToString(new ItemStack[inventorySlots]);
		}else{
			 items = itemsToString(playerContainer.getPlayerInventory());
		}
		for(int i = 0; i < items.length; i++){
			settings.setProperty("InventorySlot" + i,items[i]);
		}
		String[] armorItems;
		if(playerContainer.getPlayerArmor() == null){
			armorItems = itemsToString(new ItemStack[armorSlots]);
		}else{
			armorItems = itemsToString(playerContainer.getPlayerArmor());
		}
		for(int i = 0; i < armorItems.length; i++){
			settings.setProperty("ArmorSlot" + i, armorItems[i]);
		}
		settings.saveConfig();
	}
	
	/**
	 * Loads the player data from the directory\playername
	 * @param directory with which to load the file
	 * @param playerName name of the player and consiquently the file
	 * @return PlayerDataContainer which contains the data of the player
	 */
	public static PlayerDataContainer loadPlayerData(String directory, String playerName){
		File playerFile =  new File(directory + File.separator + playerName);
		if(!playerFile.exists()){
			return null;
		}
		SettingsHelper settings = new SettingsHelper(playerFile,"CombatTag");
		settings.loadConfig();
		
		String[] inventoryString = new String[inventorySlots];
		for(int i = 0; i < inventorySlots; i++){
			if(settings.getProperty("InventorySlot" + i) != null){
				inventoryString[i] = settings.getProperty("InventorySlot" + i);
			}
			else inventoryString[i] = stringAir;
		}
		ItemStack[] inventoryItems = stringToItems(inventoryString);
		String[] armorString = new String[armorSlots];
		for(int i = 0; i < armorSlots; i++){
			if(settings.getProperty("ArmorSlot" + i) != null){
				armorString[i] = settings.getProperty("ArmorSlot" + i);
			}
			else armorString[i] = stringAir;
		}
		ItemStack[] armorItems = stringToItems(armorString);
		int health = Integer.valueOf(settings.getProperty("Health"));
		float experience = Float.valueOf(settings.getProperty("Experience"));
		boolean punish = Boolean.valueOf(settings.getProperty("Should-Be-Punished"));

		PlayerDataContainer pdc = new PlayerDataContainer(playerName);
		pdc.setPlayerInventory(inventoryItems);
		pdc.setPlayerArmor(armorItems);
		pdc.setExp(experience);
		pdc.setHealth(health);
		pdc.setShouldBePunished(punish);
		return pdc;
	}
	
	/**
	 * Checks to see if there is a player data file
	 * @param directory
	 * @param playerName
	 * @return true if file exists
	 */
	public static boolean hasPlayerDataFile(String directory, String playerName){
		File file = new File(directory + File.separator + playerName);
		if(file.canRead() && file.canWrite() && file.exists()){
			return true;
		}
		return false;
	}
	
	/**
	 * Converts the itemstack array to a string array seperated by : 
	 * the flormat is id:durability:amount
	 * @param items
	 * @return
	 */
	private static String[] itemsToString(ItemStack[] items){
		String[] array = new String[items.length];
		for(int i = 0; i < items.length; i++){
			String temp = "";
			if(items[i] != null){
				temp = temp + items[i].getTypeId();
				temp = temp + ":" + items[i].getDurability();
				temp = temp + ":" + items[i].getAmount();
			}
			else{
				temp = stringAir;
			}
			array[i] = temp;
		}
		return array;
	}
	
	private static ItemStack[] stringToItems(String[] stringArray){
		ItemStack[] itemStack = new ItemStack[stringArray.length];
		for(int i = 0; i < stringArray.length; i++){
			String temp = stringArray[i];
			String[] itemValues = temp.split(":");
			if(itemValues.length == 3){
				ItemStack item = new ItemStack(Integer.valueOf(itemValues[0]));
				item.setDurability(Short.valueOf(itemValues[1]));
				item.setAmount(Integer.valueOf(itemValues[2]));
				itemStack[i] = item;
			}
			else{
				ItemStack item = new ItemStack(0);
				itemStack[i] = item;
			}
		}
		return itemStack;
		
	}

	public static void deletePlayerData(String directory, String playerName) {
		File f = new File(directory + File.separator + playerName);
		if(f.exists()){
			f.delete();
		}
		
	}
}
