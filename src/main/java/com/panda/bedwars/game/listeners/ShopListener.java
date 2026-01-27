package com.panda.bedwars.game.listeners;

import com.panda.bedwars.Bedwars;
import com.panda.bedwars.game.actors.shop.ShopCategory;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import java.util.Arrays;
import java.util.List;

public class ShopListener implements Listener {
    private Bedwars main;
    private List<String> titles;

    public ShopListener(Bedwars main) {
        this.main = main;
        this.titles = Arrays.asList("Quick Buy", "Blocks", "Weapons", "Armor", "Tools", "Ranged", "Potions", "Utility", "Rotating Items");
    }

    @EventHandler
    public void onClick(PlayerInteractAtEntityEvent e) {
        if (!main.getGame().isLive() && !main.getGame().getPlayers().contains(e.getPlayer().getUniqueId()))
            return;

        if (e.getRightClicked().getType() == EntityType.VILLAGER) {
            e.setCancelled(true);
            main.getGame().getLiveGame().getShop(e.getPlayer()).showShop(e.getPlayer(), ShopCategory.QUICK_BUY);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;
        if (main.getGame().isLive() && e.getClickedInventory().getType() == InventoryType.PLAYER && e.getSlotType() == InventoryType.SlotType.ARMOR) {
            e.setCancelled(true);
            return;
        } else if (e.getRawSlot() >= e.getInventory().getSize())
            return;

        String title = ChatColor.stripColor(e.getView().getTitle());
        if (title != null && titles.contains(title)) {
            Player player = (Player) e.getWhoClicked();
            e.setCancelled(true);

            if (e.getRawSlot() >= 0 && e.getRawSlot() < 9) {
                for (ShopCategory category : ShopCategory.values()) {
                    if (category.slot == e.getRawSlot()) main.getGame().getLiveGame().getShop(player).showShop(player, category);
                }
            } else {
                for (ShopCategory category : ShopCategory.values()) {
                    if (title.contains(ChatColor.stripColor(category.title))) {
                        main.getGame().getLiveGame().getShop(player).handleClick(player, e.getRawSlot(), category);
                        return;
                    }
                }
            }
        }
    }
}
