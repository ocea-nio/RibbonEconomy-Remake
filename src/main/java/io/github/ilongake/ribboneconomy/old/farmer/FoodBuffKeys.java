package io.github.ilongake.ribboneconomy.farmer;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public final class FoodBuffKeys {

    private FoodBuffKeys() {
    }

    /*
     * ==========================================================
     * 特殊食料の効果種類
     * ==========================================================
     */

    public static NamespacedKey BUFF_TYPE;


    /*
     * ==========================================================
     * 特殊食料の効果強度
     * ==========================================================
     */

    public static NamespacedKey BUFF_STRENGTH;


    /*
     * ==========================================================
     * PersistentDataType
     * ==========================================================
     */

    public static final PersistentDataType<String, String> STRING =
            PersistentDataType.STRING;

    public static final PersistentDataType<Integer, Integer> INTEGER =
            PersistentDataType.INTEGER;


    /*
     * ==========================================================
     * 初期化
     * ==========================================================
     */

    public static void initialize(
            JavaPlugin plugin
    ) {

        BUFF_TYPE =
                new NamespacedKey(
                        plugin,
                        "food_buff_type"
                );

        BUFF_STRENGTH =
                new NamespacedKey(
                        plugin,
                        "food_buff_strength"
                );
    }
}