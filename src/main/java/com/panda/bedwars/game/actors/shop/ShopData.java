package com.panda.bedwars.game.actors.shop;

import com.panda.bedwars.Bedwars;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class ShopData {
    private static File file;
    private static YamlConfiguration configuration;
    private static ItemStack tower;
    private static ItemStack bridgeEgg;

    private ShopData() {
    }

    public static void init(Bedwars main) {
        file = new File(main.getDataFolder(), "shop.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                main.getLogger().log(Level.SEVERE, "File: " + file.getName() + " was not loaded.");
            }
        }

        configuration = YamlConfiguration.loadConfiguration(file);

        tower = new ItemStack(Material.CHEST);
        ItemMeta meta = tower.getItemMeta();
        meta.setDisplayName("§bCompact Pop-up tower");
        tower.setItemMeta(meta);

        bridgeEgg = new ItemStack(Material.EGG);
        ItemMeta eggItemMeta = bridgeEgg.getItemMeta();
        eggItemMeta.setDisplayName("§bBridge Egg");
        bridgeEgg.setItemMeta(eggItemMeta);
    }

    public static List<ItemStack> getQuickBuyItems() {
        List<ItemStack> items = new ArrayList<>();
        ConfigurationSection section = configuration.getConfigurationSection("quick-buy");
        if (section == null) {
            System.out.println("[!] Quick buy items not set up!");
            return null;
        }

        for (String key : section.getKeys(false)) {
            ConfigurationSection item = section.getConfigurationSection(key);
            items.add(ItemBuilder.getItem(item));
        }

        return items;
    }

    public static List<ItemStack> getBlockItems() {
        List<ItemStack> items = new ArrayList<>();
        ConfigurationSection section = configuration.getConfigurationSection("blocks");
        if (section == null) {
            System.out.println("[!] Block items not set up!");
            return null;
        }

        for (String key : section.getKeys(false)) {
            ConfigurationSection item = section.getConfigurationSection(key);
            items.add(ItemBuilder.getItem(item));
        }

        return items;
    }

    public static List<ItemStack> getWeaponItems() {
        List<ItemStack> items = new ArrayList<>();
        ConfigurationSection section = configuration.getConfigurationSection("weapons");
        if (section == null) {
            System.out.println("[!] Weapon items not set up!");
            return null;
        }

        for (String key : section.getKeys(false)) {
            ConfigurationSection item = section.getConfigurationSection(key);
            items.add(ItemBuilder.getItem(item));
        }

        return items;
    }

    public static List<ItemStack> getArmorItems() {
        List<ItemStack> items = new ArrayList<>();
        ConfigurationSection section = configuration.getConfigurationSection("armor");
        if (section == null) {
            System.out.println("[!] Armor items not set up!");
            return null;
        }

        for (String key : section.getKeys(false)) {
            ConfigurationSection item = section.getConfigurationSection(key);
            items.add(ItemBuilder.getItem(item));
        }

        return items;
    }

    public static List<ItemStack> getToolItems() {
        List<ItemStack> items = new ArrayList<>();
        ConfigurationSection section = configuration.getConfigurationSection("tools");
        if (section == null) {
            System.out.println("[!] Tool items not set up!");
            return null;
        }

        for (String key : section.getKeys(false)) {
            ConfigurationSection item = section.getConfigurationSection(key);
            items.add(ItemBuilder.getItem(item));
        }

        return items;
    }

    public static ItemStack getTower() {
        return tower;
    }

    public static ItemStack getBridgeEgg() {
        return bridgeEgg;
    }
}
