package io.github.ilongake.ribboneconomy.feature.quest;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class QuestListGUI {

    /**
     * 公開中の依頼一覧を開く
     */
    public static void open(
            Player player,
            QuestManager questManager
    ) {

        List<Quest> quests =
                questManager.getOpenQuests();

        Inventory inventory =
                Bukkit.createInventory(
                        null,
                        54,
                        ChatColor.DARK_GREEN
                                + "公開中の依頼"
                );

        int slot = 0;

        for (Quest quest : quests) {

            if (slot >= 54) {
                break;
            }

            ItemStack item =
                    new ItemStack(
                            Material.PAPER
                    );

            ItemMeta meta =
                    item.getItemMeta();

            if (meta != null) {

                meta.setDisplayName(
                        ChatColor.YELLOW
                                + quest.getTitle()
                );

                meta.setLore(
                        List.of(
                                ChatColor.WHITE
                                        + "内容: "
                                        + quest.getDescription(),

                                ChatColor.GREEN
                                        + "報酬: "
                                        + quest.getReward()
                                        + "円",

                                ChatColor.GRAY
                                        + "依頼ID: "
                                        + quest.getQuestId(),

                                "",

                                ChatColor.AQUA
                                        + "クリックして受注"
                        ));
                NamespacedKey questKey =
                        new NamespacedKey(
                                player.getServer().getPluginManager().getPlugin("RPGEconomy"),
                                "quest_id"
                        );

                meta.getPersistentDataContainer().set(
                        questKey,
                        PersistentDataType.STRING,
                        quest.getQuestId().toString()
                );

                item.setItemMeta(meta);
            }

            inventory.setItem(
                    slot,
                    item
            );

            slot++;
        }

        if (quests.isEmpty()) {

            inventory.setItem(
                    22,
                    createItem(
                            Material.BARRIER,
                            ChatColor.RED
                                    + "公開中の依頼はありません"
                    )
            );
        }

        player.openInventory(inventory);
    }

    /**
     * アイテム作成
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