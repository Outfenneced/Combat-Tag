package com.trc202.Containers;

public class Settings {
	private int tagDuration;
	private boolean debugEnabled;
	private boolean instaKill;
	
	public Settings(){
		instaKill = false;
		tagDuration = 10;
		debugEnabled = false;
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
	
}
