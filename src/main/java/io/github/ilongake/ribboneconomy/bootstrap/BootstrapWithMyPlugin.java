package io.github.ilongake.ribboneconomy.bootstrap;

import io.github.ilongake.ribboneconomy.bootstrap.event_register.CommandExecution;
import io.github.ilongake.ribboneconomy.bootstrap.event_register.ListenerExecution;
import io.github.ilongake.ribboneconomy.core.DataManager;
import io.github.ilongake.ribboneconomy.core.EconomyService;
import io.github.ilongake.ribboneconomy.feature.jobs.JobManager;
import io.github.ilongake.ribboneconomy.feature.jobs.rewards.RewardManager;
import io.github.ilongake.ribboneconomy.feature.quest.QuestManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.java.JavaPlugin;

import java.beans.EventHandler;

public class BootstrapWithMyPlugin {
    public void start(JavaPlugin plugin, Economy economyInstance){
        //依存関係
        EconomyService economy = new EconomyService(economyInstance);

        //マネージャー登録
        DataManager data = new DataManager(plugin);
        JobManager job = new JobManager(data);
        RewardManager reward = new RewardManager(plugin);
        QuestManager quest = new QuestManager(plugin,economy);

        //コマンド,イベント登録
        CommandExecution commandExecution = new CommandExecution(plugin,economy,job, quest);
        ListenerExecution listenerExecution = new ListenerExecution(plugin,economy,job,reward,quest,data);


        commandExecution.setCommand();
        listenerExecution.setListener();
    }

    public void stop(){
        // =========================
        // 全プレイヤーデータ保存
        // =========================

        if (data != null) {
            for (var entry :
                    data.getAllPlayers()
                        .entrySet()) {

                data.savePlayer(entry.getKey());
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
