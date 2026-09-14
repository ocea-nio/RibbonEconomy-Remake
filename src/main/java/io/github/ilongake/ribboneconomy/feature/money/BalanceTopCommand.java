package io.github.ilongake.ribboneconomy.feature.money;

import io.github.ilongake.ribboneconomy.core.EconomyService;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.*;

public class BalanceTopCommand implements CommandExecutor {

    private final EconomyService economy;

    public BalanceTopCommand(EconomyService economy) {
        this.economy = economy;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {
        // ランキング用のリストを作成
        List<OfflinePlayer> ranking =
                new ArrayList<>(Arrays.asList(Bukkit.getOfflinePlayers()));



        // 残高が多い順に並べる
        ranking.sort(
                Comparator.comparingDouble(
                        (OfflinePlayer player) -> economy.getBalance(player)
                ).reversed()
        );

        // タイトル
        sender.sendMessage("§6========== 所持金ランキング ==========");

        // 最大10人まで表示
        int limit = Math.min(10, ranking.size());

        for (int i = 0; i < limit; i++) {

            OfflinePlayer player = ranking.get(i);
            double balance =
                    economy.getBalance(player);

            //プレイヤー名を取得
            String playerName =
                    player.getName();

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