package net.theiceninja.spleef.commands.subcommands;

import lombok.RequiredArgsConstructor;
import net.theiceninja.spleef.arena.Arena;
import net.theiceninja.spleef.arena.manager.ArenaManager;
import net.theiceninja.spleef.utils.ColorUtil;
import org.bukkit.entity.Player;

import java.util.Optional;

@RequiredArgsConstructor
public class QuitSubCommand implements SubCommand {

    private final ArenaManager arenaManager;

    @Override
    public void execute(Player player, String[] args) {
        if (arenaManager.getArenas().isEmpty()) {
            player.sendMessage(ColorUtil.color("&cאין שום ארנות ממה תצא אדוני?"));
            return;
        }

        Optional<Arena> optionalArena = arenaManager.getArenas().stream().filter(arena1 ->
                arena1.getAliveUUID().contains(player.getUniqueId()) ||
                arena1.getSpectatorUUID().contains(player.getUniqueId())).findAny();

        if (optionalArena.isEmpty()) {
            player.sendMessage(ColorUtil.color("&cאתה צריך להיות במשחק כדי לצאת מארנה."));
            return;
        }

        optionalArena.get().quit(player, optionalArena);
    }

    @Override
    public String getName() {
        return "quit";
    }
}
