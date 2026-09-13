package io.github.ilongake.ribboneconomy;

import io.github.ilongake.ribboneconomy.bootstrap.BootstrapWithMyPlugin;
import io.github.ilongake.ribboneconomy.bootstrap.BootstrapWithVault;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.java.JavaPlugin;

public class RibbonEconomy extends JavaPlugin {
    @Override
    public void onDisable(){
        getLogger().info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
    }

    @Override
    public void onEnable(){
        BootstrapWithVault vaultSetup = new BootstrapWithVault();
        Economy vault = vaultSetup.setup(this);
        BootstrapWithMyPlugin myPlugin = new BootstrapWithMyPlugin();
        myPlugin.start(this, vault);
    }
}
