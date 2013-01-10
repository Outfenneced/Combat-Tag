package com.trc202.CombatTag;

import static com.sk89q.worldguard.bukkit.BukkitUtil.toVector;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

//import net.slipcor.pvparena.PVPArena;
//import net.slipcor.pvparena.api.PVPArenaAPI;

import com.herocraftonline.heroes.Heroes;
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
	
	public boolean PvPArenaHook(Player plr){
		//Plugin plugin = getServer().getPluginManager().getPlugin("pvparena");
		boolean notInArena = true;
		/*
		if(plugin != null && (plugin instanceof PVPArena)){
			PVPArenaAPI pvpArenaApi = new PVPArenaAPI(); 
			if(pvpArenaApi != null)
				notInArena = PVPArenaAPI.getArenaName(plr) == "" && PVPArenaAPI.getArenaName(plr) == "";
		}
		*/
		return notInArena;
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
	        return null; // Maybe you want throw an exception instead
	    }
	 
	    return (WorldGuardPlugin) wg;
	}
	
	public Plugin getHeroes() {
	    Plugin heroes = plugin.getServer().getPluginManager().getPlugin("Heroes");
	 
	    if (heroes == null || !(heroes instanceof Heroes)) {
	        return null;
	    }
	    return plugin;
	}
	
	public Plugin getMcMMO() {
	    Plugin mcmmo = plugin.getServer().getPluginManager().getPlugin("McMMO");
	 
	    if (mcmmo == null || !(mcmmo instanceof Heroes)) {
	        return null;
	    }
	    return plugin;
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
		return PvPArenaHook(damager) && PvPArenaHook(damaged) && WarArenaHook(damager) && WarArenaHook(damaged);
	}
}
