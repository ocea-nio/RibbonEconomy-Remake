package io.github.ilongake.ribboneconomy.command;

import io.github.ilongake.ribboneconomy.core.DataManager;
import io.github.ilongake.ribboneconomy.core.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BalanceTopCommand implements CommandExecutor {

    private final DataManager dataManager;

    public BalanceTopCommand(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        // 全プレイヤーのデータを取得
        Map<UUID, PlayerData> players =
                dataManager.getAllPlayers();

        // ランキング用のリストを作成
        List<Map.Entry<UUID, PlayerData>> ranking =
                new ArrayList<>(players.entrySet());

        // 残高が多い順に並べる
        ranking.sort(
                Map.Entry.<UUID, PlayerData>comparingByValue(
                        Comparator.comparingDouble(
                                PlayerData::getBalance
                        )
                ).reversed()
        );

        // タイトル
        sender.sendMessage("§6========== 所持金ランキング ==========");

        // 最大10人まで表示
        int limit = Math.min(10, ranking.size());

        for (int i = 0; i < limit; i++) {

            Map.Entry<UUID, PlayerData> entry =
                    ranking.get(i);

            UUID uuid = entry.getKey();

            double balance =
                    entry.getValue().getBalance();

            // UUIDからプレイヤー名を取得
            String playerName =
                    Bukkit.getOfflinePlayer(uuid).getName();

            if (playerName == null) {
                playerName = "Unknown";
            }

            // ランキング表示
            sender.sendMessage(
                    "§e" + (i + 1)
                            + "位 §f"
                            + playerName
                            + " §7: §a"
                            + balance
                            + "円"
            );
        }

        sender.sendMessage("§6======================================");

        return true;
    }
}