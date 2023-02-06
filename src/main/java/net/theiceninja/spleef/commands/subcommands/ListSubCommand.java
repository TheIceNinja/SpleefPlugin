package net.theiceninja.spleef.commands.subcommands;

import lombok.RequiredArgsConstructor;
import net.theiceninja.spleef.arena.Arena;
import net.theiceninja.spleef.arena.manager.ArenaManager;
import net.theiceninja.spleef.utils.ColorUtil;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class ListSubCommand implements SubCommand {

    private final ArenaManager arenaManager;

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("spleef.admin")) {
            player.sendMessage(ColorUtil.color("&cסליחה, אבל אין לך גישה לבצע את הפקודה הזאת."));
            return;
        }

        if (arenaManager.getArenas().isEmpty()) {
            player.sendMessage(ColorUtil.color("&cעדיין אין ארנה מוכנה, אז לך צור אחת עם /spleef create"));
            return;
        }

        player.sendMessage(ColorUtil.color("&b======== &f&lArenas Info &b========"));
        for (Arena arena : arenaManager.getArenas())
            player.sendMessage(ColorUtil.color(
                    "&fArenaName&8: &b" + arena.getDisplayName() + " &fArenaState&8: " + arenaManager.getStateToString(arena) + " &fArenaMaxPlayers&8: &a"
                            + arena.getMAX_PLAYERS() + " &fArenaMinPlayers&8: &c" + arena.getMINIMUM_PLAYERS() + "\n&r\n&r"
            ));

        player.sendMessage(ColorUtil.color("&b================"));
    }

    @Override
    public String getName() {
        return "list";
    }
}
