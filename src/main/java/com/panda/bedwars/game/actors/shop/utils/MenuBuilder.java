package com.panda.bedwars.game.actors.shop.utils;

import com.panda.bedwars.game.actors.shop.ShopCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class MenuBuilder {
    private MenuBuilder() {}

    public static Inventory getShopPage(ShopCategory shopCategory) {
        Inventory page = Bukkit.createInventory(null, 54, shopCategory.title);
        setCategories(page);
        setPanes(page, shopCategory.slot);

        // Set the current page's item name to highlight it
        ItemStack item = new ItemStack(shopCategory.icon);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(shopCategory.displayName);
        meta.setLore(Arrays.asList("§eClick to view!"));
        item.setItemMeta(meta);
        page.setItem(shopCategory.slot, item);

        fill(page, shopCategory);

        return page;
    }

    private static void setCategories(Inventory inv) {
        for (ShopCategory shopCategory : ShopCategory.values()) {
            ItemStack item = new ItemStack(shopCategory.icon);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§a" + ChatColor.stripColor(shopCategory.title));
            meta.setLore(Arrays.asList("§eClick to view!"));
            item.setItemMeta(meta);
            inv.setItem(shopCategory.slot, item);
        }
    }

    private static void setPanes(Inventory inv, int currPage) {
        ItemStack gray = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = gray.getItemMeta();
        meta.setDisplayName("§8↑ Categories");
        meta.setLore(Arrays.asList("§8↓ Items"));
        gray.setItemMeta(meta);

        ItemStack green =  new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        green.setItemMeta(gray.getItemMeta());

        for (int i = 9; i <= 17; i++)
            inv.setItem(i, i == 9 + currPage ? green : gray);
    }

    private static void fill(Inventory inv, ShopCategory shopCategory) {
        int start = 19;

        for (ItemStack item : shopCategory.items) {
            while (isEdge(start)) start++;
            inv.setItem(start++, item);
        }
    }

    private static boolean isEdge(int slot) {
        return slot % 9 == 0 || (slot + 1) % 9 == 0;
    }

    // -------------------------------------------------------------------------
    public static ItemStack getQBItem(int index) { return MenuBuilder.getShopPage(ShopCategory.QUICK_BUY).getItem(index); }

    public static ItemStack getBlockItem(int index) { return MenuBuilder.getShopPage(ShopCategory.BLOCKS).getItem(index); }

    public static ItemStack getWeaponItem(int index) { return MenuBuilder.getShopPage(ShopCategory.WEAPONS).getItem(index); }

    public static ItemStack getArmorItem(int index) { return MenuBuilder.getShopPage(ShopCategory.ARMOR).getItem(index); }

    public static ItemStack getToolItem(int index) { return MenuBuilder.getShopPage(ShopCategory.TOOLS).getItem(index); }

    public static ItemStack getRangedItem(int index) { return MenuBuilder.getShopPage(ShopCategory.RANGED).getItem(index); }

    public static ItemStack getPotionItem(int index) { return MenuBuilder.getShopPage(ShopCategory.POTIONS).getItem(index); }
}