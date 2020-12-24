package io.github.danildzambrana.pvptimer.listeners;

import io.github.danildzambrana.pvptimer.cooldown.Cooldown;
import io.github.danildzambrana.pvptimer.file.FileManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.projectiles.ProjectileSource;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;

public class PlayerListener implements Listener {
    private final FileManager config;
    private final Cooldown cooldown;

    public PlayerListener(FileManager config, Cooldown cooldown) {
        this.config = config;
        this.cooldown = cooldown;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPlayedBefore()) {
            return;
        }

        cooldown.addCooldown(player.getUniqueId(), config.getBukkitFile().getInt("pvp-time"));
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        Player playerDamager;

        Entity damager = event.getDamager();
        if (damager instanceof Player) {
            playerDamager = (Player) event.getDamager();
        } else {
            if (!(damager instanceof Projectile)) {
                return;
            }

            ProjectileSource shooter = ((Projectile) damager).getShooter();
            if (!(shooter instanceof Player)) {
                return;
            }

            playerDamager = (Player) shooter;
        }

        if (playerDamager.equals(player)) {
            return;
        }

        if (cooldown.isCooldown(player.getUniqueId())) {
            event.setCancelled(true);
            playerDamager.sendMessage(
                    config.getColouredString("lang.target-has-pvp-timer",
                            new SimpleEntry<>("%target%", player.getName()),
                            new SimpleEntry<>("%target-displayName%", player.getDisplayName()),
                            new SimpleEntry<>("%time%", (cooldown.getCooldown(player.getUniqueId()) / 1000) + "")
                    )
            );
        }

        if (cooldown.isCooldown(playerDamager.getUniqueId())) {
            event.setCancelled(true);
            playerDamager.sendMessage(
                    config.getColouredString("lang.cannot-attack",
                            new SimpleEntry<>("%time%", (cooldown.getCooldown(playerDamager.getUniqueId()) / 1000) + "")
                    )
            );
        }
    }

    @EventHandler()
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            Player player = (Player) entity;

            if (!cooldown.isCooldown(player.getUniqueId())) {
                return;
            }

            switch (event.getCause()) {
                case FIRE:
                case FIRE_TICK:
                case FALL:
                case LAVA:
                case BLOCK_EXPLOSION:
                    event.setCancelled(true);
            }
        }
    }
}
