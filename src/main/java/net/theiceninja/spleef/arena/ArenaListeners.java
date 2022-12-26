package net.theiceninja.spleef.arena;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.theiceninja.spleef.arena.manager.ArenaState;
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
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class ArenaListeners implements Listener {

    @Getter
    private final Arena arena;

    @EventHandler
    private void onDrop(PlayerDropItemEvent event) {
        if (!arena.isPlaying(event.getPlayer())) return;
        // cancel drop items
        event.setCancelled(true);
    }

    @EventHandler
    private void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!arena.isPlaying((Player) event.getEntity())) return;

        event.setCancelled(true);
    }

    @EventHandler
    private void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!arena.isPlaying(event.getPlayer())) return;
            event.setDropItems(false);

        if (arena.getArenaState() != ArenaState.ACTIVE) event.setCancelled(true);

        if (arena.getArenaState() != ArenaState.ACTIVE) return;

        if (event.getBlock().getType() == Material.SNOW_BLOCK) {
            Location block = event.getBlock().getLocation();
            arena.getBrokenBlocks().add(block);
            arena.playSound(Sound.BLOCK_SNOW_BREAK, block);

            if (player.getInventory().firstEmpty() == -1) return;
            int random = (int) random(1, 8);
            int randomGet = (int) random(1, 12);

            if (random == 4 || random == 3) {
                for (int i = 1; i <= randomGet; i++)
                player.getInventory().addItem(new ItemStack(Material.SNOWBALL));
            }
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    private void onPlace(BlockPlaceEvent event) {
        if (!arena.isPlaying(event.getPlayer())) return;

        event.setCancelled(true);
    }

    @EventHandler
    private void onEntityInteractWithBlocks(ProjectileHitEvent event) {
        if (arena.getArenaState() != ArenaState.ACTIVE) return;
        if (!(event.getEntity() instanceof Snowball)) return;
        if (event.getHitBlock() == null) return;

        if (event.getHitBlock().getType() == Material.SNOW_BLOCK) {
            arena.getBrokenBlocks().add(event.getHitBlock().getLocation());
            event.getHitBlock().setType(Material.AIR);
        }
    }

    @EventHandler
    private void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!arena.isPlaying(player)) return;
        if (!(arena.getArenaState() == ArenaState.ACTIVE)) return;

        if ((player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.WATER ||
        player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.LEGACY_WATER_LILY ||
                player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.LEGACY_WATER_LILY ||
                player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.LAVA ||
                player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.LEGACY_LAVA ||
                player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.LEGACY_STATIONARY_LAVA))

            // add to spectator because he died
              arena.addSpectatorPlayers(player);
    }

    @EventHandler
    private void onSwitch(PlayerSwapHandItemsEvent event) {
        if (!arena.isPlaying(event.getPlayer())) return;
             event.setCancelled(true);
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (!arena.isPlaying(player)) return;
        arena.removePlayer(player);
    }

    @EventHandler
    private void onFoodLevelChanged(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!arena.isPlaying((Player) event.getEntity())) return;

        event.setCancelled(true);
    }

    @EventHandler
    private void onCrafting(CraftItemEvent event) {
        if (!arena.isPlaying((Player) event.getWhoClicked())) return;

        event.setCancelled(true);
    }

    @EventHandler
    private void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (getArena().isPlaying(player) || getArena().isSpectating(player)) {
            if (!event.getMessage().equalsIgnoreCase("/spleef quit")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ColorUtils.color("&cאתה לא יכול לעשות את זה בזמן משחק!"));
            }
        }
    }

    @EventHandler
    private void onPickUpItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();

        if (!arena.isPlaying(player) || !arena.isSpectating(player)) return;
        event.setCancelled(true);
    }

    private double random(int a, int b) {
        return (double) b + (Math.random() * (a - b + 1));
    }

}