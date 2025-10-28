package com.panda.bedwars.game;

import com.panda.bedwars.Bedwars;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class VarUtil {
    private static Location spawnLocation;
    private static int minPlayers;
    private static int maxPlayers;
    private static int countdownTime;
    private static int teamGenCooldown;
    private static int diamondCooldown;
    private static int emeraldCooldown;
    private static int buildLimit;
    private static String worldName;
    private static int respawnTime;

    public static void init(Bedwars main) {
        main.saveDefaultConfig();

        // Game variables
        worldName = main.getConfig().getString("world");
        World world = Bukkit.getWorld(worldName);
        spawnLocation = new Location(world,
                main.getConfig().getDouble("spawn-area.x"),
                main.getConfig().getDouble("spawn-area.y"),
                main.getConfig().getDouble("spawn-area.z"),
                main.getConfig().getInt("spawn-area.yaw"),
                main.getConfig().getInt("spawn-area.pitch"));
        minPlayers = main.getConfig().getInt("min-players");
        maxPlayers = main.getConfig().getInt("max-players");
        countdownTime = main.getConfig().getInt("countdown-seconds");
        teamGenCooldown = main.getConfig().getInt("team-gen-cooldown");
        diamondCooldown = main.getConfig().getInt("diamond-cooldown");
        emeraldCooldown = main.getConfig().getInt("emerald-cooldown");
        buildLimit = main.getConfig().getInt("build-limit");
        respawnTime = main.getConfig().getInt("respawn-time");
    }

    // This might be used only once to load the locations and save them, to improve the runtime of the plugin
    public static List<Location> getDiaGenLocs(Bedwars main) {
        List<Location> locs = new ArrayList<>();

        FileConfiguration config = main.getConfig();
        for (String s : config.getConfigurationSection("diamond.").getKeys(false)) {
            locs.add(new Location(spawnLocation.getWorld(),
                    main.getConfig().getDouble("diamond." + s + ".x"),
                    main.getConfig().getDouble("diamond." + s + ".y"),
                    main.getConfig().getDouble("diamond." + s + ".z")));
        }

        return locs;
    }

    // This might be used only once to load the locations and save them, to improve the runtime of the plugin
    public static List<Location> getEmGenLocs(Bedwars main) {
        List<Location> locs = new ArrayList<>();

        FileConfiguration config = main.getConfig();
        for (String s : config.getConfigurationSection("emerald.").getKeys(false)) {
            locs.add(new Location(spawnLocation.getWorld(),
                    main.getConfig().getDouble("emerald." + s + ".x"),
                    main.getConfig().getDouble("emerald." + s + ".y"),
                    main.getConfig().getDouble("emerald." + s + ".z")));
        }

        return locs;
    }

    public static Location getTeamSpawn(Bedwars main, String team) {
        return new Location(spawnLocation.getWorld(),
                main.getConfig().getDouble("teams." + team.toLowerCase() + ".x"),
                main.getConfig().getDouble("teams." + team.toLowerCase() + ".y"),
                main.getConfig().getDouble("teams." + team.toLowerCase() + ".z"),
                main.getConfig().getInt("teams." + team.toLowerCase() + ".yaw"),
                main.getConfig().getInt("teams." + team.toLowerCase() + ".pitch"));
    }

    public static Location getTeamGenSpawn(Bedwars main, String team) {
        return new Location(spawnLocation.getWorld(),
                main.getConfig().getDouble("teams." + team.toLowerCase() + ".generator.x"),
                main.getConfig().getDouble("teams." + team.toLowerCase() + ".generator.y"),
                main.getConfig().getDouble("teams." + team.toLowerCase() + ".generator.z"));
    }

    public static Location getTeamShopSpawn(Bedwars main, String team) {
        return new Location(spawnLocation.getWorld(),
                main.getConfig().getDouble("teams." + team.toLowerCase() + ".shop.x"),
                main.getConfig().getDouble("teams." + team.toLowerCase() + ".shop.y"),
                main.getConfig().getDouble("teams." + team.toLowerCase() + ".shop.z"),
                main.getConfig().getInt("teams." + team.toLowerCase() + ".shop.yaw"),
                main.getConfig().getInt("teams." + team.toLowerCase() + ".shop.pitch"));
    }

    public static String getTeamColor(Bedwars main, String team) {
        return ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("teams." + team.toLowerCase() + ".color"));
    }

    public static Location getSpawnLocation() {
        return spawnLocation;
    }

    public static int getMinPlayers() {
        return minPlayers;
    }

    public static int getMaxPlayers() {
        return maxPlayers;
    }

    public static int getCountdownTime() {
        return countdownTime;
    }

    public static int getTeamGenCooldown() {
        return teamGenCooldown;
    }

    public static int getDiamondCooldown() {
        return diamondCooldown;
    }

    public static int getEmeraldCooldown() {
        return emeraldCooldown;
    }

    public static int getBuildLimit() {
        return buildLimit;
    }

    public static String getWorldName() {
        return worldName;
    }

    public static int getRespawnTime() {
        return respawnTime;
    }
}
