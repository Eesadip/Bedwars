package com.panda.bedwars.game.actors.shop;

import com.panda.bedwars.Bedwars;
import com.panda.bedwars.game.VarUtil;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;

public class Shop {
    private Bedwars main;

    private Location location;
    private Villager villager;
    private ArmorStand shop;
    private ArmorStand click;

    private Inventory quickBuy;
    private Inventory blocks;
    private Inventory weapons;
    private Inventory armour;
    private Inventory tools;
    private Inventory ranged;
    private Inventory potion;
    private Inventory utility;

    private int page;

    public Shop(Bedwars main, Location location) {
        this.main = main;
        this.location = location;
        page = 0;
        quickBuy = MenuBuilder.buildQuickBuy();
        blocks = MenuBuilder.buildBlocks();
        weapons = MenuBuilder.buildWeapons();
        armour = MenuBuilder.buildArmor();
        tools = MenuBuilder.buildTools();
        ranged = MenuBuilder.buildRanged();
    }

    public void spawnShop() {
        villager = (Villager) Bukkit.getWorld(VarUtil.getWorldName()).spawnEntity(location, EntityType.VILLAGER);
        villager.setGravity(false);
        villager.setAI(false);
        villager.setInvulnerable(true);
        villager.setVillagerType(Villager.Type.SWAMP);
        villager.setSilent(true);

        click = (ArmorStand) Bukkit.getWorld(VarUtil.getWorldName()).spawnEntity(location, EntityType.ARMOR_STAND);
        click.setInvulnerable(true);
        click.setVisible(false);
        click.setGravity(false);
        click.setCustomNameVisible(true);
        click.setCustomName("§e§lClick me!");

        shop = (ArmorStand) Bukkit.getWorld(VarUtil.getWorldName()).spawnEntity(location.add(0, 0.3, 0), EntityType.ARMOR_STAND);
        shop.setInvulnerable(true);
        shop.setVisible(false);
        shop.setGravity(false);
        shop.setCustomNameVisible(true);
        shop.setCustomName("§b§lITEM SHOP");
    }

    public void showShop(Player player) {
        switch (page) {
            case 0:
                player.openInventory(quickBuy);
                break;
            case 1:
                player.openInventory(blocks);
                break;
            case 2:
                player.openInventory(weapons);
                break;
            case 3:
                player.openInventory(armour);
                break;
            case 4:
                player.openInventory(tools);
                break;
            default:
                break;
        }
    }

    public void handleClick(Player player, int slot) {
        showShop(player);
        if (page == 0) {
            handleQuickBuy(player, slot);
        } else if (page == 1) {
            handleBlocks(player, slot);
        } else if (page == 2) {
            handleWeapons(player, slot);
        } else if (page == 3) {
            handleArmor(player, slot);
        } else if (page == 4) {
            handleTools(player, slot);
        } else if (page == 5) {
            handleRanged(player, slot);
        }  else {
            player.sendMessage("§cYou don't have enough money to buy this!");
        }
    }

    private void handleQuickBuy(Player player, int slot) {
        ItemStack item = MenuBuilder.getQBItem(slot);
        if (item == null) return;
        if (!item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(Bedwars.getInstance(), "cost"), PersistentDataType.INTEGER)) return;

