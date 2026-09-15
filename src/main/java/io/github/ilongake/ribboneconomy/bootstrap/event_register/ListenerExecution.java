package io.github.ilongake.ribboneconomy.bootstrap.event_register;

import io.github.ilongake.ribboneconomy.core.EconomyService;
import io.github.ilongake.ribboneconomy.feature.jobs.JobManager;
import io.github.ilongake.ribboneconomy.feature.jobs.JobsFeature;
import io.github.ilongake.ribboneconomy.feature.jobs.rewards.RewardManager;
import io.github.ilongake.ribboneconomy.feature.mainmenu.RPGMenuListener;
import io.github.ilongake.ribboneconomy.feature.money.ExchangeGuiListener;
import io.github.ilongake.ribboneconomy.feature.money.ExchangeService;
import io.github.ilongake.ribboneconomy.feature.money.MoneyListener;
import io.github.ilongake.ribboneconomy.feature.quest.QuestCreateListener;
import io.github.ilongake.ribboneconomy.feature.quest.QuestManager;
import io.github.ilongake.ribboneconomy.feature.villager.VillagerShopListener;
import org.bukkit.plugin.java.JavaPlugin;

public class ListenerExecution {
    private final JavaPlugin plugin;
    private final EconomyService economy;
    private final JobManager jobs;
    private final RewardManager reward;
    private final QuestManager quest;
    private final ExchangeService exchangeService;
    private final DataManager dataManager;

    public ListenerExecution(JavaPlugin plugin, EconomyService economy, JobManager jobs, RewardManager reward, QuestManager quest,DataManager dataManager) {
        this.plugin = plugin;
        this.economy = economy;
        this.jobs = jobs;
        this.reward = reward;
        this.quest = quest;
        this.exchangeService = new ExchangeService(economy);
        this.dataManager = dataManager;
    }
    public void setListener(){
        //join-and-quit
        plugin.getServer()
                .getPluginManager()
                .registerEvents(
                        new PlayerJoinListener(
                                dataManager,
                                economy
                        )
                );

        plugin.getServer()
                .getPluginManager()
                .registerEvents(
                        new PlayerQuitListener(
                                dataManager
                        )
                );
                
        //money
        plugin.getServer()
                .getPluginManager()
                .registerEvents(
                        new ExchangeGuiListener(
                                exchangeService
                        ),
                        plugin
                );
        plugin.getServer()
                .getPluginManager()
                .registerEvents(
                        new MoneyListener(
                                economy
                        ),
                        plugin
                );

        //quest
        // =========================
        // 依頼作成システム
        // =========================

        plugin.getServer()
                .getPluginManager()
                .registerEvents(
                        new QuestCreateListener(
                                quest,
                                plugin
                        ),
                        plugin
                );

        //villager
        plugin.getServer()
                .getPluginManager()
                .registerEvents(
                        new VillagerShopListener(
                                economy
                        ),
                        plugin
                );

        //mainMenu
        plugin.getServer().getPluginManager().registerEvents(
                new RPGMenuListener(
                        quest,
                        economy,
                        jobs
                ),
                plugin
        );

        //jobs
        JobsFeature jobsFeature = new JobsFeature(plugin,jobs,reward,economy);
        jobsFeature.jobsBootstrap();
    }
}
