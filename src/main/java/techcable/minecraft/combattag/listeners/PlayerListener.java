package techcable.minecraft.combattag.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.trc202.CombatTag.CombatTag;

import techcable.minecraft.combattag.PluginCompatibility;
import techcable.minecraft.combattag.Utils;

public class PlayerListener implements Listener {
    private CombatTag plugin;
    public PlayerListener() {
        this.plugin = Utils.getPlugin();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (!plugin.settings.isStopCombatSafezoning()) return;
        if (event.getPlayer().hasPermission("combattag.safezone.ignore"));
        if (!plugin.inTagged(event.getPlayer().getUniqueId())) return;
        if (PluginCompatibility.isPvpDisabled(event.getTo())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("[CombatTag] You can't enter a safezone while combat tagged");
        }
    }
}