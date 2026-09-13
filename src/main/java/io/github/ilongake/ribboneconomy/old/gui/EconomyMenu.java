package io.github.ilongake.ribboneconomy.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class EconomyMenu {

    public static final String TITLE = "§2経済メニュー";

    public static void open(Player player) {

        Inventory inventory = Bukkit.createInventory(
                null,
                27,
                TITLE
        );

        // 職業
        inventory.setItem(
                10,
                createItem(
                        Material.IRON_PICKAXE,
                        "§e職業",
                        "§7職業を確認・変更します"
                )
        );

        // エメラルド交換
        inventory.setItem(
                12,
                createItem(
                        Material.EMERALD,
                        "§aエメラルド交換",
                        "§7円とエメラルドを交換します"
                )
        );

        // 依頼
        inventory.setItem(
                14,
                createItem(
                        Material.PAPER,
                        "§b依頼",
                        "§7依頼を受けて報酬を獲得します"
                )
        );

        // ショップ
        inventory.setItem(
                16,
                createItem(
                        Material.CHEST,
                        "§6ショップ",
                        "§7アイテムを売買します"
                )
        );

        player.openInventory(inventory);
    }

    private static ItemStack createItem(
            Material material,
            String name,
            String lore
    ) {

        ItemStack item = new ItemStack(material);

        ItemMeta meta = item.getItemMeta();

        if (meta != null) {

            meta.setDisplayName(name);

            meta.setLore(
                    List.of(
                            lore,
                            "",
                            "§eクリックして開く"
                    )
            );

            item.setItemMeta(meta);
        }

        return item;
    }
}