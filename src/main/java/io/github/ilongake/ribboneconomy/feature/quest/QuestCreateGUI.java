package io.github.ilongake.ribboneconomy.feature.quest;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class QuestCreateGUI {

    /**
     * 依頼作成GUIを開く
     */
    public static void open(Player player) {

        Inventory inventory =
                Bukkit.createInventory(
                        null,
                        27,
                        ChatColor.DARK_GREEN
                                + "依頼を作成"
                );

        /*
         * =========================
         * タイトル
         * =========================
         */

        inventory.setItem(
                10,
                createItem(
                        Material.NAME_TAG,
                        ChatColor.YELLOW
                                + "タイトルを設定"
                )
        );


        /*
         * =========================
         * 内容
         * =========================
         */

        inventory.setItem(
                13,
                createItem(
                        Material.WRITABLE_BOOK,
                        ChatColor.AQUA
                                + "依頼内容を設定"
                )
        );


        /*
         * =========================
         * 報酬
         * =========================
         */

        inventory.setItem(
                16,
                createItem(
                        Material.EMERALD,
                        ChatColor.GREEN
                                + "報酬金額を設定"
                )
        );


        /*
         * =========================
         * 依頼公開
         * =========================
         */

        inventory.setItem(
                22,
                createItem(
                        Material.EMERALD_BLOCK,
                        ChatColor.GREEN
                                + "依頼を公開"
                )
        );


        /*
         * =========================
         * 戻る
         * =========================
         */

        inventory.setItem(
                26,
                createItem(
                        Material.BARRIER,
                        ChatColor.RED
                                + "戻る"
                )
        );


        player.openInventory(
                inventory
        );
    }


    /**
     * 依頼作成GUIを開く
     *
     * 現在の入力状態を表示する
     */
    public static void open(
            Player player,
            String title,
            String description,
            Double reward
    ) {

        Inventory inventory =
                Bukkit.createInventory(
                        null,
                        27,
                        ChatColor.DARK_GREEN
                                + "依頼を作成"
                );


        /*
         * =========================
         * タイトル
         * =========================
         */

        List<String> titleLore =
                new ArrayList<>();

        if (title == null) {

            titleLore.add(
                    ChatColor.RED
                            + "未設定"
            );

        } else {

            titleLore.add(
                    ChatColor.WHITE
                            + title
            );
        }

        inventory.setItem(
                10,
                createItem(
                        Material.NAME_TAG,
                        ChatColor.YELLOW
                                + "タイトルを設定",
                        titleLore
                )
        );


        /*
         * =========================
         * 内容
         * =========================
         */

        List<String> descriptionLore =
                new ArrayList<>();

        if (description == null) {

            descriptionLore.add(
                    ChatColor.RED
                            + "未設定"
            );

        } else {

            descriptionLore.add(
                    ChatColor.WHITE
                            + description
            );
        }

        inventory.setItem(
                13,
                createItem(
                        Material.WRITABLE_BOOK,
                        ChatColor.AQUA
                                + "依頼内容を設定",
                        descriptionLore
                )
        );


        /*
         * =========================
         * 報酬
         * =========================
         */

        List<String> rewardLore =
                new ArrayList<>();

        if (reward == null) {

            rewardLore.add(
                    ChatColor.RED
                            + "未設定"
            );

        } else {

            rewardLore.add(
                    ChatColor.GOLD
                            + String.valueOf(reward)
                            + "円"
            );
        }

        inventory.setItem(
                16,
                createItem(
                        Material.EMERALD,
                        ChatColor.GREEN
                                + "報酬金額を設定",
                        rewardLore
                )
        );


        /*
         * =========================
         * 依頼公開
         * =========================
         */

        boolean complete =
                title != null
                        && description != null
                        && reward != null;


        if (complete) {

            inventory.setItem(
                    22,
                    createItem(
                            Material.EMERALD_BLOCK,
                            ChatColor.GREEN
                                    + "依頼を公開"
                    )
            );

        } else {

            inventory.setItem(
                    22,
                    createItem(
                            Material.GRAY_DYE,
                            ChatColor.GRAY
                                    + "依頼を公開",
                            List.of(
                                    ChatColor.RED
                                            + "タイトル・内容・報酬を",
                                    ChatColor.RED
                                            + "すべて設定してください。"
                            )
                    )
            );
        }


        /*
         * =========================
         * 戻る
         * =========================
         */

        inventory.setItem(
                26,
                createItem(
                        Material.BARRIER,
                        ChatColor.RED
                                + "戻る"
                )
        );


        player.openInventory(
                inventory
        );
    }


    /**
     * アイテム作成
     */
    private static ItemStack createItem(
            Material material,
            String name
    ) {

        return createItem(
                material,
                name,
                null
        );
    }


    /**
     * アイテム作成
     * Lore付き
     */
    private static ItemStack createItem(
            Material material,
            String name,
            List<String> lore
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

            if (lore != null) {

                meta.setLore(
                        lore
                );
            }

            item.setItemMeta(
                    meta
            );
        }

        return item;
    }
}