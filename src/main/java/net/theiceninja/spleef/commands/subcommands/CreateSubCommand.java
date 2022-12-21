package net.theiceninja.spleef.commands.subcommands;

import lombok.RequiredArgsConstructor;
import net.theiceninja.spleef.SpleefPlugin;
import net.theiceninja.spleef.arena.Arena;
import net.theiceninja.spleef.arena.ArenaManager;
import net.theiceninja.spleef.arena.ArenaState;
import net.theiceninja.spleef.utils.ColorUtils;
import net.theiceninja.spleef.utils.Message;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class CreateSubCommand implements SubCommand {

    private final ArenaManager arenaManager;

    private final SpleefPlugin plugin;

    @Override
    public void execute(Player player, String[] args) {


        if (!player.hasPermission("spleef.admin")) {
            player.sendMessage(Message.NO_PERMISSION);
            return;
        }

        if (args.length < 4) {
            player.sendMessage(ColorUtils.color("&7Usage: /spleef create <name|maximumPlayers|minimumPlayers>"));
            return;
        }

        if (plugin.getConfig().getString("arenas." + args[1]) != null) {
            player.sendMessage(ColorUtils.color("&cאתה לא יכול ליצור עוד ארנה עם אותו שם."));
            return;
        }

        Arena arena;
        arena = new Arena(args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]), player.getLocation(), player.getLocation(), ArenaState.DEFAULT, plugin);
        arenaManager.addArena(arena, plugin);
        player.sendMessage(ColorUtils.color("&aיצרת בהצלחה את הארנה &2&l" + arena.getDisplayName()));
    }

    @Override
    public String getName() {
        return "create";
    }
}
