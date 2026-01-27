package com.panda.bedwars.game.actors.shop;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public enum ShopCategory {
    QUICK_BUY("§lQuick Buy", "§bQuick Buy", Material.NETHER_STAR, 0, ShopData.getQuickBuyItems()),
    BLOCKS("§lBlocks", "§bBlocks", Material.TERRACOTTA, 1, ShopData.getBlockItems()),
    WEAPONS("§lWeapons", "§bWeapons", Material.GOLDEN_SWORD, 2, ShopData.getWeaponItems()),
    ARMOR("§lArmor", "§bArmor", Material.CHAINMAIL_BOOTS, 3, ShopData.getArmorItems()),
    TOOLS("§lTools", "§bTools", Material.STONE_PICKAXE, 4, ShopData.getToolItems()),
    RANGED("§lRanged", "§bRanged", Material.BOW, 5, ShopData.getRangedItems()),
    POTIONS("§lPotions", "§bPotions", Material.BREWING_STAND, 6, ShopData.getPotionItems()),
    UTILITY("§lUtility", "§bUtility", Material.TNT, 7, ShopData.getUtilityItems()),
    ROTATING("§lRotating Items", "§bRotating Items", Material.REDSTONE_TORCH, 8, ShopData.getRotatingItems());

    public final String title; // Name of the inventory
    public final String displayName; // Name of the icon
    public final Material icon;
    public final int slot;
    public final List<ItemStack> items;

    ShopCategory(String title, String displayName, Material icon, int slot,
                 List<ItemStack> items) {
        this.title = title;
        this.displayName = displayName;
        this.icon = icon;
        this.slot = slot;
        this.items = items;
    }
}