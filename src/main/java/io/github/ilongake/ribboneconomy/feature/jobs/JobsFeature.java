package io.github.ilongake.ribboneconomy.feature.jobs;

import io.github.ilongake.ribboneconomy.core.EconomyService;
import io.github.ilongake.ribboneconomy.feature.jobs.rewards.RewardListener;
import io.github.ilongake.ribboneconomy.feature.jobs.rewards.RewardManager;
import org.bukkit.plugin.java.JavaPlugin;

public class JobsFeature {
    private final RewardManager reward;
    private final JobManager job;
    private final JavaPlugin plugin;
    private final EconomyService economy;
    public JobsFeature(JavaPlugin plugin, JobManager job, RewardManager reward, EconomyService economy){
        this.plugin = plugin;
        this.job = job;
        this.reward = reward;
        this.economy = economy;
    }

    public void jobsBootstrap(){
        // =========================
        // 職業GUI
        // =========================

        plugin.getServer()
                .getPluginManager()
                .registerEvents(
                        new JobGUIListener(
                                job
                        ),
                        plugin
                );

        //Rewards
        plugin.getServer()
                .getPluginManager()
                .registerEvents(
                        new RewardListener(job,reward,economy),
                        plugin
                );
    }
}
