package com.panda.bedwars.game.actors.shop.utils;

import com.panda.bedwars.Bedwars;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

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
        section.getStringList("lore").forEach(l -> lore.add(ChatColor.translateAlternateColorCodes('&', l)));
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

        if (section.contains("effects") && section.getString("effects") != null) {
            String effect =  section.getString("effects");
            PotionMeta potionMeta = (PotionMeta) meta;
            potionMeta.setBasePotionData(new PotionData(PotionType.valueOf(effect)));
            potionMeta.setColor(PotionEffectType.getByName(effect).getColor());
            item.setItemMeta(potionMeta);
        }

        item.setItemMeta(meta);

        return item;
    }
}
