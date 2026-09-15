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
        ListenerExecution listenerExecution = new ListenerExecution(plugin,economy,job,reward,quest);


        commandExecution.setCommand();
        listenerExecution.setListener();
    }
}
