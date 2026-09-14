package io.github.ilongake.ribboneconomy.feature.mainmenu;

import io.github.ilongake.ribboneconomy.core.DataManager;
import io.github.ilongake.ribboneconomy.feature.jobs.JobGUI;
import io.github.ilongake.ribboneconomy.feature.jobs.JobManager;
import io.github.ilongake.ribboneconomy.quest.Quest;
import io.github.ilongake.ribboneconomy.quest.QuestManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class RPGMenuListener
        implements Listener {

    private final QuestManager questManager;
    private final DataManager dataManager;
    private final JobManager jobManager;


    public RPGMenuListener(
            QuestManager questManager,
            DataManager dataManager,
            JobManager jobManager
    ) {

        this.questManager =
                questManager;

        this.dataManager =
                dataManager;

        this.jobManager =
                jobManager;
    }


    /**
     * エンチャントテーブルを右クリック
     */
    @EventHandler
    public void onEnchantTableClick(
            PlayerInteractEvent event
    ) {

        if (event.getAction()
                != Action.RIGHT_CLICK_BLOCK) {

            return;
        }

        if (event.getClickedBlock() == null) {

            return;
        }

        if (event.getClickedBlock()
                .getType()
                != Material.ENCHANTING_TABLE) {

            return;
        }

        Player player =
                event.getPlayer();

        // 通常のエンチャント画面を開かない
        event.setCancelled(true);

        // RPGメニューを開く
        RPGMenuGUI.open(player);
    }


    /**
     * GUIクリック処理
     */
    @EventHandler
    public void onMenuClick(
            InventoryClickEvent event
    ) {

        if (!(event.getWhoClicked()
                instanceof Player player)) {

            return;
        }

        String title =
                event.getView()
                        .getTitle();


        /*
         * =========================
         * RPGメインメニュー
         * =========================
         */

        if (title.equals(
                ChatColor.DARK_PURPLE
                        + "RPGメニュー"
        )) {

            event.setCancelled(true);

            if (event.getCurrentItem() == null) {

                return;
            }

            Material material =
                    event.getCurrentItem()
                            .getType();


            /*
             * お金
             */

            if (material
                    == Material.GOLD_INGOT) {

                player.closeInventory();

                MoneyGUI.open(
                        player,
                        dataManager
                );

                return;
            }


            /*
             * エメラルド交換
             */

            if (material
                    == Material.EMERALD) {

                player.closeInventory();

                ExchangeGUI.open(
                        player
                );

                return;
            }


            /*
             * 依頼
             */

            if (material
                    == Material.BOOK) {

                player.closeInventory();

                QuestGUI.open(
                        player
                );

                return;
            }


            /*
             * 職業
             */

            if (material
                    == Material.IRON_PICKAXE) {

                player.closeInventory();

                JobGUI.open(
                        player,
                        jobManager
                );

                return;
            }

            return;
        }


        /*
         * =========================
         * 依頼メニュー
         * =========================
         */

        if (title.equals(
                ChatColor.DARK_GREEN
                        + "依頼メニュー"
        )) {

            event.setCancelled(true);

            if (event.getCurrentItem() == null) {

                return;
            }

            Material material =
                    event.getCurrentItem()
                            .getType();


            /*
             * 公開中の依頼
             */

            if (material
                    == Material.BOOK) {

                player.closeInventory();

                QuestListGUI.open(
                        player,
                        questManager
                );

                return;
            }


            /*
             * 依頼を作成
             */

            if (material
                    == Material.WRITABLE_BOOK) {

                player.closeInventory();

                QuestCreateGUI.open(
                        player
                );

                return;
            }


            /*
             * 受注中の依頼
             */

            if (material
                    == Material.CHEST) {

                player.closeInventory();

                AcceptedQuestGUI.open(
                        player,
                        questManager
                );

                return;
            }


            /*
             * 承認待ちの依頼
             */

            if (material
                    == Material.EMERALD) {

                player.closeInventory();

                CompletedQuestGUI.open(
                        player,
                        questManager
                );

                return;
            }

            return;
        }


        /*
         * =========================
         * 依頼作成画面
         * =========================
         */

        if (title.equals(
                ChatColor.DARK_GREEN
                        + "依頼を作成"
        )) {

            event.setCancelled(true);

            if (event.getCurrentItem() == null) {

                return;
            }

            Material material =
                    event.getCurrentItem()
                            .getType();


            /*
             * タイトル入力
             */

            if (material
                    == Material.NAME_TAG) {

                player.closeInventory();

                player.sendMessage(
                        ChatColor.YELLOW
                                + "依頼のタイトルをチャットに入力してください。"
                );

                return;
            }


            /*
             * 内容入力
             */

            if (material
                    == Material.WRITABLE_BOOK) {

                player.closeInventory();

                player.sendMessage(
                        ChatColor.YELLOW
                                + "依頼の内容をチャットに入力してください。"
                );

                return;
            }


            /*
             * 報酬入力
             */

            if (material
                    == Material.EMERALD) {

                player.closeInventory();

                player.sendMessage(
                        ChatColor.YELLOW
                                + "報酬額を数字で入力してください。"
                );

                return;
            }


            /*
             * 戻る
             */

            if (material
                    == Material.BARRIER) {

                player.closeInventory();

                QuestGUI.open(
                        player
                );

                return;
            }

            return;
        }


        /*
         * =========================
         * 公開中の依頼一覧
         * =========================
         */

        if (title.equals(
                ChatColor.DARK_GREEN
                        + "公開中の依頼"
        )) {

            event.setCancelled(true);

            if (event.getCurrentItem() == null) {

                return;
            }

            if (event.getCurrentItem()
                    .getType()
                    != Material.PAPER) {

                return;
            }

            UUID questId =
                    getQuestIdFromPDC(
                            player,
                            event.getCurrentItem()
                    );

            if (questId == null) {

                player.sendMessage(
                        ChatColor.RED
                                + "依頼IDを取得できませんでした。"
                );

                return;
            }

            Quest quest =
                    questManager.getQuest(
                            questId
                    );

            if (quest == null) {

                player.sendMessage(
                        ChatColor.RED
                                + "この依頼は存在しません。"
                );

                return;
            }

            player.closeInventory();

            QuestDetailGUI.open(
                    player,
                    quest
            );

            return;
        }


        /*
         * =========================
         * 受注中の依頼一覧
         * =========================
         */

        if (title.equals(
                ChatColor.DARK_GREEN
                        + "受注中の依頼"
        )) {

            event.setCancelled(true);

            if (event.getCurrentItem() == null) {

                return;
            }

            if (event.getCurrentItem()
                    .getType()
                    != Material.CHEST) {

                return;
            }

            UUID questId =
                    getQuestIdFromPDC(
                            player,
                            event.getCurrentItem()
                    );

            if (questId == null) {

                player.sendMessage(
                        ChatColor.RED
                                + "依頼IDを取得できませんでした。"
                );

                return;
            }

            Quest quest =
                    questManager.getQuest(
                            questId
                    );

            if (quest == null) {

                player.sendMessage(
                        ChatColor.RED
                                + "この依頼は存在しません。"
                );

                return;
            }

            player.closeInventory();

            AcceptedQuestDetailGUI.open(
                    player,
                    quest
            );

            return;
        }


        /*
         * =========================
         * 承認待ちの依頼一覧
         * =========================
         */

        if (title.equals(
                ChatColor.DARK_GREEN
                        + "承認待ちの依頼"
        )) {

            event.setCancelled(true);

            if (event.getCurrentItem() == null) {

                return;
            }

            Material material =
                    event.getCurrentItem()
                            .getType();


            /*
             * 依頼をクリック
             */

            if (material
                    == Material.CHEST) {

                UUID questId =
                        getQuestIdFromPDC(
                                player,
                                event.getCurrentItem()
                        );

                if (questId == null) {

                    player.sendMessage(
                            ChatColor.RED
                                    + "依頼IDを取得できませんでした。"
                    );

                    return;
                }

                Quest quest =
                        questManager.getQuest(
                                questId
                        );

                if (quest == null) {

                    player.sendMessage(
                            ChatColor.RED
                                    + "この依頼は存在しません。"
                    );

                    return;
                }

                player.closeInventory();

                CompletedQuestDetailGUI.open(
                        player,
                        quest
                );

                return;
            }


            /*
             * 戻る
             */

            if (material
                    == Material.ARROW) {

                player.closeInventory();

                QuestGUI.open(
                        player
                );

                return;
            }

            return;
        }


        /*
         * =========================
         * 公開中の依頼詳細
         * =========================
         */

        if (title.equals(
                ChatColor.DARK_GREEN
                        + "依頼詳細"
        )) {

            event.setCancelled(true);

            if (event.getCurrentItem() == null) {

                return;
            }

            Material material =
                    event.getCurrentItem()
                            .getType();


            /*
             * 受注ボタン
             */

            if (material
                    == Material.EMERALD_BLOCK) {

                UUID questId =
                        getQuestIdFromPDC(
                                player,
                                event.getCurrentItem()
                        );

                if (questId == null) {

                    player.sendMessage(
                            ChatColor.RED
                                    + "依頼IDを取得できませんでした。"
                    );

                    return;
                }

                Quest quest =
                        questManager.getQuest(
                                questId
                        );

                if (quest == null) {

                    player.sendMessage(
                            ChatColor.RED
                                    + "この依頼は存在しません。"
                    );

                    player.closeInventory();

                    return;
                }

                boolean success =
                        questManager.acceptQuest(
                                questId,
                                player.getUniqueId()
                        );

                if (!success) {

                    player.sendMessage(
                            ChatColor.RED
                                    + "この依頼は受注できません。"
                    );

                    return;
                }

                player.closeInventory();

                player.sendMessage(
                        ChatColor.GREEN
                                + "依頼を受注しました！"
                );

                player.sendMessage(
                        ChatColor.YELLOW
                                + "タイトル: "
                                + quest.getTitle()
                );

                player.sendMessage(
                        ChatColor.GREEN
                                + "報酬: "
                                + quest.getReward()
                                + "円"
                );

                return;
            }


            /*
             * 戻る
             */

            if (material
                    == Material.BARRIER) {

                player.closeInventory();

                QuestListGUI.open(
                        player,
                        questManager
                );

                return;
            }

            return;
        }


        /*
         * =========================
         * 受注中の依頼詳細
         * =========================
         */

        if (title.equals(
                ChatColor.DARK_GREEN
                        + "受注中の依頼詳細"
        )) {

            event.setCancelled(true);

            if (event.getCurrentItem() == null) {

                return;
            }

            Material material =
                    event.getCurrentItem()
                            .getType();


            /*
             * 完了報告
             */

            if (material
                    == Material.EMERALD_BLOCK) {

                UUID questId =
                        getQuestIdFromPDC(
                                player,
                                event.getCurrentItem()
                        );

                if (questId == null) {

                    player.sendMessage(
                            ChatColor.RED
                                    + "依頼IDを取得できませんでした。"
                    );

                    return;
                }

                Quest quest =
                        questManager.getQuest(
                                questId
                        );

                if (quest == null) {

                    player.sendMessage(
                            ChatColor.RED
                                    + "この依頼は存在しません。"
                    );

                    player.closeInventory();

                    return;
                }

                boolean success =
                        questManager.completeQuest(
                                questId,
                                player.getUniqueId()
                        );

                if (!success) {

                    player.sendMessage(
                            ChatColor.RED
                                    + "完了報告できませんでした。"
                    );

                    return;
                }

                player.closeInventory();

                player.sendMessage(
                        ChatColor.GREEN
                                + "依頼の完了報告をしました！"
                );

                player.sendMessage(
                        ChatColor.YELLOW
                                + "依頼主の承認を待っています。"
                );

                return;
            }


            /*
             * 戻る
             */

            if (material
                    == Material.BARRIER) {

                player.closeInventory();

                AcceptedQuestGUI.open(
                        player,
                        questManager
                );

                return;
            }

            return;
        }


        /*
         * =========================
         * 承認待ちの依頼詳細
         * =========================
         */

        if (title.equals(
                ChatColor.DARK_GREEN
                        + "承認待ちの依頼詳細"
        )) {

            event.setCancelled(true);

            if (event.getCurrentItem() == null) {

                return;
            }

            Material material =
                    event.getCurrentItem()
                            .getType();


            /*
             * 承認ボタン
             */

            if (material
                    == Material.EMERALD_BLOCK) {

                UUID questId =
                        getQuestIdFromPDC(
                                player,
                                event.getCurrentItem()
                        );

                if (questId == null) {

                    player.sendMessage(
                            ChatColor.RED
                                    + "依頼IDを取得できませんでした。"
                    );

                    return;
                }

                Quest quest =
                        questManager.getQuest(
                                questId
                        );

                if (quest == null) {

                    player.sendMessage(
                            ChatColor.RED
                                    + "この依頼は存在しません。"
                    );

                    player.closeInventory();

                    return;
                }

                boolean success =
                        questManager.approveQuest(
                                questId,
                                player.getUniqueId()
                        );

                if (!success) {

                    player.sendMessage(
                            ChatColor.RED
                                    + "依頼を承認できませんでした。"
                    );

                    return;
                }

                player.closeInventory();

                player.sendMessage(
                        ChatColor.GREEN
                                + "依頼を承認しました！"
                );

                player.sendMessage(
                        ChatColor.GOLD
                                + "受注者に "
                                + quest.getReward()
                                + "円を支払いました！"
                );

                return;
            }


            /*
             * 戻る
             */

            if (material
                    == Material.BARRIER) {

                player.closeInventory();

                CompletedQuestGUI.open(
                        player,
                        questManager
                );

                return;
            }

            return;
        }
    }


    /**
     * アイテムのPDCから依頼IDを取得
     */
    private UUID getQuestIdFromPDC(
            Player player,
            ItemStack item
    ) {

        if (item == null) {

            return null;
        }

        ItemMeta meta =
                item.getItemMeta();

        if (meta == null) {

            return null;
        }


        /*
         * 依頼IDを保存するキー
         */

        NamespacedKey questKey =
                new NamespacedKey(
                        player.getServer()
                                .getPluginManager()
                                .getPlugin("RPGEconomy"),
                        "quest_id"
                );


        /*
         * PDCから依頼IDを取得
         */

        String id =
                meta.getPersistentDataContainer()
                        .get(
                                questKey,
                                PersistentDataType.STRING
                        );


        /*
         * IDが保存されていない
         */

        if (id == null) {

            return null;
        }


        /*
         * UUIDに変換
         */

        try {

            return UUID.fromString(id);

        } catch (IllegalArgumentException e) {

            return null;
        }
    }
}