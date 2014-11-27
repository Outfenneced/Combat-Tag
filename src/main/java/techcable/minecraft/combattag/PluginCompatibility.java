package techcable.minecraft.combattag;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;

import techcable.minecraft.factionsapi.FactionsAPI;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import fr.xephi.authme.api.API;

import lombok.*;

@Getter
public class PluginCompatibility {
	private PluginCompatibility() {}
	
	public static boolean isAuthenticated(Player player) {
		if (!hasAuthme()) return true;
		return API.isAuthenticated(player);
	}
    
    public static boolean isPvpDisabled(Location location) {
    	return !isWGPvPEnabled(location) || isSafezone(location);
    }
        public static boolean isWGPvPEnabled(Location location) {
	    if (!hasWG()) return true;
	    ApplicableRegionSet set = WGBukkit.getRegionManager(location.getWorld()).getApplicableRegions(location);
	    return set.allows(DefaultFlag.PVP);
	}
    
    public static boolean isSafezone(Location location) {
    	if (!hasFactions()) return false;
    	return FactionsAPI.getInstance().getOwningFaction(location).isSafezone();
    }
    
    public static boolean hasWG() {
	try {
	    Class.forName("com.sk89q.worldguard.bukkit.WorldGuardPlugin");
	} catch (ClassNotFoundException ex) {
	    return false;
	}
	Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldGuard");
	if (plugin != null && plugin instanceof WorldGuardPlugin) return true;
	else return false;
    }
    
    public static boolean hasFactions() {
    	return FactionsAPI.isFactionsInstalled();
    }
    
	public static boolean hasAuthme() {
		try {
			Class.forName("fr.xephi.authme.AuthMe");
		} catch (ClassNotFoundException ex) {
			return false;
		}
		return true;
	}
}
