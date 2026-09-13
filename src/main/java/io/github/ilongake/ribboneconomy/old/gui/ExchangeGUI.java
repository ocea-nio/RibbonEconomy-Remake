package io.github.ilongake.ribboneconomy.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ExchangeGUI {

    public static final String TITLE = "§2エメラルド交換";

    public static void open(Player player) {

        Inventory inventory =
                Bukkit.createInventory(
                        null,
                        27,
                        TITLE
                );

        // 1個交換
        inventory.setItem(
                11,
                createItem(
                        Material.EMERALD,
                        "§aエメラルド ×1",
                        "§7必要金額: §f100円"
                )
        );

        // 10個交換
        inventory.setItem(
                13,
                createItem(
                        Material.EMERALD_BLOCK,
                        "§aエメラルド ×10",
                        "§7必要金額: §f1,000円"
                )
        );

        // 64個交換
        inventory.setItem(
                15,
                createItem(
                        Material.EMERALD,
                        "§aエメラルド ×64",
                        "§7必要金額: §f6,400円"
                )
        );

        player.openInventory(inventory);
    }

    private static ItemStack createItem(
            Material material,
            String name,
            String lore
    ) {

        ItemStack item =
                new ItemStack(material);

        ItemMeta meta =
                item.getItemMeta();

        if (meta != null) {

            meta.setDisplayName(name);

            meta.setLore(
                    java.util.List.of(
                            lore,
                            "",
                            "§eクリックして交換"
                    )
            );

            item.setItemMeta(meta);
        }

        return item;
    }
}