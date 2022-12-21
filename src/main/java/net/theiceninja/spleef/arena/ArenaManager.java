package net.theiceninja.spleef.arena;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.theiceninja.spleef.SpleefPlugin;
import net.theiceninja.spleef.states.ArenaListeners;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@RequiredArgsConstructor
public class ArenaManager {

    @Getter
    private List<Arena> arenas = new ArrayList<>();

    private final SpleefPlugin plugin;

    public void addArena(Arena arena, SpleefPlugin plugin) {
        if (arenas.isEmpty()) plugin.getServer().getPluginManager().registerEvents(new ArenaListeners(arena), plugin);
        arenas.add(arena);

        plugin.getConfig().set("arenas." + arena.getDisplayName() + ".arenaName", arena.getDisplayName());
        plugin.getConfig().set("arenas." + arena.getDisplayName() + ".minimumPlayers", arena.getMINIMUM_PLAYERS());
        plugin.getConfig().set("arenas." + arena.getDisplayName() + ".maximumPlayers", arena.getMAX_PLAYERS());
        plugin.getConfig().set("arenas." + arena.getDisplayName() + ".spectatorLocation", arena.getSpectatorLocation());
        plugin.getConfig().set("arenas." + arena.getDisplayName() + ".spawnLocation", arena.getSpawnLocation());
        plugin.saveConfig();
    }

    public void removeArena(String arenaName, SpleefPlugin plugin) {
        arenas.removeIf(arena1 ->
                arena1.getDisplayName().equalsIgnoreCase(arenaName));
        plugin.getConfig().set("arenas." + arenaName, null);
        plugin.saveConfig();
    }

    public void loadArenas(SpleefPlugin plugin) {
        for (String arenaKey : plugin.getConfig().getConfigurationSection("arenas").getKeys(false)) {
            ConfigurationSection configSection = plugin.getConfig().getConfigurationSection("arenas." + arenaKey);
            if (configSection == null) return;
            int maxPlayers = configSection.getInt("maximumPlayers");
            int minPlayers = configSection.getInt("minimumPlayers");
            Location spectatorLocation = configSection.getLocation("spectatorLocation");
            Location spawnLocation = configSection.getLocation("spawnLocation");
            String name = configSection.getString("arenaName");
            Arena arena = new Arena(name, maxPlayers, minPlayers, spawnLocation, spectatorLocation, ArenaState.DEFAULT, plugin);
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

        if (arena.getArenaState() == ArenaState.DEFAULT) return "&cמצב מכובה";

        if (arena.getArenaState() == ArenaState.COOLDOWN) return "&eמצב התכוננות למשחק";

        if (arena.getArenaState() == ArenaState.ACTIVE) return "&aמצב משחק";

        return null;
    }
}