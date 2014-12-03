package techcable.minecraft.combattag.scoreboard;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import lombok.*;

@RequiredArgsConstructor
@Getter
public class ScoreboardTask extends BukkitRunnable {
    private final ScoreboardManager manager;
    @Override
    public void run() {
        for (UUID uuid : getManager().getScoreboardMap().keySet()) {
            if (Bukkit.getPlayer(uuid) == null || !Bukkit.getPlayer(uuid).isOnline()) {
                if (getManager().getScoreboardMap().get(uuid) != null) getManager().getScoreboardMap().get(uuid).destroy();
            } else {
                if (getManager().getScoreboardMap().get(uuid) == null) {
                    getManager().makeBoard(uuid);
                }
                CombatScoreboard board = getManager().getScoreboardMap().get(uuid);
                
                if (board.getDeactivateTime() <= System.currentTimeMillis()) {
                    board.destroy();
                    continue;
                } else {
                    if (!board.isDisplayed()) {
                        board.display();
                    }
                    board.updateBoard();
                    Bukkit.getPlayer(uuid).setScoreboard(board.getScoreboard()); //Aka send
                }
            }
        } 
    }
    
}