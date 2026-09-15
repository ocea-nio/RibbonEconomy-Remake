package io.github.ilongake.ribboneconomy.feature.jobs.rewards;

import io.github.ilongake.ribboneconomy.feature.jobs.JobType;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RewardManager {
    private final File file;
    private final FileConfiguration config;

    public RewardManager(JavaPlugin plugin) {
        file = new File(
                plugin.getDataFolder(),
                "jobsReward.yml"
        );

        if (!file.exists()) {

            plugin.getDataFolder().mkdirs();

            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }
    public Map<Material,Double> getBreakReward(JobType type){
        Map<Material, Double> rewards = new HashMap<>();
        String path = "JobsReward." + type.name().toUpperCase();
        ConfigurationSection section = config.getConfigurationSection(path);
        if (section == null) {
            return rewards;
        }
        for (String block : section.getKeys(false)) {
            double amount = section.getDouble(block + ".amount");
            rewards.put(Material.matchMaterial(block), amount);
        }
        return rewards;
    }
    public Map<EntityType,Double> getKillReward(JobType type){
        Map<EntityType, Double> rewards = new HashMap<>();
        String path = "JobsReward." + type.name().toUpperCase();
        ConfigurationSection section = config.getConfigurationSection(path);
        if (section == null) {
            return rewards;
        }
        for (String entity : section.getKeys(false)) {
            double amount = section.getDouble(entity + ".amount");
            rewards.put(EntityType.fromName(entity), amount);
        }
        return rewards;
    }
}
