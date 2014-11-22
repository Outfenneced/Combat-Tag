package com.trc202.settings;

public class SettingsLoader {

    Settings settings;
    private static final String debug = "Enable-Debugging";
    private static final String tagDuration = "Tag-Duration";
    private static final String instaKill = "InstaKill";
    private static final String version = "Version";
    private static final String disabledWorlds = "disabledWorlds";
    private static final String disabledCommands = "disabledCommands";
    private static final String npcName = "npcName";
    private static final String blockEditWhileTagged = "blockEditWhileTagged";
    private static final String sendMessageWhenTagged = "sendMessageWhenTagged";
    private static final String npcDespawnTime = "npcDespawnTime";
    private static final String npcDieAfterTime = "npcDieAfterTime";
    private static final String droptagonkick = "DropTagOnKick";
    private static final String tagMessageDamager = "tagMessageDamager";
    private static final String tagMessageDamaged = "tagMessageDamaged";
    private static final String commandMessageTagged = "commandMessageTagged";
    private static final String commandMessageNotTagged = "commandMessageNotTagged";
    private static final String blockTeleport = "blockTeleport";
    private static final String blockEnderPearl = "blockEnderPearl";
    private static final String dontSpawnInWG = "dontSpawnInWG";
    private static final String onlyDamagerTagged = "onlyDamagerTagged";
    private static final String mobTag = "mobTag";
    private static final String playerTag = "playerTag";
    private static final String blockCreativeTagging = "blockCreativeTagging";
    private static final String blockFlying = "blockFlying";
    private static final String updateEnabled = "updateEnabled";
    private static final String stopCombatSafezoning = "stopCombatSafezoning";
    public Settings loadSettings(SettingsHelper helper, String version) {
        settings = new Settings();
        helper.loadConfig();
        if (!hasValidProperties(helper) || !isLatestVersion(helper, version)) {
            addOtherSettingsIfNecessary(helper, version);
            helper.saveConfig();
        }
        loadProperties(helper);
        return settings;
    }

    private void addOtherSettingsIfNecessary(SettingsHelper helper, String version) {
        Settings temp = new Settings();
	if (helper.getProperty(stopCombatSafezoning) == null) {
	    helper.setProperty(stopCombatSafezoning, Boolean.toString(temp.isStopCombatSafezoning()));
	} 
        if (helper.getProperty(debug) == null) {
            helper.setProperty(debug, Boolean.toString(temp.isDebugEnabled()));
        }
	if (helper.getProperty(updateEnabled) == null) {
	    helper.setProperty(updateEnabled, Boolean.toString(temp.isUpdateEnabled()));
	}
        if (helper.getProperty(tagDuration) == null) {
            helper.setProperty(tagDuration, String.valueOf(temp.getTagDuration()));
        }
        if (helper.getProperty(instaKill) == null) {
            helper.setProperty(instaKill, Boolean.toString(temp.isInstaKill()));
        }
        if (helper.getProperty(SettingsLoader.version) == null || !isLatestVersion(helper, version)) {
            helper.setProperty(SettingsLoader.version, version);
        }
        if (helper.getProperty(disabledWorlds) == null) {
            helper.setProperty("disabledWorlds", "[exampleWorld,exampleWorld2]");
        }
        if (helper.getProperty(disabledCommands) == null) {
            helper.setProperty("disabledCommands", "[]");
        }
        if (helper.getProperty(npcName) == null) {
            helper.setProperty(npcName, temp.getNpcName());
        }
        if (helper.getProperty(blockEditWhileTagged) == null) {
            helper.setProperty(blockEditWhileTagged, Boolean.toString(temp.isBlockEditWhileTagged()));
        }
        if (helper.getProperty(sendMessageWhenTagged) == null) {
            helper.setProperty(sendMessageWhenTagged, Boolean.toString(temp.isSendMessageWhenTagged()));
        }
        if (helper.getProperty(npcDespawnTime) == null) {
            helper.setProperty(npcDespawnTime, String.valueOf(temp.getNpcDespawnTime()));
        }
        if (helper.getProperty(npcDieAfterTime) == null) {
            helper.setProperty(npcDieAfterTime, Boolean.toString(temp.isInstaKill()));
        }
        if (helper.getProperty(droptagonkick) == null) {
            helper.setProperty(droptagonkick, Boolean.toString(temp.dropTagOnKick()));
        }
        if (helper.getProperty(tagMessageDamager) == null) {
            helper.setProperty(tagMessageDamager, temp.getTagMessageDamager());
        }
        if (helper.getProperty(tagMessageDamaged) == null) {
            helper.setProperty(tagMessageDamaged, temp.getTagMessageDamaged());
        }
        if (helper.getProperty(commandMessageTagged) == null) {
            helper.setProperty(commandMessageTagged, temp.getCommandMessageTagged());
        }
        if (helper.getProperty(commandMessageNotTagged) == null) {
            helper.setProperty(commandMessageNotTagged, temp.getCommandMessageNotTagged());
        }
        if (helper.getProperty(blockTeleport) == null) {
            helper.setProperty(blockTeleport, Boolean.toString(temp.blockTeleport()));
        }
        if (helper.getProperty(blockEnderPearl) == null) {
            helper.setProperty(blockEnderPearl, Boolean.toString(temp.blockEnderPearl()));
        }
        if (helper.getProperty(dontSpawnInWG) == null) {
            helper.setProperty(dontSpawnInWG, Boolean.toString(temp.dontSpawnInWG()));
        }
        if (helper.getProperty(onlyDamagerTagged) == null) {
            helper.setProperty(onlyDamagerTagged, Boolean.toString(temp.onlyDamagerTagged()));
        }
        if (helper.getProperty(mobTag) == null) {
            helper.setProperty(mobTag, Boolean.toString(temp.mobTag()));
        }
        if (helper.getProperty(playerTag) == null) {
            helper.setProperty(playerTag, Boolean.toString(temp.playerTag()));
        }
        if (helper.getProperty(blockCreativeTagging) == null) {
            helper.setProperty(blockCreativeTagging, Boolean.toString(temp.blockCreativeTagging()));
        }
        if (helper.getProperty(blockFlying) == null) {
            helper.setProperty(blockFlying, Boolean.toString(temp.blockFly()));
        }
    }

