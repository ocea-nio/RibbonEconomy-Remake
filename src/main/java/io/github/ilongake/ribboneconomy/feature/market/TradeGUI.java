package io.github.ilongake.ribboneconomy.feature.market;

import io.github.ilongake.ribboneconomy.feature.jobs.JobManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class TradeGUI {

    public static final String MAIN_TITLE =
            ChatColor.DARK_GREEN
                    + "マーケット";

    public static final String MARKET_TITLE =
            ChatColor.GREEN
                    + "🛒 ユーザー間売買";

    public static final String MY_TRADES_TITLE =
            ChatColor.YELLOW
                    + "📦 自分の出品";

    public static final String GUARANTEE_TITLE =
            ChatColor.GOLD
                    + "🏪 最低保証売買";

    private final TradeManager tradeManager;

    public TradeGUI(
            TradeManager tradeManager
    ) {

        this.tradeManager = tradeManager;
    }

    /*
     * ==========================================================
     * メインメニュー
     * ==========================================================
     */

    public  void openMainMenu(
            Player player
    ) {

        Inventory inventory =
                Bukkit.createInventory(
                        null,
                        27,
                        MAIN_TITLE
                );

        fillBorder(inventory);

        /*
         * ======================================================
         * ユーザー間売買
         * ======================================================
         *
         * 全職業・全レベルで使用可能。
         */

        inventory.setItem(
                11,
                createItem(
                        Material.EMERALD,

                        ChatColor.GREEN
                                + "🛒 ユーザー間売買",

                        ChatColor.GRAY
                                + "プレイヤーが出品した"
                                + "農作物を購入できます。",

                        "",

                        ChatColor.GREEN
                                + "✔ 全職業で利用可能",

                        "",

                        ChatColor.YELLOW
                                + "クリックして開く"
                )
        );

        /*
         * ======================================================
         * 自分の出品
         * ======================================================
         */


        inventory.setItem(
                13,
                createItem(
                        Material.CHEST,

                        ChatColor.YELLOW
                                + "📦 自分の出品",

                        ChatColor.GRAY
                                + "自分が出品している"
                                + "商品を確認できます。",

                        "",

                        ChatColor.GREEN
                                + "✔ 出品可能",

                        "",

                        ChatColor.YELLOW
                                + "クリックして開く"
                )
        );



        /*
         * ======================================================
         * 農作物を出品
         * ======================================================
         */


        inventory.setItem(
                15,
                createItem(
                        Material.WHEAT,

                        ChatColor.GREEN
                                + "物を出品",

                        ChatColor.GRAY
                                + "手持ちの物を"
                                + "マーケットへ出品します。",

                        "",

                        ChatColor.YELLOW
                                + "クリックして出品"
                )
        );

        /*
         * ======================================================
         * 最低保証売買
         * ======================================================
         */

        if (tradeManager.canUseGuarantee(
                player.getUniqueId()
        )) {

            inventory.setItem(
                    22,
                    createItem(
                            Material.GOLD_INGOT,

                            ChatColor.GOLD
                                    + "🏪 最低保証売買",

                            ChatColor.GRAY
                                    + "農作物をシステムへ"
                                    + "売却できます。",

                            "",

                            ChatColor.GREEN
                                    + "✔ ファーマーLv.10以上",

                            "",

                            ChatColor.YELLOW
                                    + "クリックして開く"
                    )
            );

        } else {

            inventory.setItem(
                    22,
                    createItem(
                            Material.BARRIER,

                            ChatColor.RED
                                    + "🔒 最低保証売買",

                            ChatColor.GRAY
                                    + "農作物をシステムへ"
                                    + "売却できます。",

                            "",

                            ChatColor.RED
                                    + "ファーマーLv.10以上で"
                                    + "利用できます。"
                    )
            );
        }

        player.openInventory(inventory);
    }

    /*
     * ==========================================================
     * ユーザー間マーケット
     * ==========================================================
     */

    public void openMarket(
            Player player
    ) {

        /*
         * 全職業・全レベルOK
         */

        List<TradeManager.Trade> trades =
                tradeManager.getTrades();

        Inventory inventory =
                Bukkit.createInventory(
                        null,
                        54,
                        MARKET_TITLE
                );

        int slot = 0;

        for (TradeManager.Trade trade :
                trades) {

            if (slot >= 45) {
                break;
            }

            ItemStack display =
                    trade.getItem();

            ItemMeta meta =
                    display.getItemMeta();

            if (meta != null) {

                List<String> lore =
                        meta.getLore();

                if (lore == null) {

                    lore =
                            new ArrayList<>();

                } else {

                    lore =
                            new ArrayList<>(
                                    lore
                            );
                }

                lore.add("");

                lore.add(
                        ChatColor.GOLD
                                + "価格: "
                                + ChatColor.WHITE
                                + formatPrice(
                                trade.getPrice()
                        )
                                + "円"
                );

                lore.add(
                        ChatColor.GRAY
                                + "出品者: "
                                + getSellerName(trade)
                );

                lore.add(
                        ChatColor.GRAY
                                + "数量: "
                                + ChatColor.WHITE
                                + trade.getItem()
                                .getAmount()
                                + "個"
                );

                lore.add("");

                lore.add(
                        ChatColor.GREEN
                                + "✔ 購入可能"
                );

                lore.add("");

                lore.add(
                        ChatColor.YELLOW
                                + "クリックして購入"
                );

                meta.setLore(lore);

                display.setItemMeta(meta);
            }

            inventory.setItem(
                    slot,
                    display
            );

            slot++;
        }

        /*
         * 出品がない場合
         */

        if (trades.isEmpty()) {

            inventory.setItem(
                    22,
                    createItem(
                            Material.PAPER,

                            ChatColor.GRAY
                                    + "現在出品されている"
                                    + "商品はありません。"
                    )
            );
        }

        /*
         * 戻る
         */

        inventory.setItem(
                49,
                createItem(
                        Material.BARRIER,

                        ChatColor.RED
                                + "戻る",

                        ChatColor.GRAY
                                + "メインメニューへ戻る"
                )
        );

        player.openInventory(inventory);
    }

    /*
     * ==========================================================
     * 自分の出品
     * ==========================================================
     */

    public void openMyTrades(
            Player player
    ) {

        /*
         * この画面自体も
         * 出品権限がある場合のみ開く想定。
         */

        List<TradeManager.Trade> trades =
                tradeManager.getTrades(
                        player.getUniqueId()
                );

        Inventory inventory =
                Bukkit.createInventory(
                        null,
                        54,
                        MY_TRADES_TITLE
                );

        /*
         * 新規出品
         */

        inventory.setItem(
                48,
                createItem(
                        Material.WHEAT,

                        ChatColor.GREEN
                                + "新しく出品する",

                        ChatColor.GRAY
                                + "手持ちの物を"
                                + "マーケットへ出品します。",

                        "",

                        ChatColor.YELLOW
                                + "クリックして出品"
                )
        );

        /*
         * 出品一覧
         */

        int slot = 0;

        for (TradeManager.Trade trade :
                trades) {

            if (slot >= 45) {
                break;
            }

            ItemStack display =
                    trade.getItem();

            ItemMeta meta =
                    display.getItemMeta();

            if (meta != null) {

                List<String> lore =
                        meta.getLore();

                if (lore == null) {

                    lore =
                            new ArrayList<>();

                } else {

                    lore =
                            new ArrayList<>(
                                    lore
                            );
                }

                lore.add("");

                lore.add(
                        ChatColor.GOLD
                                + "価格: "
                                + ChatColor.WHITE
                                + formatPrice(
                                trade.getPrice()
                        )
                                + "円"
                );

                lore.add(
                        ChatColor.GRAY
                                + "数量: "
                                + ChatColor.WHITE
                                + trade.getItem()
                                .getAmount()
                                + "個"
                );

                lore.add("");

                lore.add(
                        ChatColor.GRAY
                                + "出品ID: "
                                + ChatColor.DARK_GRAY
                                + trade.getId()
                                .toString()
                                .substring(0, 8)
                );

                lore.add("");

                lore.add(
                        ChatColor.RED
                                + "クリックして出品取消"
                );

                meta.setLore(lore);

                display.setItemMeta(meta);
            }

            inventory.setItem(
                    slot,
                    display
            );

            slot++;
        }

        /*
         * 戻る
         */

        inventory.setItem(
                49,
                createItem(
                        Material.BARRIER,

                        ChatColor.RED
                                + "戻る",

                        ChatColor.GRAY
                                + "メインメニューへ戻る"
                )
        );

        player.openInventory(inventory);
    }

    /*
     * ==========================================================
     * 最低保証売買
     * ==========================================================
     */

    public void openGuarantee(
            Player player
    ) {

        /*
         * 最低保証は
         * ファーマーLv.10以上のみ。
         */

        if (!tradeManager.canUseGuarantee(
                player.getUniqueId()
        )) {

            player.sendMessage(
                    ChatColor.RED
                            + "最低保証売買は"
                            + "ファーマーLv.10以上で利用できます。"
            );

            return;
        }

        Inventory inventory =
                Bukkit.createInventory(
                        null,
                        27,
                        GUARANTEE_TITLE
                );

        ItemStack border =
                createItem(
                        Material.YELLOW_STAINED_GLASS_PANE,
                        " "
                );

        for (int i = 0; i < 27; i++) {

            if (i == 13
                    || i == 22) {

                continue;
            }

            inventory.setItem(
                    i,
                    border
            );
        }

        /*
         * メインハンド
         */

        ItemStack held =
                player.getInventory()
                        .getItemInMainHand();

        if (held == null
                || held.getType().isAir()) {

            inventory.setItem(
                    13,
                    createItem(
                            Material.BARRIER,

                            ChatColor.RED
                                    + "売却するアイテムがありません",

                            ChatColor.GRAY
                                    + "メインハンドに"
                                    + "農作物を持ってください。"
                    )
            );

        } else {

            Material material =
                    held.getType();

            double price =
                    tradeManager.getGuaranteePrice(
                            player.getUniqueId(),
                            material
                    );

            if (price > 0D) {

                int amount =
                        held.getAmount();

                double total =
                        price * amount;

                List<String> lore =
                        new ArrayList<>();

                lore.add(
                        ChatColor.GRAY
                                + "現在の個数: "
                                + ChatColor.WHITE
                                + amount
                                + "個"
                );

                lore.add("");

                lore.add(
                        ChatColor.GOLD
                                + "最低保証価格: "
                                + ChatColor.WHITE
                                + formatPrice(price)
                                + "円 / 1個"
                );

                lore.add(
                        ChatColor.GREEN
                                + "売却額: "
                                + ChatColor.WHITE
                                + formatPrice(total)
                                + "円"
                );

                lore.add("");

                if (tradeManager.hasGuaranteePriceBonus(
                        player.getUniqueId()
                )) {

                    lore.add(
                            ChatColor.GOLD
                                    + "★ Lv.40ボーナス適用中"
                    );

                    lore.add(
                            ChatColor.YELLOW
                                    + "最低保証価格 ×1.25"
                    );

                    lore.add("");
                }

                lore.add(
                        ChatColor.YELLOW
                                + "クリックして売却"
                );

                inventory.setItem(
                        13,
                        createItem(
                                material,

                                ChatColor.GREEN
                                        + "🏪 "
                                        + getMaterialName(material),

                                lore.toArray(
                                        new String[0]
                                )
                        )
                );

            } else {

                inventory.setItem(
                        13,
                        createItem(
                                Material.BARRIER,

                                ChatColor.RED
                                        + "売却できません",

                                ChatColor.GRAY
                                        + "このアイテムは"
                                        + "最低保証売買の"
                                        + "対象外です。"
                        )
                );
            }
        }

        /*
         * 説明
         */

        inventory.setItem(
                4,
                createItem(
                        Material.GOLD_INGOT,

                        ChatColor.GOLD
                                + "🏪 最低保証売買",

                        ChatColor.GRAY
                                + "システムが一定価格以上で"
                                + "農作物を買い取ります。",

                        "",

                        ChatColor.YELLOW
                                + "メインハンドのアイテムを"
                                + "クリックして売却"
                )
        );

        /*
         * 戻る
         */

        inventory.setItem(
                22,
                createItem(
                        Material.BARRIER,

                        ChatColor.RED
                                + "戻る",

                        ChatColor.GRAY
                                + "メインメニューへ戻る"
                )
        );

        player.openInventory(inventory);
    }

    /*
     * ==========================================================
     * アイテム作成
     * ==========================================================
     */

    private ItemStack createItem(
            Material material,
            String name,
            String... lore
    ) {

        ItemStack item =
                new ItemStack(material);

        ItemMeta meta =
                item.getItemMeta();

        if (meta != null) {

            meta.setDisplayName(name);

            List<String> loreList =
                    new ArrayList<>();

            for (String line : lore) {

                loreList.add(line);
            }

            meta.setLore(loreList);

            item.setItemMeta(meta);
        }

        return item;
    }

    /*
     * ==========================================================
     * 枠
     * ==========================================================
     */

    private void fillBorder(
            Inventory inventory
    ) {

        ItemStack glass =
                createItem(
                        Material.GREEN_STAINED_GLASS_PANE,
                        " "
                );

        for (int i = 0; i < 9; i++) {

            inventory.setItem(
                    i,
                    glass
            );
        }

        for (int i = 18; i < 27; i++) {

            inventory.setItem(
                    i,
                    glass
            );
        }

        inventory.setItem(
                9,
                glass
        );

        inventory.setItem(
                17,
                glass
        );
    }

    /*
     * ==========================================================
     * 出品者名
     * ==========================================================
     */

    private String getSellerName(
            TradeManager.Trade trade
    ) {

        Player player =
                Bukkit.getPlayer(
                        trade.getSeller()
                );

        if (player != null) {

            return ChatColor.WHITE
                    + player.getName();
        }

        return ChatColor.GRAY
                + "オフライン";
    }

    /*
     * ==========================================================
     * アイテム名
     * ==========================================================
     */

    private String getMaterialName(
            Material material
    ) {

        return switch (material) {

            case WHEAT ->
                    "小麦";

            case WHEAT_SEEDS ->
                    "小麦の種";

            case CARROT ->
                    "ニンジン";

            case POTATO ->
                    "ジャガイモ";

            case BEETROOT ->
                    "ビートルート";

            case BEETROOT_SEEDS ->
                    "ビートルートの種";

            case NETHER_WART ->
                    "ネザーウォート";

            case PUMPKIN ->
                    "カボチャ";

            case PUMPKIN_SEEDS ->
                    "カボチャの種";

            case MELON ->
                    "スイカ";

            case MELON_SLICE ->
                    "スイカの薄切り";

            case MELON_SEEDS ->
                    "スイカの種";

            case SUGAR_CANE ->
                    "サトウキビ";

            case COCOA_BEANS ->
                    "カカオ豆";

            case CACTUS ->
                    "サボテン";

            case SWEET_BERRIES ->
                    "スイートベリー";

            case GLOW_BERRIES ->
                    "グロウベリー";

            case RED_MUSHROOM ->
                    "赤色のキノコ";

            case BROWN_MUSHROOM ->
                    "茶色のキノコ";

            case KELP ->
                    "昆布";

            case DRIED_KELP ->
                    "乾燥した昆布";

            case BAMBOO ->
                    "竹";

            case TORCHFLOWER ->
                    "トーチフラワー";

            case TORCHFLOWER_SEEDS ->
                    "トーチフラワーの種";

            case PITCHER_POD ->
                    "ウツボカズラのさや";

            default ->
                    material.name();
        };
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