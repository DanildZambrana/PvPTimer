package io.github.danildzambrana.pvptimer;

import io.github.danildzambrana.pvptimer.commands.PvPTimerCommands;
import io.github.danildzambrana.pvptimer.cooldown.Cooldown;
import io.github.danildzambrana.pvptimer.cooldown.CooldownImpl;
import io.github.danildzambrana.pvptimer.file.FileManager;
import io.github.danildzambrana.pvptimer.listeners.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class PvPTimer extends JavaPlugin {
    private FileManager config;
    private Cooldown cooldown;

    @Override
    public void onEnable() {
        this.config = new FileManager(this, "config.yml", "config.yml").load(false, "config.yml");
        this.cooldown = new CooldownImpl(new FileManager(this, "player-data.yml").load(false));

        getCommand("pvptimer").setExecutor(new PvPTimerCommands(config, this, cooldown));
        Bukkit.getPluginManager().registerEvents(new PlayerListener(config, cooldown), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
