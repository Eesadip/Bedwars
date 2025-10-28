package com.panda.bedwars.map;

import org.bukkit.*;
import org.bukkit.entity.Entity;

import java.io.File;
import java.io.IOException;

public class LocalGameMap implements GameMap {
    private final File sourceWorldFolder;
    private File activeWorldFolder;
    private World bukkitWorld;

    public LocalGameMap(File worldFolder, String worldName, boolean loadOnInit) {
        this.sourceWorldFolder = new File(worldFolder, worldName);

        if (loadOnInit) load();
    }

    @Override
    public boolean load() {
        if (isLoaded()) return true;

        this.activeWorldFolder = new File(Bukkit.getWorldContainer().getParentFile(),
                sourceWorldFolder.getName());

        try {
            FileUtil.copy(sourceWorldFolder, activeWorldFolder);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        this.bukkitWorld = Bukkit.createWorld(new WorldCreator(activeWorldFolder.getName()));

        if (bukkitWorld != null) {
            this.bukkitWorld.setAutoSave(false);
            this.bukkitWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            this.bukkitWorld.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            this.bukkitWorld.setTime(0);
            this.bukkitWorld.setClearWeatherDuration(999999999);
            this.bukkitWorld.setDifficulty(Difficulty.PEACEFUL);
            this.bukkitWorld.setPVP(false);
            for (Entity entity : this.bukkitWorld.getEntities()) {
                entity.remove();
            }
        }
        return isLoaded();
    }

    @Override
    public void unload() {
        if (bukkitWorld != null) Bukkit.unloadWorld(bukkitWorld, false);
        if (activeWorldFolder != null) FileUtil.delete(activeWorldFolder);

        bukkitWorld = null;
        activeWorldFolder = null;
    }

    @Override
    public boolean restoreFromSource() {
        unload();
        return load();
    }

    @Override
    public boolean isLoaded() {
        return this.bukkitWorld != null;
    }

    @Override
    public World getWorld() {
        return bukkitWorld;
    }
}
