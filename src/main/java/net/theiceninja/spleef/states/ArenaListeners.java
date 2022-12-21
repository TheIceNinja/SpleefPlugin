package net.theiceninja.spleef.states;

import lombok.Data;
import net.theiceninja.spleef.arena.Arena;
import net.theiceninja.spleef.arena.ArenaState;
import net.theiceninja.spleef.utils.ColorUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

@Data
public class ArenaListeners implements Listener {

    private final Arena arena;

    public ArenaListeners(Arena arena) {
        this.arena = arena;
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (!arena.isPlaying(event.getPlayer())) return;
        if (arena.getArenaState() == ArenaState.ACTIVE ||
                arena.getArenaState() == ArenaState.COOLDOWN || arena.getArenaState() == ArenaState.DEFAULT) event.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!arena.isPlaying((Player) event.getEntity())) return;
        if (arena.getArenaState() == ArenaState.ACTIVE ||
                arena.getArenaState() == ArenaState.COOLDOWN || arena.getArenaState() == ArenaState.DEFAULT) event.setCancelled(true);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!arena.isPlaying(event.getPlayer())) return;
        event.setDropItems(false);
        if (arena.getArenaState() == ArenaState.COOLDOWN || arena.getArenaState() == ArenaState.DEFAULT) event.setCancelled(true);
        if (!(arena.getArenaState() == ArenaState.ACTIVE)) return;
        if (event.getBlock().getType() == Material.SNOW_BLOCK) {
            Location block = event.getBlock().getLocation();
            arena.getBrokenBlocks().add(block);
            arena.playSound(Sound.BLOCK_SNOW_BREAK, block);
            if (player.getInventory().firstEmpty() == -1) return;
            int random = (int) (Math.random() * 7);
            if (random == 4)
            player.getInventory().addItem(new ItemStack(Material.SNOWBALL));
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (!arena.isPlaying(event.getPlayer())) return;
        if (arena.getArenaState() == ArenaState.ACTIVE ||
                arena.getArenaState() == ArenaState.COOLDOWN || arena.getArenaState() == ArenaState.DEFAULT) event.setCancelled(true);
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityInteractWithBlocks(ProjectileHitEvent event) {
        if (arena.getArenaState() != ArenaState.ACTIVE) return;
        if (!(event.getEntity() instanceof Snowball)) return;
        if (event.getHitBlock() == null) return;
        if (event.getHitBlock().getType() == Material.SNOW_BLOCK) {
            arena.getBrokenBlocks().add(event.getHitBlock().getLocation());
            event.getHitBlock().setType(Material.AIR);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!arena.isPlaying(player)) return;
        if (!(arena.getArenaState() == ArenaState.ACTIVE)) return;
        if (event.getPlayer().getLocation().getBlock() == null) return;
        if ((player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.WATER ||
        player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.LEGACY_WATER_LILY ||
                player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.LEGACY_WATER_LILY ||
                player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.LAVA ||
                player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.LEGACY_LAVA ||
                player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.LEGACY_STATIONARY_LAVA))
              arena.addSpectatorPlayers(player);

    }

    @EventHandler
    public void onSwitch(PlayerSwapHandItemsEvent event) {
        if (!arena.isPlaying(event.getPlayer())) return;
        if (arena.getArenaState() == ArenaState.COOLDOWN ||
                arena.getArenaState() == ArenaState.DEFAULT ||
                arena.getArenaState() == ArenaState.ACTIVE) event.setCancelled(true);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (!arena.isPlaying(player)) return;
        arena.removePlayer(player);
    }

    @EventHandler
    public void onFoodLevelChanged(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!arena.isPlaying((Player) event.getEntity())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onCrafting(CraftItemEvent event) {
        if (!arena.isPlaying((Player) event.getWhoClicked())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (getArena().isPlaying(event.getPlayer())) {
            if (!event.getMessage().equalsIgnoreCase("/spleef quit")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ColorUtils.color("&cאתה לא יכול לעשות את זה בזמן משחק!"));
            }
        }
        if (getArena().isSpectating(event.getPlayer())) {
            if (!event.getMessage().equalsIgnoreCase("/spleef quit")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ColorUtils.color("&cאתה לא יכול לעשות את זה בזמן משחק!"));
            }
        }
    }
}