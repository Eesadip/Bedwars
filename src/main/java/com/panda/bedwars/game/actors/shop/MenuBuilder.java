package com.panda.bedwars.game.actors.shop;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class MenuBuilder {
    private static Inventory quick;
    private static Inventory blocks;
    private static Inventory weapons;
    private static Inventory armor;
    private static Inventory tools;
    private static Inventory ranged;
    private static Inventory potion;
    private static Inventory utility;

    public static Inventory buildQuickBuy() {
        quick = Bukkit.createInventory(null, 54, "§lQuick Buy");
        setOptions(quick);
        setPanes(quick, 0);

        ItemStack item = quick.getItem(0);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bQuick Buy");
        meta.setLore(null);
        item.setItemMeta(meta);
        quick.setItem(0, item);

        int ind = 19;
        for (ItemStack s : ShopData.getQuickBuyItems()) {
            if (ind%9 == 0 || (ind + 1)%9 == 0)
                ind++;
            quick.setItem(ind++, s);
            if (ind%9 == 0 || (ind + 1)%9 == 0)
                ind++;
        }

        return quick;
    }

    public static ItemStack getQBItem(int index) {
        return quick.getItem(index);
    }

    public static Inventory buildBlocks() {
        blocks = Bukkit.createInventory(null, 54, "§lBlocks");
        setOptions(blocks);
        setPanes(blocks, 1);

        ItemStack item = blocks.getItem(1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bBlocks");
        meta.setLore(null);
        item.setItemMeta(meta);
        blocks.setItem(1, item);

        int ind = 19;
        for (ItemStack s : ShopData.getBlockItems()) {
            if (ind%9 == 0 || (ind + 1)%9 == 0)
                ind++;
            blocks.setItem(ind++, s);
            if (ind%9 == 0 || (ind + 1)%9 == 0)
                ind++;
        }

        return blocks;
    }

    public static ItemStack getBlockItem(int index) { return blocks.getItem(index); }

    public static Inventory buildWeapons() {
        weapons = Bukkit.createInventory(null, 54, "§lWeapons");
        setOptions(weapons);
        setPanes(weapons, 2);

        ItemStack item = weapons.getItem(2);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bWeapons");
        meta.setLore(null);
        item.setItemMeta(meta);
        weapons.setItem(2, item);

        int ind = 19;
        for (ItemStack s : ShopData.getWeaponItems()) {
            if (ind%9 == 0 || (ind + 1)%9 == 0)
                ind++;
            weapons.setItem(ind++, s);
            if (ind%9 == 0 || (ind + 1)%9 == 0)
                ind++;
        }

        return weapons;
    }

    public static ItemStack getWeaponItem(int index) { return weapons.getItem(index); }

    public static Inventory buildArmor() {
        armor = Bukkit.createInventory(null, 54, "§lArmor");
        setOptions(armor);
        setPanes(armor, 3);

        ItemStack item = armor.getItem(3);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bArmor");
        meta.setLore(null);
        item.setItemMeta(meta);
        armor.setItem(3, item);

        int ind = 19;
        for (ItemStack s : ShopData.getArmorItems()) {
            if (ind%9 == 0 || (ind + 1)%9 == 0)
                ind++;
            armor.setItem(ind++, s);
            if (ind%9 == 0 || (ind + 1)%9 == 0)
                ind++;
        }

        return armor;
    }

    public static ItemStack getArmorItem(int index) { return armor.getItem(index); }

    public static Inventory buildTools() {
        tools = Bukkit.createInventory(null, 54, "§lTools");
        setOptions(tools);
        setPanes(tools, 4);

        ItemStack item = tools.getItem(4);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bTools");
        meta.setLore(null);
        item.setItemMeta(meta);
        tools.setItem(4, item);

        int ind = 19;
        for (ItemStack s : ShopData.getToolItems()) {
            if (ind%9 == 0 || (ind + 1)%9 == 0)
                ind++;
            tools.setItem(ind++, s);
            if (ind%9 == 0 || (ind + 1)%9 == 0)
                ind++;
        }

        return tools;
    }

    public static ItemStack getToolItem(int index) { return tools.getItem(index); }

    public static Inventory buildRanged() {
        ranged = Bukkit.createInventory(null, 54, "§lRanged");
        setOptions(ranged);
        setPanes(ranged, 5);

        ItemStack item = ranged.getItem(5);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§bRanged");
        meta.setLore(null);
        item.setItemMeta(meta);
        ranged.setItem(5, item);

        int ind = 19;
        for (ItemStack s : ShopData.getToolItems()) {
            if (ind%9 == 0 || (ind + 1)%9 == 0)
                ind++;
            ranged.setItem(ind++, s);
            if (ind%9 == 0 || (ind + 1)%9 == 0)
                ind++;
        }

        return ranged;
    }

    public static ItemStack getRangedItem(int index) { return ranged.getItem(index); }

    /* Basic item setters common to all shop guis */
    private static void setOptions(Inventory inv) {
        ItemStack star = new ItemStack(Material.NETHER_STAR);
        ItemMeta starMeta = star.getItemMeta();
        starMeta.setDisplayName("§aQuick buy");
        starMeta.setLore(Arrays.asList("§eClick to view!"));
        star.setItemMeta(starMeta);
        inv.setItem(0, star);

        ItemStack block = new ItemStack(Material.TERRACOTTA);
        ItemMeta blockMeta = block.getItemMeta();
        blockMeta.setDisplayName("§aBlocks");
        blockMeta.setLore(Arrays.asList("§eClick to view!"));
        block.setItemMeta(blockMeta);
        inv.setItem(1, block);

        ItemStack sword = new ItemStack(Material.GOLDEN_SWORD);
        ItemMeta swordMeta = sword.getItemMeta();
        swordMeta.setDisplayName("§aWeapons");
        swordMeta.setLore(Arrays.asList("§eClick to view!"));
        sword.setItemMeta(swordMeta);
        inv.setItem(2, sword);

        ItemStack armor = new ItemStack(Material.CHAINMAIL_BOOTS);
        ItemMeta armorMeta = armor.getItemMeta();
        armorMeta.setDisplayName("§aArmor");
        armorMeta.setLore(Arrays.asList("§eClick to view!"));
        armor.setItemMeta(armorMeta);
        inv.setItem(3, armor);

        ItemStack tool = new ItemStack(Material.STONE_PICKAXE);
        ItemMeta toolMeta = tool.getItemMeta();
        toolMeta.setDisplayName("§aTools");
        toolMeta.setLore(Arrays.asList("§eClick to view!"));
        tool.setItemMeta(toolMeta);
        inv.setItem(4, tool);

        ItemStack ranged = new ItemStack(Material.BOW);
        ItemMeta rangedMeta = ranged.getItemMeta();
        rangedMeta.setDisplayName("§aRanged");
        rangedMeta.setLore(Arrays.asList("§eClick to view!"));
        ranged.setItemMeta(rangedMeta);
        inv.setItem(5, ranged);

        ItemStack potion = new ItemStack(Material.BREWING_STAND);
        ItemMeta potionMeta = potion.getItemMeta();
        potionMeta.setDisplayName("§aPotion");
        potionMeta.setLore(Arrays.asList("§eClick to view!"));
        potion.setItemMeta(potionMeta);
        inv.setItem(6, potion);

        ItemStack utility = new ItemStack(Material.BREWING_STAND);
        ItemMeta utilityMeta = utility.getItemMeta();
        utilityMeta.setDisplayName("§aPotion");
        utilityMeta.setLore(Arrays.asList("§eClick to view!"));
        utility.setItemMeta(utilityMeta);
        inv.setItem(7, utility);
    }

    private static void setPanes(Inventory inv, int page) {
        ItemStack pane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta paneMeta = pane.getItemMeta();
        paneMeta.setDisplayName("§8↑ Categories");
        paneMeta.setLore(Arrays.asList("§8↓ Items"));
        pane.setItemMeta(paneMeta);

        for (int i = 9; i <= 17; i++) {
            if (i == 9 + page) {
                ItemStack gpane = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
                ItemMeta gpaneMeta = pane.getItemMeta();
                gpaneMeta.setDisplayName("§8↑ Categories");
                gpaneMeta.setLore(Arrays.asList("§8↓ Items"));
                gpane.setItemMeta(gpaneMeta);
                inv.setItem(i, gpane);
            } else
                inv.setItem(i, pane);
        }
    }
}
