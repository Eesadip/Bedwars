package com.panda.bedwars;

import com.panda.bedwars.commands.BedwarsCommand;
import com.panda.bedwars.commands.BedwarsCompleter;
import com.panda.bedwars.commands.ShoutCommand;
import com.panda.bedwars.game.VarUtil;
import com.panda.bedwars.game.actors.shop.ShopData;
import com.panda.bedwars.game.core.Game;
import com.panda.bedwars.game.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Bedwars extends JavaPlugin {
    public Game game;
    private static Bedwars instance;

    @Override
    public void onEnable() {
        getDataFolder().mkdirs();
        instance = this;

        VarUtil.init(this);
        game = new Game(this);
        ShopData.init(this);

        getCommand("bw").setExecutor(new BedwarsCommand(this));
        getCommand("shout").setExecutor(new ShoutCommand(this)); // Fix the command after implementing the game logic EDIT: fixed
        getCommand("bw").setTabCompleter(new BedwarsCompleter());

        Bukkit.getPluginManager().registerEvents(new ConnectionListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BlockListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ShopListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ChatListener(this), this);
        Bukkit.getPluginManager().registerEvents(new DeathListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ItemListener(this), this);
    }

    @Override
    public void onDisable() { }

    public Game getGame() {
        return game;
    }

    public static Bedwars getInstance() {
        return instance;
    }
}
