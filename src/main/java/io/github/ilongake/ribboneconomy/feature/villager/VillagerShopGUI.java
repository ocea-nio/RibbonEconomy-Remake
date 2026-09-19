package io.github.ilongake.ribboneconomy.feature.villager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class VillagerShopGUI {

    private static final String TITLE =
            ChatColor.GREEN + "村人ショップ";


    /*
     * =========================
     * 防具装飾鍛冶型
     * 全18種類
     * =========================
     */

    private static final Material[] ARMOR_TRIMS = {

            Material.COAST_ARMOR_TRIM_SMITHING_TEMPLATE,

            Material.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE,

            Material.EYE_ARMOR_TRIM_SMITHING_TEMPLATE,

            Material.HOST_ARMOR_TRIM_SMITHING_TEMPLATE,

            Material.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE,

            Material.RIB_ARMOR_TRIM_SMITHING_TEMPLATE,

            Material.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE,

            Material.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE,

            Material.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE,

            Material.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE,

            Material.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE,

            Material.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE,

            Material.VEX_ARMOR_TRIM_SMITHING_TEMPLATE,

            Material.WARD_ARMOR_TRIM_SMITHING_TEMPLATE,

            Material.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE,

            Material.WILD_ARMOR_TRIM_SMITHING_TEMPLATE,

            Material.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE,

            Material.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE
    };


    /*
     * =========================
     * 1ページ目を開く
     * =========================
     */

    public static void open(
            Player player
    ) {

        openPage(
                player,
                1
        );
    }


    /*
     * =========================
     * ページを開く
     * =========================
     */

    public static void openPage(
            Player player,
            int page
    ) {

        Inventory inventory =
                Bukkit.createInventory(
                        null,
                        27,
                        TITLE
                                + " "
                                + page
                                + "ページ目"
                );


        /*
         * =========================
         * 1ページ目
         * =========================
         */

        if (page == 1) {


            /*
             * ネザライトインゴット
             */

            inventory.setItem(
                    4,
                    createShopItem(
                            Material.NETHERITE_INGOT,
                            ChatColor.DARK_GRAY
                                    + "ネザライトインゴット",
                            1_000_000
                    )
            );


            /*
             * 鍛冶型
             *
             * 0～11
             */

            for (
                    int i = 0;
                    i < 12;
                    i++
            ) {

                inventory.setItem(
                        i + 9,
                        createSmithingTemplate(
                                ARMOR_TRIMS[i]
                        )
                );
            }


            /*
             * 次のページ
             */

            inventory.setItem(
                    26,
                    createButton(
                            Material.ARROW,
                            ChatColor.YELLOW
                                    + "次のページ"
                    )
            );
        }


        /*
         * =========================
         * 2ページ目
         * =========================
         */

        else if (page == 2) {


            /*
             * 残りの鍛冶型
             *
             * 12～17
             */

            for (
                    int i = 12;
                    i < ARMOR_TRIMS.length;
                    i++
            ) {

                int slot =
                        i - 12;

                inventory.setItem(
                        slot + 9,
                        createSmithingTemplate(
                                ARMOR_TRIMS[i]
                        )
                );
            }


            /*
             * 前のページ
             */

            inventory.setItem(
                    18,
                    createButton(
                            Material.ARROW,
                            ChatColor.YELLOW
                                    + "前のページ"
                    )
            );
        }


        /*
         * =========================
         * 閉じる
         * =========================
         */

        inventory.setItem(
                22,
                createButton(
                        Material.BARRIER,
                        ChatColor.RED
                                + "閉じる"
                )
        );


        /*
         * =========================
         * GUIを開く
         * =========================
         */

        player.openInventory(
                inventory
        );
    }


    /*
     * =========================
     * 商品アイテム作成
     * =========================
     */

    private static ItemStack createShopItem(
            Material material,
            String name,
            double price
    ) {

        ItemStack item =
                new ItemStack(
                        material
                );


        ItemMeta meta =
                item.getItemMeta();


        if (meta != null) {

            meta.setDisplayName(
                    name
            );


            meta.setLore(
                    List.of(

                            ChatColor.GOLD
                                    + "価格: "
                                    + String.format(
                                    "%,.0f",
                                    price
                            )
                                    + "円",

                            "",

                            ChatColor.GREEN
                                    + "クリックして購入"
                    )
            );


            item.setItemMeta(
                    meta
            );
        }


        return item;
    }


    /*
     * =========================
     * 鍛冶型作成
     * =========================
     */

    private static ItemStack createSmithingTemplate(
            Material material
    ) {

        return createShopItem(
                material,

                ChatColor.LIGHT_PURPLE
                        + getTrimName(
                        material
                ),

                500_000
        );
    }


    /*
     * =========================
     * 鍛冶型の名前
     * =========================
     */

    private static String getTrimName(
            Material material
    ) {

        return switch (material) {

            case COAST_ARMOR_TRIM_SMITHING_TEMPLATE ->
                    "海洋風の装飾";

            case DUNE_ARMOR_TRIM_SMITHING_TEMPLATE ->
                    "砂丘風の装飾";

            case EYE_ARMOR_TRIM_SMITHING_TEMPLATE ->
                    "要塞風の装飾";

            case HOST_ARMOR_TRIM_SMITHING_TEMPLATE ->
                    "牧人風の装飾";

            case RAISER_ARMOR_TRIM_SMITHING_TEMPLATE ->
                    "牧歌風の装飾";

            case RIB_ARMOR_TRIM_SMITHING_TEMPLATE ->
                    "リブ風の装飾";

            case SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE ->
                    "略奪者風の装飾";

            case SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE ->
                    "造形師風の装飾";

            case SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE ->
                    "静寂風の装飾";

            case SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE ->
                    "ブタの鼻風の装飾";

            case SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE ->
                    "尖塔風の装飾";

            case TIDE_ARMOR_TRIM_SMITHING_TEMPLATE ->
                    "潮流風の装飾";

            case VEX_ARMOR_TRIM_SMITHING_TEMPLATE ->
                    "ヴェックス風の装飾";

            case WARD_ARMOR_TRIM_SMITHING_TEMPLATE ->
                    "監獄風の装飾";

            case WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE ->
                    "先駆者風の装飾";

            case WILD_ARMOR_TRIM_SMITHING_TEMPLATE ->
                    "野生風の装飾";

            case FLOW_ARMOR_TRIM_SMITHING_TEMPLATE ->
                    "渦巻き風の装飾";

            case BOLT_ARMOR_TRIM_SMITHING_TEMPLATE ->
                    "ボルト風の装飾";

            default ->
                    "防具装飾鍛冶型";
        };
    }


    /*
     * =========================
     * ボタン作成
     * =========================
     */

    private static ItemStack createButton(
            Material material,
            String name
    ) {

        ItemStack item =
                new ItemStack(
                        material
                );


        ItemMeta meta =
                item.getItemMeta();


        if (meta != null) {

            meta.setDisplayName(
                    name
            );


            item.setItemMeta(
                    meta
            );
        }


        return item;
    }
}