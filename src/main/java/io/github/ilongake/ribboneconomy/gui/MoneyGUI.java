package io.github.ilongake.ribboneconomy.gui;

import io.github.ilongake.ribboneconomy.core.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class MoneyGUI {

    /**
     * お金メニューを開く
     */
    public static void open(
            Player player,
            DataManager dataManager
    ) {

        Inventory inventory =
                Bukkit.createInventory(
                        null,
                        27,
                        ChatColor.GOLD
                                + "お金メニュー"
                );


        /*
         * =========================
         * 所持金
         * =========================
         */

        double balance =
                dataManager.getBalance(
                        player.getUniqueId()
                );

        ItemStack balanceItem =
                new ItemStack(
                        Material.GOLD_INGOT
                );

        ItemMeta balanceMeta =
                balanceItem.getItemMeta();

        if (balanceMeta != null) {

            balanceMeta.setDisplayName(
                    ChatColor.GOLD
                            + "現在の所持金"
            );

            balanceMeta.setLore(
                    List.of(
                            ChatColor.YELLOW
                                    + String.format(
                                    "%.0f",
                                    balance
                            )
                                    + "円"
                    )
            );

            balanceItem.setItemMeta(
                    balanceMeta
            );
        }

        inventory.setItem(
                11,
                balanceItem
        );


        /*
         * =========================
         * 送金
         * =========================
         */

        ItemStack sendItem =
                new ItemStack(
                        Material.EMERALD
                );

        ItemMeta sendMeta =
                sendItem.getItemMeta();

        if (sendMeta != null) {

            sendMeta.setDisplayName(
                    ChatColor.GREEN
                            + "プレイヤーに送金"
            );

            sendMeta.setLore(
                    List.of(
                            ChatColor.GRAY
                                    + "他のプレイヤーにお金を送ります",
                            "",
                            ChatColor.YELLOW
                                    + "クリックして送金"
                    )
            );

            sendItem.setItemMeta(
                    sendMeta
            );
        }

        inventory.setItem(
                15,
                sendItem
        );


        /*
         * =========================
         * 戻る
         * =========================
         */

        ItemStack backItem =
                new ItemStack(
                        Material.BARRIER
                );

        ItemMeta backMeta =
                backItem.getItemMeta();

        if (backMeta != null) {

            backMeta.setDisplayName(
                    ChatColor.RED
                            + "戻る"
            );

            backMeta.setLore(
                    List.of(
                            ChatColor.GRAY
                                    + "RPGメニューに戻る"
                    )
            );

            backItem.setItemMeta(
                    backMeta
            );
        }

        inventory.setItem(
                22,
                backItem
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
}