package net.theiceninja.spleef.commands.subcommands;

import lombok.RequiredArgsConstructor;
import net.theiceninja.spleef.SpleefPlugin;
import net.theiceninja.spleef.arena.manager.ArenaManager;
import net.theiceninja.spleef.utils.ColorUtil;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class DeleteSubCommand implements SubCommand {

    private final ArenaManager arenaManager;
    private final SpleefPlugin plugin;

    @Override
    public void execute(Player player, String[] args) {
        if (!player.hasPermission("spleef.admin")) {
            player.sendMessage(ColorUtil.color("&cסליחה, אבל אין לך גישה לבצע את הפקודה הזאת."));
            return;
        }

        if (args.length == 1) {
            player.sendMessage(ColorUtil.color("&7Usage: /spleef delete <arenaName>"));
            return;
        }

        if (plugin.getConfig().getString("arenas." + args[1]) == null) {
            player.sendMessage(ColorUtil.color("&cהארנה עם השם הזה לא נמצאה, תעשה&8: &c/spleef list כדי לראות את רשימת הארנות."));
            return;
        }

        arenaManager.removeArena(args[1]);
        player.sendMessage(ColorUtil.color("&aהסרת בהצלחה את הארנה &2&l" + args[1]));
    }

    @Override
    public String getName() {
        return "delete";
    }
}
