package techcable.minecraft.combattag.scoreboard;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import techcable.minecraft.combattag.CombatTagAPI;

import lombok.*;

@Getter
@Setter
public class CombatScoreboard {
	public static final String OBJECTIVE_NAME = "CombatScoreboard";
	public static final String OBJECTIVE_DISPLAY_NAME = ChatColor.RED + "CombatTag";
	
	public static final String ATTACKER_NAME = ChatColor.DARK_RED + "Attacker:";
	
	public static final String DEFENDER_NAME = ChatColor.GREEN + "Defender:";
	
	public static final String IN_COMBAT = ChatColor.BLUE + "Is In Combat?:";
	
	public static final String TIME_REMAINING = ChatColor.LIGHT_PURPLE + "Remaining Time In Combat:";
	
	public CombatScoreboard(Scoreboard scoreboard, Player player, ScoreboardManager manager) {
		this.scoreboard = scoreboard;
		this.player = player;
		this.manager = manager;
		resetObjective();
	}
	
	private final Scoreboard scoreboard;
	private final ScoreboardManager manager;
	private final Player player;
	private String attacker;
	private String defender; 
	private long deactivateTime = System.currentTimeMillis() + 30000L;
	
	public void updateBoard() {
		Map<String, Integer> txtMap = new HashMap<>(); //Maps scores
		
		if (getAttacker() != null) txtMap.put(ATTACKER_NAME + ChatColor.RESET + " " + getAttacker(), null);
		if (getDefender() != null) txtMap.put(DEFENDER_NAME + ChatColor.RESET + " " + getDefender(), null);
		if (CombatTagAPI.isTagged(getPlayer())) {
		    txtMap.put(IN_COMBAT + ChatColor.RESET + " Yes", null);
		} else {
		    txtMap.put(IN_COMBAT + ChatColor.RESET + " No", null);
		}
		long timeRemainingMill = CombatTagAPI.getRemainingTagTime(getPlayer());
		if (timeRemainingMill < 0) timeRemainingMill = 0;
		int timeRemaining = (int) Math.round(timeRemainingMill / 1000);
		txtMap.put(TIME_REMAINING, timeRemaining);
		setBoard(txtMap);
	}
	
	public void display() {
	    if (isDisplayed()) return;
		getPlayer().setScoreboard(getScoreboard());
		if (getManager().getPlugin().isDebugEnabled()) {
			getManager().getPlugin().log.info("Giving " + getPlayer().getName() + " a Combat Scoreboard");
		}
	}
	
	public boolean isDisplayed() {
	    return getPlayer().getScoreboard().equals(this);
	}
	
	public void destroy() {
	    deactivateTime = System.currentTimeMillis();
		getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
		getManager().removeScoreboard(getPlayer());
	}
	
	public Objective getObjective() {
		return getScoreboard().getObjective(OBJECTIVE_NAME);
	}
	
	public void setBoard(Map<String, Integer> txtMap) {
	    resetObjective();
	    Objective objective = getObjective();
	    int index = 0;
	    for (Map.Entry<String, Integer> txt : txtMap.entrySet()) {
	        if (txt.getKey() == null) continue;
	        String name = txt.getKey();
	        int score = txt.getValue() == null ? index : txt.getValue();
	        getObjective().getScore(name).setScore(score); 
	        index++;
	    }
	}
	
	public void resetObjective() {
	    if (getObjective() != null) {
	        getObjective().unregister();
	    }
	    Objective objective = getScoreboard().registerNewObjective(OBJECTIVE_NAME, "dummy");
		objective.setDisplayName(OBJECTIVE_DISPLAY_NAME);
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
	}
}
