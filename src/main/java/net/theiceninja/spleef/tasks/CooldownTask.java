package net.theiceninja.spleef.tasks;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.theiceninja.spleef.arena.Arena;
import net.theiceninja.spleef.arena.manager.ArenaState;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class CooldownTask extends BukkitRunnable {

    @Getter
    private final Arena arena;
    @Getter
    private int timeLeft = 11;

    @Override
    public void run() {
        timeLeft--;

        if (timeLeft <= 0) {
            cancel();
            // end cooldown and start the game
            arena.setArenaState(ArenaState.ACTIVE);
            arena.playSound(Sound.BLOCK_NOTE_BLOCK_BIT);
            return;
        }

        // do something while the cooldown
        arena.playSound(Sound.BLOCK_NOTE_BLOCK_PLING);
        arena.updateScoreboard();
        arena.sendTitle("&#E2AF0E&l> &#0EE221&l" + timeLeft + " &#E2AF0E&l<");
    }
}
