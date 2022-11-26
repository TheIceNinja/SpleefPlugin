package net.theiceninja.spleef.tasks;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.theiceninja.spleef.arena.Arena;
import net.theiceninja.spleef.arena.ArenaState;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

@Data
@RequiredArgsConstructor
public class CooldownTask extends BukkitRunnable {

    private Arena arena;
    private int timeLeft = 11;

    @Override
    public void run() {
        timeLeft--;
        if (timeLeft <= 0) {
            cancel();
            arena.setArenaState(ArenaState.ACTIVE);
            arena.playSound(Sound.BLOCK_NOTE_BLOCK_BIT);
            return;
        }
        arena.playSound(Sound.BLOCK_NOTE_BLOCK_PLING);
        arena.updateScoreboard();
        arena.sendTitle("&6&l> &2&l" + timeLeft + " &6&l<");

    }
}
