package net.theiceninja.spleef.commands.subcommands;

import lombok.RequiredArgsConstructor;
import net.theiceninja.spleef.SpleefPlugin;
import net.theiceninja.spleef.arena.Arena;
import net.theiceninja.spleef.arena.ArenaManager;
import net.theiceninja.spleef.arena.ArenaState;
import net.theiceninja.spleef.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;

@RequiredArgsConstructor
public class ForceJoinSubCommand implements SubCommand {

    private final ArenaManager arenaManager;

    @Override
    public void execute(Player player, String[] args) {

        if (arenaManager.getArenas().isEmpty()) {
            player.sendMessage(ColorUtils.color("&cאין שום ארנות."));
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

        if (Bukkit.getOnlinePlayers().size() > optionalArena.get().getMAX_PLAYERS()) {
            player.sendMessage(ColorUtils.color("&cכמות השחקנים בשרת יותר גדולה מזו שבארנה."));
            return;
        }

        for (Arena arena : arenaManager.getArenas()) {
            for (Player player1 : Bukkit.getOnlinePlayers())
                if (arena.isPlaying(player1)) {
                    player1.sendMessage(ColorUtils.color("&cאתה לא יכול להכנס כשאתה במשחק."));
                    return;
                }
        }
        for (Player player1 : Bukkit.getOnlinePlayers()) {
            player1.sendMessage(ColorUtils.color("&aנכנסת ל &2" + optionalArena.get().getDisplayName()));
            optionalArena.get().join(player1, optionalArena);
        }
    }

    @Override
    public String getName() {
        return "forceJoin";
    }
}
