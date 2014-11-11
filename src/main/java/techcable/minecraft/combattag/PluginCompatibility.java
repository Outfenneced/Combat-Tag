package techcable.minecraft.combattag;

import org.bukkit.entity.Player;

import fr.xephi.authme.api.API;

import lombok.*;

@Getter
public class PluginCompatibility {
	private PluginCompatibility() {}
	
	public static boolean isAuthenticated(Player player) {
		if (!hasAuthme()) return true;
		return API.isAuthenticated(player);
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
