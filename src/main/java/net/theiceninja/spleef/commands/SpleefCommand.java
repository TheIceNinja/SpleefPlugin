package net.theiceninja.spleef.commands;

import net.theiceninja.spleef.SpleefPlugin;
import net.theiceninja.spleef.arena.manager.ArenaManager;
import net.theiceninja.spleef.commands.subcommands.*;
import net.theiceninja.spleef.utils.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SpleefCommand implements CommandExecutor, TabCompleter {

    private final List<SubCommand> subCommandList = new ArrayList<>();

    public SpleefCommand(ArenaManager arenaManager, SpleefPlugin plugin) {
        subCommandList.add(new CreateSubCommand(arenaManager, plugin));
        subCommandList.add(new JoinSubCommand(arenaManager));
        subCommandList.add(new QuitSubCommand(arenaManager));
        subCommandList.add(new ListSubCommand(arenaManager));
        subCommandList.add(new RandomArenaSubCommand(arenaManager));
        subCommandList.add(new ForceJoinSubCommand(arenaManager));
        subCommandList.add(new DeleteSubCommand(arenaManager, plugin));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ColorUtil.color("&cOnly a player can execute this command!"));
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            if (player.hasPermission("spleef.admin")) {
                player.sendMessage(ColorUtil.color("&7Usage: /spleef <create|delete|list|join|quit|randomArena>"));
            } else {
                player.sendMessage(ColorUtil.color("&7Usage: /spleef <join|quit|randomArena>"));
            }
            return true;
        }

        for (SubCommand subCommand : subCommandList) {
            if (args[0].equalsIgnoreCase(subCommand.getName())) {
                subCommand.execute(player, args);
                break;
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> complete = new ArrayList<>();

        if (args.length == 1 && sender.hasPermission("spleef.admin")) {
            complete.add("create");
            complete.add("list");
            complete.add("delete");
            complete.add("join");
            complete.add("quit");
            complete.add("randomArena");
            complete.add("forceJoin");
        } else if (args.length == 1) {
            complete.add("join");
            complete.add("quit");
            complete.add("randomArena");
        }

            List<String> result = new ArrayList<>();
            if (args.length == 1) {
                for (String a : complete) {
                    if (a.toLowerCase().startsWith(args[0].toLowerCase()))
                        result.add(a);
                }

                return result;
            }
        return null;
    }
}