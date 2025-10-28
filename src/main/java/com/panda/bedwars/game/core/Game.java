package com.panda.bedwars.game.core;

import com.panda.bedwars.Bedwars;
import com.panda.bedwars.game.VarUtil;
import com.panda.bedwars.map.FileUtil;
import com.panda.bedwars.map.GameMap;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Game {
    private Bedwars main; // main class instance

    private List<UUID> players;
    private State state;
    private World world;
    private BossBar bar;
    private Scoreboard joinBoard;
    private BukkitTask task;
    private Countdown cd;
    private LiveGame liveGame;
    private boolean loading;

    private GameMap map;
    private File source;

    public Game(Bedwars main) {
        this.main = main;
        players = new ArrayList<>();
        state = State.WAITING;
        loading = false;
        source = new File(main.getDataFolder() + File.separator + "gameMaps" + File.separator + VarUtil.getWorldName());

        initWorld();
        bar = Bukkit.createBossBar("§eYou are playing §f§lBEDWARS", BarColor.PINK, BarStyle.SOLID);
        createJoinBoard();
    }

    public void addPlayer(Player player) {
        if (!canJoin()) {
            player.sendMessage(ChatColor.RED + "Sorry you can't join the game right now!");
            return;
        }

        if (players.contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You are already in the game!");
            return;
        }
        if (!loading)
            player.teleport(VarUtil.getSpawnLocation());
        else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThe map is loading!"));
            return;
        }

        players.add(player.getUniqueId());
        if (players.size() >= VarUtil.getMinPlayers()) {
            countdown();
        }
        player.getInventory().clear();
        player.setLevel(0);
        player.setExp(0);
        player.setGameMode(GameMode.ADVENTURE);

        bar.addPlayer(player);
        joinBoard.getTeam("players").setSuffix("§f" + players.size() + "/" + VarUtil.getMaxPlayers());
        if (task.isCancelled())
            startTask();
        player.setScoreboard(joinBoard);

        Bukkit.broadcastMessage("§8§k" + player.getName() + "§e has joined the game (§b" +
                getPlayers().size() + "§e/§b" + VarUtil.getMaxPlayers() + "§e)!");
    }

    public void removePlayer(Player player) {
        if (!players.contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You are not in a game!");
            return;
        }

        players.remove(player.getUniqueId());
        if (!isLive())
            joinBoard.getTeam("players").setSuffix("§f" + players.size() + "/" + VarUtil.getMaxPlayers());
        if (players.size() < VarUtil.getMinPlayers() && state == State.COUNTDOWN) {
            state = State.WAITING;
            cd.cancel();
            cd = null;
            for (UUID uuid : players) {
                Bukkit.getPlayer(uuid).sendMessage("§cThe countdown has been stopped as too many players have left!");
                joinBoard.getTeam("starting").setSuffix("...");
                Bukkit.getPlayer(uuid).setScoreboard(joinBoard);
            }
        } else if (state == State.LIVE) {
            liveGame.removePlayer(player);
        }

        bar.removePlayer(player);
        player.sendMessage(ChatColor.RED + "You have left the game!");
        player.teleport(new Location(Bukkit.getWorld("world"), 350, 70, 360));
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

        if (players.isEmpty())
            task.cancel();
    }

    public void countdown() {
        cd = new Countdown(this);
    }

    public void start() {
        liveGame = new LiveGame(this);
        world.setPVP(true);
    }

    public void removeAll() {
        bar.removeAll();
        for (UUID uuid : players) {
            Bukkit.getPlayer(uuid).setGameMode(GameMode.SURVIVAL);
            Bukkit.getPlayer(uuid).setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            Bukkit.getPlayer(uuid).teleport(new Location(Bukkit.getWorld("world"), 350, 70, 360));
        }

        players.clear();
        task.cancel();
        state = State.WAITING;
        liveGame = null;

        // Unloading the used world
        loading = true;

        // Delete the used world and bring a fresh copy of the world
        if (Bukkit.unloadWorld(world, false)) {
            //System.out.println("unloaded");
            File map = new File(Bukkit.getWorldContainer() + File.separator + VarUtil.getWorldName());
            FileUtil.delete(map);
            try {
                FileUtil.copy(source, map);
            } catch (IOException e) {
                e.printStackTrace();
            }
            world = Bukkit.createWorld(new WorldCreator(VarUtil.getWorldName()));
            world.setAutoSave(false);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            world.setTime(0);
            world.setClearWeatherDuration(999999999);
            world.setDifficulty(Difficulty.PEACEFUL);
            world.setPVP(false);
            for (Entity entity : world.getEntities()) {
                entity.remove();
            }

            VarUtil.init(main);
        }

        createJoinBoard();
    }

    /* Utility methods for the class */
    private void initWorld() {
        world = Bukkit.getWorld(VarUtil.getWorldName());
        if (world == null) {
            try {
                FileUtil.copy(source, new File(Bukkit.getWorldContainer() + File.separator + VarUtil.getWorldName()));
                world = Bukkit.createWorld(new WorldCreator(VarUtil.getWorldName()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        world.setAutoSave(false);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setTime(0);
        world.setClearWeatherDuration(999999999);
        world.setDifficulty(Difficulty.PEACEFUL);
        world.setPVP(false);
        for (Entity entity : world.getEntities()) {
            entity.remove();
        }
    }

    private boolean canJoin() {
        return players.size() < VarUtil.getMaxPlayers() && state != State.LIVE;
    }

    // Create the inital scoreboard which is displayed when a player joins
    private void createJoinBoard() {
        joinBoard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = joinBoard.registerNewObjective("bedwars", "dummy");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.setDisplayName("§f§lBED WARS");

        Score website = obj.getScore("§fpanda.gg");
        website.setScore(1);
        Score space = obj.getScore(" ");
        space.setScore(2);

        Team playerCount = joinBoard.registerNewTeam("players");
        playerCount.addEntry("§l");
        playerCount.setPrefix("§ePlayers: ");
        playerCount.setSuffix("§f" + players.size() + "/" + VarUtil.getMaxPlayers());
        obj.getScore("§l").setScore(3);

        Score space2 = obj.getScore("  ");
        space2.setScore(4);

        Team starting = joinBoard.registerNewTeam("starting");
        starting.addEntry("§e");
        starting.setPrefix("§eStarting in: ");
        starting.setSuffix(players.size() < VarUtil.getMinPlayers() ? "..." : "");
        obj.getScore("§e").setScore(5);

        Score map = obj.getScore("§eMap: " + "§b" + VarUtil.getWorldName());
        map.setScore(6);

        Score space3 = obj.getScore("   ");
        space3.setScore(7);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String date = sdf.format(new Date());
        Score dt = obj.getScore("§8   " + date);
        dt.setScore(8);

        startTask();
    }

    // Scoreboard animation task
    private void startTask() {
        final int[] ind = {0};
        String s = "BED WARS";
        Objective obj = joinBoard.getObjective("bedwars");
        task = Bukkit.getScheduler().runTaskTimer(main, () -> {
            if (ind[0] == s.length())
                ind[0] = 0;

            String title;
            if (ind[0] == s.length() - 1)
                title = "§f§l" + s.substring(0, ind[0]) + "§6§l" + s.charAt(ind[0]);
            else
                title = "§f§l" + s.substring(0, ind[0]) + "§6§l" + s.charAt(ind[0]) + "§f§l" + s.substring(ind[0] + 1);
            ind[0]++;

            obj.setDisplayName(title);
        }, 20, 4);
    }

    /* Getters */
    public List<UUID> getPlayers() {
        return players;
    }

    public Scoreboard getJoinBoard() {
        return joinBoard;
    }

    public Bedwars getMain() {
        return main;
    }

    public LiveGame getLiveGame() {
        return liveGame;
    }

    public boolean isLive() {return liveGame != null && state == State.LIVE;}

    /* Setters */
    public void setState(State state) {
        this.state = state;
    }

    protected void setJoinBoard(Scoreboard joinBoard) {
        this.joinBoard = joinBoard;
        task.cancel();
        startTask();
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    /* Inner classes/enums */
    enum State {
        LIVE,
        COUNTDOWN,
        WAITING;
    }
}
