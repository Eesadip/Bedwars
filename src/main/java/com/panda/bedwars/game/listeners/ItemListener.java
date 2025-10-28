package com.panda.bedwars.game.listeners;

import com.panda.bedwars.Bedwars;
import com.panda.bedwars.game.actors.shop.ShopData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Ladder;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class ItemListener implements Listener {
    private Bedwars main;
    private List<Location> locationList;
    private Egg egg;

    public ItemListener(Bedwars main) {
        this.main = main;
        locationList = new ArrayList<>();
    }

    @EventHandler
    public void onEntityDamage(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL && main.getGame().isLive() && main.getGame().getPlayers().contains(player.getUniqueId())) {
            event.setCancelled(true);
            player.teleport(event.getTo());
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (!main.getGame().isLive()) return;
        if (!main.getGame().getPlayers().contains(e.getPlayer().getUniqueId())) return;

        if (e.getHand() == EquipmentSlot.HAND && (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            if (e.getItem() != null && e.getItem().getType() == Material.FIRE_CHARGE) {
                Fireball fireball = e.getPlayer().launchProjectile(Fireball.class, e.getPlayer().getEyeLocation().getDirection());
                fireball.setYield(3);
                fireball.getLocation().add(fireball.getVelocity().normalize().multiply(3));
                e.setCancelled(true);
                ItemStack fb = new ItemStack(Material.FIRE_CHARGE, 1);
                e.getPlayer().getInventory().removeItem(fb);
                e.getPlayer().updateInventory();
            }
        }
    }

    @EventHandler
    public void onThrow(ProjectileLaunchEvent e) {
        if (!(e.getEntity() instanceof Egg)) return;
        egg = (Egg) e.getEntity();

        BukkitRunnable task = new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (egg.isDead()) {
                    cancel();
                    egg = null;
                    return;
                }
                locationList.add(egg.getLocation().clone().subtract(0, 2, 0));
                ticks++;

                if (ticks >= 30) {
                    egg.remove();
                    makeBridge(((Player) e.getEntity().getShooter()).getFacing(),
                            Material.valueOf(main.getGame().getLiveGame().getTeam(((Player) e.getEntity().getShooter())).toUpperCase() + "_WOOL"));
                    cancel();
                }
            }
        };
        task.runTaskTimer(main, 1, 1);
    }

    @EventHandler
    public void onHit(ProjectileHitEvent e) {
        if (e.getEntity() instanceof Fireball) {
            if (e.getHitBlock().getType() == Material.FIRE) {
                e.setCancelled(true);
            }
        } else if (e.getEntity() instanceof Egg) {
            makeBridge(((Player) e.getEntity().getShooter()).getFacing(),
                    Material.valueOf(main.getGame().getLiveGame().getTeam(((Player) e.getEntity().getShooter())).toUpperCase() + "_WOOL"));
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent e) {
        if (e.getEntity() instanceof TNTPrimed) {
            e.setCancelled(true);
            e.getLocation().getWorld().createExplosion(e.getLocation(), 5.0f, false, false);
            for (Block block : e.blockList()) {
                if (block.getWorld().getBlockAt(block.getLocation()).hasMetadata("bedwars")) {
                    if (block.getType() != Material.GLASS) {
                        block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(block.getType()));
                        block.getWorld().getBlockAt(block.getLocation()).setType(Material.AIR);
                    }
                }
            }
        } else if (e.getEntity() instanceof Fireball) {
            e.setCancelled(true);
            e.getLocation().getWorld().createExplosion(e.getLocation(), 6.0f, false, false);
            for (Block block : e.blockList()) {
                if (block.getWorld().getBlockAt(block.getLocation()).hasMetadata("bedwars")) {
                    if (block.getType() != Material.AIR) {
                        block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(block.getType()));
                        block.getWorld().getBlockAt(block.getLocation()).setType(Material.AIR);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if (!main.getGame().isLive()) return;
        if (!main.getGame().getPlayers().contains(e.getPlayer().getUniqueId())) return;

        makeTower(e, Material.valueOf(main.getGame().getLiveGame().getTeam(e.getPlayer()).toUpperCase() + "_WOOL"));
    }

    private void makeTower(BlockPlaceEvent e, Material m) {
        if (e.getBlock().getType() != Material.CHEST) return;
        if (e.getPlayer().getInventory().getItemInMainHand().getType() != Material.CHEST) return;
        if (!e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().contains("Pop-up")) return;

        e.setCancelled(true);
        e.getPlayer().getInventory().removeItem(ShopData.getTower());

        Location loc = e.getBlock().getLocation();
        Location copy;
        int[] num = {2, -2};
        int[] num2 = {-1, 0, 1};

        BlockFace dir = BlockFace.SOUTH;
        switch (e.getPlayer().getFacing()) {
            case NORTH:
                dir = BlockFace.SOUTH;
                break;
            case SOUTH:
                dir = BlockFace.NORTH;
                break;
            case EAST:
                dir = BlockFace.WEST;
                break;
            case WEST:
                dir = BlockFace.EAST;
                break;
        }

        int lock = 0, lock2 = 0;

        if (e.getPlayer().getFacing() == BlockFace.WEST) {
            for (int h : num) {
                for (int i : num2) {
                    for (int j = 0; j < 5; j++) {
                        loc = e.getBlock().getLocation().add(h, j, i);
                        if (loc.getWorld().getBlockAt(loc).getType() == Material.AIR) {
                            if (lock > 1 || i != 0) {
                                loc.getWorld().getBlockAt(loc).setType(m);
                                loc.getWorld().getBlockAt(loc).setMetadata("bedwars", new FixedMetadataValue(main, "shop"));
                                loc.getWorld().playSound(loc, Sound.BLOCK_STONE_STEP, 10f, 1f);
                            }

                            if (i == 0 && lock2 == 1) {
                                Location copy2 = loc.add((h > 0 ? -1 : 1), 0, 0);
                                loc.getWorld().getBlockAt(copy2).setType(Material.LADDER);
                                Ladder data = (Ladder) loc.getWorld().getBlockAt(copy2).getBlockData();
                                data.setFacing(dir);
                                loc.getWorld().getBlockAt(copy2).setBlockData(data);
                                loc.getWorld().getBlockAt(loc).setMetadata("bedwars", new FixedMetadataValue(main, "shop"));
                            }
                        }

                        if (i == 0)
                            lock++;
                    }
                }
                lock2++;
            }

            for (int h : num) {
                for (int i : num2) {
                    for (int j = 0; j < 5; j++) {
                        loc = e.getBlock().getLocation();
                        copy = loc.clone();
                        if (loc.getWorld().getBlockAt(loc.add(i, j, h)).getType() == Material.AIR) {
                            loc.getWorld().getBlockAt(copy.add(i, j, h)).setType(m);
                            loc.getWorld().playSound(loc, Sound.BLOCK_STONE_STEP, 10f, 1f);
                            loc.getWorld().getBlockAt(loc).setMetadata("bedwars", new FixedMetadataValue(main, "shop"));
                        }
                    }
                }
            }
        } else if (e.getPlayer().getFacing() == BlockFace.EAST) {
            num = new int[]{-2, 2};
            num2 = new int[]{-1, 0, 1};

            for (int h : num) {
                for (int i : num2) {
                    for (int j = 0; j < 5; j++) {
                        loc = e.getBlock().getLocation().add(h, j, i);
                        if (loc.getWorld().getBlockAt(loc).getType() == Material.AIR) {
                            if (lock > 1 || i != 0) {
                                loc.getWorld().getBlockAt(loc).setType(m);
                                loc.getWorld().playSound(loc, Sound.BLOCK_STONE_STEP, 10f, 1f);
                                loc.getWorld().getBlockAt(loc).setMetadata("bedwars", new FixedMetadataValue(main, "shop"));
                            }

                            if (i == 0 && lock2 == 1) {
                                Location copy2 = loc.add((h > 0 ? -1 : 1), 0, 0);
                                loc.getWorld().getBlockAt(copy2).setType(Material.LADDER);
                                Ladder data = (Ladder) loc.getWorld().getBlockAt(copy2).getBlockData();
                                data.setFacing(dir);
                                loc.getWorld().getBlockAt(copy2).setBlockData(data);
                                loc.getWorld().getBlockAt(loc).setMetadata("bedwars", new FixedMetadataValue(main, "shop"));
                            }
                        }

                        if (i == 0)
                            lock++;
                    }
                }
                lock2++;
            }

            for (int h : num) {
                for (int i : num2) {
                    for (int j = 0; j < 5; j++) {
                        loc = e.getBlock().getLocation();
                        copy = loc.clone();
                        if (loc.getWorld().getBlockAt(loc.add(i, j, h)).getType() == Material.AIR) {
                            loc.getWorld().getBlockAt(copy.add(i, j, h)).setType(m);
                            loc.getWorld().playSound(loc, Sound.BLOCK_STONE_STEP, 10f, 1f);
                            loc.getWorld().getBlockAt(loc).setMetadata("bedwars", new FixedMetadataValue(main, "shop"));
                        }
                    }
                }
            }
        } else if (e.getPlayer().getFacing() == BlockFace.NORTH) {
            num = new int[]{2, -2};
            num2 = new int[]{-1, 0, 1};

            for (int h : num) {
                for (int i : num2) {
                    for (int j = 0; j < 5; j++) {
                        loc = e.getBlock().getLocation().add(i, j, h);
                        if (loc.getWorld().getBlockAt(loc).getType() == Material.AIR) {
                            if (lock > 1 || i != 0) {
                                loc.getWorld().getBlockAt(loc).setType(m);
                                loc.getWorld().playSound(loc, Sound.BLOCK_STONE_STEP, 10f, 1f);
                                loc.getWorld().getBlockAt(loc).setMetadata("bedwars", new FixedMetadataValue(main, "shop"));
                            }

                            if (i == 0 && lock2 == 1) {
                                Location copy2 = loc.add(0, 0, (h > 0 ? -1 : 1));
                                loc.getWorld().getBlockAt(copy2).setType(Material.LADDER);
                                Ladder data = (Ladder) loc.getWorld().getBlockAt(copy2).getBlockData();
                                data.setFacing(dir);
                                loc.getWorld().getBlockAt(copy2).setBlockData(data);
                                loc.getWorld().getBlockAt(loc).setMetadata("bedwars", new FixedMetadataValue(main, "shop"));
                            }
                        }

                        if (i == 0)
                            lock++;
                    }
                }
                lock2++;
            }

            for (int h : num) {
                for (int i : num2) {
                    for (int j = 0; j < 5; j++) {
                        loc = e.getBlock().getLocation();
                        copy = loc.clone();
                        if (loc.getWorld().getBlockAt(loc.add(h, j, i)).getType() == Material.AIR) {
                            loc.getWorld().getBlockAt(copy.add(h, j, i)).setType(m);
                            loc.getWorld().playSound(loc, Sound.BLOCK_STONE_STEP, 10f, 1f);
                            loc.getWorld().getBlockAt(loc).setMetadata("bedwars", new FixedMetadataValue(main, "shop"));
                        }
                    }
                }
            }
        } else if (e.getPlayer().getFacing() == BlockFace.SOUTH) {
            num = new int[]{-2, 2};
            num2 = new int[]{-1, 0, 1};

            for (int h : num) {
                for (int i : num2) {
                    for (int j = 0; j < 5; j++) {
                        loc = e.getBlock().getLocation().add(i, j, h);
                        if (loc.getWorld().getBlockAt(loc).getType() == Material.AIR) {
                            if (lock > 1 || i != 0) {
                                loc.getWorld().getBlockAt(loc).setType(m);
                                loc.getWorld().playSound(loc, Sound.BLOCK_STONE_STEP, 10f, 1f);
                                loc.getWorld().getBlockAt(loc).setMetadata("bedwars", new FixedMetadataValue(main, "shop"));
                            }

                            if (i == 0 && lock2 == 1) {
                                Location copy2 = loc.add(0, 0, (h > 0 ? -1 : 1));
                                loc.getWorld().getBlockAt(copy2).setType(Material.LADDER);
                                Ladder data = (Ladder) loc.getWorld().getBlockAt(copy2).getBlockData();
                                data.setFacing(dir);
                                loc.getWorld().getBlockAt(copy2).setBlockData(data);
                                loc.getWorld().getBlockAt(loc).setMetadata("bedwars", new FixedMetadataValue(main, "shop"));
                            }
                        }

                        if (i == 0)
                            lock++;
                    }
                }
                lock2++;
            }

            for (int h : num) {
                for (int i : num2) {
                    for (int j = 0; j < 5; j++) {
                        loc = e.getBlock().getLocation();
                        copy = loc.clone();
                        if (loc.getWorld().getBlockAt(loc.add(h, j, i)).getType() == Material.AIR) {
                            loc.getWorld().getBlockAt(copy.add(h, j, i)).setType(m);
                            loc.getWorld().playSound(loc, Sound.BLOCK_STONE_STEP, 10f, 1f);
                            loc.getWorld().getBlockAt(loc).setMetadata("bedwars", new FixedMetadataValue(main, "shop"));
                        }
                    }
                }
            }
        }

        loc = e.getBlock().getLocation();
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (loc.clone().add(i, 4, j).getBlock().getType() == Material.AIR) {
                    loc.clone().add(i, 4, j).getBlock().setType(m);
                    loc.clone().add(i, 4, j).getBlock().setMetadata("bedwars", new FixedMetadataValue(main, "shop"));
                }
            }
        }

        for (int j = -1; j < 2; j++) {
            if (loc.clone().add(3, 5, j).getBlock().getType() == Material.AIR) {
                loc.clone().add(3, 5, j).getBlock().setType(m);
                loc.clone().add(3, 5, j).getBlock().setMetadata("bedwars", new FixedMetadataValue(main, "shop"));
            }
        }
        for (int j = -1; j < 2; j++) {
            if (loc.clone().add(-3, 5, j).getBlock().getType() == Material.AIR) {
                loc.clone().add(-3, 5, j).getBlock().setType(m);
                loc.clone().add(-3, 5, j).getBlock().setMetadata("bedwars", new FixedMetadataValue(main, "shop"));
            }
        }
        for (int j = -1; j < 2; j++) {
            if (loc.clone().add(j, 5, 3).getBlock().getType() == Material.AIR) {
                loc.clone().add(j, 5, 3).getBlock().setType(m);
                loc.clone().add(j, 5, 3).getBlock().setMetadata("bedwars", new FixedMetadataValue(main, "shop"));
            }
        }
        for (int j = -1; j < 2; j++) {
            if (loc.clone().add(j, 5, -3).getBlock().getType() == Material.AIR) {
                loc.clone().add(j, 5, -3).getBlock().setType(m);
                loc.clone().add(j, 5, -3).getBlock().setMetadata("bedwars", new FixedMetadataValue(main, "shop"));
            }
        }
        for (int i = -2; i < 3; i+=4) {
            for (int j = -2; j < 3; j+=4) {
                for (int k = -1; k < 2; k++) {
                    if (loc.clone().add(i, 5 + k, j).getBlock().getType() == Material.AIR) {
                        loc.clone().add(i, 5 + k, j).getBlock().setType(m);
                        loc.clone().add(i, 5 + k, j).getBlock().setMetadata("bedwars", new FixedMetadataValue(main, "shop"));
                    }
                }
            }
        }

        for (int i = -3; i < 4; i+=6) {
            for (int j = -1; j < 2; j+=2) {
                for (int k = -1; k < 2; k++) {
                    if (loc.clone().add(i, 5 + k, j).getBlock().getType() == Material.AIR) {
                        loc.clone().add(i, 5 + k, j).getBlock().setType(m);
                        loc.clone().add(i, 5 + k, j).getBlock().setMetadata("bedwars", new FixedMetadataValue(main, "shop"));
                    }
                }
            }
        }
        for (int i = -3; i < 4; i+=6) {
            for (int j = -1; j < 2; j+=2) {
                for (int k = -1; k < 2; k++) {
                    if (loc.clone().add(j, 5 + k, i).getBlock().getType() == Material.AIR) {
                        loc.clone().add(j, 5 + k, i).getBlock().setType(m);
                        loc.clone().add(j, 5 + k, i).getBlock().setMetadata("bedwars", new FixedMetadataValue(main, "shop"));
                    }
                }
            }
        }
    }

    private void makeBridge(BlockFace face, Material material) {
        int x = 0, z = 0;
        switch (face) {
            case NORTH:
                x = -1;
                z = -1;
                break;
            case WEST:
                x = 1;
                z = -1;
                break;
            case SOUTH:
                x = 1;
                z = 1;
                break;
            case EAST:
                x = -1;
                z = 1;
                break;
        }

        for (Location location : locationList) {
            if (location.getBlock().getType() == Material.AIR) {
                location.getBlock().setType(material);
                location.getBlock().setMetadata("bedwars", new FixedMetadataValue(main, "shop"));
            }
            if (location.clone().add(x, 0, 0).getBlock().getType() == Material.AIR) {
                location.clone().add(x, 0, 0).getBlock().setType(material);
                location.getWorld().getBlockAt(location.clone().add(x, 0, 0)).setMetadata("bedwars", new FixedMetadataValue(main, "shop"));
            }
            if (location.getWorld().getBlockAt(location.clone().add(0, 0, z)).getType() == Material.AIR) {
                location.clone().add(0, 0, z).getBlock().setType(material);
                location.getWorld().getBlockAt(location.clone().add(0, 0, z)).setMetadata("bedwars", new FixedMetadataValue(main, "shop"));
            }
        }
        locationList.clear();
    }
}
