package net.theiceninja.spleef.commands.subcommands;

import lombok.RequiredArgsConstructor;
import net.theiceninja.spleef.SpleefPlugin;
import net.theiceninja.spleef.arena.Arena;
import net.theiceninja.spleef.arena.ArenaManager;
import net.theiceninja.spleef.arena.ArenaState;
import net.theiceninja.spleef.utils.ColorUtils;
import org.bukkit.entity.Player;

import java.util.Optional;

@RequiredArgsConstructor
public class RandomArenaSubCommand implements SubCommand {

    private final ArenaManager arenaManager;

    @Override
    public void execute(Player player, String[] args) {

        if (arenaManager.getArenas().isEmpty()) {
            player.sendMessage(ColorUtils.color("&cאין שום ארנות שתוכל להכנס"));
            return;
        }
        Optional<Arena> optionalArena = arenaManager.getArenas().stream().filter(arena1 -> arena1.getArenaState() == ArenaState.COOLDOWN || arena1.getArenaState() == ArenaState.DEFAULT).findAny();
        if (!optionalArena.isPresent()) {
            player.sendMessage(ColorUtils.color("&cאין ארנה פנויה, תאלץ לחכות כמה זמן עד שתתפנה אחת."));
            return;
        }
        if (!(optionalArena.get().getArenaState() == ArenaState.DEFAULT || optionalArena.get().getArenaState() == ArenaState.COOLDOWN)) {
            player.sendMessage(ColorUtils.color("&cהארנה דלוקה ולכן אי אפשר להכנס."));
            return;
        }
        if (optionalArena.get().getMAX_PLAYERS() == optionalArena.get().getAliveUUID().size()) {
            player.sendMessage(ColorUtils.color("&cהארנה הגיעה לכמות השחקנית המקסימלית, לכן לא תוכל להכנס חכה לתור הבא."));
            return;
        }
        for (Arena arena : arenaManager.getArenas())
            if (arena.isPlaying(player)) {
                player.sendMessage(ColorUtils.color("&cאתה לא יכול להכנס כשאתה במשחק."));
                return;
            }

        player.sendMessage(ColorUtils.color("&aנכנסת ל &2" + optionalArena.get().getDisplayName()));
        optionalArena.get().join(player, optionalArena);

    }

    @Override
    public String getName() {
        return "randomArena";
    }
}
