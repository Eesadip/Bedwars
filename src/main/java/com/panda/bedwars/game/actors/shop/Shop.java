package com.panda.bedwars.game.actors.shop;

import com.panda.bedwars.Bedwars;
import com.panda.bedwars.game.VarUtil;
import com.panda.bedwars.game.actors.shop.utils.MenuBuilder;
import com.panda.bedwars.game.core.ArmorType;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.EnumSet;
import java.util.Set;

public class Shop {
    private Bedwars main;
    private NamespacedKey costKey;
    private NamespacedKey currencyKey;
    private Set<Material> noDuplicates;

    private Location location;
    private Villager villager;
    private ArmorStand shop;
    private ArmorStand click;

    public Shop(Bedwars main, Location location) {
        this.main = main;
        this.location = location;

        costKey = new NamespacedKey(main, "cost");
        currencyKey = new NamespacedKey(main, "currency");
        noDuplicates = EnumSet.of(Material.WOODEN_AXE, Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.STONE_AXE,
                Material.IRON_PICKAXE, Material.DIAMOND_PICKAXE, Material.STICK, Material.CHAINMAIL_BOOTS, Material.IRON_BOOTS, Material.DIAMOND_BOOTS);
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

    public void showShop(Player player, ShopCategory category) { player.openInventory(MenuBuilder.getShopPage(category)); }

    public void handleClick(Player player, int slot, ShopCategory category) {
        showShop(player, category);
        if (slot >= 9 && slot <= 17) return;

        switch (category) {
            case QUICK_BUY -> handleQuickBuy(player, slot);
            case BLOCKS -> handleBlocks(player, slot);
            case WEAPONS -> handleWeapons(player, slot);
            case ARMOR -> handleArmor(player, slot);
            case TOOLS -> handleTools(player, slot);
            case RANGED -> handleRanged(player, slot);
            case POTIONS -> handlePotions(player, slot);
        }
    }

    private Transaction canPurchase(Player player, ItemStack itemToPurchase) {
        // Ideally these won't run, but still exist to validate the item
        if (itemToPurchase == null) return null;
        if (!itemToPurchase.getItemMeta().getPersistentDataContainer().has(costKey, PersistentDataType.INTEGER)) return null;

        int cost = itemToPurchase.getItemMeta().getPersistentDataContainer().get(costKey, PersistentDataType.INTEGER);
        String currency = itemToPurchase.getItemMeta().getPersistentDataContainer().get(currencyKey, PersistentDataType.STRING);
        Material material = Material.valueOf(currency);
        if (!player.getInventory().containsAtLeast(new ItemStack(material), cost)) return null;

        return new Transaction(material, cost);
    }

    private ItemStack cloneItem(ItemStack item) {
        ItemStack clone = new  ItemStack(item.getType(), item.getAmount());
        ItemMeta itemMeta = item.getItemMeta();
        ItemMeta meta = clone.getItemMeta();

        if (itemMeta.hasEnchants()) item.getEnchantments().forEach((e, i) -> meta.addEnchant(e, i, true));
        meta.setUnbreakable(itemMeta.isUnbreakable());
        meta.setDisplayName(ChatColor.stripColor(itemMeta.getDisplayName()));

        if (item.getType() == Material.POTION) {
            ((PotionMeta) meta).setBasePotionData(((PotionMeta) itemMeta).getBasePotionData());
            ((PotionMeta) meta).setColor(((PotionMeta) itemMeta).getColor());
        }

        clone.setItemMeta(meta);
        return clone;
    }

    private boolean checkDuplicate(Player player, ItemStack item) {
        if (item == null) return false;
        return noDuplicates.contains(item.getType()) && player.getInventory().contains(item.getType());
    }

    private void completePurchase(Player player, ItemStack item, Material currency, int cost, boolean add) {
        if (add) player.getInventory().addItem(item);
        player.getInventory().removeItem(new ItemStack(currency, cost));
        player.sendMessage("§aYou bought §6" + item.getItemMeta().getDisplayName());
        player.updateInventory();
    }

    private void setArmor(Player player, Material boots, Material leggings, ArmorType type) {
        main.getGame().getLiveGame().setArmourType(player, type);
        player.getInventory().setBoots(new  ItemStack(boots));
        ItemStack legs = new ItemStack(leggings);
        ItemMeta lm = legs.getItemMeta();
        lm.setUnbreakable(true);
        legs.setItemMeta(lm);
        player.getInventory().setLeggings(legs);
    }

    private void handleQuickBuy(Player player, int slot) {
        ItemStack item = MenuBuilder.getQBItem(slot);
        Transaction t = canPurchase(player, item);
        if (t == null) {
            player.sendMessage("§cYou don't have enough money to buy this!");
            return;
        }
        if (checkDuplicate(player, item)) {
            player.sendMessage("§cYou already have that!");
            return;
        }

        int cost = t.cost;
        Material material = t.currency;
        ItemStack item2 = cloneItem(item);
        ArmorType playerArmor = main.getGame().getLiveGame().getArmourType(player);

        if (item.getType() == Material.WHITE_WOOL) {
            item2.setType(Material.valueOf(main.getGame().getLiveGame().getTeam(player).toUpperCase() + "_WOOL"));
        } else if (item.getType() == Material.STONE_SWORD) {
            if (player.getInventory().contains(Material.STONE_SWORD))
                player.sendMessage("§cYou already have that!");
            else {
                player.getInventory().setItem(0, item2);
                player.getInventory().removeItem(new ItemStack(material, cost));
                player.sendMessage("§aYou bought §6" + item.getItemMeta().getDisplayName().substring(2));
                player.updateInventory();
            }
            return;
        } else if (item.getType() == Material.CHAINMAIL_BOOTS) {
            if (playerArmor.compare(ArmorType.CHAINMAIL) >= 0) {
                player.sendMessage("§cYou already have " + (playerArmor == ArmorType.CHAINMAIL ? "that!" : "better armour!"));
                return;
            }

            setArmor(player, item.getType(), Material.CHAINMAIL_LEGGINGS, ArmorType.CHAINMAIL);
            completePurchase(player, item2, material, cost, false);
            return;
        } else if (item.getType() == Material.IRON_BOOTS) {
            if (playerArmor.compare(ArmorType.IRON) >= 0) {
                player.sendMessage("§cYou already have " + (playerArmor == ArmorType.IRON ? "that!" : "better armour!"));
                return;
            }

            setArmor(player, item.getType(), Material.IRON_LEGGINGS, ArmorType.IRON);
            completePurchase(player, item2, material, cost, false);
            return;
        } else if (item.getType() == Material.FIRE_CHARGE) {
            ItemMeta meta2 = item2.getItemMeta();
            meta2.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&cFireball"));
        } else if (item.getType() == Material.CHEST) {
            item2 = ShopData.getTower();
        } else if (item.getType() == Material.EGG) {
            item2 = ShopData.getBridgeEgg();
        }

        completePurchase(player, item2, material, cost, true);
    }

    private void handleBlocks(Player player, int slot) {
        ItemStack item = MenuBuilder.getBlockItem(slot);
        Transaction t = canPurchase(player, item);
        if (t == null) {
            player.sendMessage("§cYou don't have enough money to buy this!");
            return;
        }
        if (checkDuplicate(player, item)) {
            player.sendMessage("§cYou already have that!");
            return;
        }

        int cost = t.cost;
        Material material = t.currency;
        ItemStack item2 = cloneItem(item);

        if (item.getType() == Material.TERRACOTTA) {
            item2.setType(Material.valueOf(main.getGame().getLiveGame().getTeam(player).toUpperCase() + "_TERRACOTTA"));
        }

        completePurchase(player, item2, material, cost, true);
    }

    private void handleWeapons(Player player, int slot) {
        ItemStack item = MenuBuilder.getWeaponItem(slot);
        Transaction t = canPurchase(player, item);
        if (t == null) {
            player.sendMessage("§cYou don't have enough money to buy this!");
            return;
        }
        if (checkDuplicate(player, item)) {
            player.sendMessage("§cYou already have that!");
            return;
        }

        int cost = t.cost;
        Material material = t.currency;
        ItemStack item2 = cloneItem(item);

        completePurchase(player, item2, material, cost, true);
    }

    private void handleArmor(Player player, int slot) {
        ItemStack item = MenuBuilder.getArmorItem(slot);
        Transaction t = canPurchase(player, item);
        if (t == null) {
            player.sendMessage("§cYou don't have enough money to buy this!");
            return;
        }

        int cost = t.cost;
        Material material = t.currency;
        ItemStack item2 = cloneItem(item);
        ArmorType playerArmor = main.getGame().getLiveGame().getArmourType(player);

        if (item.getType() == Material.CHAINMAIL_BOOTS) {
            if (playerArmor.compare(ArmorType.CHAINMAIL) >= 0) {
                player.sendMessage("§cYou already have " + (playerArmor == ArmorType.CHAINMAIL ? "that!" : "better armour!"));
                return;
            }

            setArmor(player, item.getType(), Material.CHAINMAIL_LEGGINGS, ArmorType.CHAINMAIL);
        } else if (item.getType() == Material.IRON_BOOTS) {
            if (playerArmor.compare(ArmorType.IRON) >= 0) {
                player.sendMessage("§cYou already have " + (playerArmor == ArmorType.IRON ? "that!" : "better armour!"));
                return;
            }

            setArmor(player, item.getType(), Material.IRON_LEGGINGS, ArmorType.IRON);
        } else if (item.getType() == Material.DIAMOND_BOOTS) {
            if (playerArmor == ArmorType.DIAMOND) {
                player.sendMessage("§cYou already have that!");
                return;
            } else setArmor(player, item.getType(), Material.DIAMOND_LEGGINGS, ArmorType.DIAMOND);
        }

        completePurchase(player, item2, material, cost, false);
    }

    private void handleTools(Player player, int slot) {
        ItemStack item = MenuBuilder.getToolItem(slot);
        Transaction t = canPurchase(player, item);
        if (t == null) {
            player.sendMessage("§cYou don't have enough money to buy this!");
            return;
        }
        if (checkDuplicate(player, item)) {
            player.sendMessage("§cYou already have that!");
            return;
        }

        int cost = t.cost;
        Material material = t.currency;
        ItemStack item2 = cloneItem(item);

        completePurchase(player, item2, material, cost, true);
    }

    private void handleRanged(Player player, int slot) {
        ItemStack item = MenuBuilder.getRangedItem(slot);
        Transaction t = canPurchase(player, item);
        if (t == null) {
            player.sendMessage("§cYou don't have enough money to buy this!");
            return;
        }
        if (checkDuplicate(player, item)) {
            player.sendMessage("§cYou already have that!");
            return;
        }

        int cost = t.cost;
        Material material = t.currency;
        ItemStack item2 = cloneItem(item);

        completePurchase(player, item2, material, cost, true);
    }

    private void handlePotions(Player player, int slot) {
        ItemStack item = MenuBuilder.getPotionItem(slot);
        Transaction t = canPurchase(player, item);
        if (t == null) {
            player.sendMessage("§cYou don't have enough money to buy this!");
            return;
        }
        if (checkDuplicate(player, item)) {
            player.sendMessage("§cYou already have that!");
            return;
        }

        int cost = t.cost;
        Material material = t.currency;
        ItemStack item2 = cloneItem(item);

        completePurchase(player, item2, material, cost, true);
    }

    public void removeShops() {
        if (villager != null) villager.remove();
        if (shop != null) shop.remove();
        if (click != null) click.remove();
    }

    static class Transaction {
        Material currency;
        int cost;

        public Transaction(Material currency, int cost) {
            this.currency = currency;
            this.cost = cost;
        }
    }
}