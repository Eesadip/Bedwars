package com.panda.bedwars.game.listeners;

import com.panda.bedwars.Bedwars;
import com.panda.bedwars.game.VarUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {
    private Bedwars main;

    public DeathListener(Bedwars main) {
        this.main = main;
    }

    @EventHandler()
    public void onDamageByEntity(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        if (!(e.getDamager() instanceof Player)) return;
        if (!main.getGame().isLive()) return;
        if (main.getGame().isLive() && !main.getGame().getPlayers().contains(e.getEntity().getUniqueId())) return;

        Player player = (Player) e.getEntity();
        if (((player).getHealth() - e.getDamage()) <= 0.5) {
            if (!main.getGame().getLiveGame().isBedBroken(player) && main.getGame().getLiveGame().getTeam(player) != "") {
                Bukkit.broadcastMessage(VarUtil.getTeamColor(main.getGame().getMain(), main.getGame().getLiveGame().getTeam(player)) + player.getName()
                        + " §7was killed by " +
                        VarUtil.getTeamColor(main.getGame().getMain(), main.getGame().getLiveGame().getTeam((Player) e.getDamager()))
                        + e.getDamager().getName() + "§7.");
            };
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        if (!main.getGame().isLive()) return;
        if (main.getGame().isLive() && !main.getGame().getPlayers().contains(e.getEntity().getUniqueId())) return;

        if (e.getEntity().getLocation().getY() <= -15 || (((Player) e.getEntity()).getHealth() - e.getDamage()) <= 0.5) {
            Player player = (Player) e.getEntity();

            e.setCancelled(true);
            main.getGame().getLiveGame().respawn(player);

            if (main.getGame().getLiveGame().isBedBroken(player)) {
                if (e.getEntity().getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                    EntityDamageByEntityEvent lastDamage = (EntityDamageByEntityEvent) e.getEntity().getLastDamageCause();
                    Player damager = (Player) lastDamage.getDamager();
                    Bukkit.broadcastMessage(VarUtil.getTeamColor(main.getGame().getMain(), main.getGame().getLiveGame().getTeam(player)) + player.getName()
                            + " §7was killed by " +
                            VarUtil.getTeamColor(main.getGame().getMain(), main.getGame().getLiveGame().getTeam(damager))
                            + damager.getName() + "§7. §b§lFINAL KILL");
                } else
                    Bukkit.broadcastMessage(VarUtil.getTeamColor(main.getGame().getMain(), main.getGame().getLiveGame().getTeam(player)) + player.getName()
                        + " §7was eliminated. §b§lFINAL KILL");
                main.getGame().getLiveGame().finalKill(player);
                return;
            } else {
                if (e.getEntity().getLocation().getY() <= -15) {
                    Bukkit.broadcastMessage(VarUtil.getTeamColor(main.getGame().getMain(), main.getGame().getLiveGame().getTeam(player)) + player.getName()
                            + " §7fell out of the world.");
                } else if (e.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
                    Bukkit.broadcastMessage(VarUtil.getTeamColor(main.getGame().getMain(), main.getGame().getLiveGame().getTeam(player)) + player.getName()
                            + " §7blew up.");
                } else if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    Bukkit.broadcastMessage(VarUtil.getTeamColor(main.getGame().getMain(), main.getGame().getLiveGame().getTeam(player)) + player.getName()
                            + " §7fell to their death.");
                }
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        e.setDeathMessage("");
        e.getEntity().spigot().respawn();
    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent e) {
        if (e.getEntity() instanceof WanderingTrader)
            e.setCancelled(true);
    }
}
