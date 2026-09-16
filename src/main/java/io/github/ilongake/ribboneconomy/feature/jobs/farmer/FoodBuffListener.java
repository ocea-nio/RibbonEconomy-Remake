package io.github.ilongake.ribboneconomy.feature.jobs.farmer;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

public class FoodBuffListener implements Listener {

    private final FoodBuffManager foodBuffManager;

    public FoodBuffListener(
            FoodBuffManager foodBuffManager
    ) {
        this.foodBuffManager = foodBuffManager;
    }

    /*
     * ==========================================================
     * クラフト結果準備
     * ==========================================================
     *
     * Lv.20以上のファーマーなら、
     *
     * パン
     * 金のニンジン
     * 金のリンゴ
     *
     * のクラフト結果を特殊食料へ変更する。
     *
     * これによってクラフト画面上にも
     * 特殊食料が表示される。
     * ==========================================================
     */

    @EventHandler(
            priority = EventPriority.HIGHEST,
            ignoreCancelled = true
    )
    public void onPrepareCraft(
            PrepareItemCraftEvent event
    ) {

        if (!(event.getView().getPlayer() instanceof Player player)) {
            return;
        }

        CraftingInventory inventory =
                event.getInventory();

        ItemStack result =
                inventory.getResult();

        if (result == null
                || result.getType().isAir()) {
            return;
        }

        /*
         * パン・金のニンジン・金のリンゴ以外は無視
         */

        if (!foodBuffManager.isSpecialFood(
                result
        )) {
            return;
        }

        /*
         * すでに特殊食料なら何もしない
         */

        if (foodBuffManager.isSpecialFood(result)) {
            return;
        }

        /*
         * 特殊食料作成
         */

        ItemStack specialFood =
                foodBuffManager.createSpecialFood(
                        player,
                        result
                );

        if (specialFood == null
                || specialFood.getType().isAir()) {
            return;
        }

        /*
         * Lv.20未満なら通常食料のまま
         */

        if (!foodBuffManager.isSpecialFood(
                specialFood
        )) {
            return;
        }

        /*
         * クラフト結果を特殊食料に変更
         */

        specialFood.setAmount(
                result.getAmount()
        );

        inventory.setResult(
                specialFood
        );
    }

    /*
     * ==========================================================
     * 実際のクラフト
     * ==========================================================
     *
     * 通常クリック・Shiftクリックの両方を監視する。
     *
     * Shiftクリックでも通常食料が渡されないようにする。
     * ==========================================================
     */

    @EventHandler(
            priority = EventPriority.HIGHEST,
            ignoreCancelled = true
    )
    public void onCraft(
            CraftItemEvent event
    ) {

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        ItemStack result =
                event.getInventory().getResult();

        if (result == null
                || result.getType().isAir()) {
            return;
        }

        /*
         * 対象食料以外は無視
         */

        if (!foodBuffManager.isSpecialFood(
                result
        )) {
            return;
        }

        /*
         * すでに特殊食料ならそのまま
         */

        if (foodBuffManager.isSpecialFood(result)) {
            return;
        }

        /*
         * Shiftクリックの場合
         *
         * Bukkit/Paperの通常クラフト処理に
         * 通常パンを渡させない。
         */

        if (event.isShiftClick()) {

            /*
             * 一旦キャンセル
             */

            event.setCancelled(true);

            /*
             * 特殊食料を1個作成
             */

            ItemStack specialFood =
                    foodBuffManager.createSpecialFood(
                            player,
                            result
                    );

            if (specialFood == null
                    || specialFood.getType().isAir()) {
                return;
            }

            /*
             * Lv.20未満なら通常処理へ戻す
             */

            if (!foodBuffManager.isSpecialFood(
                    specialFood
            )) {
                event.setCancelled(false);
                return;
            }

            /*
             * Shiftクリックで可能な限りクラフト
             *
             * まずクラフト結果1回分を
             * 特殊食料としてプレイヤーへ入れる。
             *
             * 以降の材料消費はBukkit側に任せず、
             * 安全に1回分ずつ処理する。
             */

            int maxCrafts =
                    calculateMaxCrafts(
                            event.getInventory()
                    );

            if (maxCrafts <= 0) {
                return;
            }

            /*
             * 特殊食料をまとめて作成
             */

            specialFood.setAmount(
                    Math.min(
                            maxCrafts,
                            specialFood.getMaxStackSize()
                    )
            );

            /*
             * インベントリへ追加
             */

            player.getInventory().addItem(
                    specialFood
            );

            /*
             * 材料を1回分だけ消費
             *
             * ここでは安全性を優先し、
             * 一度に大量消費しない。
             */

            consumeCraftIngredients(
                    event.getInventory()
            );

            return;
        }

        /*
         * ======================================================
         * 通常クリック
         * ======================================================
         */

        ItemStack specialFood =
                foodBuffManager.createSpecialFood(
                        player,
                        result
                );

        if (specialFood == null
                || specialFood.getType().isAir()) {
            return;
        }

        /*
         * Lv.20未満
         */

        if (!foodBuffManager.isSpecialFood(
                specialFood
        )) {
            return;
        }

        /*
         * 個数維持
         */

        specialFood.setAmount(
                result.getAmount()
        );

        /*
         * 結果を特殊食料へ
         */

        event.getInventory().setResult(
                specialFood
        );
    }

    /*
     * ==========================================================
     * Shiftクリック可能回数
     * ==========================================================
     */

    private int calculateMaxCrafts(
            CraftingInventory inventory
    ) {

        ItemStack[] matrix =
                inventory.getMatrix();

        int minimum =
                Integer.MAX_VALUE;

        for (ItemStack ingredient : matrix) {

            if (ingredient == null
                    || ingredient.getType().isAir()) {
                continue;
            }

            minimum =
                    Math.min(
                            minimum,
                            ingredient.getAmount()
                    );
        }

        if (minimum == Integer.MAX_VALUE) {
            return 0;
        }

        return minimum;
    }

    /*
     * ==========================================================
     * 材料消費
     * ==========================================================
     */

    private void consumeCraftIngredients(
            CraftingInventory inventory
    ) {

        ItemStack[] matrix =
                inventory.getMatrix();

        for (int i = 0; i < matrix.length; i++) {

            ItemStack item =
                    matrix[i];

            if (item == null
                    || item.getType().isAir()) {
                continue;
            }

            int amount =
                    item.getAmount();

            if (amount <= 1) {

                inventory.setItem(
                        i,
                        null
                );

            } else {

                item.setAmount(
                        amount - 1
                );

                inventory.setItem(
                        i,
                        item
                );
            }
        }
    }

    /*
     * ==========================================================
     * 食べたとき
     * ==========================================================
     */

    @EventHandler(
            priority = EventPriority.HIGHEST,
            ignoreCancelled = true
    )
    public void onPlayerConsume(
            PlayerItemConsumeEvent event
    ) {

        Player player =
                event.getPlayer();

        if (player == null) {
            return;
        }

        ItemStack food =
                event.getItem();

        if (food == null
                || food.getType().isAir()) {
            return;
        }

        /*
         * 特殊食料だけ効果発動
         */

        if (!foodBuffManager.isSpecialFood(food)) {
            return;
        }

        foodBuffManager.applyBuff(
                player,
                food
        );
    }
}