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

import java.util.List;

public class AcceptedQuestGUI {

    /**
     * 受注中の依頼一覧を開く
     */
    public static void open(
            Player player,
            QuestManager questManager
    ) {

        /*
         * =========================
         * 受注中の依頼を取得
         * =========================
         */

        List<Quest> quests =
                questManager.getAcceptedQuests(
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
                                + "受注中の依頼"
                );


        /*
         * =========================
         * PDC用のキーを作成
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
                 * 依頼タイトル
                 */
                meta.setDisplayName(
                        ChatColor.YELLOW
                                + quest.getTitle()
                );


                /*
                 * 依頼情報
                 *
                 * UUIDは表示しない
                 */
                meta.setLore(
                        List.of(
                                ChatColor.WHITE
                                        + "依頼内容: "
                                        + quest.getDescription(),

                                ChatColor.GREEN
                                        + "報酬: "
                                        + quest.getReward()
                                        + "円",

                                "",

                                ChatColor.AQUA
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
                 * ItemMetaをアイテムに反映
                 */
                item.setItemMeta(
                        meta
                );
            }


            /*
             * =========================
             * GUIにアイテムを設置
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
         * 受注中の依頼がない場合
         * =========================
         */

        if (quests.isEmpty()) {

            inventory.setItem(
                    22,
                    createItem(
                            Material.BARRIER,
                            ChatColor.RED
                                    + "受注中の依頼はありません"
                    )
            );
        }


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