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

public class QuestDetailGUI {

    /**
     * 依頼詳細GUIを開く
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
                                + "依頼詳細"
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
                                    + "状態: "
                                    + quest.getStatus().name(),
                            "",
                            ChatColor.GRAY
                                    + "依頼ID:",
                            ChatColor.GRAY
                                    + quest.getQuestId().toString()
                    )
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
         * OPEN
         * 受注可能
         * =========================
         */

        if (quest.getStatus()
                == Quest.Status.OPEN) {

            ItemStack accept =
                    new ItemStack(
                            Material.EMERALD_BLOCK
                    );

            ItemMeta acceptMeta =
                    accept.getItemMeta();

            if (acceptMeta != null) {

                acceptMeta.setDisplayName(
                        ChatColor.GREEN
                                + "依頼を受注する"
                );

                acceptMeta.setLore(
                        Arrays.asList(
                                ChatColor.GRAY
                                        + "この依頼を受注します"
                        )
                );

                // 依頼IDを保存
                acceptMeta
                        .getPersistentDataContainer()
                        .set(
                                questKey,
                                PersistentDataType.STRING,
                                quest.getQuestId()
                                        .toString()
                        );

                accept.setItemMeta(
                        acceptMeta
                );
            }

            inventory.setItem(
                    11,
                    accept
            );
        }


        /*
         * =========================
         * ACCEPTED
         * 自分が受注中
         * =========================
         */

        else if (
                quest.getStatus()
                        == Quest.Status.ACCEPTED
                        && quest.getWorkerUuid() != null
                        && quest.getWorkerUuid()
                        .equals(
                                player.getUniqueId()
                        )
        ) {

            ItemStack complete =
                    new ItemStack(
                            Material.EMERALD
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
                                        + "依頼を完了したことを報告します"
                        )
                );

                // 依頼IDを保存
                completeMeta
                        .getPersistentDataContainer()
                        .set(
                                questKey,
                                PersistentDataType.STRING,
                                quest.getQuestId()
                                        .toString()
                        );

                complete.setItemMeta(
                        completeMeta
                );
            }

            inventory.setItem(
                    11,
                    complete
            );
        }


        /*
         * =========================
         * COMPLETED
         * 承認待ち
         * =========================
         */

        else if (
                quest.getStatus()
                        == Quest.Status.COMPLETED
        ) {

            inventory.setItem(
                    11,
                    createItem(
                            Material.CLOCK,
                            ChatColor.YELLOW
                                    + "承認待ち"
                    )
            );
        }


        /*
         * =========================
         * APPROVED
         * 完了済み
         * =========================
         */

        else if (
                quest.getStatus()
                        == Quest.Status.APPROVED
        ) {

            inventory.setItem(
                    11,
                    createItem(
                            Material.LIME_WOOL,
                            ChatColor.GREEN
                                    + "依頼完了"
                    )
            );
        }


        /*
         * =========================
         * 戻るボタン
         * =========================
         */

        inventory.setItem(
                15,
                createItem(
                        Material.BARRIER,
                        ChatColor.RED
                                + "戻る"
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