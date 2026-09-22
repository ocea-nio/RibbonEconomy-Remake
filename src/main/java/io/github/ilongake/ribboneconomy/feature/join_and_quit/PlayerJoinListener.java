package io.github.ilongake.ribboneconomy.feature.join_and_quit;

import io.github.ilongake.ribboneconomy.core.DataManager;
import io.github.ilongake.ribboneconomy.core.EconomyService;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PlayerJoinListener implements Listener {

    private final DataManager dataManager;
    private final EconomyService economy;


    public PlayerJoinListener(
            DataManager dataManager,
            EconomyService economy
    ) {

        this.dataManager = dataManager;
        this.economy = economy;
    }


    @EventHandler
    public void onPlayerJoin(
            PlayerJoinEvent event
    ) {

        Player player =
                event.getPlayer();

        UUID uuid =
                player.getUniqueId();


        // ======================================================
        // プレイヤーデータを読み込む
        // ======================================================

        dataManager.loadPlayer(uuid);


        // ======================================================
        // ログインメッセージ
        // ======================================================

        player.sendMessage(
                "§aプレイヤーデータを読み込みました！"
        );

        player.sendMessage(
                "§e現在の所持金: "
                        + economy.getBalance(uuid)
        );
    }
}

