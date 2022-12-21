package net.theiceninja.spleef.arena;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.theiceninja.spleef.SpleefPlugin;
import net.theiceninja.spleef.tasks.CooldownGameTask;
import net.theiceninja.spleef.tasks.CooldownTask;
import net.theiceninja.spleef.utils.ColorUtils;
import net.theiceninja.spleef.utils.Items;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Getter @Setter
public class Arena {

    private String displayName;
    private int MAX_PLAYERS;
    private int MINIMUM_PLAYERS;
    private Location spawnLocation;
    private Location spectatorLocation;
    private ArenaState arenaState;
    private ArenaManager arenaManager;
    private CooldownGameTask cooldownGameTask;
    private CooldownTask cooldownTask;
    private List<Location> brokenBlocks = new ArrayList<>();
    private List<UUID> aliveUUID = new ArrayList<>();
    private List<UUID> spectatorUUID = new ArrayList<>();
    private SpleefPlugin plugin;
    private PlayerRollBackManager playerRollBackManager;


    public Arena(SpleefPlugin plugin) {
        this.plugin = plugin;
    }

    public Arena(String displayName, int MAX_PLAYERS, int MINIMUM_PLAYERS, Location spawnLocation, Location spectatorLocation, ArenaState arenaState, SpleefPlugin plugin) {
        this.displayName = displayName;
        this.MAX_PLAYERS = MAX_PLAYERS;
        this.MINIMUM_PLAYERS = MINIMUM_PLAYERS;
        this.spawnLocation = spawnLocation;
        this.spectatorLocation = spectatorLocation;
        this.arenaState = arenaState;
        this.plugin = plugin;
        playerRollBackManager = new PlayerRollBackManager();
    }

    public void setArenaState(ArenaState arenaState) {
        this.arenaState = arenaState;
     //   getServer().getPluginManager().registerEvents(new ArenaListeners(this), plugin);
        switch (arenaState) {
            case DEFAULT:
                if (cooldownTask != null) cooldownTask.cancel();
                break;
            case COOLDOWN:
                if (cooldownTask != null) cooldownTask.cancel();

                updateScoreboard();
                cooldownTask = new CooldownTask();
                cooldownTask.setArena(this);
                cooldownTask.runTaskTimer(plugin, 0, 20);

                for (UUID playerUUID : aliveUUID) {
                    Player player = Bukkit.getPlayer(playerUUID);
                    player.teleport(spawnLocation);
                    player.setHealth(20);
                }

                break;
            case ACTIVE:
                if (cooldownGameTask != null) cooldownGameTask.cancel();

                updateScoreboard();
                for (UUID playerUUID : aliveUUID) {
                    Player player = Bukkit.getPlayer(playerUUID);
                    player.getInventory().addItem(Items.spleefItem);
                }

                cooldownGameTask = new CooldownGameTask();
                cooldownGameTask.setArena(this);
                cooldownGameTask.runTaskTimer(plugin, 0, 20);
                playSound(Sound.MUSIC_DISC_PIGSTEP);
                break;
        }
    }

    public void addAlivePlayers(Player player) {
        playerRollBackManager.save(player);
        setScoreboard(player);
        aliveUUID.add(player.getUniqueId());
        updateScoreboard();
        player.setGameMode(GameMode.SURVIVAL);

        if (aliveUUID.size() == MINIMUM_PLAYERS) {
            setArenaState(ArenaState.COOLDOWN);
        }
    }

