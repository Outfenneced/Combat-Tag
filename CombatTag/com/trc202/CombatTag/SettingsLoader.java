package com.trc202.CombatTag;

import com.trc202.Containers.Settings;
import com.trc202.helpers.SettingsHelper;

public class SettingsLoader {
	Settings settings;
	private static String debug = "Enable-Debugging";
	private static String tagDuration = "Tag-Duration";
	private static String instaKill = "InstaKill";
	private static String version = "Version";
	private static String disabledWorlds = "disabledWorlds";
	private static String disabledCommands = "disabledCommands";
	private static String npcName = "npcName";
	private static String blockEditWhileTagged = "blockEditWhileTagged";
	private static String sendMessageWhenTagged = "sendMessageWhenTagged";

	public Settings loadSettings(SettingsHelper helper, String version){
		settings = new Settings();
		helper.loadConfig();
		if(!hasValidProperties(helper) || !isLatestVersion(helper, version)){
			addOtherSettingsIfNecessary(helper,version);
			helper.saveConfig();
		}			
		loadProperties(helper);
		return settings;
	}
	
	private void addOtherSettingsIfNecessary(SettingsHelper helper, String version) {
		Settings temp = new Settings();
		if(helper.getProperty(debug) == null){helper.setProperty(debug, Boolean.toString(temp.isDebugEnabled()));}
		if(helper.getProperty(tagDuration) == null){helper.setProperty(tagDuration, String.valueOf(temp.getTagDuration()));}
		if(helper.getProperty(instaKill) == null){helper.setProperty(instaKill, Boolean.toString(temp.isInstaKill()));}
		if(helper.getProperty(SettingsLoader.version) == null || !isLatestVersion(helper, version)){helper.setProperty(SettingsLoader.version, version);}
		if(helper.getProperty(disabledWorlds) == null){helper.setProperty("disabledWorlds", "[exampleWorld,exampleWorld2]");}
		if(helper.getProperty(disabledCommands) == null){helper.setProperty("disabledCommands", "[]");}
		if(helper.getProperty(npcName) == null){helper.setProperty(npcName, temp.getNpcName());}
		if(helper.getProperty(blockEditWhileTagged) == null){helper.setProperty(blockEditWhileTagged, Boolean.toString(temp.isBlockEditWhileTagged()));}
		if(helper.getProperty(sendMessageWhenTagged) == null){helper.setProperty(sendMessageWhenTagged, Boolean.toString(temp.isSendMessageWhenTagged()));}
	}

	private boolean isLatestVersion(SettingsHelper helper, String vers){
		if(helper.getProperty(version) == null){return false;}
		return helper.getProperty(version).equals(vers);
	}
	
	private boolean hasValidProperties(SettingsHelper helper) {
		if((helper.getProperty(version) != null) && 
		(helper.getProperty(tagDuration) != null) && 
		(helper.getProperty(debug) != null) && 
		(helper.getProperty(instaKill) != null) &&
		(helper.getProperty(disabledCommands) != null) &&
		(helper.getProperty(disabledWorlds) != null) &&
		(helper.getProperty(npcName) != null) &&
		(helper.getProperty(blockEditWhileTagged) != null) &&
		(helper.getProperty(sendMessageWhenTagged) != null)
		){
			return true;
		}else{
			return false;
			}
	}
	
	private void loadProperties(SettingsHelper helper) {
		settings.setDebugEnabled(Boolean.valueOf(helper.getProperty(debug)));
		settings.setTagDuration(Integer.valueOf(helper.getProperty(tagDuration)));
		settings.setBlockEditWhileTagged(Boolean.valueOf(helper.getProperty(blockEditWhileTagged)));
		settings.setInstaKill(Boolean.valueOf(helper.getProperty(instaKill)));
		settings.setSendMessageWhenTagged(Boolean.valueOf(helper.getProperty(sendMessageWhenTagged)));
		settings.setNpcName(String.valueOf(helper.getProperty(npcName)));
		String disabledCommandsString = helper.getProperty(disabledCommands).replace("[", "");
		disabledCommandsString = disabledCommandsString.replace("]", "");
		String disabledCmds[] = disabledCommandsString.split(",");
		if(disabledCmds.length == 1 && disabledCmds[0].equals("")){
			settings.setDisabledCommands(new String[0]);
		}else{
			settings.setDisabledCommands(disabledCmds);
		}
		String disabledWorldsString = helper.getProperty(disabledWorlds).replace("[", "");
		disabledWorldsString = disabledWorldsString.replace("]", "");
		settings.setDisallowedWorlds(disabledWorldsString.split(","));
	}
}
