package io.github.ilongake.ribboneconomy.bootstrap.event_register;

import io.github.ilongake.ribboneconomy.core.DataManager;
import io.github.ilongake.ribboneconomy.core.EconomyService;
import io.github.ilongake.ribboneconomy.feature.jobs.command.JobAdminCommand;
import io.github.ilongake.ribboneconomy.feature.jobs.command.JobCommand;
import io.github.ilongake.ribboneconomy.feature.jobs.JobManager;
import io.github.ilongake.ribboneconomy.feature.money.BalanceTopCommand;
import io.github.ilongake.ribboneconomy.feature.money.ExchangeCommand;
import io.github.ilongake.ribboneconomy.feature.money.MoneyCommand;
import io.github.ilongake.ribboneconomy.feature.money.PayCommand;
import io.github.ilongake.ribboneconomy.feature.quest.QuestCommand;
import io.github.ilongake.ribboneconomy.feature.quest.QuestManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandExecution {
    private final JavaPlugin plugin;
    private final EconomyService economy;
    private final JobManager jobs;
    private final QuestManager quest;
    private final DataManager data;

    public CommandExecution(JavaPlugin plugin, EconomyService economy, JobManager jobs, QuestManager quest, DataManager data) {
        this.plugin = plugin;
        this.economy = economy;
        this.jobs = jobs;
        this.quest = quest;
        this.data = data;
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
        plugin.getCommand("exchange").setExecutor(new ExchangeCommand());

        // =========================
        // 職業コマンド
        // =========================

        plugin.getCommand("job").setExecutor(
                new JobCommand(
                        jobs
                )
        );

        plugin.getCommand("jobadmin").setExecutor(
                new JobAdminCommand(
                        jobs,data
                )
        );

        // =========================
        // 依頼コマンド
        // =========================

        plugin.getCommand("quest").setExecutor(
                new QuestCommand(
                        quest
                )
        );
    }
}
