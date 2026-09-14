package io.github.ilongake.ribboneconomy.feature.jobs;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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
    public Map<String,Double> getReward(JobType type){
        Map<String,Double> jobsRewards = new HashMap<>();
        String path =
                "JobsReward."
                        + type.name().toLowerCase();

        return
    }
}
