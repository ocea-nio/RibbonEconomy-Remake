package io.github.ilongake.ribboneconomy.bootstrap;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public class BootstrapWithVault {
    public Economy setup(Plugin plugin){
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null){throw new IllegalStateException("Vault is not installed.");}
        RegisteredServiceProvider<Economy> provider =
                plugin.getServer()
                        .getServicesManager()
                        .getRegistration(Economy.class);
        if (provider == null) {
            throw new IllegalStateException("Economy provider is not available.");
        }
        return provider.getProvider();
    }
}