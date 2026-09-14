package io.github.ilongake.ribboneconomy.bootstrap.event_register;

import io.github.ilongake.ribboneconomy.core.EconomyService;
import io.github.ilongake.ribboneconomy.feature.jobs.command.JobCommand;
import io.github.ilongake.ribboneconomy.feature.jobs.JobManager;
import io.github.ilongake.ribboneconomy.feature.money.BalanceTopCommand;
import io.github.ilongake.ribboneconomy.feature.money.MoneyCommand;
import io.github.ilongake.ribboneconomy.feature.money.PayCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandExecution {
    private final JavaPlugin plugin;
    private final EconomyService economy;
    private final JobManager jobs;

    public CommandExecution(JavaPlugin plugin, EconomyService economy, JobManager jobs) {
        this.plugin = plugin;
        this.economy = economy;
        this.jobs = jobs;
    }

    public void setCommand(){
        // =========================
        // 経済コマンド
        // =========================
        plugin.getCommand("balancetop").setExecutor(
                new BalanceTopCommand(
                        economy
                )
        );
        plugin.getCommand("money").setExecutor(
                new MoneyCommand(
                        economy
                )
        );

        plugin.getCommand("pay").setExecutor(
                new PayCommand(
                        economy
                )
        );

        // =========================
        // 職業コマンド
        // =========================

        plugin.getCommand("job").setExecutor(
                new JobCommand(
                        jobs
                )
        );
    }
}
