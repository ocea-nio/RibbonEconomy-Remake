package io.github.ilongake.ribboneconomy.listener;

import io.github.ilongake.ribboneconomy.core.DataManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerQuitListener implements Listener {

    private final DataManager dataManager;

    public PlayerQuitListener(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        UUID uuid = event.getPlayer().getUniqueId();

        // 保存
        dataManager.savePlayer(uuid);

        // メモリから削除
        dataManager.unloadPlayer(uuid);
    }
}