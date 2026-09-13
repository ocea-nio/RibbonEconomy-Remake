package io.github.ilongake.ribboneconomy.bootstrap;

import io.github.ilongake.ribboneconomy.bootstrap.command_exe.CommandExecution;
import io.github.ilongake.ribboneconomy.core.EconomyService;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.java.JavaPlugin;

public class BootstrapWithMyPlugin {
    public void start(JavaPlugin plugin, Economy economyInstance){
        EconomyService economy =
                new EconomyService(economyInstance);
        CommandExecution commandExecution =
                new CommandExecution(plugin,economy);



    }
}
