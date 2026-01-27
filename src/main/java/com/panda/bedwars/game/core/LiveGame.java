package com.panda.bedwars.game.core;

import com.panda.bedwars.game.VarUtil;
import com.panda.bedwars.game.actors.generators.DiamondGenerator;
import com.panda.bedwars.game.actors.generators.EmeraldGenerator;
import com.panda.bedwars.game.actors.generators.Generator;
import com.panda.bedwars.game.actors.generators.TeamGenerator;
import com.panda.bedwars.game.actors.shop.Shop;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.text.SimpleDateFormat;
import java.util.*;

public class LiveGame {
    private Game game;

    private Map<String, UUID> teams;
    private List<String> colors;
    private Map<String, Generator> teamGens;
    private List<Location> diaGenLocs;
    private List<Generator> diaGens;
    private List<Location> emGenLocs;
    private List<Generator> emGens;
    private Map<UUID, Shop> shops;
    private Map<UUID, ArmorType> armourMap;
    private Map<String, Boolean> bedBroken;
    private List<UUID> respawning;

    public LiveGame(Game game) {
        this.game = game;
        game.setState(Game.State.LIVE);
        teams = new HashMap<>();
        teamGens = new HashMap<>();
        diaGenLocs = VarUtil.getDiaGenLocs(game.getMain());
        emGenLocs = VarUtil.getEmGenLocs(game.getMain());
        shops = new HashMap<>();
        armourMap = new HashMap<>();
        bedBroken = new HashMap<>();
        diaGens = new ArrayList<>();
        emGens = new ArrayList<>();
        respawning = new ArrayList<>();

        gameStart();
    }

    void gameStart() {
        assignTeams();
        spawnTeams();
        updateScoreboard();
        spawnDiaGens();
        spawnEmGens();
    }

    void removePlayer(Player player) {
        String team = getTeam(player);
        if (!team.isEmpty()) {
            teamGens.get(team).genStop();
            teams.remove(team);
        }

        if (teams.isEmpty()) {
            endGame();
        }
    }

    private void assignTeams() {
        FileConfiguration config = game.getMain().getConfig();
        colors = new ArrayList<>(config.getConfigurationSection("teams.").getKeys(false));

        Random r = new Random();
        for (UUID uuid : game.getPlayers()) {
            String key = colors.get(r.nextInt(colors.size()));
            while (teams.containsKey(key)) {
                key = colors.get(r.nextInt(colors.size()));
            }
            teams.put(key, uuid);
        }
    }

    private void spawnTeams() {
        for (String s : teams.keySet()) {
            Player player = Bukkit.getPlayer(teams.get(s));
            player.teleport(VarUtil.getTeamSpawn(game.getMain(), s.trim()));
            player.setGameMode(GameMode.SURVIVAL);
            player.setHealth(20);
            player.getEnderChest().clear();

            armourMap.put(player.getUniqueId(), ArmorType.LEATHER);
            setArmor(player, s.toLowerCase());

            bedBroken.put(s.toLowerCase(), false);

            Generator generator = new TeamGenerator("",
                    VarUtil.getTeamGenCooldown(),
                    VarUtil.getTeamGenSpawn(game.getMain(), s.trim()),
                    game.getMain());
            teamGens.put(s, generator);
            generator.genRun();

            Shop shop = new Shop(game.getMain(), VarUtil.getTeamShopSpawn(game.getMain(), s.trim()));
            shops.put(player.getUniqueId(), shop);
            shop.spawnShop();
        }
    }

