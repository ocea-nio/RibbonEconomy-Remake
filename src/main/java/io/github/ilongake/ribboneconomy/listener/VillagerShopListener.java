package io.github.ilongake.ribboneconomy.listener;

import io.github.ilongake.ribboneconomy.core.DataManager;
import io.github.ilongake.ribboneconomy.gui.VillagerShopGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class VillagerShopListener implements Listener {

    private final DataManager dataManager;

    /*
     * =========================
     * コンストラクター
     * =========================
     */

    public VillagerShopListener(
            DataManager dataManager
    ) {

        this.dataManager =
                dataManager;
    }


    /*
     * =========================
     * 無職の村人を右クリック
     * =========================
     */

    @EventHandler
    public void onVillagerClick(
            PlayerInteractEntityEvent event
    ) {

        // 村人以外は無視
        if (event.getRightClicked().getType()
                != EntityType.VILLAGER) {

            return;
        }

        Villager villager =
                (Villager) event.getRightClicked();


        // 無職の村人以外は無視
        if (villager.getProfession()
                != Villager.Profession.NONE) {

            return;
        }


        Player player =
                event.getPlayer();


        // 通常の村人交易を開かない
        event.setCancelled(true);


        // 村人ショップを開く
        VillagerShopGUI.open(
                player
        );
    }


    /*
     * =========================
     * ショップクリック
     * =========================
     */

    @EventHandler
    public void onShopClick(
            InventoryClickEvent event
    ) {

        // プレイヤー以外は無視
        if (!(event.getWhoClicked()
                instanceof Player player)) {

            return;
        }


        /*
         * =========================
         * 村人ショップか確認
         * =========================
         *
         * 1ページ目
         * 村人ショップ 1ページ目
         *
         * 2ページ目
         * 村人ショップ 2ページ目
         */

        if (!event.getView()
                .getTitle()
                .startsWith(
                        ChatColor.GREEN
                                + "村人ショップ"
                )) {

            return;
        }


        /*
         * GUI内のアイテムを操作できないようにする
         */

        event.setCancelled(true);


        /*
         * クリックしたアイテムがない場合
         */

        ItemStack clickedItem =
                event.getCurrentItem();

        if (clickedItem == null) {

            return;
        }


        /*
         * AIRの場合
         */

        if (clickedItem.getType()
                == Material.AIR) {

            return;
        }


        Material material =
                clickedItem.getType();


        /*
         * =========================
         * 閉じる
         * =========================
         */

        if (material
                == Material.BARRIER) {

            player.closeInventory();

            return;
        }


        /*
         * =========================
         * 次のページ
         * =========================
         */

        if (material
                == Material.ARROW) {

            String title =
                    event.getView()
                            .getTitle();


            /*
             * 1ページ目なら2ページ目へ
             */

            if (title.endsWith(
                    "1ページ目"
            )) {

                player.closeInventory();

                VillagerShopGUI.openPage(
                        player,
                        2
                );

                return;
            }


            /*
             * 2ページ目なら1ページ目へ
             */

            if (title.endsWith(
                    "2ページ目"
            )) {

                player.closeInventory();

                VillagerShopGUI.openPage(
                        player,
                        1
                );

                return;
            }

            return;
        }


        /*
         * =========================
         * ネザライトインゴット
         * =========================
         *
         * 1個
         * 1,000,000円
         */

        if (material
                == Material.NETHERITE_INGOT) {

            buyItem(
                    player,
                    Material.NETHERITE_INGOT,
                    1,
                    1_000_000
            );

            return;
        }


        /*
         * =========================
         * 防具装飾鍛冶型
         * =========================
         *
         * 1個
         * 500,000円
         */

        if (isSmithingTemplate(
                material
        )) {

            buyItem(
                    player,
                    material,
                    1,
                    500_000
            );

            return;
        }
    }


    /*
     * =========================
     * 防具装飾鍛冶型か判定
     * =========================
     */

    private boolean isSmithingTemplate(
            Material material
    ) {

        return material
                == Material.COAST_ARMOR_TRIM_SMITHING_TEMPLATE

                || material
                == Material.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE

                || material
                == Material.EYE_ARMOR_TRIM_SMITHING_TEMPLATE

                || material
                == Material.HOST_ARMOR_TRIM_SMITHING_TEMPLATE

                || material
                == Material.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE

                || material
                == Material.RIB_ARMOR_TRIM_SMITHING_TEMPLATE

                || material
                == Material.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE

                || material
                == Material.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE

                || material
                == Material.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE

                || material
                == Material.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE

                || material
                == Material.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE

                || material
                == Material.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE

                || material
                == Material.VEX_ARMOR_TRIM_SMITHING_TEMPLATE

                || material
                == Material.WARD_ARMOR_TRIM_SMITHING_TEMPLATE

                || material
                == Material.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE

                || material
                == Material.WILD_ARMOR_TRIM_SMITHING_TEMPLATE

                || material
                == Material.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE

                || material
                == Material.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE;
    }


    /*
     * =========================
     * 商品購入処理
     * =========================
     */

    private void buyItem(
            Player player,
            Material material,
            int amount,
            double price
    ) {

        double balance =
                dataManager.getBalance(
                        player.getUniqueId()
                );

        /*
         * =========================
         * お金が足りない
         * =========================
         */

        if (balance < price) {

            player.sendMessage(
                    ChatColor.RED
                            + "お金が足りません！"
            );

            player.sendMessage(
                    ChatColor.YELLOW
                            + "必要な金額: "
                            + String.format(
                            "%,.0f",
                            price
                    )
                            + "円"
            );

            player.sendMessage(
                    ChatColor.YELLOW
                            + "現在の所持金: "
                            + String.format(
                            "%,.0f",
                            balance
                    )
                            + "円"
            );

            return;
        }


        /*
         * =========================
         * 商品を作成
         * =========================
         */

        ItemStack item =
                new ItemStack(
                        material,
                        amount
                );


        /*
         * =========================
         * インベントリに空きがあるか確認
         * =========================
         */

        if (player.getInventory()
                .firstEmpty()
                == -1) {

            player.sendMessage(
                    ChatColor.RED
                            + "インベントリに空きがありません！"
            );

            return;
        }


        /*
         * =========================
         * お金を引く
         * =========================
         */

        dataManager.removeBalance(
                player.getUniqueId(),
                price
        );


        /*
         * =========================
         * 商品を渡す
         * =========================
         */

        player.getInventory()
                .addItem(item);


        /*
         * =========================
         * 購入成功メッセージ
         * =========================
         */

        player.sendMessage(
                ChatColor.GREEN
                        + "購入しました！"
        );

        player.sendMessage(
                ChatColor.YELLOW
                        + "商品: "
                        + material.name()
                        + " ×"
                        + amount
        );

        player.sendMessage(
                ChatColor.GOLD
                        + "支払い: "
                        + String.format(
                        "%,.0f",
                        price
                )
                        + "円"
        );

        player.sendMessage(
                ChatColor.AQUA
                        + "残高: "
                        + String.format(
                        "%,.0f",
                        dataManager.getBalance(
                                player.getUniqueId()
                        )
                )
                        + "円"
        );
    }
}