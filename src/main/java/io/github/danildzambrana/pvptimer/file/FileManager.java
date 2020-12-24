package io.github.danildzambrana.pvptimer.file;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Manager from file to load, reload, save and get file.
 */
public final class FileManager {
    private final Plugin plugin;
    private final String outputFileName;
    private final Logger logger;
    private File file;
    private String inputFileName;
    private String defaultInputFile;
    private FileConfiguration bukkitFile;

    /**
     * Create new file manager to bukkit with blank file.
     *
     * @param plugin         the plugin main.
     * @param outputFileName the out file.
     */
    public FileManager(@NotNull Plugin plugin, String outputFileName) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.outputFileName = outputFileName;
    }

    /**
     * Create new file manager to bukkit with base file.
     *
     * @param plugin         the plugin main.
     * @param inputFileName  the base file.
     * @param outputFileName the out file.
     */
    public FileManager(@NotNull Plugin plugin, String outputFileName, String inputFileName) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.outputFileName = outputFileName;
        this.inputFileName = inputFileName;
    }

    /**
     * Create new file manager to bukkit with base file.
     *
     * @param defaultInputFile this file is used of the {@link FileManager#inputFileName} is not founded.
     * @param plugin           the plugin main.
     * @param outputFileName   the base file.
     */
    public FileManager(String defaultInputFile, @NotNull Plugin plugin, String outputFileName) {
        this.defaultInputFile = defaultInputFile;
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.outputFileName = outputFileName;
    }

    /**
     * Create new file manager to bukkit with base file.
     *
     * @param defaultInputFile this file is used of the {@link FileManager#inputFileName} is not founded.
     * @param plugin           the plugin main.
     * @param inputFileName    the base file.
     * @param outputFileName   the out file.
     */
    public FileManager(String defaultInputFile, @NotNull Plugin plugin, String outputFileName, String inputFileName) {
        this.defaultInputFile = defaultInputFile;
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.outputFileName = outputFileName;
        this.inputFileName = inputFileName;
    }


    /**
     * Load the file in memory, and create default file if not exist.
     *
     * @param silent   set to false to seen creation message.
     * @param fileNick the NickName tp file, to show in the console.
     * @return this instance.
     */
    public FileManager load(boolean silent, String fileNick) {
        file = new File(plugin.getDataFolder(), outputFileName);

        if (!file.exists()) {
            if (!silent) {
                logger.info(ChatColor.RED + fileNick + ChatColor.GRAY + " is not founded, creating archive...");
            }

            this.getBukkitFile().options().copyDefaults(true);
            saveFile();
        }

        return this;
    }

    /**
     * Load the file in memory, and create default file if not exist.
     *
     * @param silent set to false to seen creation message.
     * @return this instance.
     */
    public FileManager load(boolean silent) {
        return load(silent, "");
    }

    /**
     * Save the changes in the file.
     */
    public void saveFile() {
        try {
            getBukkitFile().save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reload the file data in memory.
     */
    public void reloadFile() {
        if (bukkitFile == null) {
            file = new File(plugin.getDataFolder(), outputFileName);
        }

        bukkitFile = YamlConfiguration.loadConfiguration(file);

        if (inputFileName != null) {
            InputStream stream = plugin.getResource(inputFileName);

            if (stream == null && !defaultInputFile.isEmpty()) {
                logger.warning("The file corresponding to '"
                        + inputFileName
                        + "' has not been found, the default '"
                        + defaultInputFile
                        + "' file will be used");
                stream = plugin.getResource(defaultInputFile);
            }

            if (stream != null) {
                Reader defConfigStream = new InputStreamReader(stream, StandardCharsets.UTF_8);

                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
                bukkitFile.setDefaults(defConfig);
            }
        }
    }

    /**
     * Get the {@link FileConfiguration} instance to the file.
     *
     * @return an instance of FileConfiguration.
     */
    public @NotNull FileConfiguration getBukkitFile() {
        if (bukkitFile == null) {
            reloadFile();
        }

        return bukkitFile;
    }

    /**
     * Get an string from the file.
     *
     * @param path         the path to obtains the String.
     * @param placeholders the key/value to replace placeholders in the string.
     * @return a string from the file.
     */
    @SafeVarargs
    public final String getString(String path, Map.Entry<String, String> @NotNull ... placeholders) {
        String text = getBukkitFile().contains(path) ? getBukkitFile().getString(path) : "";
        for (Map.Entry<String, String> placeholder : placeholders) {
            text = text.replace(placeholder.getKey(), placeholder.getValue());
        }
        return text;
    }

    /**
     * Get the string with colors.
     *
     * @param path         the path to obtains the String.
     * @param placeholders the key/value to replace placeholders in the string
     * @return a string with color codes.
     */
    @SafeVarargs
    public @NotNull
    final String getColouredString(String path, Map.Entry<String, String> @NotNull ... placeholders) {
        return ChatColor.translateAlternateColorCodes('&', getString(path, placeholders));
    }

    /**
     * Get the String without color codes.
     *
     * @param path         the path to obtains the String.
     * @param placeholders the key/value to replace placeholders in the string
     * @return a string without color codes.
     */
    @SafeVarargs
    public @NotNull
    final String getStripedString(String path, Map.Entry<String, String> @NotNull ... placeholders) {
        return ChatColor.stripColor(getColouredString(path, placeholders));
    }

    /**
     * Get the list of a String with color codes
     *
     * @param path         the path to obtains the String.
     * @param placeholders the key/value to replace placeholders in the string
     * @return a strings without color codes.
     */
    @SafeVarargs
    public final @NotNull List<String> getColouredList(String path, Map.Entry<String, String> @NotNull ... placeholders) {
        return getBukkitFile().getStringList(path).stream().map(line -> {
            line = ChatColor.translateAlternateColorCodes('&', line);
            for (Map.Entry<String, String> placeholder : placeholders) {
                line = line.replaceAll(placeholder.getKey(), placeholder.getValue());
            }

            return line;
        }).collect(Collectors.toList());
    }
}
