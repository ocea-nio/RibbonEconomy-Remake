package io.github.ilongake.ribboneconomy.command;

import io.github.ilongake.ribboneconomy.core.DataManager;
import io.github.ilongake.ribboneconomy.gui.ExchangeGUI;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ExchangeCommand implements CommandExecutor {

    private final DataManager dataManager;

    public ExchangeCommand(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        // プレイヤー以外は実行できない
        if (!(sender instanceof Player player)) {

            sender.sendMessage(
                    "このコマンドはプレイヤーのみ使用できます。"
            );

            return true;
        }

        // GUIを開く
        ExchangeGUI.open(player);

        return true;
    }

    /**
     * 円をエメラルドに交換する
     *
     * @param player 交換するプレイヤー
     * @param amount エメラルドの個数
     */
    public void exchange(
            Player player,
            int amount
    ) {

        // 0以下は禁止
        if (amount <= 0) {

            player.sendMessage(
                    "§c1個以上を指定してください！"
            );

            return;
        }

        // 交換レート
        double costPerEmerald = 100;

        // 必要な金額
        double totalCost =
                costPerEmerald * amount;

        // 所持金確認
        if (dataManager.getBalance(
                player.getUniqueId()
        ) < totalCost) {

            player.sendMessage(
                    "§cお金が足りません！"
            );

            player.sendMessage(
                    "§e必要な金額: "
                            + totalCost
                            + "円"
            );

            return;
        }

        // エメラルド
        ItemStack emeralds =
                new ItemStack(
                        Material.EMERALD,
                        amount
                );

        // インベントリに入るか事前確認
        if (!canFit(
                player.getInventory(),
                emeralds
        )) {

            player.sendMessage(
                    "§cインベントリに十分な空きがありません！"
            );

            return;
        }

        // お金を減らす
        dataManager.removeBalance(
                player.getUniqueId(),
                totalCost
        );

        // エメラルドを追加
        player.getInventory().addItem(
                emeralds
        );

        // 成功メッセージ
        player.sendMessage(
                "§a"
                        + totalCost
                        + "円を支払い、"
                        + "エメラルドを"
                        + amount
                        + "個受け取りました！"
        );
    }

    /**
     * アイテムがインベントリに全部入るか確認
     */
    private boolean canFit(
            PlayerInventory inventory,
            ItemStack item
    ) {

        int remaining =
                item.getAmount();

        // インベントリ内を確認
        for (ItemStack slot :
                inventory.getStorageContents()) {

            // 空きスロット
            if (slot == null) {

                remaining -=
                        item.getMaxStackSize();

            }

            // 同じアイテムでスタック可能
            else if (
                    slot.isSimilar(item)
            ) {

                int space =
                        slot.getMaxStackSize()
                                - slot.getAmount();

                remaining -= space;
            }

            // 全部入るならOK
            if (remaining <= 0) {

                return true;
            }
        }

        // 入りきらない
        return false;
    }
}