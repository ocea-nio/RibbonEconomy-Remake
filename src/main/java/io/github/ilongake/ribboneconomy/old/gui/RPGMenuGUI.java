package io.github.ilongake.ribboneconomy.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RPGMenuGUI {

    /**
     * RPGメインメニューを開く
     */
    public static void open(org.bukkit.entity.Player player) {

        Inventory inventory =
                Bukkit.createInventory(
                        null,
                        27,
                        ChatColor.DARK_PURPLE
                                + "RPGメニュー"
                );

        // お金
        inventory.setItem(
                10,
                createItem(
                        Material.GOLD_INGOT,
                        ChatColor.GOLD
                                + "お金"
                )
        );

        // エメラルド交換
        inventory.setItem(
                12,
                createItem(
                        Material.EMERALD,
                        ChatColor.GREEN
                                + "エメラルド交換"
                )
        );

        // 依頼
        inventory.setItem(
                14,
                createItem(
                        Material.BOOK,
                        ChatColor.YELLOW
                                + "依頼"
                )
        );

        // 職業
        inventory.setItem(
                16,
                createItem(
                        Material.IRON_PICKAXE,
                        ChatColor.AQUA
                                + "職業"
                )
        );

        player.openInventory(inventory);
    }

    /**
     * GUIアイテム作成
     */
    private static ItemStack createItem(
            Material material,
            String name
    ) {

        ItemStack item =
                new ItemStack(material);

        ItemMeta meta =
                item.getItemMeta();

        if (meta != null) {

            meta.setDisplayName(name);

            item.setItemMeta(meta);
        }

        return item;
    }
}