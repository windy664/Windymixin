package com.github.ginirohikocha.core.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.util.List;

/**
 * ChaUI 依赖的 LangYaml — Windymixin 提供的 stub。
 */
public class LangYaml {
    private final YamlConfiguration config;
    private final File file;
    private final JavaPlugin plugin;

    public LangYaml(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "lang.yml");
        if (!file.exists()) {
            plugin.saveResource("lang.yml", false);
        }
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public String getLang(String key) {
        return config.getString(key, key);
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
