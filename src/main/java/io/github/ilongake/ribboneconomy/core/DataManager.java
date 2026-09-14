package io.github.ilongake.ribboneconomy.core;

import io.github.ilongake.ribboneconomy.feature.jobs.JobData;
import io.github.ilongake.ribboneconomy.feature.jobs.JobType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DataManager {

    private final File file;
    private final FileConfiguration config;

    private final Map<UUID, JobData> players = new HashMap<>();

    public DataManager(JavaPlugin plugin) {

        file = new File(
                plugin.getDataFolder(),
                "players.yml"
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

    /**
     * プレイヤーデータを読み込む
     */
    public void loadPlayer(UUID uuid) {

        String path = uuid.toString();

        String jobName = config.getString(
                path + ".job",
                "NONE"
        );

        JobType jobType;

        try {

            jobType = JobType.valueOf(jobName);

        } catch (IllegalArgumentException e) {

            jobType = JobType.NONE;
        }

        JobData data = new JobData(
                jobType
        );

        /*
         * 職業ごとの進行度を読み込む
         *
         * 古いplayers.ymlには存在しないので、
         * 存在しない場合は0になる。
         */
        for (JobType job : JobType.values()) {

            long progress = config.getLong(
                    path + ".job-progress." + job.name(),
                    0
            );

            data.setJobProgress(
                    job,
                    progress
            );
        }

        players.put(
                uuid,
                data
        );
    }

    /**
     * プレイヤーデータを保存
     */
    public void savePlayer(UUID uuid) {

        JobData data = players.get(uuid);

        if (data == null) {
            return;
        }

        String path = uuid.toString();

        // 職業
        config.set(
                path + ".job",
                data.getJobType().name()
        );

        // 職業ごとの進行度
        for (JobType job : JobType.values()) {

            config.set(
                    path + ".job-progress." + job.name(),
                    data.getJobProgress(job)
            );
        }

        save();
    }

    /**
     * メモリから削除
     */
    public void unloadPlayer(UUID uuid) {
        players.remove(uuid);
    }

    /**
     * PlayerData取得
     */
    public JobData getPlayerData(UUID uuid) {

        return players.get(uuid);
    }

    /**
     * 全プレイヤーのデータを取得
     */
    public Map<UUID, JobData> getAllPlayers() {
        return players;
    }

    /**
     * players.yml保存
     */
    public void save() {

        try {
            config.save(file);

        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}