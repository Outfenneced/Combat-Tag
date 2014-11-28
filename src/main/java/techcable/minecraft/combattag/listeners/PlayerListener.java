package techcable.minecraft.combattag.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import com.trc202.CombatTag.CombatTag;

import techcable.minecraft.combattag.PluginCompatibility;
import techcable.minecraft.combattag.Utils;

public class PlayerListener implements Listener {
    private CombatTag plugin;
    public PlayerListener(CombatTag plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (!plugin.settings.isStopCombatSafezoning()) return;
        if (event.getPlayer().hasPermission("combattag.safezone.ignore"));
        if (!plugin.inTagged(event.getPlayer().getUniqueId())) return;
        if (PluginCompatibility.isPvpDisabled(event.getTo())) {
            event.getPlayer().setVelocity(reverse(event.getPlayer().getVelocity()));
            event.getPlayer().sendMessage("[CombatTag] You can't enter a safezone while combat tagged");
        }
    }

    public static Vector reverse(Vector vector) {
	Vector reverse = vector.multiply(-1);
	reverse.setY(0); //Don't fly
	return reverse;
    }
}