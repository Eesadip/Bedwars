package com.panda.bedwars.commands;

import com.panda.bedwars.Bedwars;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BedwarsCommand implements CommandExecutor {
    private Bedwars main;

    public BedwarsCommand(Bedwars main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("join")) {
                    main.getGame().addPlayer(player);
                } else if (args[0].equalsIgnoreCase("leave")) {
                    main.getGame().removePlayer(player);
                } else {
                    player.sendMessage(ChatColor.RED + "Invalid option! Available options are: ");
                    player.sendMessage(ChatColor.RED + " - join");
                    player.sendMessage(ChatColor.RED + " - leave");
                    player.sendMessage(ChatColor.RED + " - admin");
                }
            } else if (args.length == 2) {
                if (player.hasPermission("bedwars.admin") && args[0].equalsIgnoreCase("admin")) {
                    if (args[1].equalsIgnoreCase("start") && !main.getGame().isLive()) {
                        main.getGame().countdown();
                    } else if (args[1].equalsIgnoreCase("stop")) {
                        if (main.getGame().isLive()) {
                            main.getGame().getLiveGame().endGame();
                        }
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You don't have the permission to use this command!");
                }
            } else {
                player.sendMessage(ChatColor.RED + "Invalid usage! Please use: /bw <join/leave>");
            }
        }

        return false;
    }
}
