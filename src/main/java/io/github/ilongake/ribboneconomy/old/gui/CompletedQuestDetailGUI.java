package io.github.ilongake.ribboneconomy.gui;

import io.github.ilongake.ribboneconomy.quest.Quest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class CompletedQuestDetailGUI {

    /**
     * 承認待ち依頼の詳細GUIを開く
     */
    public static void open(
            Player player,
            Quest quest
    ) {

        Inventory inventory =
                Bukkit.createInventory(
                        null,
                        27,
                        ChatColor.DARK_GREEN
                                + "承認待ちの依頼詳細"
                );

        /*
         * =========================
         * 依頼ID保存用キー
         * =========================
         */

        NamespacedKey questKey =
                new NamespacedKey(
                        player.getServer()
                                .getPluginManager()
                                .getPlugin("RPGEconomy"),
                        "quest_id"
                );


        /*
         * =========================
         * 依頼情報
         * =========================
         */

        ItemStack info =
                new ItemStack(
                        Material.PAPER
                );

        ItemMeta infoMeta =
                info.getItemMeta();

        if (infoMeta != null) {

            infoMeta.setDisplayName(
                    ChatColor.YELLOW
                            + quest.getTitle()
            );

            List<String> lore =
                    new ArrayList<>();

            lore.add(
                    ChatColor.WHITE
                            + "依頼内容:"
            );

            lore.add(
                    ChatColor.GRAY
                            + quest.getDescription()
            );

            lore.add("");

            lore.add(
                    ChatColor.GREEN
                            + "報酬: "
                            + quest.getReward()
                            + "円"
            );

            lore.add("");

            lore.add(
                    ChatColor.AQUA
                            + "受注者: "
                            + quest.getWorkerUuid()
            );

            lore.add("");

            /*
             * IDは表示だけ
             * IDの取得には使用しない
             */

            lore.add(
                    ChatColor.GRAY
                            + "依頼ID:"
            );

            lore.add(
                    quest.getQuestId()
                            .toString()
            );

            infoMeta.setLore(
                    lore
            );


            /*
             * =========================
             * 依頼IDをPDCに保存
             * =========================
             */

            infoMeta
                    .getPersistentDataContainer()
                    .set(
                            questKey,
                            PersistentDataType.STRING,
                            quest.getQuestId()
                                    .toString()
                    );

            info.setItemMeta(
                    infoMeta
            );
        }


        /*
         * =========================
         * 情報表示
         * =========================
         */

        inventory.setItem(
                13,
                info
        );


        /*
         * =========================
         * 承認ボタン
         * =========================
         */

        ItemStack approve =
                new ItemStack(
                        Material.EMERALD_BLOCK
                );

        ItemMeta approveMeta =
                approve.getItemMeta();

        if (approveMeta != null) {

            approveMeta.setDisplayName(
                    ChatColor.GREEN
                            + "依頼を承認する"
            );

            approveMeta.setLore(
                    List.of(
                            ChatColor.GRAY
                                    + "クリックすると依頼を承認します",
                            ChatColor.GRAY
                                    + "受注者に報酬が支払われます"
                    )
            );


            /*
             * =========================
             * 承認ボタンにも
             * 依頼IDをPDC保存
             * =========================
             *
             * RPGMenuListener側では
             * 承認ボタンをクリックしたときに
             * このPDCから依頼IDを取得できる
             */

            approveMeta
                    .getPersistentDataContainer()
                    .set(
                            questKey,
                            PersistentDataType.STRING,
                            quest.getQuestId()
                                    .toString()
                    );

            approve.setItemMeta(
                    approveMeta
            );
        }

        inventory.setItem(
                11,
                approve
        );


        /*
         * =========================
         * 戻るボタン
         * =========================
         */

        ItemStack back =
                new ItemStack(
                        Material.BARRIER
                );

        ItemMeta backMeta =
                back.getItemMeta();

        if (backMeta != null) {

            backMeta.setDisplayName(
                    ChatColor.RED
                            + "戻る"
            );

            back.setItemMeta(
                    backMeta
            );
        }

        inventory.setItem(
                15,
                back
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