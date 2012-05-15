package com.trc202.Containers;

import org.bukkit.inventory.ItemStack;


public class PlayerDataContainer{
	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 3478271626107787024L;

	public static final ItemStack[] emptyInventory = new ItemStack[36];
	public static final ItemStack[] emptyArmor = new ItemStack[4];

	private boolean hasSpawnedNPC;
	private String npcId;
	private String playerName;
	private long pvpTimeOut; //Time at which the current tag is over
	private boolean shouldBePunished;
	private int health;
	private float experience;
	private ItemStack[] playerInventory;
	private ItemStack[] playerArmor;
	
	public long banExpireTime;
	public long banDuration;
	public long banDurationResetTime;
	
	public PlayerDataContainer(String playerName){
		setSpawnedNPC(false);
		this.playerName = playerName;
		this.setNPCId("");
		this.setShouldBePunished(false);
		setHealth(0);
		pvpTimeOut = 0L;
		banExpireTime = 0;
		banDuration = 0;
		banDurationResetTime = 0;
	}
	
	public void setNPCId(String npcId){
		this.npcId = npcId;
	}
	public String getNPCId(){
		return npcId;
	}
	
	public void setPvPTimeout(int seconds){
		pvpTimeOut = System.currentTimeMillis() + (seconds * 1000);
	}
	public boolean hasPVPtagExpired(){
		return (pvpTimeOut <= System.currentTimeMillis());
	}
	
	public String getPlayerName(){
		return playerName;
	}

	
	public void setShouldBePunished(boolean shouldBePunished) {
		this.shouldBePunished = shouldBePunished;
	}

	public boolean shouldBePunished() {
		return shouldBePunished;
	}



	public void setPlayerInventory(ItemStack[] playerInventory){
		this.playerInventory = playerInventory;
	}
	public ItemStack[] getPlayerInventory(){
		return playerInventory;
	}
	public void setPlayerArmor(ItemStack[] armor){
		this.playerArmor = armor;
	}
	public ItemStack[] getPlayerArmor(){
		return playerArmor;
	}

	public void setHealth(int health) {
		this.health = health;	
	}
	public int getHealth(){
		return health;
	}

	public void setExp(float exp) {
		this.experience = exp;	
	}

	public float getExp() {
		return experience;
	}

	public boolean hasSpawnedNPC() {
		return hasSpawnedNPC;
	}

	public void setSpawnedNPC(boolean b) {
		hasSpawnedNPC = b;
	}
	
	public long getRemainingTagTime(){
		long endOfTag = (pvpTimeOut - System.currentTimeMillis());
		return endOfTag;
	}
}
