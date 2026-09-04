package io.github.ilongake.ribboneconomy.listener;

import io.github.ilongake.ribboneconomy.core.DataManager;
import io.github.ilongake.ribboneconomy.farmer.FoodBuffManager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PlayerJoinListener implements Listener {

    private final DataManager dataManager;

    private final FoodBuffManager foodBuffManager;


    public PlayerJoinListener(
            DataManager dataManager,
            FoodBuffManager foodBuffManager
    ) {

        this.dataManager = dataManager;

        this.foodBuffManager = foodBuffManager;
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
        // Lv.50パッシブを自動更新
        // ======================================================
        //
        // FARMER Lv.50
        // → 最大体力 +4
        // → 満腹度 +4
        //
        // FARMER Lv.50未満
        // → パッシブなし
        //
        // FARMER以外
        // → パッシブなし
        // ======================================================

        foodBuffManager.applyLevel50Passive(
                player
        );


        // ======================================================
        // ログインメッセージ
        // ======================================================

        player.sendMessage(
                "§aプレイヤーデータを読み込みました！"
        );

        player.sendMessage(
                "§e現在の所持金: "
                        + dataManager.getBalance(uuid)
        );
    }
}

