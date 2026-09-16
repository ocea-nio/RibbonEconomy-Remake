package io.github.ilongake.ribboneconomy.bootstrap;

import io.github.ilongake.ribboneconomy.bootstrap.event_register.CommandExecution;
import io.github.ilongake.ribboneconomy.bootstrap.event_register.ListenerExecution;
import io.github.ilongake.ribboneconomy.core.DataManager;
import io.github.ilongake.ribboneconomy.core.EconomyService;
import io.github.ilongake.ribboneconomy.feature.jobs.JobManager;
import io.github.ilongake.ribboneconomy.feature.jobs.farmer.FoodBuffKeys;
import io.github.ilongake.ribboneconomy.feature.jobs.farmer.FoodBuffListener;
import io.github.ilongake.ribboneconomy.feature.jobs.farmer.FoodBuffManager;
import io.github.ilongake.ribboneconomy.feature.jobs.rewards.RewardManager;
import io.github.ilongake.ribboneconomy.feature.quest.QuestManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;


public class BootstrapWithMyPlugin {
    private DataManager data;
    private QuestManager quest;
    public void start(JavaPlugin plugin, Economy economyInstance){
        //依存
        EconomyService economy = new EconomyService(economyInstance);

        //マネージャー登録
        data = new DataManager(plugin);
        JobManager job = new JobManager(data);
        RewardManager reward = new RewardManager(plugin);
        quest = new QuestManager(plugin,economy);

        //コマンド,イベント登録
        CommandExecution commandExecution = new CommandExecution(plugin,economy,job, quest,data);
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

        if (quest != null) {

            quest.saveQuests();
        }


        // =========================
        // 停止メッセージ
        // =========================

        Bukkit.getLogger().info(
                "RPGEconomyが停止しました！"
        );
    }
}