        int cost = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Bedwars.getInstance(), "cost"), PersistentDataType.INTEGER);
        String currency = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Bedwars.getInstance(), "currency"), PersistentDataType.STRING);
        Material material = Material.valueOf(currency);
        ItemStack item2 = new ItemStack(item.getType(), item.getAmount());
        ItemMeta meta = item2.getItemMeta();

        if (item.getItemMeta().hasEnchants()) {
            for (Map.Entry<Enchantment, Integer> map : item.getItemMeta().getEnchants().entrySet()) {
                meta.addEnchant(map.getKey(), map.getValue(), true);
            }
        }
        item2.setItemMeta(meta);

        if (player.getInventory().containsAtLeast(new ItemStack(material), cost)) {
            if (item.getType() == Material.WHITE_WOOL) {
                item2.setType(Material.valueOf(main.getGame().getLiveGame().getTeam(player).toUpperCase() + "_WOOL"));
            } else if (item.getType() == Material.WOODEN_PICKAXE) {
                if (player.getInventory().contains(Material.WOODEN_PICKAXE)) {
                    player.sendMessage("§cYou already have that!");
                    return;
                }
            } else if (item.getType() == Material.WOODEN_AXE) {
                if (player.getInventory().contains(Material.WOODEN_AXE)) {
                    player.sendMessage("§cYou already have that!");
                    return;
                }
                meta.setUnbreakable(true);
                item2.setItemMeta(meta);
            } else if (item.getType() == Material.STONE_SWORD) {
                if (player.getInventory().contains(Material.STONE_SWORD))
                    player.sendMessage("§cYou already have that!");
                else {
                    meta.setUnbreakable(true);
                    item2.setItemMeta(meta);
                    player.getInventory().setItem(0, item2);
                    player.getInventory().removeItem(new ItemStack(material, cost));
                    player.sendMessage("§aYou bought §6" + item.getItemMeta().getDisplayName().substring(2));
                    player.updateInventory();
                }
                return;
            } else if (item.getType() == Material.CHAINMAIL_BOOTS) {
                if (main.getGame().getLiveGame().getArmourType(player).equalsIgnoreCase("chainmail")) {
                    player.sendMessage("§cYou already have that!");
                    return;
                } else if (main.getGame().getLiveGame().getArmourType(player).equalsIgnoreCase("iron") ||
                        main.getGame().getLiveGame().getArmourType(player).equalsIgnoreCase("diamond")) {
                    player.sendMessage("§cYou already have better armour!");
                    return;
                }

                meta.setUnbreakable(true);
                item2.setItemMeta(meta);
                main.getGame().getLiveGame().setArmourType(player, "chainmail");
                player.getInventory().setBoots(item2);
                ItemStack legs = new ItemStack(Material.CHAINMAIL_LEGGINGS);
                ItemMeta lm = legs.getItemMeta();
                lm.setUnbreakable(true);
                legs.setItemMeta(lm);
                player.getInventory().setLeggings(legs);
                player.getInventory().removeItem(new ItemStack(material, cost));
                player.sendMessage("§aYou bought §6" + item.getItemMeta().getDisplayName().substring(2));
                player.updateInventory();
                return;
            } else if (item.getType() == Material.IRON_BOOTS) {
                if (main.getGame().getLiveGame().getArmourType(player).equalsIgnoreCase("iron")) {
                    player.sendMessage("§cYou already have that!");
                    return;
                }

                meta.setUnbreakable(true);
                item2.setItemMeta(meta);
                main.getGame().getLiveGame().setArmourType(player, "iron");
                player.getInventory().setBoots(item2);
                ItemStack legs = new ItemStack(Material.IRON_LEGGINGS);
                ItemMeta lm = legs.getItemMeta();
                lm.setUnbreakable(true);
                legs.setItemMeta(lm);
                player.getInventory().setLeggings(legs);
                player.getInventory().removeItem(new ItemStack(material, cost));
                player.sendMessage("§aYou bought §6" + item.getItemMeta().getDisplayName().substring(2));
                player.updateInventory();
                return;
            } else if (item.getType() == Material.FIRE_CHARGE) {
                ItemMeta meta2 = item2.getItemMeta();
                meta2.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&cFireball"));
            } else if (item.getType() == Material.CHEST) {
                player.getInventory().addItem(ShopData.getTower());
                player.getInventory().removeItem(new ItemStack(material, cost));
                player.updateInventory();
                player.sendMessage("§aYou bought §6" + item.getItemMeta().getDisplayName().substring(2));
                return;
            } else if (item.getType() == Material.EGG) {
                player.getInventory().addItem(ShopData.getBridgeEgg());
                player.getInventory().removeItem(new ItemStack(material, cost));
                player.updateInventory();
                player.sendMessage("§aYou bought §6" + item.getItemMeta().getDisplayName().substring(2));
                return;
            }

            player.getInventory().addItem(item2);
            player.getInventory().removeItem(new ItemStack(material, cost));
            player.sendMessage("§aYou bought §6" + item.getItemMeta().getDisplayName().substring(2));
            player.updateInventory();
        } else {
            player.sendMessage("§cYou don't have enough money to buy this!");
        }
    }

    private void handleBlocks(Player player, int slot) {
        ItemStack item = MenuBuilder.getBlockItem(slot);
        if (item == null) return;
        if (!item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(Bedwars.getInstance(), "cost"), PersistentDataType.INTEGER)) return;

        int cost = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Bedwars.getInstance(), "cost"), PersistentDataType.INTEGER);
        String currency = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Bedwars.getInstance(), "currency"), PersistentDataType.STRING);
        Material material = Material.valueOf(currency);
        ItemStack item2 = new ItemStack(item.getType(), item.getAmount());

        if (player.getInventory().containsAtLeast(new ItemStack(material), cost)) {
            if (item.getType() == Material.TERRACOTTA) {
                item2.setType(Material.valueOf(main.getGame().getLiveGame().getTeam(player).toUpperCase() + "_TERRACOTTA"));
            }

            player.getInventory().addItem(item2);
            player.getInventory().removeItem(new ItemStack(material, cost));
            player.sendMessage("§aYou bought §6" + item.getItemMeta().getDisplayName().substring(2));
            player.updateInventory();
        } else {
            player.sendMessage("§cYou don't have enough money to buy this!");
        }
    }

    private void handleWeapons(Player player, int slot) {
        ItemStack item = MenuBuilder.getWeaponItem(slot);
        if (item == null) return;
        if (!item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(Bedwars.getInstance(), "cost"), PersistentDataType.INTEGER)) return;

        int cost = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Bedwars.getInstance(), "cost"), PersistentDataType.INTEGER);
        String currency = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Bedwars.getInstance(), "currency"), PersistentDataType.STRING);
        Material material = Material.valueOf(currency);
        ItemStack item2 = new ItemStack(item.getType(), item.getAmount());
        ItemMeta meta = item2.getItemMeta();
        meta.setUnbreakable(true);

        if (item.getItemMeta().hasEnchants()) {
            for (Map.Entry<Enchantment, Integer> map : item.getItemMeta().getEnchants().entrySet()) {
                meta.addEnchant(map.getKey(), map.getValue(), true);
            }
        }
        item2.setItemMeta(meta);

        if (player.getInventory().containsAtLeast(new ItemStack(material), cost)) {
            if (player.getInventory().contains(item2) && item2.getType() == Material.STICK) {
                player.sendMessage("§cYou already have that!");
                return;
            }

            player.getInventory().addItem(item2);
            player.getInventory().removeItem(new ItemStack(material, cost));
            player.sendMessage("§aYou bought §6" + item.getItemMeta().getDisplayName().substring(2));
            player.updateInventory();
        } else {
            player.sendMessage("§cYou don't have enough money to buy this!");
        }
    }

    private void handleArmor(Player player, int slot) {
        ItemStack item = MenuBuilder.getArmorItem(slot);
        if (item == null) return;
        if (!item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(Bedwars.getInstance(), "cost"), PersistentDataType.INTEGER)) return;

        int cost = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Bedwars.getInstance(), "cost"), PersistentDataType.INTEGER);
        String currency = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Bedwars.getInstance(), "currency"), PersistentDataType.STRING);
        Material material = Material.valueOf(currency);
        ItemStack item2 = new ItemStack(item.getType(), item.getAmount());
        ItemMeta meta = item2.getItemMeta();
        meta.setUnbreakable(true);

        if (item.getItemMeta().hasEnchants()) {
            for (Map.Entry<Enchantment, Integer> map : item.getItemMeta().getEnchants().entrySet()) {
                meta.addEnchant(map.getKey(), map.getValue(), true);
            }
        }
        item2.setItemMeta(meta);

        if (player.getInventory().containsAtLeast(new ItemStack(material), cost)) {
            if (item.getType() == Material.CHAINMAIL_BOOTS) {
                if (main.getGame().getLiveGame().getArmourType(player).equalsIgnoreCase("chainmail")) {
                    player.sendMessage("§cYou already have that!");
                    return;
                } else if (main.getGame().getLiveGame().getArmourType(player).equalsIgnoreCase("iron") ||
                        main.getGame().getLiveGame().getArmourType(player).equalsIgnoreCase("diamond")) {
                    player.sendMessage("§cYou already have better armour!");
                    return;
                }

                meta.setUnbreakable(true);
                item2.setItemMeta(meta);
                main.getGame().getLiveGame().setArmourType(player, "chainmail");
                player.getInventory().setBoots(item2);
                ItemStack legs = new ItemStack(Material.CHAINMAIL_LEGGINGS);
                ItemMeta lm = legs.getItemMeta();
                lm.setUnbreakable(true);
                legs.setItemMeta(lm);

                player.getInventory().setLeggings(legs);
                player.getInventory().removeItem(new ItemStack(material, cost));
                player.sendMessage("§aYou bought §6" + item.getItemMeta().getDisplayName().substring(2));
                player.updateInventory();
                return;
            } else if (item.getType() == Material.IRON_BOOTS) {
                if (main.getGame().getLiveGame().getArmourType(player).equalsIgnoreCase("iron")) {
                    player.sendMessage("§cYou already have that!");
                    return;
                } else if (main.getGame().getLiveGame().getArmourType(player).equalsIgnoreCase("diamond")) {
                    player.sendMessage("§cYou already have better armour!");
                    return;
                }

                meta.setUnbreakable(true);
                item2.setItemMeta(meta);
                main.getGame().getLiveGame().setArmourType(player, "iron");
                player.getInventory().setBoots(item2);
                ItemStack legs = new ItemStack(Material.IRON_LEGGINGS);
                ItemMeta lm = legs.getItemMeta();
                lm.setUnbreakable(true);
                legs.setItemMeta(lm);

                player.getInventory().setLeggings(legs);
                player.getInventory().removeItem(new ItemStack(material, cost));
                player.sendMessage("§aYou bought §6" + item.getItemMeta().getDisplayName().substring(2));
                player.updateInventory();
                return;
            } else if (item.getType() == Material.DIAMOND_BOOTS) {
                if (main.getGame().getLiveGame().getArmourType(player).equalsIgnoreCase("diamond")) {
                    player.sendMessage("§cYou already have that!");
                    return;
                }

                meta.setUnbreakable(true);
                item2.setItemMeta(meta);
                main.getGame().getLiveGame().setArmourType(player, "diamond");
                player.getInventory().setBoots(item2);
                ItemStack legs = new ItemStack(Material.DIAMOND_LEGGINGS);
                ItemMeta lm = legs.getItemMeta();
                lm.setUnbreakable(true);
                legs.setItemMeta(lm);

                player.getInventory().setLeggings(legs);
                player.getInventory().removeItem(new ItemStack(material, cost));
                player.sendMessage("§aYou bought §6" + item.getItemMeta().getDisplayName().substring(2));
                player.updateInventory();
                return;
            }

            player.getInventory().addItem(item2);
            player.getInventory().removeItem(new ItemStack(material, cost));
            player.sendMessage("§aYou bought §6" + item.getItemMeta().getDisplayName().substring(2));
            player.updateInventory();
        } else {
            player.sendMessage("§cYou don't have enough money to buy this!");
        }
    }

    private void handleTools(Player player, int slot) {
        ItemStack item = MenuBuilder.getToolItem(slot);
        if (item == null) return;
        if (!item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(Bedwars.getInstance(), "cost"), PersistentDataType.INTEGER))
            return;

        int cost = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Bedwars.getInstance(), "cost"), PersistentDataType.INTEGER);
        String currency = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Bedwars.getInstance(), "currency"), PersistentDataType.STRING);
        Material material = Material.valueOf(currency);
        ItemStack item2 = new ItemStack(item.getType(), item.getAmount());
        ItemMeta meta = item2.getItemMeta();
        meta.setUnbreakable(true);

        if (item.getItemMeta().hasEnchants()) {
            for (Map.Entry<Enchantment, Integer> map : item.getItemMeta().getEnchants().entrySet()) {
                meta.addEnchant(map.getKey(), map.getValue(), true);
            }
        }
        item2.setItemMeta(meta);

        if (player.getInventory().containsAtLeast(new ItemStack(material), cost)) {
            if (item.getType() == Material.WOODEN_AXE) {
                if (player.getInventory().contains(Material.WOODEN_AXE)) {
                    player.sendMessage("§cYou already have that!");
                    return;
                }

                meta.setUnbreakable(true);
                item2.setItemMeta(meta);
                player.getInventory().addItem(item2);
                player.getInventory().removeItem(new ItemStack(material, cost));
                player.sendMessage("§aYou bought §6" + item.getItemMeta().getDisplayName().substring(2));
                player.updateInventory();
                return;
            } else if (item.getType() == Material.WOODEN_PICKAXE) {
                if (player.getInventory().contains(Material.WOODEN_PICKAXE)) {
                    player.sendMessage("§cYou already have that!");
                    return;
                }
                meta.setUnbreakable(true);
                item2.setItemMeta(meta);
                player.getInventory().addItem(item2);
                player.getInventory().removeItem(new ItemStack(material, cost));
                player.sendMessage("§aYou bought §6" + item.getItemMeta().getDisplayName().substring(2));
                player.updateInventory();
                return;
            } else if (item.getType() == Material.STONE_PICKAXE) {
                if (player.getInventory().contains(Material.STONE_PICKAXE)) {
                    player.sendMessage("§cYou already have that!");
                    return;
                }

                meta.setUnbreakable(true);
                item2.setItemMeta(meta);
                player.getInventory().addItem(item2);
                player.getInventory().removeItem(new ItemStack(material, cost));
                player.sendMessage("§aYou bought §6" + item.getItemMeta().getDisplayName().substring(2));
                player.updateInventory();
                return;
            } else if (item.getType() == Material.STONE_AXE) {
                if (player.getInventory().contains(Material.STONE_AXE)) {
                    player.sendMessage("§cYou already have that!");
                    return;
                }

                meta.setUnbreakable(true);
                item2.setItemMeta(meta);
                player.getInventory().addItem(item2);
                player.getInventory().removeItem(new ItemStack(material, cost));
                player.sendMessage("§aYou bought §6" + item.getItemMeta().getDisplayName().substring(2));
                player.updateInventory();
                return;
            } else if (item.getType() == Material.IRON_PICKAXE) {
                if (player.getInventory().contains(Material.IRON_PICKAXE)) {
                    player.sendMessage("§cYou already have that!");
                    return;
                }

                meta.setUnbreakable(true);
                item2.setItemMeta(meta);
                player.getInventory().addItem(item2);
                player.getInventory().removeItem(new ItemStack(material, cost));
                player.sendMessage("§aYou bought §6" + item.getItemMeta().getDisplayName().substring(2));
                player.updateInventory();
                return;
            } else if (item.getType() == Material.DIAMOND_PICKAXE) {
                if (player.getInventory().contains(Material.DIAMOND_PICKAXE)) {
                    player.sendMessage("§cYou already have that!");
                    return;
                }

                meta.setUnbreakable(true);
                item2.setItemMeta(meta);
                player.getInventory().addItem(item2);
                player.getInventory().removeItem(new ItemStack(material, cost));
                player.sendMessage("§aYou bought §6" + item.getItemMeta().getDisplayName().substring(2));
                player.updateInventory();
                return;
            } else if (item.getType() == Material.SHEARS) {
                meta.setUnbreakable(true);
                item2.setItemMeta(meta);
                player.getInventory().addItem(item2);
                player.getInventory().removeItem(new ItemStack(material, cost));
                player.sendMessage("§aYou bought §6" + item.getItemMeta().getDisplayName().substring(2));
                player.updateInventory();
                return;
            }

            player.getInventory().addItem(item2);
            player.getInventory().removeItem(new ItemStack(material, cost));
            player.sendMessage("§aYou bought §6" + item.getItemMeta().getDisplayName().substring(2));
            player.updateInventory();
        }
    }

    private void handleRanged(Player player, int slot) {
        ItemStack item = MenuBuilder.getToolItem(slot);
        if (item == null) return;
        if (!item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(Bedwars.getInstance(), "cost"), PersistentDataType.INTEGER))
            return;

        int cost = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Bedwars.getInstance(), "cost"), PersistentDataType.INTEGER);
        String currency = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Bedwars.getInstance(), "currency"), PersistentDataType.STRING);
        Material material = Material.valueOf(currency);
        ItemStack item2 = new ItemStack(item.getType(), item.getAmount());
        ItemMeta meta = item2.getItemMeta();
        meta.setUnbreakable(true);

        if (item.getItemMeta().hasEnchants()) {
            for (Map.Entry<Enchantment, Integer> map : item.getItemMeta().getEnchants().entrySet()) {
                meta.addEnchant(map.getKey(), map.getValue(), true);
            }
        }
        item2.setItemMeta(meta);

        if (player.getInventory().containsAtLeast(new ItemStack(material), cost)) {
            if (item.getType() == Material.WOODEN_AXE) {
                if (player.getInventory().contains(Material.WOODEN_AXE)) {
                    player.sendMessage("§cYou already have that!");
                    return;
                }

                meta.setUnbreakable(true);
                item2.setItemMeta(meta);
                player.getInventory().addItem(item2);
                player.getInventory().removeItem(new ItemStack(material, cost));
                player.sendMessage("§aYou bought §6" + item.getItemMeta().getDisplayName().substring(2));
                player.updateInventory();
                return;
            } else if (item.getType() == Material.WOODEN_PICKAXE) {
                if (player.getInventory().contains(Material.WOODEN_PICKAXE)) {
                    player.sendMessage("§cYou already have that!");
                    return;
                }
                meta.setUnbreakable(true);
                item2.setItemMeta(meta);
                player.getInventory().addItem(item2);
                player.getInventory().removeItem(new ItemStack(material, cost));
                player.sendMessage("§aYou bought §6" + item.getItemMeta().getDisplayName().substring(2));
                player.updateInventory();
                return;
            } else if (item.getType() == Material.STONE_PICKAXE) {
                if (player.getInventory().contains(Material.STONE_PICKAXE)) {
                    player.sendMessage("§cYou already have that!");
                    return;
                }

                meta.setUnbreakable(true);
                item2.setItemMeta(meta);
                player.getInventory().addItem(item2);
                player.getInventory().removeItem(new ItemStack(material, cost));
                player.sendMessage("§aYou bought §6" + item.getItemMeta().getDisplayName().substring(2));
                player.updateInventory();
                return;
            } else if (item.getType() == Material.STONE_AXE) {
                if (player.getInventory().contains(Material.STONE_AXE)) {
                    player.sendMessage("§cYou already have that!");
                    return;
                }

                meta.setUnbreakable(true);
                item2.setItemMeta(meta);
                player.getInventory().addItem(item2);
                player.getInventory().removeItem(new ItemStack(material, cost));
                player.sendMessage("§aYou bought §6" + item.getItemMeta().getDisplayName().substring(2));
                player.updateInventory();
                return;
            } else if (item.getType() == Material.IRON_PICKAXE) {
                if (player.getInventory().contains(Material.IRON_PICKAXE)) {
                    player.sendMessage("§cYou already have that!");
                    return;
                }

                meta.setUnbreakable(true);
                item2.setItemMeta(meta);
                player.getInventory().addItem(item2);
                player.getInventory().removeItem(new ItemStack(material, cost));
                player.sendMessage("§aYou bought §6" + item.getItemMeta().getDisplayName().substring(2));
                player.updateInventory();
                return;
            } else if (item.getType() == Material.DIAMOND_PICKAXE) {
                if (player.getInventory().contains(Material.DIAMOND_PICKAXE)) {
                    player.sendMessage("§cYou already have that!");
                    return;
                }

                meta.setUnbreakable(true);
                item2.setItemMeta(meta);
                player.getInventory().addItem(item2);
                player.getInventory().removeItem(new ItemStack(material, cost));
                player.sendMessage("§aYou bought §6" + item.getItemMeta().getDisplayName().substring(2));
                player.updateInventory();
                return;
            } else if (item.getType() == Material.SHEARS) {
                meta.setUnbreakable(true);
                item2.setItemMeta(meta);
                player.getInventory().addItem(item2);
                player.getInventory().removeItem(new ItemStack(material, cost));
                player.sendMessage("§aYou bought §6" + item.getItemMeta().getDisplayName().substring(2));
                player.updateInventory();
                return;
            }

            player.getInventory().addItem(item2);
            player.getInventory().removeItem(new ItemStack(material, cost));
            player.sendMessage("§aYou bought §6" + item.getItemMeta().getDisplayName().substring(2));
            player.updateInventory();
        }
    }

    public void removeShops() {
        villager.remove();
        shop.remove();
        click.remove();
    }

    public void setPage(int page) {
        this.page = page;
    }
}
