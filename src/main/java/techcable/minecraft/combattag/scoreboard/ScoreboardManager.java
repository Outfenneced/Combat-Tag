package techcable.minecraft.combattag.scoreboard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scoreboard.Scoreboard;

import com.trc202.CombatTag.CombatTag;

import lombok.*;

@Getter
public class ScoreboardManager implements Listener {
	public static final long BOARD_UPDATE_INTERVAL = 20; //1 second
	public static final long BOARD_UPDATE_DELAY = 80; //4 Seconds - Give it some time so its less likely to blow up
	private Map<UUID, CombatScoreboard> scoreboardMap = new HashMap<>();
	private CombatTag plugin;
	private ScoreboardTask task;
	@SuppressWarnings("deprecation")
	public ScoreboardManager(CombatTag plugin) {
	    this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
		task = new ScoreboardTask(this);
		task.runTaskTimer(plugin, BOARD_UPDATE_DELAY, BOARD_UPDATE_INTERVAL);
	}
	
	public CombatScoreboard getScoreboard(Player player) {
		return scoreboardMap.get(player.getUniqueId());
	}
	
	public void setScoreboard(Player player, CombatScoreboard scoreboard) {
		scoreboardMap.put(player.getUniqueId(), scoreboard);
	}
	
	public void removeScoreboard(Player player) {
	    scoreboardMap.remove(player.getUniqueId());
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			Entity damager = event.getDamager();
			if (damager instanceof Projectile) {
				ProjectileSource source = ((Projectile)damager).getShooter();
				if (source instanceof Entity) {
					damager = (Entity) source;
				} else return; //Not an entity
			}
			if (getScoreboard(player) != null) {
				CombatScoreboard scoreboard = getScoreboard(player);
				if (damager instanceof Player) { //If its a player display the attack regardless of whether or not another player attacked them first
					Player attacker = (Player) damager;
					if (!player.canSee(attacker)) return; //He is invisible
					scoreboard.setAttacker(attacker.getDisplayName());
				} else if (damager instanceof LivingEntity) { //If it is a mob only display the attack if a player didn't attack them first
					if (scoreboard.getAttacker() == null) { //No one attacked him yet
						if (damager.getCustomName() != null) { //He has a special name
							scoreboard.setAttacker(damager.getCustomName());
						} else { //Give his mob type
							scoreboard.setAttacker(damager.getType().getName());
						}
					}
				} //Otherwise its not alive and don't display it
			}
		}
		if (event.getDamager() instanceof Player) {
			Player player = (Player) event.getEntity();
			Entity defender = event.getEntity();
			if (getScoreboard(player) != null) {
				CombatScoreboard scoreboard = getScoreboard(player);
				if (defender instanceof Player) { //If its a player display the attack regardless of whether or not another player attacked them first
					Player attacker = (Player) defender;
					if (!player.canSee(attacker)) return; //He is invisible
					scoreboard.setDefender(attacker.getDisplayName());
				} else if (defender instanceof LivingEntity) { //If it is a mob only display the attack if a player didn't attack them first
					if (scoreboard.getDefender() == null) { //No one attacked him yet
						if (defender.getCustomName() != null) { //He has a special name
							scoreboard.setDefender(defender.getCustomName());
						} else { //Give his mob type
							scoreboard.setDefender(defender.getType().getName());
						}
					}
				} //Otherwise its not alive and don't display it
			}
		}
	}
	
	@EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
	public void onLeave(PlayerQuitEvent event) {
		if (getScoreboard(event.getPlayer()) != null) {
		    getScoreboard(event.getPlayer()).destroy();
		}
	}
	
	@EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
	public void onKick(PlayerKickEvent event) {
		if (getScoreboard(event.getPlayer()) != null) {
		    getScoreboard(event.getPlayer()).destroy();
		}
	}
	
	public CombatScoreboard makeBoard(UUID id) {
	    Player player = Bukkit.getPlayer(id);
	    Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
	    CombatScoreboard combatBoard = new CombatScoreboard(board, player, this);
	    getScoreboardMap().put(id, combatBoard);
	    return combatBoard;
	}
}
