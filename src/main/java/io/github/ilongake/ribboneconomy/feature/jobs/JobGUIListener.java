package io.github.ilongake.ribboneconomy.feature.jobs;

import io.github.ilongake.ribboneconomy.feature.mainmenu.RPGMenuGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class JobGUIListener
        implements Listener {

    private final JobManager jobManager;

    public JobGUIListener(
            JobManager jobManager
    ) {

        this.jobManager =
                jobManager;
    }


    /**
     * 職業メニューのクリック処理
     */
    @EventHandler
    public void onJobMenuClick(
            InventoryClickEvent event
    ) {

        if (!(event.getWhoClicked()
                instanceof Player player)) {

            return;
        }


        /*
         * =========================
         * 職業メニューか確認
         * =========================
         */

        if (!event.getView()
                .getTitle()
                .equals(
                        ChatColor.AQUA
                                + "職業メニュー"
                )) {

            return;
        }


        /*
         * GUI内のアイテムを操作できないようにする
         */

        event.setCancelled(true);


        /*
         * クリックされたアイテムがない
         */

        if (event.getCurrentItem() == null) {

            return;
        }


        Material material =
                event.getCurrentItem()
                        .getType();


        /*
         * =========================
         * 採掘師
         * =========================
         */

        if (material
                == Material.IRON_PICKAXE) {

            jobManager.setJob(
                    player.getUniqueId(),
                    JobType.MINER
            );

            player.closeInventory();

            player.sendMessage(
                    ChatColor.GREEN
                            + "採掘師に就職しました！"
            );

            return;
        }


        /*
         * =========================
         * 農家
         * =========================
         */

        if (material
                == Material.WHEAT) {

            jobManager.setJob(
                    player.getUniqueId(),
                    JobType.FARMER
            );

            player.closeInventory();

            player.sendMessage(
                    ChatColor.GREEN
                            + "農家に就職しました！"
            );

            return;
        }


        /*
         * =========================
         * 狩人
         * =========================
         */

        if (material
                == Material.BOW) {

            jobManager.setJob(
                    player.getUniqueId(),
                    JobType.HUNTER
            );

            player.closeInventory();

            player.sendMessage(
                    ChatColor.GREEN
                            + "狩人に就職しました！"
            );

            return;
        }

        /*
         * =========================
         * 木こり
         * =========================
         */

        if (material
                == Material.IRON_AXE) {

            jobManager.setJob(
                    player.getUniqueId(),
                    JobType.LUMBERJACK
            );

            player.closeInventory();

            player.sendMessage(
                    ChatColor.GREEN
                            + "木こりに就職しました！"
            );

            return;
        }


        /*
         * =========================
         * 職業を辞める
         * =========================
         */

        if (material
                == Material.BARRIER) {

            jobManager.leaveJob(
                    player.getUniqueId()
            );

            player.closeInventory();

            player.sendMessage(
                    ChatColor.YELLOW
                            + "職業を辞めました。"
            );

            return;
        }


        /*
         * =========================
         * 戻る
         * =========================
         */

        if (material
                == Material.ARROW) {

            player.closeInventory();

            RPGMenuGUI.open(
                    player
            );

            return;
        }
    }
}