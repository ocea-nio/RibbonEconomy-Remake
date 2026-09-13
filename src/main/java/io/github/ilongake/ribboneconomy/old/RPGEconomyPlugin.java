package io.github.ilongake.ribboneconomy.old;

import io.github.ilongake.ribboneconomy.feature.command.BalanceTopCommand;
import io.github.ilongake.ribboneconomy.command.ExchangeCommand;
import io.github.ilongake.ribboneconomy.command.MoneyCommand;
import io.github.ilongake.ribboneconomy.command.PayCommand;
import io.github.ilongake.ribboneconomy.core.DataManager;
import io.github.ilongake.ribboneconomy.farmer.FarmerTradeCommand;
import io.github.ilongake.ribboneconomy.farmer.FarmerTradeGUI;
import io.github.ilongake.ribboneconomy.farmer.FarmerTradeListener;
import io.github.ilongake.ribboneconomy.farmer.FarmerTradeManager;
import io.github.ilongake.ribboneconomy.farmer.FoodBuffKeys;
import io.github.ilongake.ribboneconomy.farmer.FoodBuffManager;
import io.github.ilongake.ribboneconomy.farmer.FoodBuffListener;
import io.github.ilongake.ribboneconomy.gui.JobListener;
import io.github.ilongake.ribboneconomy.gui.MoneyListener;
import io.github.ilongake.ribboneconomy.gui.QuestCreateListener;
import io.github.ilongake.ribboneconomy.gui.RPGMenuListener;
import io.github.ilongake.ribboneconomy.job.JobAdminCommand;
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
import io.github.ilongake.ribboneconomy.slot.GiveSlotCommand;
import io.github.ilongake.ribboneconomy.slot.SlotMachineListener;

import org.bukkit.plugin.java.JavaPlugin;

public final class RPGEconomyPlugin extends JavaPlugin {

    private DataManager dataManager;

    private JobManager jobManager;

    private QuestManager questManager;

    private FarmerTradeManager farmerTradeManager;

    private FarmerTradeGUI farmerTradeGUI;

    /**
     * 農民特殊食料・Lv.50パッシブ管理
     */
    private FoodBuffManager foodBuffManager;


    @Override
    public void onEnable() {

        // =========================
        // DataManager
        // =========================

        dataManager =
                new DataManager(this);


        // =========================
        // JobManager
        // =========================

        jobManager =
                new JobManager(
                        dataManager
                );

        FoodBuffKeys.initialize(this);

        foodBuffManager =
                new FoodBuffManager(
                        jobManager
                );
        // =========================================================
// 農民特殊食料Listener
// =========================================================
//
// ・Lv.20以上でクラフト時に特殊食料化
// ・特殊食料を食べた時に効果発動
// =========================================================

        getServer()
                .getPluginManager()
                .registerEvents(
                        new FoodBuffListener(
                                foodBuffManager
                        ),
                        this
                );

        jobManager.setFoodBuffManager(
                foodBuffManager
        );


        // =========================
        // Farmer Trade
        // =========================

        farmerTradeManager =
                new FarmerTradeManager(
                        this,
                        dataManager,
                        jobManager
                );

        farmerTradeGUI =
                new FarmerTradeGUI(
                        farmerTradeManager
                );

        FarmerTradeListener farmerTradeListener =
                new FarmerTradeListener(
                        this,
                        farmerTradeManager,
                        farmerTradeGUI
                );

        getServer()
                .getPluginManager()
                .registerEvents(
                        farmerTradeListener,
                        this
                );

        getCommand("market").setExecutor(
                new FarmerTradeCommand(
                        farmerTradeManager,
                        farmerTradeGUI
                )
        );


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
        // 職業管理者コマンド
        // =========================

        getCommand("jobadmin").setExecutor(
                new JobAdminCommand(
                        jobManager,
                        dataManager
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
        //
        // ログイン時にLv.50パッシブを自動更新
        // =========================

        getServer()
                .getPluginManager()
                .registerEvents(
                        new PlayerJoinListener(
                                dataManager,
                                foodBuffManager
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
                                dataManager,
                                jobManager
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
        // スロットマシン
        // =========================

        SlotMachineListener slotMachineListener =
                new SlotMachineListener(
                        this,
                        dataManager
                );

        getServer()
                .getPluginManager()
                .registerEvents(
                        slotMachineListener,
                        this
                );

        getCommand("giveslot").setExecutor(
                new GiveSlotCommand(
                        slotMachineListener
                )
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
        // Farmer Trade保存
        // =========================

        if (farmerTradeManager != null) {

            farmerTradeManager.shutdown();
        }


        // =========================
        // 全プレイヤーデータ保存
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
        // 依頼データ保存
        // =========================

        if (questManager != null) {

            questManager.saveQuests();
        }


        // =========================
        // 停止メッセージ
        // =========================

        getLogger().info(
                "RPGEconomyが停止しました！"
        );
    }
}
