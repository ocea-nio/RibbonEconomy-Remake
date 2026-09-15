package io.github.ilongake.ribboneconomy.feature.money;

import io.github.ilongake.ribboneconomy.core.DataManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExchangeCommand implements CommandExecutor {
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

}