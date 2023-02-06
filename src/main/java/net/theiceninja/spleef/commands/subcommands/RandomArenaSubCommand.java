package net.theiceninja.spleef.commands.subcommands;

import lombok.RequiredArgsConstructor;
import net.theiceninja.spleef.arena.Arena;
import net.theiceninja.spleef.arena.manager.ArenaManager;
import net.theiceninja.spleef.arena.manager.ArenaState;
import net.theiceninja.spleef.utils.ColorUtil;
import org.bukkit.entity.Player;

import java.util.Optional;

@RequiredArgsConstructor
public class RandomArenaSubCommand implements SubCommand {

    private final ArenaManager arenaManager;

    @Override
    public void execute(Player player, String[] args) {

        if (arenaManager.getArenas().isEmpty()) {
            player.sendMessage(ColorUtil.color("&cאין שום ארנות שתוכל להכנס"));
            return;
        }

        Optional<Arena> optionalArena = arenaManager.getArenas().stream().filter(arena1 -> arena1.getArenaState() == ArenaState.COOLDOWN || arena1.getArenaState() == ArenaState.DEFAULT).findAny();
        if (optionalArena.isEmpty()) {
            player.sendMessage(ColorUtil.color("&cאין ארנה פנויה, תאלץ לחכות כמה זמן עד שתתפנה אחת."));
            return;
        }

        if (!(optionalArena.get().getArenaState() == ArenaState.DEFAULT || optionalArena.get().getArenaState() == ArenaState.COOLDOWN)) {
            player.sendMessage(ColorUtil.color("&cהארנה דלוקה ולכן אי אפשר להכנס."));
            return;
        }

        if (optionalArena.get().getMAX_PLAYERS() == optionalArena.get().getAliveUUID().size()) {
            player.sendMessage(ColorUtil.color("&cהארנה הגיעה לכמות השחקנית המקסימלית, לכן לא תוכל להכנס חכה לתור הבא."));
            return;
        }

        for (Arena arena : arenaManager.getArenas())
            if (arena.isPlaying(player)) {
                player.sendMessage(ColorUtil.color("&cאתה לא יכול להכנס כשאתה במשחק."));
                return;
            }

        player.sendMessage(ColorUtil.color("&aנכנסת ל &2" + optionalArena.get().getDisplayName()));
        optionalArena.get().join(player, optionalArena);

    }

    @Override
    public String getName() {
        return "randomArena";
    }
}
