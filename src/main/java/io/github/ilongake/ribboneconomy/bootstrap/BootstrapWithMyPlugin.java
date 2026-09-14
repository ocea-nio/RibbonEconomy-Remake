package io.github.ilongake.ribboneconomy.bootstrap;

import io.github.ilongake.ribboneconomy.bootstrap.event_register.CommandExecution;
import io.github.ilongake.ribboneconomy.bootstrap.event_register.ListenerExecution;
import io.github.ilongake.ribboneconomy.core.DataManager;
import io.github.ilongake.ribboneconomy.core.EconomyService;
import io.github.ilongake.ribboneconomy.feature.jobs.JobManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.java.JavaPlugin;

import java.beans.EventHandler;

public class BootstrapWithMyPlugin {
    public void start(JavaPlugin plugin, Economy economyInstance){
        //依存関係
        EconomyService economy = new EconomyService(economyInstance);

        //マネージャー登録
        DataManager dataManager = new DataManager(plugin);
        JobManager jobManager = new JobManager(dataManager);

        //コマンド,イベント登録
        CommandExecution commandExecution = new CommandExecution(plugin,economy,jobManager);
        ListenerExecution listenerExecution = new ListenerExecution(plugin,economy,jobManager,dataManager);



    }
}
