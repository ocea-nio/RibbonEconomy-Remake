package io.github.ilongake.ribboneconomy.core;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class EconomyService {
    private final Economy economy;
    public EconomyService(Economy economy) {
        this.economy = economy;
    }



    public double getBalance(UUID player){
        return  economy.getBalance(uuidTranslator(player));
    }
    public double getBalance(OfflinePlayer player){
        return  economy.getBalance(player);
    }



    public boolean deposit(UUID player, double amount) {
        if (amount < 0){return false;}
        return economy.depositPlayer(uuidTranslator(player),amount).transactionSuccess();
    }
    public boolean deposit(OfflinePlayer player, double amount) {
        if (amount < 0){return false;}
        return economy.depositPlayer(player,amount).transactionSuccess();
    }


    public boolean withdraw(UUID player, double amount) {
        if (amount < 0){return false;}
        if (amount > getBalance(player)){return false;}
        return economy.withdrawPlayer(uuidTranslator(player),amount).transactionSuccess();
    }

    public boolean transfer(UUID from, UUID to, double amount) {
        if (from == to) return false;
        if (amount < 0) return false;

        if (!withdraw(from, amount)) {
            return false;
        }
        return deposit(to, amount);
    }

    private OfflinePlayer uuidTranslator(UUID uuid){
       return Bukkit.getOfflinePlayer(uuid);
    }
}
