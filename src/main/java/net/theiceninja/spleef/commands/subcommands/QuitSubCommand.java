package net.theiceninja.spleef.commands.subcommands;

import lombok.RequiredArgsConstructor;
import net.theiceninja.spleef.SpleefPlugin;
import net.theiceninja.spleef.arena.Arena;
import net.theiceninja.spleef.arena.ArenaManager;
import net.theiceninja.spleef.utils.ColorUtils;
import org.bukkit.entity.Player;

import java.util.Optional;

@RequiredArgsConstructor
public class QuitSubCommand implements SubCommand {

    private final ArenaManager arenaManager;

    private final SpleefPlugin plugin;

    @Override
    public void execute(Player player, String[] args) {

        if (arenaManager.getArenas().isEmpty()) {
            player.sendMessage(ColorUtils.color("&cאין שום ארנות ממה תצא אדוני?"));
            return;
        }

        Optional<Arena> optionalArena = arenaManager.getArenas().stream().filter(arena1 ->  arena1.isPlaying(player)).findAny();
        if (!optionalArena.isPresent()) {
            player.sendMessage(ColorUtils.color("&cאתה צריך להיות במשחק כדי לצאת מארנה."));
            return;
        }

        optionalArena.get().quit(player, optionalArena);
    }

    @Override
    public String getName() {
        return "quit";
    }
}