    public void removePlayer(Player player) {
        playerRollBackManager.restore(player);
        player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        aliveUUID.remove(player.getUniqueId());
        if (isSpectating(player)) {
            spectatorUUID.remove(player.getUniqueId());
        }

        updateScoreboard();
        player.stopAllSounds();

        if (arenaState == ArenaState.COOLDOWN) {
            if (cooldownTask != null) cooldownTask.cancel();

            setArenaState(ArenaState.DEFAULT);
            sendMessage("&cשחקן יצא, לכן המשחק חזר להיות במצב ממתין חכו לעוד שחקנים.");
            updateScoreboard();
        } else if (arenaState == ArenaState.ACTIVE) {
            updateScoreboard();
            if (isSpectating(player)) {
                spectatorUUID.remove(player.getUniqueId());
            }

            if (aliveUUID.size() == 0) {
                sendMessage("&c&l-> המשחק נגמר רבותי <-");
                cleanup();
            } else if (aliveUUID.size() == 1) {
                updateScoreboard();
                Player winner = Bukkit.getPlayer(aliveUUID.get(0));
                sendMessage(
                        "&r\n&b&l%name% &6&lwon the game!&r\n".replaceAll("%name%", winner.getDisplayName())
                );
                cleanup();
            }
        }
    }

    public void join(Player player, Optional<Arena> optionalArena) {
        if (!optionalArena.isPresent()) return;

        optionalArena.get().addAlivePlayers(player);
        playSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
        player.teleport(optionalArena.get().spawnLocation);
        optionalArena.get().sendMessage("&7[&a+&7] &2" + player.getDisplayName() + " &f#" + aliveUUID.size());
    }
    public void quit(Player player, Optional<Arena> optionalArena) {
        if (!optionalArena.isPresent()) return;

        playSound(Sound.ENTITY_WARDEN_HURT);
        sendMessage("&7[&c-&7] &4" + player.getDisplayName());
        optionalArena.get().removePlayer(player);
    }

    public void addSpectatorPlayers(Player player) {
        player.teleport(spectatorLocation);
        playSound(Sound.ENTITY_BLAZE_DEATH);
        spectatorUUID.add(player.getUniqueId());
        aliveUUID.remove(player.getUniqueId());
        updateScoreboard();
        player.setGameMode(GameMode.SPECTATOR);
        player.sendTitle(ColorUtils.color("&b&lSpleef"), ColorUtils.color("&cאתה מתת!"));
        sendMessage("&c" + player.getDisplayName() + " &edied!");

        if (aliveUUID.size() == 1) {
            updateScoreboard();
            playSound(Sound.ENTITY_ENDER_DRAGON_DEATH);
            Player winner = Bukkit.getPlayer(aliveUUID.get(0));
            sendMessage(
                    "&r\n&b&l%name% &6&lwon the game!&r\n".replaceAll("%name%", winner.getDisplayName())
            );
            cleanup();
        } else if (aliveUUID.size() == 0) {
            updateScoreboard();
            sendMessage("&cאין מנצח אבל המשחק נגמר!");
            cleanup();
        }
    }

