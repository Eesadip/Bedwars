package com.panda.bedwars.game.listeners;

import com.panda.bedwars.Bedwars;
import com.panda.bedwars.game.VarUtil;
import com.panda.bedwars.game.core.Game;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldLoadEvent;

import java.util.UUID;

public class ConnectionListener implements Listener {
    private Bedwars main;
    private Game game;

    public ConnectionListener(Bedwars main) {
        this.main = main;
        game = main.getGame();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (main.getGame().getPlayers().contains(e.getPlayer().getUniqueId())) {
            main.getGame().removePlayer(e.getPlayer());
            for (UUID uuid : main.getGame().getPlayers()) {
                Bukkit.getPlayer(uuid).sendMessage("§8§k" + e.getPlayer().getName() + "§e has left the game!");
            }
        }
    }

    @EventHandler
    public void onLoad(WorldLoadEvent e) {
        if (e.getWorld().getName().equalsIgnoreCase(VarUtil.getWorldName())) {
            main.getGame().setLoading(false);
        }
    }
}
