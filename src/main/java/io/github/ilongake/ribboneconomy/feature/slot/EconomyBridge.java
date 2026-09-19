package io.github.ilongake.ribboneconomy.feature.slot;

import io.github.ilongake.ribboneconomy.core.DataManager;
import io.github.ilongake.ribboneconomy.core.EconomyService;
import org.bukkit.entity.Player;

/**
 * スロットマシンから DataManager を呼び出すための薄いラッパー。
 * 既存の DataManager(UUIDベース)にそのまま委譲するだけなので、
 * DataManager 側の仕様が変わってもここだけ直せばよい。
 */
public class EconomyBridge {

    private final EconomyService service;

    public EconomyBridge(EconomyService service) {
        this.service = service;
    }

    /** 残高取得 */
    public double getBalance(Player player) {
        return service.getBalance(player.getUniqueId());
    }

    /** 引き出し(残高不足ならfalseを返し何もしない) */
    public boolean withdraw(Player player, double amount) {
        return service.withdraw(player.getUniqueId(), amount);
    }

    /** 入金 */
    public void deposit(Player player, double amount) {
        service.deposit(player.getUniqueId(), amount);
    }
}