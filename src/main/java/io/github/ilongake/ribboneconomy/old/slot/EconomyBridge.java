package io.github.ilongake.ribboneconomy.slot;

import io.github.ilongake.ribboneconomy.core.DataManager;
import org.bukkit.entity.Player;

/**
 * スロットマシンから DataManager を呼び出すための薄いラッパー。
 * 既存の DataManager(UUIDベース)にそのまま委譲するだけなので、
 * DataManager 側の仕様が変わってもここだけ直せばよい。
 */
public class EconomyBridge {

    private final DataManager dataManager;

    public EconomyBridge(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    /** 残高取得 */
    public double getBalance(Player player) {
        return dataManager.getBalance(player.getUniqueId());
    }

    /** 引き出し(残高不足ならfalseを返し何もしない) */
    public boolean withdraw(Player player, double amount) {
        return dataManager.withdraw(player.getUniqueId(), amount);
    }

    /** 入金 */
    public void deposit(Player player, double amount) {
        dataManager.addBalance(player.getUniqueId(), amount);
    }
}