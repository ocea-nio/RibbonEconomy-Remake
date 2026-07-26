package io.github.ilongake.ribboneconomy.command;

import io.github.ilongake.ribboneconomy.core.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

public class PayCommand implements CommandExecutor {

    private final DataManager dataManager;

    public PayCommand(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        if (command.getName().equalsIgnoreCase("money")) {

            // OP以外は使用不可
            if (!sender.isOp()) {

                sender.sendMessage(
                        ChatColor.RED
                                + "このコマンドを使用する権限がありません。"
                );

                return true;
            }


            // /money give プレイヤー名 金額
            if (args.length == 3
                    && args[0].equalsIgnoreCase("give")) {

                Player target =
                        Bukkit.getPlayer(args[1]);

                // 以下は今までのコード
            }
        }

        // プレイヤー以外が実行した場合
        if (!(sender instanceof Player player)) {
            sender.sendMessage("このコマンドはプレイヤーのみ使用できます。");
            return true;
        }

        // 引数が2つない場合
        // /pay <プレイヤー> <金額>
        if (args.length != 2) {
            player.sendMessage("§c使い方: /pay <プレイヤー> <金額>");
            return true;
        }

        // 送金相手を探す
        Player target = Bukkit.getPlayerExact(args[0]);

        if (target == null) {
            player.sendMessage("§cそのプレイヤーはオンラインではありません。");
            return true;
        }

        // 自分自身への送金を禁止
        if (target.equals(player)) {
            player.sendMessage("§c自分自身には送金できません。");
            return true;
        }

        // 金額を数字に変換
        double amount;

        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("§c金額は数字で入力してください。");
            return true;
        }

        // 0以下は禁止
        if (amount <= 0) {
            player.sendMessage("§c送金額は0より大きくしてください。");
            return true;
        }

        // 自分の残高を取得
        double senderBalance = dataManager.getBalance(
                player.getUniqueId()
        );

        // 残高不足
        if (senderBalance < amount) {
            player.sendMessage("§c残高が足りません。");
            player.sendMessage(
                    "§7現在の残高: §e" + senderBalance + "円"
            );
            return true;
        }

        // 送金元からお金を減らす
        dataManager.removeBalance(
                player.getUniqueId(),
                amount
        );

        // 送金先にお金を追加
        dataManager.addBalance(
                target.getUniqueId(),
                amount
        );

        // 送金したプレイヤーに通知
        player.sendMessage(
                "§a" + target.getName()
                        + " §aに §e" + amount
                        + "円 §a送金しました！"
        );

        // 受け取ったプレイヤーに通知
        target.sendMessage(
                "§e" + player.getName()
                        + " §aから §e" + amount
                        + "円 §a受け取りました！"
        );

        return true;
    }
}