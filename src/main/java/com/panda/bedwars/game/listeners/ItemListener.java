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
                        if (block.getType() != Material.AIR) block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(block.getType()));
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

    // animated the tower build animation
    private void makeTower(BlockPlaceEvent e, Material m) {
        if (e.getBlock().getType() != Material.CHEST) return;
        if (e.getPlayer().getInventory().getItemInMainHand().getType() != Material.CHEST) return;
        if (!e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().contains("Pop-up")) return;

        e.setCancelled(true);
        e.getPlayer().getInventory().removeItem(ShopData.getTower());

        Location loc;
        int[] num = {2, -2};
        int[] num2 = {-1, 0, 1};

        BlockFace playerDir = e.getPlayer().getFacing();
        BlockFace dir = playerDir.getOppositeFace();

        int lock = 0, lock2 = 0;
        List<Location> locations = new ArrayList<>();
        List<Location> ladders = new ArrayList<>();

        if (playerDir == BlockFace.WEST) {
            for (int h : num) {
                for (int i : num2) {
                    for (int j = 0; j < 5; j++) {
                        loc = e.getBlock().getLocation().add(h, j, i);
                        if (loc.getWorld().getBlockAt(loc).getType() == Material.AIR) {
                            if (lock > 1 || i != 0) locations.add(loc);
                            // ladders
                            if (i == 0 && lock2 == 1) ladders.add(loc.add((h > 0 ? -1 : 1), 0, 0));
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
                        if (loc.getWorld().getBlockAt(loc.clone().add(i, j, h)).getType() == Material.AIR) locations.add(loc.clone().add(i, j, h));
                    }
                }
            }
        } else if (playerDir == BlockFace.EAST) {
            num = new int[]{-2, 2};
            num2 = new int[]{-1, 0, 1};

            for (int h : num) {
                for (int i : num2) {
                    for (int j = 0; j < 5; j++) {
                        loc = e.getBlock().getLocation().add(h, j, i);
                        if (loc.getWorld().getBlockAt(loc).getType() == Material.AIR) {
                            if (lock > 1 || i != 0) locations.add(loc);
                            if (i == 0 && lock2 == 1) ladders.add(loc.add((h > 0 ? -1 : 1), 0, 0));
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
                        if (loc.getWorld().getBlockAt(loc.clone().add(i, j, h)).getType() == Material.AIR) locations.add(loc.clone().add(i, j, h));
                    }
                }
            }
        } else if (playerDir == BlockFace.NORTH) {
            num = new int[]{2, -2};
            num2 = new int[]{-1, 0, 1};

            for (int h : num) {
                for (int i : num2) {
                    for (int j = 0; j < 5; j++) {
                        loc = e.getBlock().getLocation().add(i, j, h);
                        if (loc.getWorld().getBlockAt(loc).getType() == Material.AIR) {
                            if (lock > 1 || i != 0) locations.add(loc);
                            if (i == 0 && lock2 == 1) ladders.add(loc.add(0, 0, (h > 0 ? -1 : 1)));
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
                        if (loc.getWorld().getBlockAt(loc.clone().add(h, j, i)).getType() == Material.AIR) locations.add(loc.clone().add(h, j, i));
                    }
                }
            }
        } else if (playerDir == BlockFace.SOUTH) {
            num = new int[]{-2, 2};
            num2 = new int[]{-1, 0, 1};

            for (int h : num) {
                for (int i : num2) {
                    for (int j = 0; j < 5; j++) {
                        loc = e.getBlock().getLocation().add(i, j, h);
                        if (loc.getWorld().getBlockAt(loc).getType() == Material.AIR) {
                            if (lock > 1 || i != 0) locations.add(loc);
                            if (i == 0 && lock2 == 1) ladders.add(loc.add(0, 0, (h > 0 ? -1 : 1)));
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
                        if (loc.getWorld().getBlockAt(loc.clone().add(h, j, i)).getType() == Material.AIR) locations.add(loc.clone().add(h, j, i));
                    }
                }
            }
        }

        loc = e.getBlock().getLocation();
        for (int i : new int[]{-1, 0, 1})
            for (int j : new int[]{-1, 0, 1})
                if (loc.clone().add(i, 4, j).getBlock().getType() == Material.AIR) locations.add(loc.clone().add(i, 4, j));

        for (int j : new int[]{-1, 0, 1}) {
            if (loc.clone().add(3, 5, j).getBlock().getType() == Material.AIR) locations.add(loc.clone().add(3, 5, j));
            if (loc.clone().add(-3, 5, j).getBlock().getType() == Material.AIR) locations.add(loc.clone().add(-3, 5, j));
            if (loc.clone().add(j, 5, 3).getBlock().getType() == Material.AIR) locations.add(loc.clone().add(j, 5, 3));
            if (loc.clone().add(j, 5, -3).getBlock().getType() == Material.AIR) locations.add(loc.clone().add(j, 5, -3));
        }

        for (int i : new int[] {-2, 2})
            for (int j : new int[] {-2, 2})
                for (int k : new int[]{-1, 0, 1})
                    if (loc.clone().add(i, 5 + k, j).getBlock().getType() == Material.AIR) locations.add(loc.clone().add(i, 5 + k, j));

        for (int i : new int[]{-3, 3}) {
            for (int j : new int[]{-1, 1}) {
                for (int k : new int[]{-1, 0, 1}) {
                    if (loc.clone().add(j, 5 + k, i).getBlock().getType() == Material.AIR) locations.add(loc.clone().add(j, 5 + k, i));
                    if (loc.clone().add(i, 5 + k, j).getBlock().getType() == Material.AIR) locations.add(loc.clone().add(i, 5 + k, j));
                }
            }
        }

        locations.sort((a,b) -> {
            if (a.getY() != b.getY()) return Double.compare(a.getY(), b.getY());
            if (a.getX() != b.getX()) return Double.compare(a.getX(), b.getX());
            return Double.compare(a.getZ(), b.getZ());
        });

        int N = 4; // blocks per tick
        final int[] index = {0};
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                boolean x = dir == BlockFace.EAST || dir == BlockFace.WEST;
                int dx = dir == BlockFace.EAST ? -1 : (dir == BlockFace.WEST ? 1 : (dir == BlockFace.NORTH ? 1 : -1));

                for (int i = 0; i < N; i++) {
                    if (index[0] >= locations.size()) break;
                    Location l = locations.get(index[0]++);
                    Location l2 = null;
                    if (ladders.contains(l)) {
                        l2 = l;
                        l = l.clone().add(x ? dx : 0, 0, x ? 0 : dx);
                    }

                    l.getBlock().setType(m);
                    l.getBlock().setMetadata("bedwars", new FixedMetadataValue(main, "shop"));
                    l.getWorld().playSound(l, Sound.ENTITY_CHICKEN_EGG, 10f, 1f);

                    if (l2 != null) {
                        l2.getBlock().setType(Material.LADDER);
                        Ladder data = (Ladder) l2.getWorld().getBlockAt(l2).getBlockData();
                        data.setFacing(dir);
                        l2.getWorld().getBlockAt(l2).setBlockData(data);
                        l2.getWorld().getBlockAt(l2).setMetadata("bedwars", new FixedMetadataValue(main, "shop"));
                    }
                }

                if (index[0] >= locations.size()) cancel();
            }
        };
        task.runTaskTimer(main, 0L, 1L);
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
