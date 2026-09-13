package io.github.ilongake.ribboneconomy.job;

import io.github.ilongake.ribboneconomy.core.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class JobAdminCommand implements CommandExecutor {

    private final JobManager jobManager;
    private final DataManager dataManager;

    public JobAdminCommand(
            JobManager jobManager,
            DataManager dataManager
    ) {

        this.jobManager = jobManager;
        this.dataManager = dataManager;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        // =========================
        // OPチェック
        // =========================

        if (!sender.isOp()) {

            sender.sendMessage(
                    ChatColor.RED
                            + "このコマンドはOP専用です。"
            );

            return true;
        }

        // =========================
        // 引数チェック
        // =========================

        if (args.length == 0) {

            sendUsage(sender);

            return true;
        }

        // =========================
        // level
        // =========================

        if (args[0].equalsIgnoreCase("level")) {

            handleLevel(
                    sender,
                    args
            );

            return true;
        }

        // =========================
        // 不明なサブコマンド
        // =========================

        sender.sendMessage(
                ChatColor.RED
                        + "不明なサブコマンドです。"
        );

        sendUsage(sender);

        return true;
    }

    /**
     * レベル変更処理
     */
    private void handleLevel(
            CommandSender sender,
            String[] args
    ) {

        // /jobadmin level <player> <job> <level>

        if (args.length != 4) {

            sender.sendMessage(
                    ChatColor.RED
                            + "使い方: "
                            + "/jobadmin level "
                            + "<プレイヤー> "
                            + "<職業> "
                            + "<レベル>"
            );

            return;
        }

        String playerName =
                args[1];

        String jobName =
                args[2];

        int level;

        // =========================
        // レベルを数値に変換
        // =========================

        try {

            level =
                    Integer.parseInt(
                            args[3]
                    );

        } catch (NumberFormatException e) {

            sender.sendMessage(
                    ChatColor.RED
                            + "レベルは数字で指定してください。"
            );

            return;
        }

        // =========================
        // レベル範囲
        // =========================

        if (level < 1 || level > 50) {

            sender.sendMessage(
                    ChatColor.RED
                            + "レベルは1～50の範囲で指定してください。"
            );

            return;
        }

        // =========================
        // 職業変換
        // =========================

        JobType jobType;

        try {

            jobType =
                    JobType.valueOf(
                            jobName.toUpperCase()
                    );

        } catch (IllegalArgumentException e) {

            sender.sendMessage(
                    ChatColor.RED
                            + "存在しない職業です。"
            );

            sendJobList(sender);

            return;
        }

        // =========================
        // NONE禁止
        // =========================

        if (jobType == JobType.NONE) {

            sender.sendMessage(
                    ChatColor.RED
                            + "NONEを指定することはできません。"
            );

            return;
        }

        // =========================
        // プレイヤー取得
        // =========================

        OfflinePlayer target =
                Bukkit.getOfflinePlayer(
                        playerName
                );

        // =========================
        // データ存在確認
        // =========================

        if (!target.hasPlayedBefore()
                && !target.isOnline()) {

            sender.sendMessage(
                    ChatColor.RED
                            + "そのプレイヤーのデータが見つかりません。"
            );

            return;
        }

        // =========================
        // UUID
        // =========================

        var uuid =
                target.getUniqueId();

        // =========================
        // PlayerData存在確認
        // =========================

        if (dataManager.getPlayerData(uuid) == null) {

            sender.sendMessage(
                    ChatColor.RED
                            + "プレイヤーデータが見つかりません。"
            );

            return;
        }

        // =========================
        // 変更前レベル
        // =========================

        int oldLevel =
                jobManager.getJobLevel(
                        uuid,
                        jobType
                );

        // =========================
        // レベル変更
        // =========================

        boolean success =
                jobManager.setJobLevel(
                        uuid,
                        jobType,
                        level
                );

        if (!success) {

            sender.sendMessage(
                    ChatColor.RED
                            + "職業レベルの変更に失敗しました。"
            );

            return;
        }

        // =========================
        // 成功メッセージ
        // =========================

        sender.sendMessage("");

        sender.sendMessage(
                ChatColor.GOLD
                        + "===== 職業レベル変更 ====="
        );

        sender.sendMessage(
                ChatColor.YELLOW
                        + "プレイヤー: "
                        + ChatColor.WHITE
                        + target.getName()
        );

        sender.sendMessage(
                ChatColor.YELLOW
                        + "職業: "
                        + ChatColor.WHITE
                        + jobType.name()
        );

        sender.sendMessage(
                ChatColor.YELLOW
                        + "レベル: "
                        + ChatColor.RED
                        + oldLevel
                        + ChatColor.GRAY
                        + " → "
                        + ChatColor.GREEN
                        + level
        );

        sender.sendMessage("");

        // =========================
        // 対象プレイヤーにも通知
        // =========================

        if (target.isOnline()) {

            target.getPlayer().sendMessage("");

            target.getPlayer().sendMessage(
                    ChatColor.GOLD
                            + "§l職業レベルが変更されました！"
            );

            target.getPlayer().sendMessage(
                    ChatColor.YELLOW
                            + "職業: "
                            + ChatColor.WHITE
                            + jobType.name()
            );

            target.getPlayer().sendMessage(
                    ChatColor.YELLOW
                            + "レベル: "
                            + ChatColor.RED
                            + oldLevel
                            + ChatColor.GRAY
                            + " → "
                            + ChatColor.GREEN
                            + level
            );

            target.getPlayer().sendMessage("");
        }
    }

    /**
     * 使用方法
     */
    private void sendUsage(
            CommandSender sender
    ) {

        sender.sendMessage("");

        sender.sendMessage(
                ChatColor.GOLD
                        + "===== JobAdmin ====="
        );

        sender.sendMessage(
                ChatColor.YELLOW
                        + "/jobadmin level "
                        + "<プレイヤー> "
                        + "<職業> "
                        + "<レベル>"
        );

        sender.sendMessage("");

        sendJobList(sender);
    }

    /**
     * 職業一覧
     */
    private void sendJobList(
            CommandSender sender
    ) {

        sender.sendMessage(
                ChatColor.GRAY
                        + "職業:"
        );

        sender.sendMessage(
                ChatColor.WHITE
                        + "FARMER"
        );

        sender.sendMessage(
                ChatColor.WHITE
                        + "MINER"
        );

        sender.sendMessage(
                ChatColor.WHITE
                        + "HUNTER"
        );

        sender.sendMessage(
                ChatColor.WHITE
                        + "LUMBERJACK"
        );
    }
}