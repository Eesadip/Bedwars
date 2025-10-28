package com.panda.bedwars.game.actors.shop;

import com.panda.bedwars.Bedwars;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {
    private ItemBuilder() {
    }

    public static ItemStack getItem(ConfigurationSection section) {
        Material material;
        try {
            material = Material.valueOf(section.getString("material"));
        } catch (Exception e) {
            material = Material.AIR;
        }

        String name =  ChatColor.translateAlternateColorCodes('&', section.getString("name"));
        int amount = section.getInt("amount");
        List<String> lore = new ArrayList<>();
        for (String s : section.getStringList("lore"))
            lore.add(ChatColor.translateAlternateColorCodes('&', s));
        int cost = section.getInt("cost");
        String currency = section.getString("currency");

        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        meta.getPersistentDataContainer().set(new NamespacedKey(Bedwars.getInstance(), "cost"), PersistentDataType.INTEGER, cost);
        meta.getPersistentDataContainer().set(new NamespacedKey(Bedwars.getInstance(), "currency"), PersistentDataType.STRING, currency);

        ConfigurationSection ench = section.getConfigurationSection("enchantments");
        if (ench != null) {
            for (String enchantmentKey : ench.getKeys(false)) {
                Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantmentKey.toLowerCase()));
                if (enchantment != null) {
                    int level = ench.getInt(enchantmentKey);
                    meta.addEnchant(enchantment, level, true);
                }
            }
        }

        item.setItemMeta(meta);

        return item;
    }
}
