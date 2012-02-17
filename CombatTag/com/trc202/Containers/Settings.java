package com.trc202.Containers;

public class Settings {
	
	public enum SettingsType {NPC,TIMED,OTHER};
	
	private int tagDuration;
	private boolean debugEnabled;
	private boolean instaKill;
	private SettingsType currentMode;
	private String[] disabledCommands;
	private String[] disallowedWorlds;
	private String npcName;
	
	public Settings(){
		currentMode = SettingsType.NPC;
		instaKill = false;
		tagDuration = 10;
		debugEnabled = false;
		disabledCommands = new String[0];
		disallowedWorlds = new String[0];
		npcName = "PvpLogger";
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
	
	public SettingsType getCurrentMode(){
		return currentMode;
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
}
