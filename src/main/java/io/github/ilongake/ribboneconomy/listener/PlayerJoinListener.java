package io.github.ilongake.ribboneconomy.listener;

import io.github.ilongake.ribboneconomy.core.DataManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PlayerJoinListener implements Listener {

    private final DataManager dataManager;

    public PlayerJoinListener(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        // プレイヤーデータを読み込む
        dataManager.loadPlayer(uuid);

        player.sendMessage("§aプレイヤーデータを読み込みました！");
        player.sendMessage("§e現在の所持金: " + dataManager.getBalance(uuid));
    }
}