package io.github.ilongake.ribboneconomy.gui;

import io.github.ilongake.ribboneconomy.quest.Quest;
import io.github.ilongake.ribboneconomy.quest.QuestManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class CompletedQuestGUI {

    /**
     * 承認待ちの依頼一覧GUIを開く
     */
    public static void open(
            Player player,
            QuestManager questManager
    ) {

        /*
         * =========================
         * 承認待ちの依頼を取得
         * =========================
         */

        List<Quest> quests =
                questManager.getCompletedQuests(
                        player.getUniqueId()
                );


        /*
         * =========================
         * GUI作成
         * =========================
         */

        Inventory inventory =
                Bukkit.createInventory(
                        null,
                        54,
                        ChatColor.DARK_GREEN
                                + "承認待ちの依頼"
                );


        /*
         * =========================
         * PDC用キー
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
         * 依頼を表示
         * =========================
         */

        int slot = 0;

        for (Quest quest : quests) {

            if (slot >= 45) {
                break;
            }

            ItemStack item =
                    new ItemStack(
                            Material.CHEST
                    );

            ItemMeta meta =
                    item.getItemMeta();

            if (meta != null) {

                /*
                 * =========================
                 * 表示名
                 * =========================
                 */

                meta.setDisplayName(
                        ChatColor.YELLOW
                                + quest.getTitle()
                );


                /*
                 * =========================
                 * Lore
                 *
                 * UUIDは表示しない
                 * =========================
                 */

                meta.setLore(
                        List.of(
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

                                ChatColor.AQUA
                                        + "受注者: "
                                        + quest.getWorkerUuid(),

                                "",

                                ChatColor.YELLOW
                                        + "クリックして詳細を確認"
                        )
                );


                /*
                 * =========================
                 * 依頼IDをPDCに保存
                 * =========================
                 */

                meta.getPersistentDataContainer()
                        .set(
                                questKey,
                                PersistentDataType.STRING,
                                quest.getQuestId()
                                        .toString()
                        );


                /*
                 * ItemMetaを反映
                 */

                item.setItemMeta(
                        meta
                );
            }


            /*
             * =========================
             * GUIに設置
             * =========================
             */

            inventory.setItem(
                    slot,
                    item
            );

            slot++;
        }


        /*
         * =========================
         * 依頼がない場合
         * =========================
         */

        if (quests.isEmpty()) {

            inventory.setItem(
                    22,
                    createItem(
                            Material.BARRIER,
                            ChatColor.RED
                                    + "承認待ちの依頼はありません"
                    )
            );
        }


        /*
         * =========================
         * 戻るボタン
         * =========================
         */

        ItemStack back =
                new ItemStack(
                        Material.ARROW
                );

        ItemMeta backMeta =
                back.getItemMeta();

        if (backMeta != null) {

            backMeta.setDisplayName(
                    ChatColor.RED
                            + "戻る"
            );

            backMeta.setLore(
                    List.of(
                            ChatColor.GRAY
                                    + "依頼メニューに戻る"
                    )
            );

            back.setItemMeta(
                    backMeta
            );
        }

        inventory.setItem(
                49,
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