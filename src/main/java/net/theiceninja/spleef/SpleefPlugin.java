package net.theiceninja.spleef;

import net.theiceninja.spleef.arena.ArenaManager;
import net.theiceninja.spleef.commands.SpleefCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class SpleefPlugin extends JavaPlugin {

    private ArenaManager arenaManager;


    @Override
    public void onEnable() {
        super.onEnable();
        getConfig().options().copyDefaults(false);
        saveDefaultConfig();
        arenaManager = new ArenaManager(this);
        getCommand("spleef").setExecutor(new SpleefCommand(arenaManager, this));
        getCommand("spleef").setTabCompleter(new SpleefCommand(arenaManager, this));

        if (getConfig().getConfigurationSection("arenas") != null || !arenaManager.getArenas().isEmpty()) arenaManager.loadArenas(this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}