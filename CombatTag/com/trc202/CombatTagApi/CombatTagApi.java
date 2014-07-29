package com.trc202.CombatTagApi;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.trc202.CombatTag.CombatTag;

public class CombatTagApi {

    private final CombatTag plugin;

    public CombatTagApi(CombatTag plugin) {
        this.plugin = plugin;
    }

    /**
     * Checks to see if the player is in combat. The combat time can be
     * configured by the server owner If the player has died while in combat the
     * player is no longer considered in combat and as such will return false
     *
     * @param player
     * @return true if player is in combat
     */
    public boolean isInCombat(Player player) {
        return plugin.isInCombat(player.getUniqueId());
    }

    /**
     * Checks to see if the player is in combat. The combat time can be
     * configured by the server owner If the player has died while in combat the
     * player is no longer considered in combat and as such will return false
     *
     * @param name
     * @return true if player is online and in combat
     */
    @SuppressWarnings("deprecation")
    public boolean isInCombat(String name) {
        Player player = Bukkit.getPlayerExact(name);
        if (player != null) {
            return plugin.isInCombat(player.getUniqueId());
        }
        return false;
    }

    /**
     * Returns the time before the tag is over -1 if the tag has expired -2 if
     * the player is not in combat
     *
     * @param player
     * @return
     */
    public long getRemainingTagTime(Player player) {
        if (plugin.isInCombat(player.getUniqueId())) {
            return plugin.getRemainingTagTime(player.getUniqueId());
        } else {
            return -1L;
        }
    }

    /**
     * Returns the time before the tag is over -1 if the tag has expired -2 if
     * the player is not in combat
     *
     * @param name
     * @return
     */
    @SuppressWarnings("deprecation")
    public long getRemainingTagTime(String name) {
        if (Bukkit.getPlayerExact(name) != null) {
            Player player = Bukkit.getPlayerExact(name);
            if (plugin.isInCombat(player.getUniqueId())) {
                return plugin.getRemainingTagTime(player.getUniqueId());
            } else {
                return -1L;
            }
        }
        return -2L;
    }

    /**
     * Returns if the entity is an NPC
     *
     * @param entity
     * @return true if the player is an NPC
     */
    public boolean isNPC(Entity entity) {
        return plugin.npcm.isNPC(entity);
    }

    /**
     * Tags player
     *
     * @param player
     * @return true if the action is successful, false if not
     */
    public boolean tagPlayer(Player player) {
        return plugin.addTagged(player);
    }

    /**
     * Untags player
     *
     * @param player
     */
    public void untagPlayer(Player player) {
        plugin.removeTagged(player.getUniqueId());
    }

    /**
     * Returns the value of a configuration option with the specified name
     *
     * @param configKey
     * @return String value of option
     */
    public String getConfigOption(String configKey) {
        return plugin.getSettingsHelper().getProperty(configKey);
    }
}
