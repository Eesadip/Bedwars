package com.panda.bedwars.commands;

import com.panda.bedwars.Bedwars;
import com.panda.bedwars.game.VarUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShoutCommand implements CommandExecutor {
    private Bedwars main;

    public ShoutCommand(Bedwars main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            if (args.length == 0) {
                player.sendMessage(ChatColor.RED + "Usage: /shout <message>");
                return false;
            }

            if (main.getGame().isLive() && main.getGame().getPlayers().contains(player.getUniqueId())) {
                StringBuilder content = new StringBuilder();
                for (String a : args) {
                    content.append(a).append(" ");
                }

                main.getGame().shout(VarUtil.getTeamColor(main, main.getGame().getLiveGame().getTeam(player)) + "[" + main.getGame().getLiveGame().getTeam(player).toUpperCase()
                        + "] " + player.getName() + "§f: " + content.toString());
            } else {
                player.sendMessage(ChatColor.RED + "You can't use this command now!");
            }
        }

        return false;
    }
}
