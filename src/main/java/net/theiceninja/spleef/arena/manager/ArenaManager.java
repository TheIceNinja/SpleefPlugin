package net.theiceninja.spleef.arena.manager;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.theiceninja.spleef.SpleefPlugin;
import net.theiceninja.spleef.arena.Arena;
import net.theiceninja.spleef.arena.ArenaListeners;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class ArenaManager {

    @Getter
    private final List<Arena> arenas = new ArrayList<>();

    private SpleefPlugin plugin;

    public void addArena(Arena arena) {
        plugin.getServer().getPluginManager().registerEvents(new ArenaListeners(arena), plugin);
        arenas.add(arena);

        // add arena settings on the config
        plugin.getConfig().set("arenas." + arena.getDisplayName() + ".arenaName", arena.getDisplayName());
        plugin.getConfig().set("arenas." + arena.getDisplayName() + ".minimumPlayers", arena.getMINIMUM_PLAYERS());
        plugin.getConfig().set("arenas." + arena.getDisplayName() + ".maximumPlayers", arena.getMAX_PLAYERS());
        plugin.getConfig().set("arenas." + arena.getDisplayName() + ".spawnLocation", arena.getSpawnLocation());

        plugin.saveConfig();
    }

    public void removeArena(String arenaName) {
        arenas.removeIf(arena1 ->
                arena1.getDisplayName().equalsIgnoreCase(arenaName));

        plugin.getConfig().set("arenas." + arenaName, null);
        plugin.saveConfig();
    }

    public void loadArenas() {
        for (String arenaKey : plugin.getConfig().getConfigurationSection("arenas").getKeys(false)) {
            ConfigurationSection configSection = plugin.getConfig().getConfigurationSection("arenas." + arenaKey);
            if (configSection == null) return;

            int maxPlayers = configSection.getInt("maximumPlayers");
            int minPlayers = configSection.getInt("minimumPlayers");
            Location spawnLocation = configSection.getLocation("spawnLocation");
            String name = configSection.getString("arenaName");

            Arena arena = new Arena(name, maxPlayers, minPlayers, spawnLocation, ArenaState.DEFAULT, plugin);
            plugin.getServer().getPluginManager().registerEvents(new ArenaListeners(arena), plugin);

            arenas.add(arena);
        }
    }

    public Optional<Arena> findArena(String arenaName) {
       return getArenas().stream().filter(arena1 ->
               arena1.getDisplayName().equalsIgnoreCase(arenaName)).findAny();
    }

    public String getStateToString(Arena arena) {
        if (arena == null) return null;

        if (arena.getArenaState() == ArenaState.DEFAULT) return "&c?????? ??????????";
        if (arena.getArenaState() == ArenaState.COOLDOWN) return "&e?????? ???????????????? ??????????";
        if (arena.getArenaState() == ArenaState.ACTIVE) return "&a?????? ????????";

        return null;
    }
}
