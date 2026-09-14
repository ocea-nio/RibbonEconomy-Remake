package io.github.ilongake.ribboneconomy.feature.money;

import io.github.ilongake.ribboneconomy.core.EconomyService;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PayCommand implements CommandExecutor {

    private final EconomyService economy;

    public PayCommand(EconomyService economy) {
        this.economy = economy;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

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
        double senderBalance = economy.getBalance(player.getUniqueId());

        // 残高不足
        if (senderBalance < amount) {
            player.sendMessage("§c残高が足りません。");
            player.sendMessage(
                    "§7現在の残高: §e" + senderBalance + "円"
            );
            return true;
        }

        //送金する
        economy.transfer(player.getUniqueId(),target.getUniqueId(),amount);

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