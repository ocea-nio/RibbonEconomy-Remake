package io.github.ilongake.ribboneconomy.bootstrap.event_register;

import io.github.ilongake.ribboneconomy.core.DataManager;
import io.github.ilongake.ribboneconomy.core.EconomyService;
import io.github.ilongake.ribboneconomy.feature.jobs.JobManager;
import io.github.ilongake.ribboneconomy.feature.jobs.JobsFeature;
import io.github.ilongake.ribboneconomy.feature.jobs.rewards.RewardManager;
import io.github.ilongake.ribboneconomy.feature.mainmenu.RPGMenuListener;
import io.github.ilongake.ribboneconomy.feature.money.ExchangeGuiListener;
import io.github.ilongake.ribboneconomy.feature.money.ExchangeService;
import io.github.ilongake.ribboneconomy.feature.money.MoneyListener;
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

    public ListenerExecution(JavaPlugin plugin, EconomyService economy, JobManager jobs, RewardManager reward, QuestManager quest) {
        this.plugin = plugin;
        this.economy = economy;
        this.jobs = jobs;
        this.reward = reward;
        this.quest = quest;
        this.exchangeService = new ExchangeService(economy);
    }
    public void setListener(){
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
