package io.github.ilongake.ribboneconomy.core;

import io.github.ilongake.ribboneconomy.job.JobType;
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

    private final Map<UUID, PlayerData> players = new HashMap<>();

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

        double balance = config.getDouble(
                path + ".balance",
                1000
        );

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

        PlayerData data = new PlayerData(
                balance,
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

        PlayerData data = players.get(uuid);

        if (data == null) {
            return;
        }

        String path = uuid.toString();

        // 残高
        config.set(
                path + ".balance",
                data.getBalance()
        );

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
    public PlayerData getPlayerData(UUID uuid) {

        return players.get(uuid);
    }

    /**
     * 残高取得
     */
    public double getBalance(UUID uuid) {

        PlayerData data = players.get(uuid);

        if (data == null) {
            return 0;
        }

        return data.getBalance();
    }

    /**
     * 残高設定
     */
    public void setBalance(
            UUID uuid,
            double amount
    ) {

        PlayerData data = players.get(uuid);

        if (data == null) {
            return;
        }

        data.setBalance(amount);
    }

    /**
     * 残高追加
     */
    public void addBalance(
            UUID uuid,
            double amount
    ) {

        PlayerData data = players.get(uuid);

        if (data == null) {
            return;
        }

        data.setBalance(
                data.getBalance() + amount
        );
    }

    /**
     * 残高減算
     */
    public void removeBalance(
            UUID uuid,
            double amount
    ) {

        PlayerData data = players.get(uuid);

        if (data == null) {
            return;
        }

        data.setBalance(
                data.getBalance() - amount
        );
    }

    /**
     * 安全に残高を引き出す
     */
    public boolean withdraw(
            UUID uuid,
            double amount
    ) {

        PlayerData data = players.get(uuid);

        if (data == null) {
            return false;
        }

        if (amount <= 0) {
            return false;
        }

        if (data.getBalance() < amount) {
            return false;
        }

        data.setBalance(
                data.getBalance() - amount
        );

        return true;
    }

    /**
     * 全プレイヤーのデータを取得
     */
    public Map<UUID, PlayerData> getAllPlayers() {

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