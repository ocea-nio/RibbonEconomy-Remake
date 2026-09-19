package io.github.ilongake.ribboneconomy.feature.money;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ExchangeGuiListener implements Listener {

    private final ExchangeService exchange;

    public ExchangeGuiListener(ExchangeService exchange) {
        this.exchange = exchange;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        // プレイヤー以外は無視
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        // エメラルド交換GUI以外は無視
        if (!event.getView().getTitle().equals(ExchangeGUI.TITLE)) {
            return;
        }

        // GUI内のアイテムをクリックしても動かせないようにする
        event.setCancelled(true);

        // クリックしたアイテム
        if (event.getCurrentItem() == null) {
            return;
        }

        // クリックした場所
        int slot = event.getRawSlot();

        // 1個交換
        if (slot == 11) {
            exchange.exchange(player, 1);
        }

        // 10個交換
        else if (slot == 13) {
            exchange.exchange(player, 10);
        }

        // 64個交換
        else if (slot == 15) {
            exchange.exchange(player, 64);
        }
    }
}