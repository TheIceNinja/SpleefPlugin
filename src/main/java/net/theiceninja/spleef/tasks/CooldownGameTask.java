package net.theiceninja.spleef.tasks;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.theiceninja.spleef.arena.Arena;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class CooldownGameTask extends BukkitRunnable {

    @Getter
    private final Arena arena;
    @Getter
    private int timeLeftUntilEnd = 180;

    @Override
    public void run() {
        timeLeftUntilEnd--;

        if (timeLeftUntilEnd <= 0) {
            // end game and cleanup
            arena.sendMessage("&cהמשחק נגמר כי נגמר הזמן!");
            arena.cleanup();
            return;
        }

        // update scoreboard every time with the timer.
        arena.updateScoreboard();
        arena.sendActionBar("&cהמשחק נגמר בעוד&8: &e" + timeLeftUntilEnd/60 + "&7:&e" + timeLeftUntilEnd%60);
    }
}