    private void setArmor(Player player, String color) {
        int r = 0, g = 0, b = 0;
        switch (color) {
            case "red":
                r = 255;
                g = 0;
                b = 0;
                break;
            case "blue":
                r = 0;
                g = 0;
                b = 255;
                break;
            case "lime":
                r = 102;
                g = 255;
                b = 102;
                break;
            case "yellow":
                r = 255;
                g = 255;
                b = 51;
                break;
            case "cyan":
                r = 0;
                g = 153;
                b = 153;
                break;
            case "white":
                r = 255;
                g = 255;
                b = 255;
                break;
            case "pink":
                r = 255;
                g = 153;
                b = 204;
                break;
            case "gray":
                r = 128;
                g = 128;
                b = 128;
                break;
        }

        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta helmetMeta = (LeatherArmorMeta) helmet.getItemMeta();
        helmetMeta.setColor(Color.fromRGB(r, g, b));
        helmetMeta.setUnbreakable(true);
        helmet.setItemMeta(helmetMeta);
        player.getInventory().setHelmet(helmet);

        ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta chestMeta = (LeatherArmorMeta) chest.getItemMeta();
        chestMeta.setColor(Color.fromRGB(r, g, b));
        chestMeta.setUnbreakable(true);
        chest.setItemMeta(chestMeta);
        player.getInventory().setChestplate(chest);

        ItemStack leg = new ItemStack(Material.LEATHER_LEGGINGS);
        LeatherArmorMeta legMeta = (LeatherArmorMeta) leg.getItemMeta();
        legMeta.setColor(Color.fromRGB(r, g, b));
        legMeta.setUnbreakable(true);
        leg.setItemMeta(legMeta);
        player.getInventory().setLeggings(leg);

        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta bootMeta = (LeatherArmorMeta) boots.getItemMeta();
        bootMeta.setColor(Color.fromRGB(r, g, b));
        bootMeta.setUnbreakable(true);
        boots.setItemMeta(bootMeta);
        player.getInventory().setBoots(boots);

        ItemStack legMaterial = null, bootMaterial = null;
        if (armourMap.get(player.getUniqueId()) == ArmorType.CHAINMAIL) {
            legMaterial = new ItemStack(Material.CHAINMAIL_LEGGINGS);
            bootMaterial = new ItemStack(Material.CHAINMAIL_BOOTS);
        } else if (armourMap.get(player.getUniqueId()) == ArmorType.IRON) {
            legMaterial = new ItemStack(Material.IRON_LEGGINGS);
            bootMaterial = new ItemStack(Material.IRON_BOOTS);
        } else if (armourMap.get(player.getUniqueId()) == ArmorType.DIAMOND) {
            legMaterial = new ItemStack(Material.DIAMOND_LEGGINGS);
            bootMaterial = new ItemStack(Material.DIAMOND_BOOTS);
        }

        if (legMaterial != null) {
            ItemMeta meta = legMaterial.getItemMeta();
            meta.setUnbreakable(true);
            legMaterial.setItemMeta(meta);
            player.getInventory().setLeggings(legMaterial);

            meta = bootMaterial.getItemMeta();
            meta.setUnbreakable(true);
            bootMaterial.setItemMeta(meta);
            player.getInventory().setBoots(bootMaterial);
        }

        ItemStack sword = new ItemStack(Material.WOODEN_SWORD);
        ItemMeta meta = sword.getItemMeta();
        meta.setUnbreakable(true);
        sword.setItemMeta(meta);
        player.getInventory().setItem(0, sword);
    }

    private void spawnDiaGens() {
        for (Location loc : diaGenLocs) {
            Generator diaGen = new DiamondGenerator(VarUtil.getDiamondCooldown(), loc, game.getMain());
            diaGens.add(diaGen);
            diaGen.genRun();
        }
    }

    private void spawnEmGens() {
        for (Location loc : emGenLocs) {
            Generator emGen = new EmeraldGenerator(VarUtil.getEmeraldCooldown(), loc, game.getMain());
            emGens.add(emGen);
            emGen.genRun();
        }
    }

    private void updateScoreboard() {
        int ind = 3;
        String mark = "";

        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = board.registerNewObjective("bedwars", "dummy");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.setDisplayName("§f§lBED WARS");

        Score website = obj.getScore("§epanda.gg");
        website.setScore(1);
        Score space = obj.getScore(" ");
        space.setScore(2);

        for (String color : colors) {
            if (teams.containsKey(color)) {
                if (!bedBroken.get(color.toLowerCase()))
                    mark = ChatColor.GREEN + "✔";
                else if (bedBroken.get(color.toLowerCase()) && teams.containsKey(color))
                    mark = ChatColor.GREEN + "1";
            } else
                mark = ChatColor.RED + "✖";
            Score t = obj.getScore("§f" + color.substring(0, 1).toUpperCase() + color.substring(1) + "§f: " + mark);
            t.setScore(ind++);
        }
        Score space2 = obj.getScore("    ");
        space2.setScore(ind++);
        Score map = obj.getScore("§eMap: " + "§b" + VarUtil.getWorldName());
        map.setScore(ind++);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String date = sdf.format(new Date());
        Score dt = obj.getScore("§8   " + date);
        dt.setScore(ind++);

        game.setJoinBoard(board);

        for (UUID uuid : game.getPlayers()) {
            Bukkit.getPlayer(uuid).setScoreboard(game.getJoinBoard());
        }
    }

