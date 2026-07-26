package io.github.ilongake.ribboneconomy;

import io.github.ilongake.ribboneconomy.command.BalanceTopCommand;
import io.github.ilongake.ribboneconomy.command.ExchangeCommand;
import io.github.ilongake.ribboneconomy.command.MoneyCommand;
import io.github.ilongake.ribboneconomy.command.PayCommand;
import io.github.ilongake.ribboneconomy.core.DataManager;
import io.github.ilongake.ribboneconomy.gui.JobListener;
import io.github.ilongake.ribboneconomy.gui.MoneyListener;
import io.github.ilongake.ribboneconomy.gui.QuestCreateListener;
import io.github.ilongake.ribboneconomy.gui.RPGMenuListener;
import io.github.ilongake.ribboneconomy.job.JobCommand;
import io.github.ilongake.ribboneconomy.job.JobManager;
import io.github.ilongake.ribboneconomy.listener.BlockBreakListener;
import io.github.ilongake.ribboneconomy.listener.ExchangeGuiListener;
import io.github.ilongake.ribboneconomy.listener.MobKillListener;
import io.github.ilongake.ribboneconomy.listener.PlayerJoinListener;
import io.github.ilongake.ribboneconomy.listener.PlayerQuitListener;
import io.github.ilongake.ribboneconomy.listener.VillagerShopListener;
import io.github.ilongake.ribboneconomy.quest.QuestCommand;
import io.github.ilongake.ribboneconomy.quest.QuestManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class RPGEconomyPlugin extends JavaPlugin {

    private DataManager dataManager;

    private JobManager jobManager;

    private QuestManager questManager;


    @Override
    public void onEnable() {

        // =========================
        // DataManager
        // =========================

        dataManager =
                new DataManager(this);


        // =========================
        // 村人ショップ
        // =========================

        getServer()
                .getPluginManager()
                .registerEvents(
                        new VillagerShopListener(
                                dataManager
                        ),
                        this
                );


        // =========================
        // JobManager
        // =========================

        jobManager =
                new JobManager(
                        dataManager
                );


        // =========================
        // 職業GUI
        // =========================

        getServer()
                .getPluginManager()
                .registerEvents(
                        new JobListener(
                                jobManager
                        ),
                        this
                );


        // =========================
        // QuestManager
        // =========================

        questManager =
                new QuestManager(
                        this,
                        dataManager
                );


        // =========================
        // RPGメニュー
        // =========================

        getServer()
                .getPluginManager()
                .registerEvents(
                        new RPGMenuListener(
                                questManager,
                                dataManager,
                                jobManager
                        ),
                        this
                );


        // =========================
        // お金メニュー
        // =========================

        getServer()
                .getPluginManager()
                .registerEvents(
                        new MoneyListener(
                                dataManager
                        ),
                        this
                );


        // =========================
        // 経済コマンド
        // =========================

        getCommand("money").setExecutor(
                new MoneyCommand(
                        dataManager
                )
        );

        getCommand("pay").setExecutor(
                new PayCommand(
                        dataManager
                )
        );

        getCommand("balancetop").setExecutor(
                new BalanceTopCommand(
                        dataManager
                )
        );


        // =========================
        // 職業コマンド
        // =========================

        getCommand("job").setExecutor(
                new JobCommand(
                        jobManager
                )
        );


        // =========================
        // 依頼コマンド
        // =========================

        getCommand("quest").setExecutor(
                new QuestCommand(
                        questManager
                )
        );


        // =========================
        // エメラルド交換
        // =========================

        ExchangeCommand exchangeCommand =
                new ExchangeCommand(
                        dataManager
                );

        getCommand("exchange").setExecutor(
                exchangeCommand
        );

        getServer()
                .getPluginManager()
                .registerEvents(
                        new ExchangeGuiListener(
                                exchangeCommand
                        ),
                        this
                );


        // =========================
        // ログインイベント
        // =========================

        getServer()
                .getPluginManager()
                .registerEvents(
                        new PlayerJoinListener(
                                dataManager
                        ),
                        this
                );


        // =========================
        // ログアウトイベント
        // =========================

        getServer()
                .getPluginManager()
                .registerEvents(
                        new PlayerQuitListener(
                                dataManager
                        ),
                        this
                );


        // =========================
        // 農家システム
        // =========================

        getServer()
                .getPluginManager()
                .registerEvents(
                        new BlockBreakListener(
                                dataManager
                        ),
                        this
                );


        // =========================
        // ハンターシステム
        // =========================

        getServer()
                .getPluginManager()
                .registerEvents(
                        new MobKillListener(
                                dataManager
                        ),
                        this
                );


        // =========================
        // 依頼作成システム
        // =========================

        getServer()
                .getPluginManager()
                .registerEvents(
                        new QuestCreateListener(
                                questManager,
                                this
                        ),
                        this
                );


        // =========================
        // 起動メッセージ
        // =========================

        getLogger().info(
                "RPGEconomyが起動しました！"
        );
    }


    @Override
    public void onDisable() {

        // =========================
        // 全プレイヤーデータを保存
        // =========================

        if (dataManager != null) {

            for (var entry :
                    dataManager
                            .getAllPlayers()
                            .entrySet()) {

                dataManager.savePlayer(
                        entry.getKey()
                );
            }
        }


        // =========================
        // 依頼データを保存
        // =========================

        if (questManager != null) {

            questManager.saveQuests();
        }

        // =========================
// 停止メッセージ
// =========================

        getLogger().info(
                "RPGEconomyが停止しました！"
        );}}