package io.github.ilongake.ribboneconomy.bootstrap.event_register;

import io.github.ilongake.ribboneconomy.core.DataManager;
import io.github.ilongake.ribboneconomy.core.EconomyService;
import io.github.ilongake.ribboneconomy.feature.jobs.JobManager;
import io.github.ilongake.ribboneconomy.feature.mainmenu.RPGMenuListener;
import org.bukkit.plugin.java.JavaPlugin;

public class ListenerExecution {
    private final JavaPlugin plugin;
    private final EconomyService economy;
    private final JobManager jobs;
    private final DataManager data;

    public ListenerExecution(JavaPlugin plugin, EconomyService economy, JobManager jobs, DataManager data) {
        this.plugin = plugin;
        this.economy = economy;
        this.jobs = jobs;
        this.data = data;
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
    }
}
