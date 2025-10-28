package com.panda.bedwars.game.listeners;

import com.panda.bedwars.Bedwars;
import com.panda.bedwars.game.VarUtil;
import com.panda.bedwars.game.core.Game;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class BlockListener implements Listener {
    private Bedwars main;
    private Game game;

    public BlockListener(Bedwars mains) {
        this.main = mains;
        game = main.getGame();
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if (game.isLive() && game.getPlayers().contains(e.getPlayer().getUniqueId())) {
            if (e.getPlayer().getLocation().getY() >= VarUtil.getBuildLimit()) {
                e.getPlayer().sendMessage(ChatColor.RED + "You have reached the build height limit!");
                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 1.0f);
                e.setCancelled(true);
                return;
            }
            e.getBlock().setMetadata("bedwars", new FixedMetadataValue(main, "shop"));

            if (e.getBlock().getType() == Material.TNT) {
                e.getBlock().setType(Material.AIR);
                Entity tnt = e.getBlock().getWorld().spawnEntity(e.getBlock().getLocation(), EntityType.PRIMED_TNT);
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (game.getLiveGame() == null) return;

        if (game.isLive() && e.getBlock().getType() == Material.valueOf(game.getLiveGame().getTeam(e.getPlayer()).toUpperCase() + "_BED")) {
            e.getPlayer().sendMessage(ChatColor.RED + "You can't break your own bed!");
            e.setCancelled(true);
            e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 1.0f);
            return;
        }

        if (game.isLive() && e.getBlock().getType().toString().contains("BED")) {
            String[] words = e.getBlock().getType().toString().split("_");
            game.getLiveGame().setBedBroken(words[0], e.getPlayer());
            e.setDropItems(false);
            return;
        }

        if (game.isLive() && game.getPlayers().contains(e.getPlayer().getUniqueId())) {
            if (!e.getBlock().hasMetadata("bedwars")) {
                e.getPlayer().sendMessage(ChatColor.RED + "Sorry you can't break that block!");
                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 1.0f);
                e.setCancelled(true);
            } else if (e.getBlock().getType() == Material.GLASS) {
                Item item = Bukkit.getWorld(VarUtil.getWorldName()).dropItem(e.getBlock().getLocation().add(0, 0.5, 0), new ItemStack(Material.GLASS));
                item.setVelocity(item.getVelocity().zero());
            }
        }
    }
}
