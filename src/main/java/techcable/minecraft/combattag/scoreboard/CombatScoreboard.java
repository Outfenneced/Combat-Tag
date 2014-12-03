package techcable.minecraft.combattag.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.sun.org.apache.bcel.internal.generic.GETSTATIC;

import lombok.*;

@Getter
@Setter
public class CombatScoreboard {
	public static final String SCOREBOARD_NAME = "CombatScoreboard";
	public static final String SCOREBOARD_DISPLAY_NAME = ChatColor.RED + "CombatTag";
	
	public static final String ATTACKER_NAME = "attacker";
	public static final String ATTACKER_DISPLAY_NAME = ChatColor.DARK_RED + "Attacker:";
	
	public static final String DEFENDER_NAME = "defender";
	public static final String DEFENDER_DISPLAY_NAME = ChatColor.GREEN + "Defender:";
	
	public static final String IN_COMBAT = "inCombat";
	public static final String IN_COMBAT_DISPLAY_NAME = ChatColor.BLUE + "Is In Combat?:";
	
	public static final String TIME_REMAINING = "remainingTime";
	public static final String TIME_REMAINING_DISPLAY_NAME = ChatColor.LIGHT_PURPLE + "Remaining Time In Combat:";
	
	public CombatScoreboard(Scoreboard scoreboard, Player player, ScoreboardManager manager) {
		this.scoreboard = scoreboard;
		this.player = player;
		this.manager = manager;
		
		getScoreboard().
	}
	
	private final Scoreboard scoreboard;
	private final ScoreboardManager manager;
	private final Player player;
	private String attacker;
	private String deffender; 
	
	public void updateBoard() {
		Objective objective = getNewObjective(); //initialize new and updated objective
		
	}
	
	public void display() {
		getPlayer().setScoreboard(getScoreboard());
		if (getManager().getPlugin().isDebugEnabled()) {
			getManager().getPlugin().log.info("Giving " + getPlayer().getName() + " a Combat Scoreboard");
		}
	}
	
	public void destroy() {
		getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
		getManager().removeScoreboard(getPlayer());
	}
	
	public Objective getNewObjective(String name) {
		Objective objective = getObjective(name);
		objective.unregister();
		objective = getScoreboard().registerNewObjective(name, "dummy");
		return objective;
	}
	
	public Objective getObjective(String name) {
		return getScoreboard().getObjective(OBJECTIVE_NAME);
	}
}
