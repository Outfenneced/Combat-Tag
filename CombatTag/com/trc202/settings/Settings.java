package com.trc202.settings;

public class Settings {
	
	private int tagDuration;
	private boolean debugEnabled;
	private boolean instaKill;
	private String[] disabledCommands;
	private String[] disallowedWorlds;
	private String npcName;
	private boolean blockEditWhileTagged;
	private boolean sendMessageWhenTagged;
	private int npcDespawnTime;
	private boolean npcDieAfterTime;
	private boolean droptagonkick;
	private String commandMessageTagged;
	private String commandMessageNotTagged;
	private String tagMessageDamager;
	private String tagMessageDamaged;
	private boolean blockTeleport;
	private boolean blockEnderPearl;
	private boolean dontSpawnInWG;
	private boolean onlyDamagerTagged;
	private boolean mobTag;
	private boolean playerTag;
	private boolean blockCreativeTagging;
	private boolean blockFly;
	
	public Settings(){
		instaKill = false;
		tagDuration = 10;
		debugEnabled = false;
		disabledCommands = new String[0];
		disallowedWorlds = new String[0];
		npcName = "PvpLogger";
		blockEditWhileTagged = true;
		sendMessageWhenTagged = false;
		npcDespawnTime = -1;
		npcDieAfterTime = false;
		droptagonkick = true;
		commandMessageTagged = "You are in combat for [time] seconds.";
		commandMessageNotTagged = "You are not currently in combat.";
		tagMessageDamager = "You have hit [player]. Type /ct to check your remaining tag time.";
		tagMessageDamaged = "You have been hit by [player]. Type /ct to check your remaining tag time.";
		blockTeleport = false;
		blockEnderPearl = false;
		dontSpawnInWG = false;
		onlyDamagerTagged = false;
		mobTag = false;
		playerTag = true;
		blockCreativeTagging = true;
		blockFly = false;
	}

	public void setDebugEnabled(boolean debugEnabled) {
		this.debugEnabled = debugEnabled;
	}

	public boolean isDebugEnabled() {
		return debugEnabled;
	}

	public void setTagDuration(int tagDuration) {
		this.tagDuration = tagDuration;
	}

	public int getTagDuration() {
		return tagDuration;
	}

	public void setInstaKill(boolean instaKill) {
		this.instaKill = instaKill;
	}

	public boolean isInstaKill() {
		return instaKill;
	}
	
	public void setDropTagonKick(boolean droptagonkick) {
		this.droptagonkick = droptagonkick;
	}
	
	public boolean dropTagOnKick() {
		return droptagonkick;
	} 

	public String[] getDisabledCommands() {
		return disabledCommands;
	}
	
	public void setDisabledCommands(String[] disabledCommands){
		this.disabledCommands = disabledCommands;
	}

	public void setDisallowedWorlds(String[] disallowedWorlds) {
		this.disallowedWorlds = disallowedWorlds;
	}

	public String[] getDisallowedWorlds() {
		return disallowedWorlds;
	}

	public String getNpcName() {
		return npcName;
	}

	public void setNpcName(String npcName) {
		this.npcName = npcName;
		
	}

	public boolean isBlockEditWhileTagged() {
		return blockEditWhileTagged;
	}
	
	public void setBlockEditWhileTagged(boolean blockEditWhileTagged) {
		this.blockEditWhileTagged = blockEditWhileTagged;
	}

	public boolean isSendMessageWhenTagged() {
		return sendMessageWhenTagged;
	}
	
	public void setSendMessageWhenTagged(boolean sendMessageWhenTagged) {
		this.sendMessageWhenTagged = sendMessageWhenTagged;
	}

	public int getNpcDespawnTime() {
		return npcDespawnTime;
	}
	
	public void setNpcDespawnTime(int npcDespawnTime) {
		this.npcDespawnTime = npcDespawnTime;
	}

	public void setNpcDieAfterTime(Boolean npcDieAfterTime) {
		this.npcDieAfterTime = npcDieAfterTime;
		
	}
	
	public boolean isNpcDieAfterTime() {
		return npcDieAfterTime;
	}

	public void setCommandMessageTagged(String message) {
		this.commandMessageTagged = message;
	}

	public String getCommandMessageTagged() {
		return commandMessageTagged;
	}

	public void setCommandMessageNotTagged(String message) {
		this.commandMessageNotTagged = message;
	}

	public String getCommandMessageNotTagged() {
		return commandMessageNotTagged;
	}
	
	public void setBlockTeleport(boolean blockTeleport) {
		this.blockTeleport = blockTeleport;
	}
	
	public boolean blockTeleport() {
		return blockTeleport;
	}

	public void setDontSpawnInWG(boolean dontSpawnInWG) {
		this.dontSpawnInWG = dontSpawnInWG;
	}
	
	public boolean dontSpawnInWG(){
		return dontSpawnInWG;
	}

	public void setTagMessageDamaged(String tagMessageDamaged) {
		this.tagMessageDamaged = tagMessageDamaged;
	}
	
	public String getTagMessageDamaged() {
		return tagMessageDamaged;
	}
	
	public void setTagMessageDamager(String tagMessageDamager) {
		this.tagMessageDamager = tagMessageDamager;
	}
	
	public String getTagMessageDamager() {
		return tagMessageDamager;
	}

	public void setBlockEnderPearl(boolean blockEnderPearl) {
		this.blockEnderPearl = blockEnderPearl;
	}
	
	public boolean blockEnderPearl() {
		return blockEnderPearl;
	}

	public void setOnlyDamager(boolean onlyDamagerTagged) {
		this.onlyDamagerTagged = onlyDamagerTagged;
	}
	
	public boolean onlyDamagerTagged() {
		return onlyDamagerTagged;
	}
	
	public void setMobTag(boolean mobTag) {
		this.mobTag = mobTag;
	}
	
	public boolean mobTag() {
		return mobTag;
	}
	
	public void setPlayerTag(boolean playerTag) {
		this.playerTag = playerTag;
	}
	
	public boolean playerTag() {
		return playerTag;
	}

	public void setBlockCreativeTagging(boolean blockCreativeTagging) {
		this.blockCreativeTagging = blockCreativeTagging;
	}

	public boolean blockCreativeTagging() {
		return blockCreativeTagging;
	}

	public void setBlockFly(boolean blockFly) {
		this.blockFly = blockFly;
	}
	
	public boolean blockFly() {
		return blockFly;
	}
}
