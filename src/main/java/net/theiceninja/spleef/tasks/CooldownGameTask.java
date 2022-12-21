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
            arena.sendMessage("&cהמשחק נגמר כי נגמר הזמן!");
            arena.cleanup();
            return;
        }

        arena.updateScoreboard();
        arena.sendActionBar("&cהמשחק נגמר בעוד&8: &e" + timeLeftUntilEnd/60 + "&7:&e" + timeLeftUntilEnd%60);
    }
}