    public String getTeam(Player player) {
        for (String key : teams.keySet()) {
            if (teams.get(key).equals(player.getUniqueId()))
                return key;
        }
        return "";
    }

    public Shop getShop(Player player) {
        return shops.get(player.getUniqueId());
    }

    public void setArmourType(Player player, ArmorType type) {
        armourMap.replace(player.getUniqueId(), type);
    }

    public void setBedBroken(String team, Player destroyer) {
        bedBroken.replace(team.toLowerCase(), true);
        if (teams.get(team.toLowerCase()) != null) {
            Player p = Bukkit.getPlayer(teams.get(team.toLowerCase()));
            p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.0f);
            p.sendTitle("§cBED DESTROYED", "§fYou can't respawn anymore!", 0, 30, 10);
            p.sendMessage("§cYour bed was destroyed! You can't respawn anymore.");

            String t = team.substring(0, 1).toUpperCase() + team.substring(1).toLowerCase();
            Bukkit.broadcastMessage("§f§lBED DESTRUCTION > " + VarUtil.getTeamColor(game.getMain(), team) + t + " Bed§7 was destroyed by "
                    + VarUtil.getTeamColor(game.getMain(), game.getLiveGame().getTeam(destroyer)) + destroyer.getName() + "§7!");
        }

        updateScoreboard();
    }

    void respawnPlayer(Player player) {
        setArmor(player, getTeam(player));
        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(VarUtil.getTeamSpawn(game.getMain(), getTeam(player)));
        player.setHealth(20);
        respawning.remove(player.getUniqueId());
        player.sendMessage("§aRespawned.");
    }

    public void respawn(Player player) {
        if (respawning.contains(player.getUniqueId())) return;

        respawning.add(player.getUniqueId());
        player.teleport(VarUtil.getSpawnLocation().add(4, -14, 0));
        player.setGameMode(GameMode.SPECTATOR);
        player.getInventory().clear();
        new RespawnTimer(player.getUniqueId(), game);
    }

    public void finalKill(Player player) {
        player.sendTitle("§cYOU DIED!", "", 0, 60, 10);
        player.teleport(VarUtil.getSpawnLocation().add(4, -14, 0));
        player.setGameMode(GameMode.SPECTATOR);

        String team = getTeam(player);
        String t = team.substring(0, 1).toUpperCase() + team.substring(1).toLowerCase();
        Bukkit.broadcastMessage("§f§lTEAM ELIMINATION > " + VarUtil.getTeamColor(game.getMain(), team) + t + " §7team was eliminated!");
        teams.remove(getTeam(player));
        updateScoreboard();

        if (teams.size() <= 1) {
            endGame();
        }
    }

    public boolean isBedBroken(Player player) {
        return bedBroken.get(getTeam(player).toLowerCase()) != null && bedBroken.get(getTeam(player).toLowerCase());
    }

    // Reset everything
    public void endGame() {
        if (teams.size() == 1) {
            UUID uuid = UUID.randomUUID();
            for (String s : teams.keySet()) {
                uuid = teams.get(s);
            }
            Player player = Bukkit.getPlayer(uuid);
            Bukkit.broadcastMessage("§6" + player.getName() + " has won!");
            player.sendTitle("§6VICTORY", "", 0, 100, 0);
            player.sendMessage("§aYou have won the game!");
            player.getInventory().clear();
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(game.getMain(), () -> {
            // Remove all shops etc
            for (String s : teamGens.keySet()) {
                teamGens.get(s).genStop();
            }
            for (Generator g : diaGens) {
                g.genStop();
            }
            for (Generator g : emGens) {
                g.genStop();
            }
            for (UUID uuid : shops.keySet()) {
                shops.get(uuid).removeShops();
            }

            bedBroken.clear();
            teams.clear();
            colors.clear();
            teams.clear();
            diaGenLocs.clear();
            diaGens.clear();
            emGenLocs.clear();
            emGens.clear();
            shops.clear();
            armourMap.clear();
            respawning.clear();

            game.removeAll();
        }, teams.size() == 1 ? 100 : 40);
    }

    public ArmorType getArmourType(Player player) { return armourMap.get(player.getUniqueId()); }
}
