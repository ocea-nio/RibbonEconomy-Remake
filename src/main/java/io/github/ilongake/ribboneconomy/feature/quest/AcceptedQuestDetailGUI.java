package io.github.ilongake.ribboneconomy.feature.quest;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;

public class AcceptedQuestDetailGUI {

    /**
     * 受注中の依頼の詳細GUIを開く
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
                                + "受注中の依頼詳細"
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

            infoMeta.setLore(
                    Arrays.asList(
                            ChatColor.WHITE
                                    + "依頼内容:",

                            ChatColor.GRAY
                                    + quest.getDescription(),

                            "",

                            ChatColor.GREEN
                                    + "報酬: "
                                    + quest.getReward()
                                    + "円",

                            "",

                            ChatColor.GRAY
                                    + "依頼ID:",

                            ChatColor.GRAY
                                    + quest.getQuestId().toString()
                    )
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
                            quest.getQuestId().toString()
                    );

            info.setItemMeta(
                    infoMeta
            );
        }

        inventory.setItem(
                13,
                info
        );


        /*
         * =========================
         * 完了報告ボタン
         * =========================
         */

        ItemStack complete =
                new ItemStack(
                        Material.EMERALD_BLOCK
                );

        ItemMeta completeMeta =
                complete.getItemMeta();

        if (completeMeta != null) {

            completeMeta.setDisplayName(
                    ChatColor.GREEN
                            + "完了報告する"
            );

            completeMeta.setLore(
                    Arrays.asList(
                            ChatColor.GRAY
                                    + "依頼主に完了を報告します",
                            "",
                            ChatColor.YELLOW
                                    + "クリックして完了報告"
                    )
            );


            /*
             * =========================
             * 完了報告ボタンに
             * 依頼IDをPDC保存
             * =========================
             */

            completeMeta
                    .getPersistentDataContainer()
                    .set(
                            questKey,
                            PersistentDataType.STRING,
                            quest.getQuestId().toString()
                    );

            complete.setItemMeta(
                    completeMeta
            );
        }

        inventory.setItem(
                11,
                complete
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

            backMeta.setLore(
                    Arrays.asList(
                            ChatColor.GRAY
                                    + "受注中の依頼一覧に戻る"
                    )
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