package io.github.ilongake.ribboneconomy.feature.jobs;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class JobGUI {

    /**
     * 職業メニューを開く
     */
    public static void open(
            Player player,
            JobManager jobManager
    ) {

        Inventory inventory =
                Bukkit.createInventory(
                        null,
                        27,
                        ChatColor.AQUA
                                + "職業メニュー"
                );


        /*
         * =========================
         * 現在の職業
         * =========================
         */

        JobType currentJob =
                jobManager.getJob(
                        player.getUniqueId()
                );

        ItemStack current =
                new ItemStack(
                        Material.PAPER
                );

        ItemMeta currentMeta =
                current.getItemMeta();

        if (currentMeta != null) {

            currentMeta.setDisplayName(
                    ChatColor.YELLOW
                            + "現在の職業"
            );

            currentMeta.setLore(
                    List.of(
                            ChatColor.WHITE
                                    + currentJob
                                    .getDisplayName()
                    )
            );

            current.setItemMeta(
                    currentMeta
            );
        }

        inventory.setItem(
                4,
                current
        );


        /*
         * =========================
         * 採掘師
         * =========================
         */

        inventory.setItem(
                10,
                createJobItem(
                        Material.IRON_PICKAXE,
                        ChatColor.GRAY
                                + "採掘師",
                        JobType.MINER,
                        currentJob
                )
        );


        /*
         * =========================
         * 農家
         * =========================
         */

        inventory.setItem(
                11,
                createJobItem(
                        Material.WHEAT,
                        ChatColor.GREEN
                                + "農家",
                        JobType.FARMER,
                        currentJob
                )
        );


        /*
         * =========================
         * 木こり
         * =========================
         */

        inventory.setItem(
                12,
                createJobItem(
                        Material.IRON_AXE,
                        ChatColor.GOLD
                                + "木こり",
                        JobType.LUMBERJACK,
                        currentJob
                )
        );


        /*
         * =========================
         * 職業を辞める
         * =========================
         */

        inventory.setItem(
                13,
                createJobItem(
                        Material.BARRIER,
                        ChatColor.RED
                                + "職業を辞める",
                        JobType.NONE,
                        currentJob
                )
        );


        /*
         * =========================
         * 狩人
         * =========================
         */

        inventory.setItem(
                15,
                createJobItem(
                        Material.BOW,
                        ChatColor.RED
                                + "狩人",
                        JobType.HUNTER,
                        currentJob
                )
        );


        /*
         * =========================
         * 戻る
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
                                    + "RPGメニューに戻る"
                    )
            );

            back.setItemMeta(
                    backMeta
            );
        }

        inventory.setItem(
                22,
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
     * 職業アイテム作成
     */
    private static ItemStack createJobItem(
            Material material,
            String name,
            JobType jobType,
            JobType currentJob
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

            if (currentJob == jobType) {

                meta.setLore(
                        List.of(
                                ChatColor.GREEN
                                        + "現在の職業です"
                        )
                );

            } else {

                meta.setLore(
                        List.of(
                                ChatColor.YELLOW
                                        + "クリックして就職"
                        )
                );
            }

            item.setItemMeta(
                    meta
            );
        }

        return item;
    }
}