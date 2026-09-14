package io.github.ilongake.ribboneconomy.bootstrap.event_register;

import io.github.ilongake.ribboneconomy.core.DataManager;
import io.github.ilongake.ribboneconomy.core.EconomyService;
import io.github.ilongake.ribboneconomy.feature.jobs.JobManager;
import io.github.ilongake.ribboneconomy.feature.jobs.JobsFeature;
import io.github.ilongake.ribboneconomy.feature.jobs.RewardManager;
import io.github.ilongake.ribboneconomy.feature.mainmenu.RPGMenuListener;
import org.bukkit.plugin.java.JavaPlugin;

public class ListenerExecution {
    private final JavaPlugin plugin;
    private final EconomyService economy;
    private final JobManager jobs;
    private final DataManager data;
    private final RewardManager reward;

    public ListenerExecution(JavaPlugin plugin, EconomyService economy, JobManager jobs, DataManager data, RewardManager reward) {
        this.plugin = plugin;
        this.economy = economy;
        this.jobs = jobs;
        this.data = data;
        this.reward = reward;
    }
    public void setListener(){

        //RPGGUI用
        plugin.getServer().getPluginManager().registerEvents(
                new RPGMenuListener(
                        questManager,
                        data,
                        jobs
                ),
                this
        );

        //jobs
        JobsFeature jobsFeature = new JobsFeature(plugin,jobs,reward,economy);
        jobsFeature.jobsBootstrap();
    }
}
