package com.trc202.CombatTag;

import com.trc202.Containers.Settings;
import com.trc202.helpers.SettingsHelper;

public class SettingsLoader {
	Settings settings;
	private static String debug = "Enable-Debugging";
	private static String tagDuration = "Tag-Duration";
	private static String instaKill = "InstaKill";
	private static String version = "Version";

	public Settings loadSettings(SettingsHelper helper, String version){
		settings = new Settings();
		helper.loadConfig();
		if(hasValidProperties(helper) || !isLatestVersion(helper, version)){
			addOtherSettingsIfNecessary(helper,version);
			helper.saveConfig();
		}
		else if(true){			
			loadProperties(helper);
		}
		return settings;
	}
	
	private void addOtherSettingsIfNecessary(SettingsHelper helper, String version) {
		Settings temp = new Settings();
		if(helper.getProperty(debug) == null){helper.setProperty(debug, Boolean.toString(temp.isDebugEnabled()));}
		if(helper.getProperty(tagDuration) == null){helper.setProperty(tagDuration, String.valueOf(temp.getTagDuration()));}
		if(helper.getProperty(instaKill) == null){helper.setProperty(instaKill, Boolean.toString(temp.isInstaKill()));}
		if(helper.getProperty(SettingsLoader.version) == null){helper.setProperty(SettingsLoader.version, version);}
	}

	private boolean isLatestVersion(SettingsHelper helper, String version){
		if(helper.getProperty(version) == null) return false;
		return helper.getProperty(version).equals(version);
	}
	
	private boolean hasValidProperties(SettingsHelper helper) {
		if((helper.getProperty(version) != null) && 
		(helper.getProperty(tagDuration) != null) && 
		(helper.getProperty(debug) != null) && 
		(helper.getProperty(instaKill) != null)){
			return true;
		}else{
			return false;
			}
	}
	
	private void loadProperties(SettingsHelper helper) {
		settings.setDebugEnabled(Boolean.valueOf(helper.getProperty(debug)));
		settings.setTagDuration(Integer.valueOf(helper.getProperty(tagDuration)));
		settings.setInstaKill(Boolean.valueOf(helper.getProperty(instaKill)));
		
	}
}
