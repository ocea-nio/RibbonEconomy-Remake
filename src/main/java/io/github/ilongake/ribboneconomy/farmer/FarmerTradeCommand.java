package io.github.ilongake.ribboneconomy.farmer;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FarmerTradeCommand implements CommandExecutor {

    private final FarmerTradeManager tradeManager;
    private final FarmerTradeGUI tradeGUI;

    public FarmerTradeCommand(
            FarmerTradeManager tradeManager,
            FarmerTradeGUI tradeGUI
    ) {
        this.tradeManager = tradeManager;
        this.tradeGUI = tradeGUI;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        /*
         * ==========================================================
         * プレイヤー専用
         * ==========================================================
         */

        if (!(sender instanceof Player player)) {

            sender.sendMessage(
                    "このコマンドはプレイヤーのみ使用できます。"
            );

            return true;
        }

        /*
         * ==========================================================
         * /market
         * ==========================================================
         *
         * マーケットを見るだけなら
         * 全職業・全レベルで使用可能。
         */

        if (args.length == 0) {

            tradeGUI.openMainMenu(player);

            return true;
        }

        /*
         * ==========================================================
         * /market help
         * ==========================================================
         */

        if (args[0].equalsIgnoreCase("help")) {

            sendHelp(player);

            return true;
        }

        /*
         * ==========================================================
         * /market sell
         * ==========================================================
         *
         * 出品にはファーマーLv.10以上が必要。
         */

        if (args[0].equalsIgnoreCase("sell")) {

            /*
             * Lv.10未満なら出品不可
             *
             * isUnlocked() は
             * FarmerTradeManager側に
             * 既に存在しているメソッドを使用。
             */

            if (!tradeManager.isUnlocked(
                    player.getUniqueId()
            )) {

                player.sendMessage(
                        ChatColor.RED
                                + "農作物の出品には"
                                + "ファーマーLv.10以上が必要です。"
                );

                return true;
            }

            handleSell(
                    player,
                    args
            );

            return true;
        }

        /*
         * ==========================================================
         * 未知のコマンド
         * ==========================================================
         */

        player.sendMessage(
                ChatColor.RED
                        + "使用方法: /market"
        );

        player.sendMessage(
                ChatColor.GRAY
                        + "/market help"
                        + ChatColor.WHITE
                        + " でヘルプを確認できます。"
        );

        return true;
    }

    /*
     * ==========================================================
     * 出品処理
     * ==========================================================
     */

    private void handleSell(
            Player player,
            String[] args
    ) {

        /*
         * /market sell <個数> <価格>
         */

        if (args.length != 3) {

            player.sendMessage(
                    ChatColor.RED
                            + "使用方法: /market sell <個数> <価格>"
            );

            player.sendMessage(
                    ChatColor.GRAY
                            + "例: /market sell 32 500"
            );

            return;
        }

        /*
         * ======================================================
         * 個数
         * ======================================================
         */

        int amount;

        try {

            amount =
                    Integer.parseInt(
                            args[1]
                    );

        } catch (NumberFormatException e) {

            player.sendMessage(
                    ChatColor.RED
                            + "個数には整数を指定してください。"
            );

            return;
        }

        if (amount <= 0) {

            player.sendMessage(
                    ChatColor.RED
                            + "個数は1以上にしてください。"
            );

            return;
        }

        if (amount > 64) {

            player.sendMessage(
                    ChatColor.RED
                            + "1回の出品は64個までです。"
            );

            return;
        }

        /*
         * ======================================================
         * 価格
         * ======================================================
         */

        double price;

        try {

            price =
                    Double.parseDouble(
                            args[2]
                    );

        } catch (NumberFormatException e) {

            player.sendMessage(
                    ChatColor.RED
                            + "価格には数字を指定してください。"
            );

            return;
        }

        if (Double.isNaN(price)
                || Double.isInfinite(price)
                || price <= 0) {

            player.sendMessage(
                    ChatColor.RED
                            + "価格は0より大きい数字にしてください。"
            );

            return;
        }

        /*
         * ======================================================
         * メインハンド
         * ======================================================
         */

        ItemStack held =
                player.getInventory()
                        .getItemInMainHand();

        if (held == null
                || held.getType().isAir()) {

            player.sendMessage(
                    ChatColor.RED
                            + "出品する作物を手に持ってください。"
            );

            return;
        }

        /*
         * ======================================================
         * 所持数確認
         * ======================================================
         */

        if (held.getAmount() < amount) {

            player.sendMessage(
                    ChatColor.RED
                            + "手に持っているアイテムが足りません。"
            );

            player.sendMessage(
                    ChatColor.GRAY
                            + "所持数: "
                            + held.getAmount()
                            + "個"
            );

            return;
        }

        /*
         * ======================================================
         * 出品用ItemStack
         * ======================================================
         */

        ItemStack tradeItem =
                held.clone();

        tradeItem.setAmount(
                amount
        );

        /*
         * ======================================================
         * 出品
         * ======================================================
         */

        FarmerTradeManager.Trade trade =
                tradeManager.createTrade(
                        player.getUniqueId(),
                        tradeItem,
                        price
                );

        /*
         * ======================================================
         * 失敗
         * ======================================================
         */

        if (trade == null) {

            player.sendMessage(
                    ChatColor.RED
                            + "出品に失敗しました。"
            );

            return;
        }

        /*
         * ======================================================
         * 成功
         * ======================================================
         */

        player.sendMessage("");

        player.sendMessage(
                ChatColor.GREEN
                        + "✔ 出品しました！"
        );

        player.sendMessage(
                ChatColor.YELLOW
                        + "商品: "
                        + ChatColor.WHITE
                        + tradeItem.getType().name()
        );

        player.sendMessage(
                ChatColor.YELLOW
                        + "個数: "
                        + ChatColor.WHITE
                        + amount
                        + "個"
        );

        player.sendMessage(
                ChatColor.YELLOW
                        + "価格: "
                        + ChatColor.WHITE
                        + formatPrice(price)
                        + "円"
        );

        player.sendMessage("");
    }

    /*
     * ==========================================================
     * ヘルプ
     * ==========================================================
     */

    private void sendHelp(
            Player player
    ) {

        player.sendMessage("");

        player.sendMessage(
                ChatColor.GOLD
                        + "========== 農家マーケット =========="
        );

        player.sendMessage(
                ChatColor.YELLOW
                        + "/market"
                        + ChatColor.WHITE
                        + " - マーケットを開く"
        );

        player.sendMessage(
                ChatColor.GRAY
                        + "全職業・全レベルで閲覧できます。"
        );

        player.sendMessage(
                ChatColor.YELLOW
                        + "/market sell <個数> <価格>"
                        + ChatColor.WHITE
                        + " - 作物を出品"
        );

        player.sendMessage(
                ChatColor.GRAY
                        + "※出品にはファーマーLv.10以上が必要です。"
        );

        player.sendMessage(
                ChatColor.GRAY
                        + "例: /market sell 32 500"
        );

        player.sendMessage(
                ChatColor.YELLOW
                        + "/market help"
                        + ChatColor.WHITE
                        + " - ヘルプを表示"
        );

        player.sendMessage(
                ChatColor.GOLD
                        + "===================================="
        );

        player.sendMessage("");
    }

    /*
     * ==========================================================
     * 金額表示
     * ==========================================================
     */

    private String formatPrice(
            double price
    ) {

        if (price == Math.floor(price)) {

            return String.format(
                    "%,.0f",
                    price
            );
        }

        return String.format(
                "%,.2f",
                price
        );
    }
}