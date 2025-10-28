package com.panda.bedwars.game.core;

import com.panda.bedwars.game.VarUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class Countdown extends BukkitRunnable {
    private Game game;
    private int countdownSeconds = VarUtil.getCountdownTime();

    public Countdown(Game game) {
        this.game = game;
        game.setState(Game.State.COUNTDOWN);
        runTaskTimer(game.getMain(), 0, 20);
    }

    @Override
    public void run() {
        for (UUID uuid : game.getPlayers()) {
            Player p = Bukkit.getPlayer(uuid);
            game.getJoinBoard().getTeam("starting").setSuffix("§a" + countdownSeconds + "s");
            p.setScoreboard(game.getJoinBoard());
        }

        if (countdownSeconds%10 == 0 || countdownSeconds <= 5) {
            for (UUID uuid : game.getPlayers()) {
                Player p = Bukkit.getPlayer(uuid);
                p.sendMessage("§eGame starting in " + "§6" + countdownSeconds + "§e second" + (countdownSeconds == 1 ? "!" : "s!"));
                String color;
                if (countdownSeconds%10 == 0)
                    color = "§6";
                else
                    color = "§c";
                p.sendTitle(color + countdownSeconds ,"", 0, 20, 0);
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1.0f, 1.0f);
            }
        }

        countdownSeconds--;
        if (countdownSeconds == 0) {
            cancel();
            Bukkit.getScheduler().scheduleSyncDelayedTask(game.getMain(), () -> {
                game.start();
            }, 15);
        }
    }
}
