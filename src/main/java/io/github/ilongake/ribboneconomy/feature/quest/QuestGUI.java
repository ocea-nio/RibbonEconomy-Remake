package io.github.ilongake.ribboneconomy.feature.quest;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class QuestGUI {

    /**
     * 依頼メニューを開く
     */
    public static void open(Player player) {

        Inventory inventory =
                Bukkit.createInventory(
                        null,
                        27,
                        ChatColor.DARK_GREEN
                                + "依頼メニュー"
                );


        /*
         * =========================
         * 公開中の依頼
         * =========================
         */

        inventory.setItem(
                10,
                createItem(
                        Material.BOOK,
                        ChatColor.YELLOW
                                + "公開中の依頼"
                )
        );


        /*
         * =========================
         * 依頼を作成
         * =========================
         */

        inventory.setItem(
                12,
                createItem(
                        Material.WRITABLE_BOOK,
                        ChatColor.GREEN
                                + "依頼を作成"
                )
        );


        /*
         * =========================
         * 受注中の依頼
         * =========================
         */

        inventory.setItem(
                14,
                createItem(
                        Material.CHEST,
                        ChatColor.AQUA
                                + "受注中の依頼"
                )
        );


        /*
         * =========================
         * 承認待ちの依頼
         * =========================
         */

        inventory.setItem(
                16,
                createItem(
                        Material.EMERALD,
                        ChatColor.GOLD
                                + "承認待ちの依頼"
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


    /**
     * GUIアイテム作成
     */
    private static ItemStack createItem(
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