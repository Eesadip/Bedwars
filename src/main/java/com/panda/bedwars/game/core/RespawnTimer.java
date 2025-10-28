package com.panda.bedwars.game.core;

import com.panda.bedwars.game.VarUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class RespawnTimer extends BukkitRunnable {
    private int time;
    private Game game;
    private Player player;

    public RespawnTimer(UUID uuid, Game game) {
        time = VarUtil.getRespawnTime();
        this.game = game;
        runTaskTimer(game.getMain(), 0, 20);
        player = Bukkit.getPlayer(uuid);
    }

    @Override
    public void run() {
        player.sendTitle("§cYOU DIED!", "§eRespawning in §c" + time + "§e second" + (time == 1 ? "" : "s"), 0, 20, 0);
        time--;

        if (time == 0) {
            cancel();
            Bukkit.getScheduler().scheduleSyncDelayedTask(game.getMain(), () -> {
                game.getLiveGame().respawnPlayer(player);
            }, 20);
        }
    }
}
