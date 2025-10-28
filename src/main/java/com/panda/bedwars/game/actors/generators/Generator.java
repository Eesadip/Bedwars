package com.panda.bedwars.game.actors.generators;

import com.panda.bedwars.Bedwars;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class Generator extends BukkitRunnable {
    protected String name;
    protected int cooldown;
    protected Location genLoc;
    protected Bedwars main;

    public Generator(String name, int cooldown, Location genLoc, Bedwars main) {
        this.name = name;
        this.cooldown = cooldown;
        this.genLoc = genLoc;
        this.main = main;
    }

    abstract public void genRun();
    public void genStop() {
        cancel();
    }

    public String getName() {
        return name;
    }

    public int getCooldown() {
        return cooldown;
    }

    public Location getGenLoc() {
        return genLoc;
    }
}
