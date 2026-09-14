package io.github.ilongake.ribboneconomy.farmer;

import io.github.ilongake.ribboneconomy.feature.jobs.JobManager;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class FoodBuffManager {

    /*
     * ==========================================================
     * レベル設定
     * ==========================================================
     */

    /**
     * 特殊パン解放レベル
     */
    private static final int SPECIAL_FOOD_LEVEL = 20;

    /**
     * パン効果アップグレード Lv.30
     */
    private static final int UPGRADE_LEVEL_30 = 30;

    /**
     * パン効果アップグレード Lv.40
     */
    private static final int UPGRADE_LEVEL_40 = 40;

    /**
     * Lv.50パッシブ
     */
    private static final int PASSIVE_LEVEL = 50;


    /*
     * ==========================================================
     * 特殊パンの満腹度
     * ==========================================================
     *
     * Lv.20～29 → +1
     * Lv.30～39 → +2
     * Lv.40～   → +5
     *
     * 完全固定。
     * ランダム要素なし。
     * ==========================================================
     */

    private static final int BREAD_STRENGTH_LEVEL_20 = 1;

    private static final int BREAD_STRENGTH_LEVEL_30 = 2;

    private static final int BREAD_STRENGTH_LEVEL_40 = 5;


    /*
     * ==========================================================
     * Lv.50パッシブ設定
     * ==========================================================
     */

    /**
     * 最大体力 +4
     *
     * 4.0 = ハート2個分
     */
    private static final double MAX_HEALTH_BONUS = 4.0D;

    /**
     * 満腹度 +4
     */
    private static final int FOOD_BONUS = 4;

    /**
     * 最大体力Modifierキー
     */
    private static final String MAX_HEALTH_MODIFIER_KEY =
            "farmer_lv50_health";


    /*
     * ==========================================================
     * フィールド
     * ==========================================================
     */

    private final JobManager jobManager;


    /*
     * ==========================================================
     * コンストラクタ
     * ==========================================================
     */

    public FoodBuffManager(
            JobManager jobManager
    ) {
        this.jobManager = jobManager;
    }


    /*
     * ==========================================================
     * 最大体力Modifier用NamespacedKey
     * ==========================================================
     */

    private NamespacedKey getMaxHealthModifierKey() {

        JavaPlugin plugin =
                JavaPlugin.getProvidingPlugin(
                        FoodBuffManager.class
                );

        return new NamespacedKey(
                plugin,
                MAX_HEALTH_MODIFIER_KEY
        );
    }


    /*
     * ==========================================================
     * 特殊食料対象判定
     * ==========================================================
     *
     * 今回の特殊食料は「パンだけ」。
     *
     * 金のニンジン・金のリンゴは対象外。
     * ==========================================================
     */

    public boolean isSpecialFood(
            Material material
    ) {

        return material == Material.BREAD;
    }


    /*
     * ==========================================================
     * 特殊食料判定
     * ==========================================================
     *
     * ItemStackに特殊パン用PDCが存在するか確認。
     * ==========================================================
     */

    public boolean isSpecialFood(
            ItemStack item
    ) {

        if (item == null
                || item.getType().isAir()) {

            return false;
        }

        /*
         * パン以外は特殊食料ではない
         */

        if (item.getType() != Material.BREAD) {
            return false;
        }

        /*
         * ItemMeta取得
         */

        ItemMeta meta =
                item.getItemMeta();

        if (meta == null) {
            return false;
        }

        /*
         * PDC取得
         */

        String buffType =
                meta.getPersistentDataContainer().get(
                        FoodBuffKeys.BUFF_TYPE,
                        FoodBuffKeys.STRING
                );

        Integer buffStrength =
                meta.getPersistentDataContainer().get(
                        FoodBuffKeys.BUFF_STRENGTH,
                        FoodBuffKeys.INTEGER
                );

        /*
         * 両方存在していて、
         * 効果値が1以上なら特殊パン
         */

        return buffType != null
                && buffStrength != null
                && buffStrength > 0;
    }


    /*
     * ==========================================================
     * 特殊パン作成
     * ==========================================================
     */

    public ItemStack createSpecialFood(
            Player player,
            ItemStack original
    ) {

        /*
         * ======================================================
         * 安全確認
         * ======================================================
         */

        if (player == null) {
            return original;
        }

        if (original == null
                || original.getType().isAir()) {

            return original;
        }

        /*
         * ======================================================
         * パン以外は処理しない
         * ======================================================
         */

        if (original.getType() != Material.BREAD) {
            return original;
        }

        /*
         * ======================================================
         * ファーマーレベル取得
         * ======================================================
         */

        int level =
                jobManager.getCurrentJobLevel(
                        player.getUniqueId()
                );

        /*
         * ======================================================
         * Lv.20未満
         * ======================================================
         *
         * 特殊パンはまだ作れない。
         */

        if (level < SPECIAL_FOOD_LEVEL) {
            return original;
        }

        /*
         * ======================================================
         * パンの効果値取得
         * ======================================================
         */

        int strength =
                getBreadStrength(level);

        if (strength <= 0) {
            return original;
        }

        /*
         * ======================================================
         * ItemStackコピー
         * ======================================================
         */

        ItemStack result =
                original.clone();

        /*
         * ======================================================
         * ItemMeta取得
         * ======================================================
         */

        ItemMeta meta =
                result.getItemMeta();

        if (meta == null) {
            return original;
        }

        /*
         * ======================================================
         * 表示名
         * ======================================================
         */

        meta.setDisplayName(
                ChatColor.GOLD
                        + "✦ "
                        + ChatColor.WHITE
                        + "特殊パン"
        );

        /*
         * ======================================================
         * Lore
         * ======================================================
         */

        List<String> lore =
                new ArrayList<>();

        lore.add("");

        lore.add(
                ChatColor.GOLD
                        + "特殊効果"
        );

        lore.add(
                ChatColor.YELLOW
                        + "満腹度回復 +"
                        + strength
        );

        lore.add("");

        lore.add(
                ChatColor.GRAY
                        + "ファーマーLv."
                        + level
                        + "製造"
        );

        meta.setLore(lore);

        /*
         * ======================================================
         * PDC
         * ======================================================
         */

        meta.getPersistentDataContainer().set(
                FoodBuffKeys.BUFF_TYPE,
                FoodBuffKeys.STRING,
                FoodBuffType.SATURATION.name()
        );

        meta.getPersistentDataContainer().set(
                FoodBuffKeys.BUFF_STRENGTH,
                FoodBuffKeys.INTEGER,
                strength
        );

        /*
         * ======================================================
         * ItemMeta適用
         * ======================================================
         */

        result.setItemMeta(meta);

        /*
         * ======================================================
         * 重要
         * ======================================================
         *
         * スタック可能にするため、
         *
         * ・setAmount(1)しない
         * ・setMaxStackSize(1)しない
         *
         * 元のクラフト結果の個数をそのまま維持する。
         *
         * さらに同じレベルなら
         * strength / Lore / PDCが同じになる。
         *
         * そのためスタック可能。
         * ======================================================
         */

        return result;
    }


    /*
     * ==========================================================
     * パン効果値
     * ==========================================================
     *
     * Lv.20～29 → +1
     * Lv.30～39 → +2
     * Lv.40～   → +5
     * ==========================================================
     */

    private int getBreadStrength(
            int level
    ) {

        if (level >= UPGRADE_LEVEL_40) {
            return BREAD_STRENGTH_LEVEL_40;
        }

        if (level >= UPGRADE_LEVEL_30) {
            return BREAD_STRENGTH_LEVEL_30;
        }

        if (level >= SPECIAL_FOOD_LEVEL) {
            return BREAD_STRENGTH_LEVEL_20;
        }

        return 0;
    }


    /*
     * ==========================================================
     * 効果適用
     * ==========================================================
     */

    public void applyBuff(
            Player player,
            ItemStack food
    ) {

        if (player == null) {
            return;
        }

        if (food == null
                || food.getType().isAir()) {

            return;
        }

        /*
         * ======================================================
         * 特殊パン確認
         * ======================================================
         */

        if (!isSpecialFood(food)) {
            return;
        }

        /*
         * ======================================================
         * ItemMeta
         * ======================================================
         */

        ItemMeta meta =
                food.getItemMeta();

        if (meta == null) {
            return;
        }

        /*
         * ======================================================
         * PDC取得
         * ======================================================
         */

        String type =
                meta.getPersistentDataContainer().get(
                        FoodBuffKeys.BUFF_TYPE,
                        FoodBuffKeys.STRING
                );

        Integer strength =
                meta.getPersistentDataContainer().get(
                        FoodBuffKeys.BUFF_STRENGTH,
                        FoodBuffKeys.INTEGER
                );

        /*
         * ======================================================
         * 安全確認
         * ======================================================
         */

        if (type == null
                || strength == null
                || strength <= 0) {

            return;
        }

        /*
         * ======================================================
         * 効果種類確認
         * ======================================================
         */

        FoodBuffType buffType;

        try {

            buffType =
                    FoodBuffType.valueOf(type);

        } catch (IllegalArgumentException e) {

            return;
        }

        /*
         * ======================================================
         * パン効果
         * ======================================================
         */

        if (buffType != FoodBuffType.SATURATION) {
            return;
        }

        /*
         * ======================================================
         * 満腹度計算
         * ======================================================
         */

        int before =
                player.getFoodLevel();

        int after =
                Math.min(
                        20,
                        before + strength
                );

        int actual =
                after - before;

        /*
         * ======================================================
         * 満腹度適用
         * ======================================================
         */

        if (actual > 0) {

            player.setFoodLevel(
                    after
            );
        }

        /*
         * ======================================================
         * メッセージ
         * ======================================================
         */

        player.sendMessage(
                ChatColor.GREEN
                        + "✦ 特殊効果発動！ "
                        + ChatColor.YELLOW
                        + "満腹度 +"
                        + actual
        );
    }


    /*
     * ==========================================================
     * Lv.50パッシブ更新
     * ==========================================================
     *
     * ログイン時などに使用。
     *
     * 最大体力だけを適用する。
     * ==========================================================
     */

    public void applyLevel50Passive(
            Player player
    ) {

        if (player == null) {
            return;
        }

        JobType jobType =
                jobManager.getJob(
                        player.getUniqueId()
                );

        /*
         * FARMER以外
         */

        if (jobType != JobType.FARMER) {

            removeLevel50Passive(player);

            return;
        }

        /*
         * レベル確認
         */

        int level =
                jobManager.getCurrentJobLevel(
                        player.getUniqueId()
                );

        if (level < PASSIVE_LEVEL) {

            removeLevel50Passive(player);

            return;
        }

        /*
         * 最大体力Modifier適用
         */

        applyMaxHealthModifier(player);
    }


    /*
     * ==========================================================
     * Lv.50到達時パッシブ
     * ==========================================================
     *
     * 最大体力 +4
     * 満腹度 +4
     *
     * このメソッドはLv.50到達時など、
     * 一度だけ発動させる用途。
     * ==========================================================
     */

    public void applyLevel50PassiveWithFoodBonus(
            Player player
    ) {

        if (player == null) {
            return;
        }

        /*
         * 職業確認
         */

        JobType jobType =
                jobManager.getJob(
                        player.getUniqueId()
                );

        if (jobType != JobType.FARMER) {
            return;
        }

        /*
         * レベル確認
         */

        int level =
                jobManager.getCurrentJobLevel(
                        player.getUniqueId()
                );

        if (level < PASSIVE_LEVEL) {
            return;
        }

        /*
         * ======================================================
         * 最大体力 +4
         * ======================================================
         */

        applyMaxHealthModifier(player);

        /*
         * ======================================================
         * 満腹度 +4
         * ======================================================
         */

        int before =
                player.getFoodLevel();

        int after =
                Math.min(
                        20,
                        before + FOOD_BONUS
                );

        player.setFoodLevel(after);

        /*
         * ======================================================
         * メッセージ
         * ======================================================
         */

        player.sendMessage(
                ChatColor.GOLD
                        + "✦ 農民Lv.50パッシブ発動！"
        );

        player.sendMessage(
                ChatColor.RED
                        + "❤ 最大体力 +"
                        + formatNumber(
                        MAX_HEALTH_BONUS
                )
        );

        player.sendMessage(
                ChatColor.YELLOW
                        + "🍖 満腹度 +"
                        + FOOD_BONUS
        );
    }


    /*
     * ==========================================================
     * 最大体力Modifier適用
     * ==========================================================
     */

    private void applyMaxHealthModifier(
            Player player
    ) {

        AttributeInstance maxHealth =
                player.getAttribute(
                        Attribute.MAX_HEALTH
                );

        if (maxHealth == null) {
            return;
        }

        NamespacedKey modifierKey =
                getMaxHealthModifierKey();

        /*
         * ======================================================
         * 既に存在するか確認
         * ======================================================
         */

        for (AttributeModifier modifier :
                maxHealth.getModifiers()) {

            if (modifier.getKey().equals(
                    modifierKey
            )) {

                return;
            }
        }

        /*
         * ======================================================
         * Modifier作成
         * ======================================================
         */

        AttributeModifier modifier =
                new AttributeModifier(
                        modifierKey,
                        MAX_HEALTH_BONUS,
                        AttributeModifier.Operation.ADD_NUMBER
                );

        /*
         * ======================================================
         * 適用
         * ======================================================
         */

        maxHealth.addModifier(
                modifier
        );
    }


    /*
     * ==========================================================
     * Lv.50パッシブ削除
     * ==========================================================
     */

    public void removeLevel50Passive(
            Player player
    ) {

        if (player == null) {
            return;
        }

        AttributeInstance maxHealth =
                player.getAttribute(
                        Attribute.MAX_HEALTH
                );

        if (maxHealth == null) {
            return;
        }

        NamespacedKey modifierKey =
                getMaxHealthModifierKey();

        AttributeModifier target =
                null;

        /*
         * ======================================================
         * 対象Modifier検索
         * ======================================================
         */

        for (AttributeModifier modifier :
                maxHealth.getModifiers()) {

            if (modifier.getKey().equals(
                    modifierKey
            )) {

                target = modifier;

                break;
            }
        }

        /*
         * ======================================================
         * 削除
         * ======================================================
         */

        if (target != null) {

            maxHealth.removeModifier(
                    target
            );
        }

        /*
         * ======================================================
         * 現在HPが最大HPを超えている場合
         * ======================================================
         */

        double currentHealth =
                player.getHealth();

        double max =
                maxHealth.getValue();

        if (currentHealth > max) {

            player.setHealth(max);
        }
    }


    /*
     * ==========================================================
     * Lv.50パッシブ所持確認
     * ==========================================================
     */

    public boolean hasLevel50Passive(
            Player player
    ) {

        if (player == null) {
            return false;
        }

        /*
         * FARMER確認
         */

        if (jobManager.getJob(
                player.getUniqueId()
        ) != JobType.FARMER) {

            return false;
        }

        /*
         * Lv.50確認
         */

        return jobManager.getCurrentJobLevel(
                player.getUniqueId()
        ) >= PASSIVE_LEVEL;
    }


    /*
     * ==========================================================
     * 効果種類
     * ==========================================================
     */

    public enum FoodBuffType {

        SATURATION
    }


    /*
     * ==========================================================
     * 数値表示
     * ==========================================================
     */

    private String formatNumber(
            double value
    ) {

        if (value == Math.floor(value)) {

            return String.format(
                    "%.0f",
                    value
            );
        }

        return String.format(
                "%.2f",
                value
        );
    }
}