    private boolean isLatestVersion(SettingsHelper helper, String vers) {
        if (helper.getProperty(version) == null) {
            return false;
        }
        return helper.getProperty(version).equals(vers);
    }

    private boolean hasValidProperties(SettingsHelper helper) {
        return (helper.getProperty(version) != null)
                && (helper.getProperty(tagDuration) != null)
                && (helper.getProperty(debug) != null)
                && (helper.getProperty(instaKill) != null)
                && (helper.getProperty(disabledCommands) != null)
                && (helper.getProperty(disabledWorlds) != null)
                && (helper.getProperty(npcName) != null)
                && (helper.getProperty(blockEditWhileTagged) != null)
                && (helper.getProperty(sendMessageWhenTagged) != null)
                && (helper.getProperty(npcDespawnTime) != null)
                && (helper.getProperty(npcDieAfterTime) != null)
                && (helper.getProperty(droptagonkick) != null)
                && (helper.getProperty(tagMessageDamager) != null)
                && (helper.getProperty(tagMessageDamaged) != null)
                && (helper.getProperty(commandMessageTagged) != null)
                && (helper.getProperty(commandMessageNotTagged) != null)
                && (helper.getProperty(blockTeleport) != null)
                && (helper.getProperty(blockEnderPearl) != null)
                && (helper.getProperty(dontSpawnInWG) != null)
                && (helper.getProperty(onlyDamagerTagged) != null)
                && (helper.getProperty(mobTag) != null)
                && (helper.getProperty(playerTag) != null)
                && (helper.getProperty(blockCreativeTagging) != null)
                && (helper.getProperty(blockFlying) != null)
	        && (helper.getProperty(updateEnabled) != null)
 	        && (helper.getProperty(stopCombatSafezoning) != null);
    }

    private void loadProperties(SettingsHelper helper) {
        settings.setStopCombatSafezoning(Boolean.valueOf(helper.getProperty(stopCombatSafezoning)));
	settings.setDebugEnabled(Boolean.valueOf(helper.getProperty(debug)));
        settings.setTagDuration(Integer.valueOf(helper.getProperty(tagDuration)));
        settings.setNpcDespawnTime(Integer.valueOf(helper.getProperty(npcDespawnTime)));
        settings.setBlockEditWhileTagged(Boolean.valueOf(helper.getProperty(blockEditWhileTagged)));
        settings.setInstaKill(Boolean.valueOf(helper.getProperty(instaKill)));
        settings.setSendMessageWhenTagged(Boolean.valueOf(helper.getProperty(sendMessageWhenTagged)));
        settings.setNpcName(String.valueOf(helper.getProperty(npcName)));
        settings.setNpcDieAfterTime(Boolean.valueOf(helper.getProperty(npcDieAfterTime)));
        settings.setUpdateEnabled(Boolean.valueOf(helper.getProperty(updateEnabled)));
	String disabledCommandsString = helper.getProperty(disabledCommands).replace("[", "");
        disabledCommandsString = disabledCommandsString.replace("]", "");
        String disabledCmds[] = disabledCommandsString.split(",");
        if (disabledCmds.length == 1 && disabledCmds[0].equals("")) {
            settings.setDisabledCommands(new String[0]);
        } else {
            settings.setDisabledCommands(disabledCmds);
        }
        String disabledWorldsString = helper.getProperty(disabledWorlds).replace("[", "");
        disabledWorldsString = disabledWorldsString.replace("]", "");
        settings.setDisallowedWorlds(disabledWorldsString.split(","));
        settings.setDropTagonKick(Boolean.valueOf(helper.getProperty(droptagonkick)));
        settings.setTagMessageDamager(helper.getProperty(tagMessageDamager));
        settings.setTagMessageDamaged(helper.getProperty(tagMessageDamaged));
        settings.setCommandMessageTagged(helper.getProperty(commandMessageTagged));
        settings.setCommandMessageNotTagged(helper.getProperty(commandMessageNotTagged));
        settings.setBlockTeleport(Boolean.valueOf(helper.getProperty(blockTeleport)));
        settings.setBlockEnderPearl(Boolean.valueOf(helper.getProperty(blockEnderPearl)));
        settings.setDontSpawnInWG(Boolean.valueOf(helper.getProperty(dontSpawnInWG)));
        settings.setOnlyDamager(Boolean.valueOf(helper.getProperty(onlyDamagerTagged)));
        settings.setMobTag(Boolean.valueOf(helper.getProperty(mobTag)));
        settings.setPlayerTag(Boolean.valueOf(helper.getProperty(playerTag)));
        settings.setBlockCreativeTagging(Boolean.valueOf(helper.getProperty(blockCreativeTagging)));
        settings.setBlockFly(Boolean.valueOf(helper.getProperty(blockFlying)));
    }
}
