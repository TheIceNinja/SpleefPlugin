package net.theiceninja.spleef.commands.subcommands;

import lombok.RequiredArgsConstructor;
import net.theiceninja.spleef.arena.Arena;
import net.theiceninja.spleef.arena.ArenaManager;
import net.theiceninja.spleef.utils.ColorUtils;
import net.theiceninja.spleef.utils.Message;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class ListSubCommand implements SubCommand {

    private final ArenaManager arenaManager;


    @Override
    public void execute(Player player, String[] args) {

        if (!player.hasPermission("spleef.admin")) {
            player.sendMessage(Message.NO_PERMISSION);
            return;
        }

        if (arenaManager.getArenas().isEmpty()) {
            player.sendMessage(ColorUtils.color("&cעדיין אין ארנה מוכנה, אז לך צור אחת עם /spleef create"));
            return;
        }

        player.sendMessage(ColorUtils.color("&b======== &f&lArenas Info &b========"));
        for (Arena arena : arenaManager.getArenas())
            player.sendMessage(ColorUtils.color(
                    "&fArenaName&8: &b" + arena.getDisplayName() + " &fArenaState&8: " + arenaManager.getStateToString(arena) + " &fArenaMaxPlayers&8: &a"
                            + arena.getMAX_PLAYERS() + " &fArenaMinPlayers&8: &c" + arena.getMINIMUM_PLAYERS() + "\n&r\n&r"
            ));

        player.sendMessage(ColorUtils.color("&b================"));
    }

    @Override
    public String getName() {
        return "list";
    }
}
