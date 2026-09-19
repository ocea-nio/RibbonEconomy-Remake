package io.github.ilongake.ribboneconomy.feature.quest;

import io.github.ilongake.ribboneconomy.core.DataManager;
import io.github.ilongake.ribboneconomy.core.EconomyService;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class QuestManager {

    // 現在存在する依頼
    private final List<Quest> quests =
            new ArrayList<>();

    // 経済データ管理
    private final EconomyService economy;

    // プラグイン
    private final JavaPlugin plugin;

    // 依頼保存ファイル
    private final File file;

    // YAML設定
    private FileConfiguration config;


    /**
     * QuestManager作成
     */
    public QuestManager(
            JavaPlugin plugin,
            EconomyService economy
    ) {

        this.plugin = plugin;
        this.economy = economy;

        // quests.yml
        file = new File(
                plugin.getDataFolder(),
                "quests.yml"
        );

        // フォルダがなければ作成
        if (!plugin.getDataFolder().exists()) {

            plugin.getDataFolder().mkdirs();
        }

        // ファイルがなければ作成
        if (!file.exists()) {

            try {

                file.createNewFile();

            } catch (IOException e) {

                e.printStackTrace();
            }
        }

        // YAML読み込み
        config =
                YamlConfiguration
                        .loadConfiguration(file);

        // 依頼読み込み
        loadQuests();
    }


    /**
     * 新しい依頼を作成
     *
     * 作成時に依頼主の所持金から
     * 報酬を引き出してロックする
     */
    public Quest createQuest(
            UUID creatorUuid,
            String title,
            String description,
            double reward
    ) {

        // 報酬が0以下
        if (reward <= 0) {

            return null;
        }

        // 依頼主から報酬を引き出す
        boolean success = economy.withdraw(creatorUuid,reward);


        // お金が足りない
        if (!success) {

            return null;
        }

        // 依頼作成
        Quest quest =
                new Quest(
                        UUID.randomUUID(),
                        creatorUuid,
                        title,
                        description,
                        reward
                );

        // リストに追加
        quests.add(quest);

        // 保存
        saveQuests();

        return quest;
    }


    /**
     * すべての依頼を取得
     */
    public List<Quest> getAllQuests() {

        return quests;
    }


    /**
     * 公開中の依頼だけ取得
     */
    public List<Quest> getOpenQuests() {

        List<Quest> openQuests =
                new ArrayList<>();

        for (Quest quest : quests) {

            if (quest.getStatus()
                    == Quest.Status.OPEN) {

                openQuests.add(quest);
            }
        }

        return openQuests;
    }


    /**
     * 自分が受注した依頼を取得
     *
     * /quest my で使用
     */
    public List<Quest> getMyQuests(
            UUID workerUuid
    ) {

        List<Quest> myQuests =
                new ArrayList<>();

        for (Quest quest : quests) {

            // 受注者が存在する
            if (quest.getWorkerUuid() != null

                    // 自分が受注者
                    && quest.getWorkerUuid()
                    .equals(workerUuid)) {

                myQuests.add(quest);
            }
        }

        return myQuests;
    }


    /**
     * 自分が作成した依頼を取得
     *
     * /quest created で使用
     */
    public List<Quest> getCreatedQuests(
            UUID creatorUuid
    ) {

        List<Quest> createdQuests =
                new ArrayList<>();

        for (Quest quest : quests) {

            // 自分が依頼主
            if (quest.getCreatorUuid()
                    .equals(creatorUuid)) {

                createdQuests.add(quest);
            }
        }

        return createdQuests;
    }


    /**
     * IDから依頼を探す
     */
    public Quest getQuest(
            UUID questId
    ) {

        for (Quest quest : quests) {

            if (quest.getQuestId()
                    .equals(questId)) {

                return quest;
            }
        }

        return null;
    }


    /**
     * 依頼を受注する
     */
    public boolean acceptQuest(
            UUID questId,
            UUID workerUuid
    ) {

        Quest quest =
                getQuest(questId);

        // 依頼が存在しない
        if (quest == null) {

            return false;
        }

        // OPENではない
        if (quest.getStatus()
                != Quest.Status.OPEN) {

            return false;
        }

        // 自分の依頼
        if (quest.getCreatorUuid()
                .equals(workerUuid)) {

            return false;
        }

        // 受注者設定
        quest.setWorkerUuid(
                workerUuid
        );

        // 状態変更
        quest.setStatus(
                Quest.Status.ACCEPTED
        );

        // 保存
        saveQuests();

        return true;
    }


    /**
     * 依頼を完了報告する
     */
    public boolean completeQuest(
            UUID questId,
            UUID workerUuid
    ) {

        Quest quest =
                getQuest(questId);

        // 依頼が存在しない
        if (quest == null) {

            return false;
        }

        // ACCEPTEDではない
        if (quest.getStatus()
                != Quest.Status.ACCEPTED) {

            return false;
        }

        // 受注者本人ではない
        if (quest.getWorkerUuid() == null
                || !quest.getWorkerUuid()
                .equals(workerUuid)) {

            return false;
        }

        // 完了
        quest.setStatus(
                Quest.Status.COMPLETED
        );

        // 保存
        saveQuests();

        return true;
    }


    /**
     * 依頼主が承認する
     *
     * 承認すると受注者へ報酬を支払う
     */
    public boolean approveQuest(
            UUID questId,
            UUID creatorUuid
    ) {

        Quest quest =
                getQuest(questId);

        // 依頼が存在しない
        if (quest == null) {

            return false;
        }

        // COMPLETEDではない
        if (quest.getStatus()
                != Quest.Status.COMPLETED) {

            return false;
        }

        // 依頼主本人ではない
        if (!quest.getCreatorUuid()
                .equals(creatorUuid)) {

            return false;
        }

        // 受注者が存在しない
        if (quest.getWorkerUuid() == null) {

            return false;
        }

        // 報酬
        double reward =
                quest.getReward();

        // 受注者へ報酬を送金
        economy.deposit(quest.getCreatorUuid(),reward);

        // 承認
        quest.setStatus(
                Quest.Status.APPROVED
        );

        // 保存
        saveQuests();

        return true;
    }


    /**
     * 依頼をキャンセルする
     *
     * キャンセルできるのはOPEN状態のみ
     *
     * Bが受注した後はキャンセル不可
     *
     * キャンセル成功時は
     * ロックしていた報酬を依頼主へ返金する
     */
    public boolean cancelQuest(
            UUID questId,
            UUID creatorUuid
    ) {

        // 依頼を取得
        Quest quest =
                getQuest(questId);

        // 依頼が存在しない
        if (quest == null) {

            return false;
        }

        // 依頼主本人ではない
        if (!quest.getCreatorUuid()
                .equals(creatorUuid)) {

            return false;
        }

        // OPEN状態以外はキャンセル不可
        if (quest.getStatus()
                != Quest.Status.OPEN) {

            return false;
        }

        // ロックしていた報酬を返金
        economy.deposit(creatorUuid,quest.getReward());

        // 依頼を削除
        boolean removed =
                quests.remove(quest);

        // 削除成功
        if (removed) {

            // 保存
            saveQuests();

            return true;
        }

        return false;
    }


    /**
     * 依頼を削除
     *
     * このメソッドは報酬を返金しない
     *
     * 通常のキャンセルには
     * cancelQuest()を使用する
     */
    public boolean removeQuest(
            UUID questId
    ) {

        Quest quest =
                getQuest(questId);

        if (quest == null) {

            return false;
        }

        boolean removed =
                quests.remove(quest);

        if (removed) {

            saveQuests();
        }

        return removed;
    }


    /**
     * 依頼を保存
     */
    public void saveQuests() {

        // 全データを削除
        config.set(
                "quests",
                null
        );

        for (Quest quest : quests) {

            String path =
                    "quests."
                            + quest
                            .getQuestId()
                            .toString();


            // ID
            config.set(
                    path + ".questId",
                    quest
                            .getQuestId()
                            .toString()
            );


            // 依頼主
            config.set(
                    path + ".creatorUuid",
                    quest
                            .getCreatorUuid()
                            .toString()
            );


            // 受注者
            if (quest.getWorkerUuid()
                    != null) {

                config.set(
                        path + ".workerUuid",
                        quest
                                .getWorkerUuid()
                                .toString()
                );
            }


            // タイトル
            config.set(
                    path + ".title",
                    quest.getTitle()
            );


            // 内容
            config.set(
                    path + ".description",
                    quest.getDescription()
            );


            // 報酬
            config.set(
                    path + ".reward",
                    quest.getReward()
            );


            // 状態
            config.set(
                    path + ".status",
                    quest
                            .getStatus()
                            .name()
            );
        }


        try {

            config.save(file);

        } catch (IOException e) {

            e.printStackTrace();
        }
    }


    /**
     * 依頼を読み込む
     */
    public void loadQuests() {

        quests.clear();

        ConfigurationSection section =
                config.getConfigurationSection(
                        "quests"
                );

        // 依頼が存在しない
        if (section == null) {

            return;
        }


        for (String key :
                section.getKeys(false)) {

            String path =
                    "quests."
                            + key;

            try {

                // 依頼ID
                UUID questId =
                        UUID.fromString(
                                config.getString(
                                        path
                                                + ".questId"
                                )
                        );


                // 依頼主UUID
                UUID creatorUuid =
                        UUID.fromString(
                                config.getString(
                                        path
                                                + ".creatorUuid"
                                )
                        );


                // 受注者UUID
                String workerString =
                        config.getString(
                                path
                                        + ".workerUuid"
                        );

                UUID workerUuid = null;

                if (workerString != null) {

                    workerUuid =
                            UUID.fromString(
                                    workerString
                            );
                }


                // タイトル
                String title =
                        config.getString(
                                path
                                        + ".title",
                                ""
                        );


                // 内容
                String description =
                        config.getString(
                                path
                                        + ".description",
                                ""
                        );


                // 報酬
                double reward =
                        config.getDouble(
                                path
                                        + ".reward"
                        );


                // 状態
                Quest.Status status =
                        Quest.Status.valueOf(
                                config.getString(
                                        path
                                                + ".status",
                                        "OPEN"
                                )
                        );


                // Quest作成
                Quest quest =
                        new Quest(
                                questId,
                                creatorUuid,
                                workerUuid,
                                title,
                                description,
                                reward,
                                status
                        );


                // リストに追加
                quests.add(quest);

            } catch (Exception e) {

                plugin.getLogger().warning(
                        "依頼データの読み込みに失敗しました: "
                                + key
                );

                e.printStackTrace();
            }
        }


        plugin.getLogger().info(
                quests.size()
                        + "件の依頼を読み込みました。"
        );
    }
    /**
     * 指定したプレイヤーが受注中の依頼を取得
     */
    public List<Quest> getAcceptedQuests(
            UUID workerUuid
    ) {

        List<Quest> acceptedQuests =
                new ArrayList<>();

        for (Quest quest : quests) {

            // 受注中の依頼
            if (quest.getStatus()
                    == Quest.Status.ACCEPTED

                    // 受注者が自分
                    && quest.getWorkerUuid() != null
                    && quest.getWorkerUuid()
                    .equals(workerUuid)) {

                acceptedQuests.add(quest);
            }
        }

        return acceptedQuests;
    }
    /**
     * 指定したプレイヤーが作成した
     * 承認待ちの依頼を取得
     */
    public List<Quest> getCompletedQuests(
            UUID creatorUuid
    ) {

        List<Quest> completedQuests =
                new ArrayList<>();

        for (Quest quest : quests) {

            // 自分が依頼主
            if (quest.getCreatorUuid()
                    .equals(creatorUuid)

                    // 完了報告済み
                    && quest.getStatus()
                    == Quest.Status.COMPLETED) {

                completedQuests.add(quest);
            }
        }

        return completedQuests;
    }
}