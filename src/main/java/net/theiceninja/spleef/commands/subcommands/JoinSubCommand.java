package net.theiceninja.spleef.commands.subcommands;

import lombok.RequiredArgsConstructor;
import net.theiceninja.spleef.arena.Arena;
import net.theiceninja.spleef.arena.manager.ArenaManager;
import net.theiceninja.spleef.arena.manager.ArenaState;
import net.theiceninja.spleef.utils.ColorUtil;
import org.bukkit.entity.Player;

import java.util.Optional;

@RequiredArgsConstructor
public class JoinSubCommand implements SubCommand {

    private final ArenaManager arenaManager;

    @Override
    public void execute(Player player, String[] args) {

        if (arenaManager.getArenas().isEmpty()) {
            player.sendMessage(ColorUtil.color("&cאין כרגע שום ארנה מוכנה שאתה יכול להכנס אליה."));
            return;
        }

        if (args.length == 1) {
            player.sendMessage(ColorUtil.color("&cבבקשה ציין את שם הארנה שאתה רוצה להכנס."));
            return;
        }

        String arenaName = args[1];
        Optional<Arena> optionalArena = arenaManager.findArena(arenaName);

        if (optionalArena.isEmpty()) {
            player.sendMessage(ColorUtil.color("&cלא נמצאה שום ארנה עם השם &6'" + args[1] + "'"));
            return;
        }

        if (!(optionalArena.get().getArenaState() == ArenaState.DEFAULT || optionalArena.get().getArenaState() == ArenaState.COOLDOWN)) {
            player.sendMessage(ColorUtil.color("&cאי אפשר להכנס לארנה כשהיא במהלך משחק."));
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

        optionalArena.get().join(player, optionalArena);
    }

    @Override
    public String getName() {
        return "join";
    }
}
