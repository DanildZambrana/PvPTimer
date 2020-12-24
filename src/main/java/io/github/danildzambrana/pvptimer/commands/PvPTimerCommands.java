package io.github.danildzambrana.pvptimer.commands;

import io.github.danildzambrana.pvptimer.cooldown.Cooldown;
import io.github.danildzambrana.pvptimer.file.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.logging.Logger;

public class PvPTimerCommands implements CommandExecutor {
    private final FileManager config;
    private final Logger logger;
    private final Cooldown cooldown;

    public PvPTimerCommands(FileManager config, Plugin plugin, Cooldown cooldown) {
        this.config = config;
        this.logger = plugin.getLogger();
        this.cooldown = cooldown;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            logger.warning(config.getStripedString("lang.console-sender"));
            return true;
        }

        Player player = ((Player) sender);

        if (args.length < 1) {
            player.sendMessage(config.getColouredString("lang.bad-command-usage",
                    new SimpleEntry<>("%usage%", "/pvptimer <enable:check:reset:grant>")));
            return true;
        }

        if (args[0].equalsIgnoreCase("enable") ) {
            if (!player.hasPermission("pvptimer.enable")) {
                player.sendMessage(config.getColouredString("lang.no-permission"));
                return true;
            }
            if (args.length < 2) {
                cooldown.removeCooldown(player.getUniqueId());
                player.sendMessage(config.getColouredString("lang.enable-pvp"));
            } else {
                Player target = Bukkit.getPlayer(args[1]);

                if (target == null) {
                    player.sendMessage(config.getColouredString("lang.target-offline",
                            new SimpleEntry<>("%target%", args[1])));
                    return true;
                }

                cooldown.removeCooldown(target.getUniqueId());
                target.sendMessage(config.getColouredString("lang.enable-pvp-others.target",
                        new SimpleEntry<>("%from%", player.getName()),
                        new SimpleEntry<>("%from-displayname%", player.getDisplayName())
                        )
                );

                player.sendMessage(config.getColouredString("lang.enable-pvp-others.sender",
                        new SimpleEntry<>("%target%", target.getName())
                        )
                );
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("check")) {
            if (!player.hasPermission("pvptimer.check")) {
                player.sendMessage(config.getColouredString("lang.no-permission"));
                return true;
            }
            if (args.length < 2) {
                player.sendMessage(config.getColouredString("lang.bad-command-usage",
                        new SimpleEntry<>("%usage%", "/pvptimer check <player_name>")));
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                player.sendMessage(config.getColouredString("lang.target-offline",
                        new SimpleEntry<>("%target%", args[1])));
                return true;
            }

            player.sendMessage(config.getColouredString("lang.check-remaining-cooldown",
                    new SimpleEntry<>("%target%", args[1]),
                    new SimpleEntry<>("%time%", (cooldown.getCooldown(target.getUniqueId()) / 1000) + "")
                    )
            );
            return true;
        }

        if (args[0].equalsIgnoreCase("grant")) {
            if (!player.hasPermission("pvptimer.grant")) {
                player.sendMessage(config.getColouredString("lang.no-permission"));
                return true;
            }
            if (args.length < 2) {
                player.sendMessage(config.getColouredString("lang.bad-command-usage",
                        new SimpleEntry<>("%usage%", "/pvptimer grant <player_name>")));
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                player.sendMessage(config.getColouredString("lang.target-offline",
                        new SimpleEntry<>("%target%", args[1])));
                return true;
            }

            cooldown.addCooldown(target.getUniqueId(), config.getBukkitFile().getInt("pvp-time"));

            target.sendMessage(config.getColouredString("lang.grant.target",
                    new SimpleEntry<>("%from%", player.getName()),
                    new SimpleEntry<>("%from-displayname%", player.getDisplayName())
                    )
            );
            player.sendMessage(config.getColouredString("lang.grant.sender",
                    new SimpleEntry<>("%target%", target.getName()),
                    new SimpleEntry<>("%target_displayname%", target.getDisplayName())
                    )
            );

            return true;
        }

        if (args[0].equalsIgnoreCase("reset")) {
            if (!player.hasPermission("pvptimer.reset")) {
                player.sendMessage(config.getColouredString("lang.no-permission"));
                return true;
            }
            if (args.length < 2) {
                player.sendMessage(config.getColouredString("lang.bad-command-usage",
                        new SimpleEntry<>("%usage%", "/pvptimer reset <player_name>")));
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                player.sendMessage(config.getColouredString("lang.target-offline",
                        new SimpleEntry<>("%target%", args[1])));
                return true;
            }

            cooldown.removeCooldown(target.getUniqueId());
            cooldown.addCooldown(target.getUniqueId(), config.getBukkitFile().getInt("pvp-time"));

            target.sendMessage(config.getColouredString("lang.reset.target",
                    new SimpleEntry<>("%from%", player.getName()),
                    new SimpleEntry<>("%from-displayname%", player.getDisplayName())
                    )
            );
            player.sendMessage(config.getColouredString("lang.reset.sender",
                    new SimpleEntry<>("%target%", target.getName()),
                    new SimpleEntry<>("%target_displayname%", target.getDisplayName())
                    )
            );

            return true;
        }

        player.sendMessage(config.getColouredString("lang.bad-command-usage",
                new SimpleEntry<>("%usage%", "/pvptimer <enable:check:reset:grant>")));
        return true;
    }
}
