package io.github.ilongake.ribboneconomy.feature.money;

import io.github.ilongake.ribboneconomy.core.EconomyService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MoneyCommand implements CommandExecutor {

    private final EconomyService economy;

    public MoneyCommand(EconomyService economy) {
        this.economy = economy;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        /*
         * =========================
         * /money
         * =========================
         *
         * 自分の所持金を表示
         */

        if (args.length == 0) {

            if (!(sender instanceof Player player)) {

                sender.sendMessage(
                        ChatColor.RED
                                + "このコマンドはプレイヤーのみ使用できます。"
                );

                return true;
            }

            double balance =
                    economy.getBalance(player.getUniqueId());

            player.sendMessage(
                    ChatColor.GREEN
                            + "あなたの所持金: "
                            + ChatColor.YELLOW
                            + String.format(
                            "%,.0f",
                            balance
                    )
                            + "円"
            );

            return true;
        }


        /*
         * =========================
         * /money give <プレイヤー> <金額>
         * =========================
         */

        if (args[0].equalsIgnoreCase("give")) {

            /*
             * OPのみ使用可能
             */

            if (!sender.isOp()) {

                sender.sendMessage(
                        ChatColor.RED
                                + "このコマンドはOPのみ使用できます。"
                );

                return true;
            }


            /*
             * 引数チェック
             */

            if (args.length < 3) {

                sender.sendMessage(
                        ChatColor.RED
                                + "使い方: /money give <プレイヤー> <金額>"
                );

                return true;
            }


            /*
             * プレイヤーを取得
             */

            OfflinePlayer target =
                    Bukkit.getOfflinePlayer(
                            args[1]
                    );


            /*
             * 金額を数字に変換
             */

            double amount;

            try {

                amount =
                        Double.parseDouble(
                                args[2]
                        );

            } catch (NumberFormatException e) {

                sender.sendMessage(
                        ChatColor.RED
                                + "金額は数字で入力してください。"
                );

                return true;
            }


            /*
             * 0以下は禁止
             */

            if (amount <= 0) {

                sender.sendMessage(
                        ChatColor.RED
                                + "金額は1円以上にしてください。"
                );

                return true;
            }

            /*
             * プレイヤーデータが読み込まれているか確認
             */

            economy.deposit(target,amount);

            /*
             * 実行者にメッセージ
             */

            sender.sendMessage(
                    ChatColor.GREEN
                            + target.getName()
                            + " に "
                            + String.format(
                            "%,.0f",
                            amount
                    )
                            + "円を与えました。"
            );

            /*
             * 対象プレイヤーがオンラインなら通知
             */

            if (target.isOnline()) {

                Player targetPlayer =
                        target.getPlayer();

                if (targetPlayer != null) {

                    targetPlayer.sendMessage(
                            ChatColor.GREEN
                                    + "管理者から "
                                    + ChatColor.YELLOW
                                    + String.format(
                                    "%,.0f",
                                    amount
                            )
                                    + "円が付与されました！"
                    );
                }
            }

            return true;
        }

        if (args[0].equalsIgnoreCase("delete")){
            if (!sender.isOp()) {

                sender.sendMessage(
                        ChatColor.RED
                                + "このコマンドはOPのみ使用できます。"
                );

                return true;
            }

            if (args.length < 3) {

                sender.sendMessage(
                        ChatColor.RED
                                + "使い方: /money delete <プレイヤー> <金額>"
                );

                return true;
            }
            OfflinePlayer target =
                    Bukkit.getOfflinePlayer(
                            args[1]
                    );
            /*
             * 金額を数字に変換
             */

            double amount;

            try {

                amount =
                        Double.parseDouble(
                                args[2]
                        );

            } catch (NumberFormatException e) {

                sender.sendMessage(
                        ChatColor.RED
                                + "金額は数字で入力してください。"
                );

                return true;
            }


            /*
             * 0以下は禁止
             */

            if (amount <= 0) {

                sender.sendMessage(
                        ChatColor.RED
                                + "金額は1円以上にしてください。"
                );

                return true;
            }

            /*
             * プレイヤーデータが読み込まれているか確認
             */

            economy.forceWithdraw(target.getUniqueId(),amount);

            /*
             * 実行者にメッセージ
             */

            sender.sendMessage(
                    ChatColor.GREEN
                            + target.getName()
                            + " から "
                            + String.format(
                            "%,.0f",
                            amount
                    )
                            + "円を奪いました。"
            );
        }


        /*
         * =========================
         * 知らないコマンド
         * =========================
         */

        sender.sendMessage(
                ChatColor.RED
                        + "使い方: /money"
        );

        if (!sender.isOp()) {
            sender.sendMessage(
                    ChatColor.RED
                            + "/money give <プレイヤー> <金額>"
            );

            sender.sendMessage(
                    ChatColor.RED
                            + "/money delete <プレイヤー> <金額>"
            );
        }
        return true;
    }
}