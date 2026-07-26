package com.github.ginirohikocha.core.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.util.List;

/**
 * ChaUI 依赖的 ConfigYaml — Windymixin 提供的 stub。
 */
public class ConfigYaml {
    private final YamlConfiguration config;
    private final File file;

    public ConfigYaml(JavaPlugin plugin) {
        this.file = new File(plugin.getDataFolder(), "config.yml");
        if (!file.exists()) {
            plugin.saveResource("config.yml", false);
        }
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public boolean getBoolean(String key) {
        return config.getBoolean(key, false);
    }

    public String getString(String key) {
        return config.getString(key, "");
    }

    public List<String> getStringList(String key) {
        return config.getStringList(key);
    }

    public boolean contains(String key) {
        return config.contains(key);
    }

    public YamlConfiguration getConfig() {
        return config;
    }

    public void reload() {
        try {
            config.load(file);
        } catch (Exception e) {
            // ignore
        }
    }

    public void save() {
        try {
            config.save(file);
        } catch (Exception e) {
            // ignore
        }
    }
}
