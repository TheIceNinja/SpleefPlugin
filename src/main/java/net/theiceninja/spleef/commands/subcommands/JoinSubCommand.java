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
public class JoinSubCommand implements SubCommand {

    private final ArenaManager arenaManager;

    @Override
    public void execute(Player player, String[] args) {

        if (arenaManager.getArenas().isEmpty()) {
            player.sendMessage(ColorUtils.color("&cאין כרגע שום ארנה מוכנה שאתה יכול להכנס אליה."));
            return;
        }
        if (args.length == 1) {
            player.sendMessage(ColorUtils.color("&cבבקשה ציין את שם הארנה שאתה רוצה להכנס."));
            return;
        }

        String arenaName = args[1];
        Optional<Arena> optionalArena = arenaManager.findArena(arenaName);

        if (!optionalArena.isPresent()) {
            player.sendMessage(ColorUtils.color("&cלא נמצאה שום ארנה עם השם &6'" + args[1] + "'"));
            return;
        }

        if (!(optionalArena.get().getArenaState() == ArenaState.DEFAULT || optionalArena.get().getArenaState() == ArenaState.COOLDOWN)) {
            player.sendMessage(ColorUtils.color("&cאי אפשר להכנס לארנה כשהיא במהלך משחק."));
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

        optionalArena.get().join(player, optionalArena);
    }

    @Override
    public String getName() {
        return "join";
    }
}