    public void sendMessage(String str) {
        for (UUID playerUUID : aliveUUID) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) continue;
            player.sendMessage(ColorUtils.color(str));
        }
        for (UUID playerUUID : spectatorUUID) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) continue;
            player.sendMessage(ColorUtils.color(str));
        }
    }

    public void cleanup() {
        setArenaState(ArenaState.DEFAULT);
        if (cooldownGameTask != null) cooldownGameTask.cancel();
        if (cooldownTask != null) cooldownTask.cancel();
        stopSound(Sound.MUSIC_DISC_PIGSTEP);
        brokenBlocks.forEach(location -> location.getBlock().setType(Material.SNOW_BLOCK));

        for (UUID playerUUID : aliveUUID) {
            Player player = Bukkit.getPlayer(playerUUID);
            playerRollBackManager.restore(player);
            player.setFireTicks(0);
            player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        }

        for (UUID playerUUID : spectatorUUID) {
            Player player = Bukkit.getPlayer(playerUUID);
            playerRollBackManager.restore(player);
            player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        }

        aliveUUID.clear();
        spectatorUUID.clear();
    }

    public boolean isPlaying(Player player) {
        return aliveUUID.contains(player.getUniqueId());
    }

    public boolean isSpectating(Player player) {
        return spectatorUUID.contains(player.getUniqueId());
    }

    public void sendTitle(String s) {
        for (UUID playerUUID : aliveUUID) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) continue;
            player.sendTitle(ColorUtils.color("&b&lSpleef"), ColorUtils.color(s), 0, 40, 0);
        }
        for (UUID playerUUID : spectatorUUID) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) continue;
            player.sendTitle(ColorUtils.color("&b&lSpleef"), ColorUtils.color(s), 0, 40, 0);
        }
    }

    public void playSound(Sound sound, Location location) {
        Bukkit.getWorld(location.getWorld().getName()).playSound(location, sound, 1, 1);
    }

    public void sendActionBar(String s) {
        for (UUID playerUUID : aliveUUID) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) continue;
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ColorUtils.color(s)));
        }
        for (UUID playerUUID : spectatorUUID) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) continue;
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ColorUtils.color(s)));
        }
    }

    public void playSound(Sound sound) {
        for (UUID playerUUID : aliveUUID) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) continue;
            player.playSound(player, sound, (float) 0.3, 1);
        }

        for (UUID playerUUID : spectatorUUID) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) continue;
            player.playSound(player, sound, (float) 0.3, 1);
        }
    }

    public void stopSound(Sound sound) {
        for (UUID playerUUID : aliveUUID) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) continue;
            player.stopSound(sound);
        }
        for (UUID playerUUID : spectatorUUID) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) continue;
            player.stopSound(sound);
        }
    }

    public String getPlayerStatus(Player player) {
        if (isPlaying(player)) return "&2שחקן חי";
        if (isSpectating(player)) return "&7מצב צופה";
        return null;
    }

    public String getStateToString() {
        if (getArenaState() == ArenaState.DEFAULT) return "&cממתין לשחקנים..";
        if (getArenaState() == ArenaState.COOLDOWN) return "&eהכנה למשחק";
        if (getArenaState() == ArenaState.ACTIVE) return "&aפעיל";
        return null;
    }

    public void setScoreboard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getNewScoreboard();
        List<String> scoreboardLines = new ArrayList<>();
        Objective objective = scoreboard.registerNewObjective("ice", "dummy", ColorUtils.color("&#0bc1fb&lS&#34cefc&lk&#5cdafc&ly&#85e7fd&lu&#adf3fd&lp &7| &fספליף"));
        scoreboardLines.add("&f");
        if (arenaState == ArenaState.DEFAULT) {
            scoreboardLines.add("&fמצב שחקנים&8: &6" + aliveUUID.size() + "&7/&6" + MAX_PLAYERS);
            int playersLess = MINIMUM_PLAYERS - aliveUUID.size();
            scoreboardLines.add("&fחסר עוד שחקנים&8: &c" + playersLess);
        }

        if (arenaState == ArenaState.COOLDOWN) {
            if (cooldownTask != null)
            scoreboardLines.add("&fהמשחק מתחיל בעוד&8: &b" + cooldownTask.getTimeLeft());
        }
        scoreboardLines.add("&fמצב משחק&8: " + getStateToString());
        if (arenaState == ArenaState.ACTIVE && cooldownGameTask != null) {
            scoreboardLines.add("&fשחקנים חיים&8: &a" + aliveUUID.size());
            scoreboardLines.add("&fהמצב שלך&8: " + getPlayerStatus(player));
            scoreboardLines.add("&f ");
            scoreboardLines.add("&cהמשחק נגמר בעוד&8: &e" + cooldownGameTask.getTimeLeftUntilEnd()/60 + "&7:&e" + cooldownGameTask.getTimeLeftUntilEnd()%60);
        }
        scoreboardLines.add("&r");
        scoreboardLines.add("&7play.skyup.cf");
        for (int i = 0; i < scoreboardLines.size(); i++) {
            String line = ColorUtils.color(scoreboardLines.get(i));
            objective.getScore(line).setScore(scoreboardLines.size() - i);
        }
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        player.setScoreboard(scoreboard);
    }

    public void updateScoreboard() {
        for (UUID playerUUID : aliveUUID) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) continue;
            setScoreboard(player);
        }

        for (UUID playerUUID : spectatorUUID) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player == null) continue;
            setScoreboard(player);
        }
    }
}