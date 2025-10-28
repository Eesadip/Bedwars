package com.panda.bedwars.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BedwarsCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], Arrays.asList("admin", "join", "leave"), new ArrayList<>());
        } else if (args.length == 2 && args[0].equalsIgnoreCase("admin")) {
            return StringUtil.copyPartialMatches(args[1], Arrays.asList("start", "stop"), new ArrayList<>());
        }

        return new ArrayList<>();
    }
}
