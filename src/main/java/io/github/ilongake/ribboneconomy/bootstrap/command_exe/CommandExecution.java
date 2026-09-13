package io.github.ilongake.ribboneconomy.bootstrap.command_exe;

import io.github.ilongake.ribboneconomy.core.EconomyService;
import io.github.ilongake.ribboneconomy.feature.command.BalanceTopCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandExecution {
    private final JavaPlugin plugin;
    private final EconomyService economy;

    public CommandExecution(JavaPlugin plugin, EconomyService economy) {
        this.plugin = plugin;
        this.economy = economy;
    }

    public void setCommand(){
        plugin.getCommand("balancetop").setExecutor(
                new BalanceTopCommand(
                        economy
                )
        );
    }
}
