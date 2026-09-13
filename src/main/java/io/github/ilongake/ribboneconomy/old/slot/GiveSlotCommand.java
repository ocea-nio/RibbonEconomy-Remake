package io.github.ilongake.ribboneconomy.slot;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * /giveslot [プレイヤー名] : スロットマシン看板を配布する
 *
 * plugin.yml に以下を追加してください:
 *
 * commands:
 *   giveslot:
 *     description: スロットマシン看板を配布します
 *     usage: /giveslot [player]
 *     permission: slotmachine.give
 *
 * permissions:
 *   slotmachine.give:
 *     description: スロットマシン看板を配布する権限
 *     default: op
 */
public class GiveSlotCommand implements CommandExecutor {

    private final SlotMachineListener slotMachineListener;

    public GiveSlotCommand(SlotMachineListener slotMachineListener) {
        this.slotMachineListener = slotMachineListener;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("slotmachine.give")) {
            sender.sendMessage(ChatColor.RED + "権限がありません。");
            return true;
        }

        Player target;
        if (args.length >= 1) {
            Server server = sender.getServer();
            target = server.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "プレイヤーが見つかりません: " + args[0]);
                return true;
            }
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            sender.sendMessage(ChatColor.RED + "コンソールから実行する場合はプレイヤー名を指定してください。");
            return true;
        }

        ItemStack item = slotMachineListener.createSlotSignItem();
        target.getInventory().addItem(item);
        sender.sendMessage(ChatColor.GOLD + "スロットマシン看板を " + target.getName() + " に付与しました。");
        return true;
    }
}