package com.panda.bedwars.game.actors.generators;

import com.panda.bedwars.Bedwars;
import com.panda.bedwars.game.VarUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class EmeraldGenerator extends Generator {
    ArmorStand stand, spawner, timer;
    float yaw;
    int time, cd;

    public EmeraldGenerator(int cooldown, Location genLoc, Bedwars main) {
        super("§2§lEmerald", cooldown, genLoc, main);
        yaw = 0.0f;
        time = 1;
        cd = cooldown;
    }

    @Override
    public void genRun() {
        stand = (ArmorStand) Bukkit.getWorld(VarUtil.getWorldName()).spawnEntity(genLoc.clone().add(0, 2.4, 0), EntityType.ARMOR_STAND);
        stand.setVisible(false);
        stand.setGravity(false);
        stand.setInvulnerable(true);
        stand.setCustomNameVisible(true);
        stand.setCustomName(name);

        Location loc = stand.getLocation();
        timer = (ArmorStand) Bukkit.getWorld(VarUtil.getWorldName()).spawnEntity(loc.clone().subtract(0, 0.3, 0), EntityType.ARMOR_STAND);
        timer.setVisible(false);
        timer.setGravity(false);
        timer.setInvulnerable(true);
        timer.setCustomNameVisible(true);

        spawner = (ArmorStand) Bukkit.getWorld(VarUtil.getWorldName()).spawnEntity(loc.clone().subtract(0, 0.4, 0), EntityType.ARMOR_STAND);
        spawner.setVisible(false);
        spawner.setGravity(false);
        spawner.setInvulnerable(true);
        spawner.setCustomNameVisible(false);
        spawner.getEquipment().setHelmet(new ItemStack(Material.EMERALD_BLOCK));

        runTaskTimer(main, 20, 1);
    }

    @Override
    public void genStop() {
        super.genStop();
        spawner.remove();
        stand.remove();
    }

    @Override
    public void run() {
        timer.setCustomName("§eNext drop in " + "§c" + cd + " §esecond" + (cd == 1 ? "" : "s"));

        Location loc = stand.getLocation();
        if (time%20 == 0) {
            cd--;
        }
        time++;

        if (cd == 0) {
            cd = cooldown;
            Item em = Bukkit.getWorld(VarUtil.getWorldName()).dropItem(loc.subtract(0, 1, 0), new ItemStack(Material.EMERALD));
            em.setVelocity(em.getVelocity().zero());
        }

        // Animation
        Vector vector = new Vector(0.01 * Math.cos(yaw) + loc.getX(), loc.getY() - 0.5 * (1 + 0.2 * Math.sin(17.5 * yaw)), 0.01 * Math.sin(yaw) + loc.getZ());
        yaw += 0.01f;
        float y = spawner.getLocation().getYaw();
        y += 5.33333334f;
        Location location = new Location(Bukkit.getWorld(VarUtil.getWorldName()), vector.getX(), vector.getY(), vector.getZ(), y, loc.getPitch());
        spawner.teleport(location);
    }
}
