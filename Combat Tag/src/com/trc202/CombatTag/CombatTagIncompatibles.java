package com.trc202.CombatTag;

import static com.sk89q.worldguard.bukkit.BukkitUtil.toVector;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.tommytony.war.Warzone;

public class CombatTagIncompatibles {
	
	CombatTag plugin;
	
	public CombatTagIncompatibles(CombatTag combatTag){
		this.plugin = combatTag;
	}

	public boolean WarArenaHook(Player plr){
		boolean notInArena = true;
		if(plugin.getServer().getPluginManager().getPlugin("War") != null){
			notInArena = Warzone.getZoneByPlayerName(plr.getName()) == null && Warzone.getZoneByPlayerName(plr.getName()) == null;
		}
		return notInArena;
	}
	
	public WorldGuardPlugin getWorldGuard() {
	    Plugin wg = plugin.getServer().getPluginManager().getPlugin("WorldGuard");
	 
	    // WorldGuard may not be loaded
	    if (wg == null || !(wg instanceof WorldGuardPlugin)) {
	        return null;
	    }
	 
	    return (WorldGuardPlugin) wg;
	}
	
	public boolean InWGCheck(Player plr){
		WorldGuardPlugin wg = getWorldGuard();
		if (wg != null) {
			Location plrLoc = plr.getLocation();
			Vector pt = toVector(plrLoc);
			
			RegionManager regionManager = wg.getRegionManager(plr.getWorld());
			ApplicableRegionSet set = regionManager.getApplicableRegions(pt);
			if(set != null){
				return set.allows(DefaultFlag.PVP) && !set.allows(DefaultFlag.INVINCIBILITY);
			} else {
				return true;
			}
		}
		return true;
	}
	
	public boolean notInArena(Player damaged, Player damager){
		return WarArenaHook(damager) && WarArenaHook(damaged);
	}
}
