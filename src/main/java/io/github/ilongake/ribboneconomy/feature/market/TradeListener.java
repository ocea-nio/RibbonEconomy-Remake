package io.github.ilongake.ribboneconomy.feature.market;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TradeListener implements Listener {

    /*
     * ==========================================================
     * フィールド
     * ==========================================================
     */

    private final JavaPlugin plugin;
    private final TradeGUI tradeGUI;
    private final TradeManager tradeManager;


    /**
     * 出品作成中のプレイヤー
     */
    private final Map<UUID, SellSession> sessions =
            new HashMap<>();

    /*
     * ==========================================================
     * コンストラクタ
     * ==========================================================
     */

    public TradeListener(
            JavaPlugin plugin,
            TradeGUI tradeGUI,
            TradeManager tradeManager
    ) {

        this.plugin = plugin;
        this.tradeGUI = tradeGUI;
        this.tradeManager = tradeManager;
    }

    /*
     * ==========================================================
     * GUIクリック
     * ==========================================================
     */

    @EventHandler
    public void onInventoryClick(
            InventoryClickEvent event
    ) {

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        String title =
                event.getView().getTitle();

        int slot =
                event.getRawSlot();

        /*
         * ======================================================
         * メインメニュー
         * ======================================================
         */

        if (title.equals(TradeGUI.MAIN_TITLE)) {

            event.setCancelled(true);

            /*
             * GUI上のスロット以外
             */
            if (slot < 0) {
                return;
            }

            /*
             * ユーザー間マーケット
             */
            if (slot == 11) {

                tradeGUI.openMarket(player);

                return;
            }

            /*
             * 自分の出品
             */
            if (slot == 13) {

                tradeGUI.openMyTrades(player);

                return;
            }

            /*
             * 農作物を出品
             */
            if (slot == 15) {

                startSell(player);

                return;
            }

            /*
             * 最低保証売買
             */
            if (slot == 22) {

                openGuarantee(player);

                return;
            }

            return;
        }

        /*
         * ======================================================
         * ユーザー間マーケット
         * ======================================================
         */

        if (title.equals(TradeGUI.MARKET_TITLE)) {

            event.setCancelled(true);

            /*
             * GUI外クリック
             */
            if (slot < 0) {
                return;
            }

            /*
             * 戻る
             */
            if (slot == 49) {

                tradeGUI.openMainMenu(player);

                return;
            }

            /*
             * 商品欄以外
             *
             * 0～44だけが商品欄
             */
            if (slot < 0 || slot >= 45) {
                return;
            }


            // TradeManagerのTradeをArrayList形式で取得 -> Collection<trade>
            List<TradeManager.Trade> trades =
                    tradeManager.getTrades();

            /*
             * 存在しない商品
             */
            if (slot >= trades.size()) {
                return;
            }

            TradeManager.Trade trade =
                    trades.get(slot);

            purchase(
                    player,
                    trade
            );

            return;
        }

        /*
         * ======================================================
         * 自分の出品
         * ======================================================
         */

        if (title.equals(TradeGUI.MY_TRADES_TITLE)) {

            event.setCancelled(true);

            /*
             * GUI外クリック
             */
            if (slot < 0) {
                return;
            }

            /*
             * 新しく出品
             */
            if (slot == 48) {

                startSell(player);

                return;
            }

            /*
             * 戻る
             */
            if (slot == 49) {

                tradeGUI.openMainMenu(player);

                return;
            }

            /*
             * 商品欄以外
             */
            if (slot < 0 || slot >= 45) {
                return;
            }

            List<TradeManager.Trade> trades =
                    tradeManager.getTrades(
                            player.getUniqueId()
                    );

            /*
             * 存在しない商品
             */
            if (slot >= trades.size()) {
                return;
            }

            TradeManager.Trade trade =
                    trades.get(slot);

            cancelTrade(
                    player,
                    trade
            );

            return;
        }

        /*
         * ======================================================
         * 最低保証売買
         * ======================================================
         */

        if (title.equals(TradeGUI.GUARANTEE_TITLE)) {

            event.setCancelled(true);

            /*
             * GUI外クリック
             */
            if (slot < 0) {
                return;
            }

            /*
             * ==================================================
             * 売却ボタン
             *
             * 13番だけを売却ボタンとして扱う
             * ==================================================
             */

            if (slot == 13) {

                sellHeldItem(player);

                return;
            }

            /*
             * ==================================================
             * 戻る
             * ==================================================
             */

            if (slot == 22) {

                tradeGUI.openMainMenu(player);

                return;
            }

            /*
             * その他のスロット
             *
             * 黄色ガラス等をクリックしても
             * 売却されない。
             */

            return;
        }
    }

    /*
     * ==========================================================
     * GUIドラッグ防止
     * ==========================================================
     */

    @EventHandler
    public void onInventoryDrag(
            InventoryDragEvent event
    ) {

        String title =
                event.getView().getTitle();

        if (title.equals(TradeGUI.MAIN_TITLE)
                || title.equals(TradeGUI.MARKET_TITLE)
                || title.equals(TradeGUI.MY_TRADES_TITLE)
                || title.equals(TradeGUI.GUARANTEE_TITLE)) {

            event.setCancelled(true);
        }
    }

    /*
     * ==========================================================
     * 最低保証GUIを開く
     * ==========================================================
     */

    private void openGuarantee(
            Player player
    ) {
        tradeGUI.openGuarantee(player);
    }

    /*
     * ==========================================================
     * 最低保証売却
     * ==========================================================
     */

    private void sellHeldItem(
            Player player
    ) {

        UUID uuid =
                player.getUniqueId();


        /*
         * ======================================================
         * メインハンド確認
         * ======================================================
         */

        ItemStack held =
                player.getInventory()
                        .getItemInMainHand();

        if (held == null
                || held.getType().isAir()) {

            player.sendMessage(
                    ChatColor.RED
                            + "メインハンドに"
                            + "売却する農作物を持ってください。"
            );

            return;
        }

        /*
         * ======================================================
         * 売却前の情報を保存
         *
         * Managerがアイテムを削除した後でも、
         * 表示用の情報を失わないようにする。
         * ======================================================
         */

        ItemStack sellingItem =
                held.clone();

        int amount =
                sellingItem.getAmount();

        /*
         * ======================================================
         * 最低保証価格確認
         * ======================================================
         */

        double unitPrice =
                tradeManager.getGuaranteePrice(
                        uuid,
                        sellingItem.getType()
                );

        /*
         * 対象外
         */
        if (unitPrice <= 0D) {

            player.sendMessage(
                    ChatColor.RED
                            + "このアイテムは"
                            + "最低保証売買の対象外です。"
            );

            return;
        }

        /*
         * ======================================================
         * 売却
         * ======================================================
         */

        double totalPrice =
                tradeManager.sellHeldItemToGuarantee(
                        uuid
                );

        /*
         * ======================================================
         * 売却失敗
         * ======================================================
         */

        if (totalPrice <= 0D) {

            player.sendMessage(
                    ChatColor.RED
                            + "売却に失敗しました。"
            );

            return;
        }

        /*
         * ======================================================
         * 売却成功
         * ======================================================
         */

        player.sendMessage("");

        player.sendMessage(
                ChatColor.GREEN
                        + "========== 最低保証売買 =========="
        );

        player.sendMessage(
                ChatColor.YELLOW
                        + "商品: "
                        + ChatColor.WHITE
                        + sellingItem.getType().name()
        );

        player.sendMessage(
                ChatColor.YELLOW
                        + "個数: "
                        + ChatColor.WHITE
                        + amount
        );

        player.sendMessage(
                ChatColor.YELLOW
                        + "1個あたり: "
                        + ChatColor.WHITE
                        + formatPrice(unitPrice)
                        + "円"
        );

        player.sendMessage(
                ChatColor.GREEN
                        + "獲得金額: "
                        + ChatColor.WHITE
                        + formatPrice(totalPrice)
                        + "円"
        );

        /*
         * ======================================================
         * Lv.40ボーナス表示
         * ======================================================
         */

        if (tradeManager.hasGuaranteePriceBonus(uuid)) {

            player.sendMessage(
                    ChatColor.GOLD
                            + "ファーマーLv.40ボーナス "
                            + ChatColor.WHITE
                            + "×1.25"
                            + ChatColor.GOLD
                            + " が適用されています。"
            );
        }

        player.sendMessage("");

        /*
         * ======================================================
         * GUI更新
         * ======================================================
         */

        tradeGUI.openGuarantee(player);
    }

    /*
     * ==========================================================
     * 出品開始
     * ==========================================================
     */

    private void startSell(
            Player player
    ) {

        UUID uuid =
                player.getUniqueId();

        /*
         * 既に出品処理中
         */
        if (sessions.containsKey(uuid)) {

            player.sendMessage(
                    ChatColor.RED
                            + "すでに出品処理中です。"
            );

            return;
        }

        /*
         * ======================================================
         * メインハンド確認
         * ======================================================
         */

        ItemStack hand =
                player.getInventory()
                        .getItemInMainHand();

        if (hand == null
                || hand.getType().isAir()) {

            player.sendMessage(
                    ChatColor.RED
                            + "メインハンドに"
                            + "出品する農作物を持ってください。"
            );

            return;
        }

        /*
         * ======================================================
         * GUIを閉じる
         * ======================================================
         */

        player.closeInventory();

        /*
         * ======================================================
         * セッション作成
         * ======================================================
         */

        SellSession session =
                new SellSession(
                        hand.clone()
                );

        sessions.put(
                uuid,
                session
        );

        player.sendMessage("");

        player.sendMessage(
                ChatColor.GREEN
                        + "========== 農作物出品 =========="
        );

        player.sendMessage(
                ChatColor.YELLOW
                        + "商品: "
                        + ChatColor.WHITE
                        + hand.getType().name()
        );

        player.sendMessage(
                ChatColor.YELLOW
                        + "現在の個数: "
                        + ChatColor.WHITE
                        + hand.getAmount()
        );

        player.sendMessage("");

        player.sendMessage(
                ChatColor.AQUA
                        + "出品する個数を入力してください。"
        );

        player.sendMessage(
                ChatColor.GRAY
                        + "1～64 / cancel でキャンセル"
        );

        player.sendMessage("");
    }

    /*
     * ==========================================================
     * チャット入力
     * ==========================================================
     */

    @EventHandler
    public void onChat(
            AsyncPlayerChatEvent event
    ) {

        Player player =
                event.getPlayer();

        UUID uuid =
                player.getUniqueId();

        SellSession session =
                sessions.get(uuid);

        /*
         * 出品処理中ではない
         */
        if (session == null) {
            return;
        }

        /*
         * 通常チャットへ表示しない
         */
        event.setCancelled(true);

        String message =
                event.getMessage().trim();

        /*
         * ======================================================
         * キャンセル
         * ======================================================
         */

        if (message.equalsIgnoreCase("cancel")
                || message.equalsIgnoreCase("キャンセル")) {

            sessions.remove(uuid);

            Bukkit.getScheduler().runTask(
                    plugin,
                    () -> player.sendMessage(
                            ChatColor.RED
                                    + "出品をキャンセルしました。"
                    )
            );

            return;
        }

        /*
         * ======================================================
         * 個数入力
         * ======================================================
         */

        if (session.getAmount() == -1) {

            int amount;

            try {

                amount =
                        Integer.parseInt(message);

            } catch (NumberFormatException e) {

                Bukkit.getScheduler().runTask(
                        plugin,
                        () -> player.sendMessage(
                                ChatColor.RED
                                        + "数字を入力してください。"
                        )
                );

                return;
            }

            /*
             * 1～64
             */
            if (amount <= 0
                    || amount > 64) {

                Bukkit.getScheduler().runTask(
                        plugin,
                        () -> player.sendMessage(
                                ChatColor.RED
                                        + "個数は1～64で"
                                        + "入力してください。"
                        )
                );

                return;
            }

            /*
             * ==================================================
             * Bukkit APIは同期処理
             * ==================================================
             */

            Bukkit.getScheduler().runTask(
                    plugin,
                    () -> {

                        /*
                         * セッションが消えていないか確認
                         */
                        if (!sessions.containsKey(uuid)) {
                            return;
                        }

                        ItemStack current =
                                player.getInventory()
                                        .getItemInMainHand();

                        /*
                         * ==================================================
                         * アイテム確認
                         * ==================================================
                         */

                        if (current == null
                                || current.getType().isAir()
                                || !current.isSimilar(
                                session.getItem()
                        )
                                || current.getAmount() < amount) {

                            sessions.remove(uuid);

                            player.sendMessage(
                                    ChatColor.RED
                                            + "必要なアイテムが"
                                            + "メインハンドにありません。"
                            );

                            return;
                        }

                        /*
                         * 個数保存
                         */
                        session.setAmount(
                                amount
                        );

                        player.sendMessage(
                                ChatColor.GREEN
                                        + "個数を "
                                        + ChatColor.WHITE
                                        + amount
                                        + ChatColor.GREEN
                                        + "個に設定しました。"
                        );

                        player.sendMessage(
                                ChatColor.AQUA
                                        + "次に価格を入力してください。"
                        );

                        player.sendMessage(
                                ChatColor.GRAY
                                        + "例: 100 / cancel でキャンセル"
                        );
                    }
            );

            return;
        }

        /*
         * ======================================================
         * 価格入力
         * ======================================================
         */

        if (session.getPrice() < 0) {

            double price;

            try {

                price =
                        Double.parseDouble(message);

            } catch (NumberFormatException e) {

                Bukkit.getScheduler().runTask(
                        plugin,
                        () -> player.sendMessage(
                                ChatColor.RED
                                        + "正しい価格を"
                                        + "入力してください。"
                        )
                );

                return;
            }

            /*
             * ==================================================
             * 価格チェック
             * ==================================================
             */

            if (price < 0.01D
                    || Double.isNaN(price)
                    || Double.isInfinite(price)
                    || price > 1_000_000_000D) {

                Bukkit.getScheduler().runTask(
                        plugin,
                        () -> player.sendMessage(
                                ChatColor.RED
                                        + "価格は0.01円以上"
                                        + "1,000,000,000円以下で"
                                        + "入力してください。"
                        )
                );

                return;
            }

            /*
             * 価格保存
             */
            session.setPrice(
                    price
            );

            /*
             * ==================================================
             * Bukkit APIを触るため同期処理
             * ==================================================
             */

            Bukkit.getScheduler().runTask(
                    plugin,
                    () -> finishSell(
                            player,
                            session
                    )
            );
        }
    }

    /*
     * ==========================================================
     * 出品完了
     * ==========================================================
     */

    private void finishSell(
            Player player,
            SellSession session
    ) {

        UUID uuid =
                player.getUniqueId();

        /*
         * セッション存在確認
         */
        if (!sessions.containsKey(uuid)) {
            return;
        }

        /*
         * ======================================================
         * メインハンド再確認
         * ======================================================
         */

        ItemStack current =
                player.getInventory()
                        .getItemInMainHand();

        if (current == null
                || current.getType().isAir()
                || !current.isSimilar(
                session.getItem()
        )
                || current.getAmount()
                < session.getAmount()) {

            sessions.remove(uuid);

            player.sendMessage(
                    ChatColor.RED
                            + "出品に必要なアイテムが"
                            + "足りません。"
            );

            return;
        }

        /*
         * ======================================================
         * 出品アイテム作成
         * ======================================================
         */

        ItemStack tradeItem =
                current.clone();

        tradeItem.setAmount(
                session.getAmount()
        );

        /*
         * ======================================================
         * Managerで出品作成
         * ======================================================
         */

        TradeManager.Trade trade =
                tradeManager.createTrade(
                        uuid,
                        tradeItem,
                        session.getPrice()
                );

        /*
         * セッション終了
         */
        sessions.remove(uuid);

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
                        + "========== 出品完了 =========="
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
                        + tradeItem.getAmount()
        );

        player.sendMessage(
                ChatColor.YELLOW
                        + "価格: "
                        + ChatColor.WHITE
                        + formatPrice(
                        trade.getPrice()
                )
                        + "円"
        );

        player.sendMessage(
                ChatColor.GREEN
                        + "マーケットへ出品しました！"
        );

        player.sendMessage("");

        tradeGUI.openMyTrades(player);
    }

    /*
     * ==========================================================
     * 購入
     * ==========================================================
     */

    private void purchase(
            Player player,
            TradeManager.Trade trade
    ) {

        UUID buyer =
                player.getUniqueId();

        /*
         * 自分の商品
         */
        if (trade.getSeller()
                .equals(buyer)) {

            player.sendMessage(
                    ChatColor.RED
                            + "自分の商品は"
                            + "購入できません。"
            );

            return;
        }

        /*
         * 残高確認
         */
        if (tradeManager.getBalance(buyer)
                < trade.getPrice()) {

            player.sendMessage(
                    ChatColor.RED
                            + "所持金が足りません。"
            );

            return;
        }

        /*
         * 購入
         */
        boolean success =
                tradeManager.purchase(
                        buyer,
                        trade.getId()
                );

        /*
         * 失敗
         */
        if (!success) {

            player.sendMessage(
                    ChatColor.RED
                            + "購入に失敗しました。"
            );

            return;
        }

        /*
         * 成功
         */
        player.sendMessage(
                ChatColor.GREEN
                        + "購入しました！"
        );

        player.sendMessage(
                ChatColor.YELLOW
                        + "商品: "
                        + ChatColor.WHITE
                        + trade.getItem()
                        .getType()
                        .name()
        );

        player.sendMessage(
                ChatColor.YELLOW
                        + "個数: "
                        + ChatColor.WHITE
                        + trade.getItem()
                        .getAmount()
        );

        player.sendMessage(
                ChatColor.YELLOW
                        + "価格: "
                        + ChatColor.WHITE
                        + formatPrice(
                        trade.getPrice()
                )
                        + "円"
        );

        tradeGUI.openMarket(player);
    }

    /*
     * ==========================================================
     * 出品取消
     * ==========================================================
     */

    private void cancelTrade(
            Player player,
            TradeManager.Trade trade
    ) {

        boolean success =
                tradeManager.cancelTrade(
                        player.getUniqueId(),
                        trade.getId()
                );

        if (!success) {

            player.sendMessage(
                    ChatColor.RED
                            + "出品の取り消しに"
                            + "失敗しました。"
            );

            return;
        }

        player.sendMessage(
                ChatColor.GREEN
                        + "出品を取り消しました。"
        );

        player.sendMessage(
                ChatColor.YELLOW
                        + "商品を返却しました。"
        );

        tradeGUI.openMyTrades(player);
    }





    /*
     * ==========================================================
     * プレイヤー退出
     * ==========================================================
     */

    @EventHandler
    public void onPlayerQuit(
            PlayerQuitEvent event
    ) {

        UUID uuid =
                event.getPlayer()
                        .getUniqueId();

        /*
         * チャット入力中の出品セッションを削除
         */
        sessions.remove(uuid);
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

    /*
     * ==========================================================
     * 出品セッション
     * ==========================================================
     */

    private static class SellSession {

        private final ItemStack item;

        private int amount = -1;

        private double price = -1;

        private SellSession(
                ItemStack item
        ) {

            this.item =
                    item.clone();
        }

        public ItemStack getItem() {

            return item.clone();
        }

        public int getAmount() {

            return amount;
        }

        public void setAmount(
                int amount
        ) {

            this.amount = amount;
        }

        public double getPrice() {

            return price;
        }

        public void setPrice(
                double price
        ) {

            this.price = price;
        }
    }
}