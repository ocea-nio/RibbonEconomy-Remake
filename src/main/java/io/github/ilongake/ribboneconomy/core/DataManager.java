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

    // メモリ上のプレイヤーデータ
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

        // 残高を読み込む
        double balance = config.getDouble(
                path + ".balance",
                1000
        );

        // 職業を読み込む
        String jobName = config.getString(
                path + ".job",
                "NONE"
        );

        JobType jobType;

        try {

            jobType = JobType.valueOf(jobName);

        } catch (IllegalArgumentException e) {

            // 存在しない職業なら無職
            jobType = JobType.NONE;
        }

        // PlayerDataを作成
        PlayerData data = new PlayerData(
                balance,
                jobType
        );

        // メモリに保存
        players.put(uuid, data);
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

        // 残高を保存
        config.set(
                path + ".balance",
                data.getBalance()
        );

        // 職業を保存
        config.set(
                path + ".job",
                data.getJobType().name()
        );

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
     *
     * @return 引き出しに成功した場合 true
     */
    public boolean withdraw(
            UUID uuid,
            double amount
    ) {

        PlayerData data = players.get(uuid);

        // プレイヤーデータが存在しない
        if (data == null) {
            return false;
        }

        // 0以下の金額は無効
        if (amount <= 0) {
            return false;
        }

        // 残高不足
        if (data.getBalance() < amount) {
            return false;
        }

        // 残高を減らす
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