package io.github.ilongake.ribboneconomy.gui;

import io.github.ilongake.ribboneconomy.core.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class MoneyListener
        implements Listener {

    private final DataManager dataManager;

    /*
     * 送金先の入力待ちプレイヤー
     */
    private final java.util.Map<Player, Player> transferTargets =
            new java.util.HashMap<>();

    public MoneyListener(
            DataManager dataManager
    ) {

        this.dataManager =
                dataManager;
    }


    /**
     * お金メニューのクリック処理
     */
    @EventHandler
    public void onMoneyMenuClick(
            InventoryClickEvent event
    ) {

        if (!(event.getWhoClicked()
                instanceof Player player)) {

            return;
        }


        /*
         * =========================
         * お金メニュー
         * =========================
         */

        if (!event.getView()
                .getTitle()
                .equals(
                        ChatColor.GOLD
                                + "お金メニュー"
                )) {

            return;
        }


        /*
         * GUI内のアイテムを操作できないようにする
         */

        event.setCancelled(true);


        /*
         * クリックされたアイテムがない
         */

        if (event.getCurrentItem() == null) {

            return;
        }


        Material material =
                event.getCurrentItem()
                        .getType();


        /*
         * =========================
         * 送金
         * =========================
         */

        if (material
                == Material.EMERALD) {

            player.closeInventory();

            player.sendMessage(
                    ChatColor.GREEN
                            + "送金するプレイヤーの名前を入力してください。"
            );

            player.sendMessage(
                    ChatColor.GRAY
                            + "キャンセルする場合は「cancel」と入力してください。"
            );

            /*
             * 相手の入力待ち
             */
            transferTargets.put(
                    player,
                    null
            );

            return;
        }


        /*
         * =========================
         * 戻る
         * =========================
         */

        if (material
                == Material.BARRIER) {

            player.closeInventory();

            RPGMenuGUI.open(
                    player
            );

            return;
        }
    }


    /**
     * チャット入力処理
     */
    @EventHandler
    public void onPlayerChat(
            AsyncPlayerChatEvent event
    ) {

        Player player =
                event.getPlayer();

        /*
         * 送金処理中でない
         */
        if (!transferTargets.containsKey(player)) {

            return;
        }

        /*
         * チャットを通常の会話として送信しない
         */
        event.setCancelled(true);

        String message =
                event.getMessage()
                        .trim();


        /*
         * =========================
         * キャンセル
         * =========================
         */

        if (message.equalsIgnoreCase("cancel")) {

            transferTargets.remove(player);

            player.sendMessage(
                    ChatColor.YELLOW
                            + "送金をキャンセルしました。"
            );

            return;
        }


        /*
         * =========================
         * プレイヤー名を取得
         * =========================
         */

        Player target =
                Bukkit.getPlayerExact(
                        message
                );

        if (target == null) {

            player.sendMessage(
                    ChatColor.RED
                            + "そのプレイヤーはオンラインではありません。"
            );

            player.sendMessage(
                    ChatColor.YELLOW
                            + "もう一度プレイヤー名を入力してください。"
            );

            return;
        }


        /*
         * 自分自身への送金
         */

        if (target.equals(player)) {

            player.sendMessage(
                    ChatColor.RED
                            + "自分自身には送金できません。"
            );

            return;
        }


        /*
         * 相手を保存
         */

        transferTargets.put(
                player,
                target
        );

        player.sendMessage(
                ChatColor.GREEN
                        + target.getName()
                        + " に送金します。"
        );

        player.sendMessage(
                ChatColor.YELLOW
                        + "送金額を数字で入力してください。"
        );

        player.sendMessage(
                ChatColor.GRAY
                        + "キャンセルする場合は「cancel」と入力してください。"
        );
    }


    /**
     * 金額入力処理
     */
    @EventHandler
    public void onAmountChat(
            AsyncPlayerChatEvent event
    ) {

        Player player =
                event.getPlayer();

        /*
         * 送金処理中でない
         */
        if (!transferTargets.containsKey(player)) {

            return;
        }

        /*
         * まだ相手を選択している
         */
        if (transferTargets.get(player) == null) {

            return;
        }

        /*
         * チャットを通常の会話として送信しない
         */
        event.setCancelled(true);

        String message =
                event.getMessage()
                        .trim();


        /*
         * =========================
         * キャンセル
         * =========================
         */

        if (message.equalsIgnoreCase("cancel")) {

            transferTargets.remove(player);

            player.sendMessage(
                    ChatColor.YELLOW
                            + "送金をキャンセルしました。"
            );

            return;
        }


        /*
         * =========================
         * 金額を数字に変換
         * =========================
         */

        double amount;

        try {

            amount =
                    Double.parseDouble(
                            message
                    );

        } catch (NumberFormatException e) {

            player.sendMessage(
                    ChatColor.RED
                            + "金額は数字で入力してください。"
            );

            return;
        }


        /*
         * 0円以下は禁止
         */

        if (amount <= 0) {

            player.sendMessage(
                    ChatColor.RED
                            + "1円以上を指定してください。"
            );

            return;
        }


        /*
         * 小数を禁止
         */

        if (amount != Math.floor(amount)) {

            player.sendMessage(
                    ChatColor.RED
                            + "送金額は整数で入力してください。"
            );

            return;
        }


        /*
         * 送金先を取得
         */

        Player target =
                transferTargets.get(player);


        /*
         * 念のためnullチェック
         */

        if (target == null) {

            transferTargets.remove(player);

            player.sendMessage(
                    ChatColor.RED
                            + "送金先が見つかりませんでした。"
            );

            return;
        }


        /*
         * =========================
         * 送金実行
         * =========================
         */

        boolean success =
                dataManager.withdraw(
                        player.getUniqueId(),
                        amount
                );


        /*
         * 残高不足
         */

        if (!success) {

            player.sendMessage(
                    ChatColor.RED
                            + "残高が不足しています。"
            );

            return;
        }


        /*
         * 相手にお金を追加
         */

        dataManager.addBalance(
                target.getUniqueId(),
                amount
        );


        /*
         * 送金処理終了
         */

        transferTargets.remove(player);


        /*
         * =========================
         * 送金者へのメッセージ
         * =========================
         */

        player.sendMessage(
                ChatColor.GREEN
                        + target.getName()
                        + " に "
                        + String.format(
                        "%.0f",
                        amount
                )
                        + "円送金しました！"
        );


        /*
         * =========================
         * 受取人へのメッセージ
         * =========================
         */

        target.sendMessage(
                ChatColor.GREEN
                        + player.getName()
                        + " から "
                        + String.format(
                        "%.0f",
                        amount
                )
                        + "円受け取りました！"
        );
    }
}