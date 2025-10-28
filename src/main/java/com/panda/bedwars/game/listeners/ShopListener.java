package com.panda.bedwars.game.listeners;

import com.panda.bedwars.Bedwars;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class ShopListener implements Listener {
    private Bedwars main;

    public ShopListener(Bedwars main) {
        this.main = main;
    }

    @EventHandler
    public void onClick(PlayerInteractAtEntityEvent e) {
        if (!main.getGame().isLive() && !main.getGame().getPlayers().contains(e.getPlayer().getUniqueId()))
            return;

        if (e.getRightClicked().getType() == EntityType.VILLAGER) {
            e.setCancelled(true);
            main.getGame().getLiveGame().getShop(e.getPlayer()).setPage(0);
            main.getGame().getLiveGame().getShop(e.getPlayer()).showShop(e.getPlayer());
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (main.getGame().isLive() && e.getClickedInventory().getType() == InventoryType.PLAYER && e.getSlotType() == InventoryType.SlotType.ARMOR) {
            e.setCancelled(true);
            return;
        } else if (e.getRawSlot() >= e.getInventory().getSize())
            return;

        String title = e.getView().getTitle();
        if (title != null && (title.contains("Buy") || title.contains("Blocks") || title.contains("Weapons") || title.contains("Armor") || title.contains("Tools"))) {
            Player player = (Player) e.getWhoClicked();
            e.setCancelled(true);

            if (e.getRawSlot() >= 0 && e.getRawSlot() < 9) {
                main.getGame().getLiveGame().getShop(player).setPage(e.getRawSlot());
                main.getGame().getLiveGame().getShop(player).showShop(player);
            } else
                main.getGame().getLiveGame().getShop(player).handleClick(player, e.getRawSlot());
        }
    }
}
