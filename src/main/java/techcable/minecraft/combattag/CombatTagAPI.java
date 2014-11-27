package techcable.minecraft.combattag;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.trc202.CombatTag.CombatTag;

import lombok.*;

/**
 * API to Interface with Combat Tag Reloaded
 * 
 * All methods are static
 * 
 * @author Techcable
 *
 */
public class CombatTagAPI {
	private CombatTagAPI() {}
	
	/**
	 * Returns if a player is combat tagged
	 * @param player the player to check
	 * @return true if combat tagged
	 */
	public static boolean isTagged(Player player) {
		return getPlugin().inTagged(player.getUniqueId());
	}
	
	/**
	 * Returns the time a player has left in combat
	 * @param player the player to check
	 * @return time in milliseconds until the player is no longer in combat
	 */
	public static long getRemainingTagTime(Player player) {
		if (isTagged(player)) {
			return getPlugin().getRemainingTagTime(player.getUniqueId());
		} else {
			return -1;
		}
	}
	
	/**
	 * Checks if an entity is a NPC
	 * @param entity the entity to check
	 * @return true if entity is a NPC
	 */
	public static boolean isNPC(Entity entity) {
		if (getPlugin().getNpcMaster() == null) return false; //Npcs not enabled
		return getPlugin().getNpcMaster().isNPC(entity);
	}
	
	/**
	 * Set if a player is tagged
	 * @param player the player to set the tag status off
	 * @param tagged true if player should be tagged, false if he should be untagged
	 */
	public static void setTagged(Player player, boolean tagged) {
		if (tagged) {
			getPlugin().addTagged(player);
		} else {
			getPlugin().removeTagged(player.getUniqueId());
		}
	}
	
	/**
	 * Tag this player
	 * Equivelent to setTagged(player, true)
	 * @param player player to tag
	 */
	public static void addTagged(Player player) {
		setTagged(player, true);
	}
	
	/**
	 * Untag this player
	 * Equivelent to setTagged(player, false)
	 * @param player player to un-tag
	 */
	public static void removeTagged(Player player) {
		setTagged(player, false);
	}
	
	public static CombatTag getPlugin() {
		return Utils.getPlugin();
	}
}
