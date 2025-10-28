package com.panda.bedwars.game.actors.generators;

import com.panda.bedwars.Bedwars;
import com.panda.bedwars.game.VarUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class TeamGenerator extends Generator {
    private Random random;

    public TeamGenerator(String name, int cooldown, Location genLoc, Bedwars main) {
        super(name, cooldown, genLoc, main);
        random = new Random();
    }

    @Override
    public void genRun() {
        runTaskTimer(main, 40, cooldown * 20L);
    }

    @Override
    public void run() {
        Item iron = Bukkit.getWorld(VarUtil.getWorldName()).dropItem(genLoc, new ItemStack(Material.IRON_INGOT, random.nextInt(2) + 1));
        iron.setVelocity(iron.getVelocity().zero());
        if (random.nextBoolean()) {
            Item gold = Bukkit.getWorld(VarUtil.getWorldName()).dropItem(genLoc, new ItemStack(Material.GOLD_INGOT, random.nextInt(1) + 1));
            gold.setVelocity(iron.getVelocity().zero());
        }
    }
}
