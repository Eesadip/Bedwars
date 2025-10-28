package com.panda.bedwars.game.listeners;

import com.panda.bedwars.Bedwars;
import com.panda.bedwars.game.VarUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
    private Bedwars main;

    public ChatListener(Bedwars main) {
        this.main = main;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (main.getGame().isLive() && main.getGame().getPlayers().contains(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
            String msg = VarUtil.getTeamColor(main, main.getGame().getLiveGame().getTeam(e.getPlayer())) + "[" + main.getGame().getLiveGame().getTeam(e.getPlayer()).toUpperCase()
                    + "] " + e.getPlayer().getName() + "§f: " + e.getMessage();
            Bukkit.broadcastMessage(msg);
        }
    }
}
