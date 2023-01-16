package net.theiceninja.spleef;

import net.theiceninja.spleef.arena.manager.ArenaManager;
import net.theiceninja.spleef.commands.SpleefCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class SpleefPlugin extends JavaPlugin {

    private ArenaManager arenaManager;

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(false);
        saveDefaultConfig();

        this.arenaManager = new ArenaManager(this);
        registerCommands();

        // register arenas
        if (getConfig().getConfigurationSection("arenas") != null || !arenaManager.getArenas().isEmpty())
            arenaManager.loadArenas();
    }

    @Override
    public void onDisable() {}

    private void registerCommands() {
        getCommand("spleef").setExecutor(new SpleefCommand(arenaManager, this));
        getCommand("spleef").setTabCompleter(new SpleefCommand(arenaManager, this));
    }
}
