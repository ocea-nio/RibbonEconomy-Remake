package io.github.ilongake.ribboneconomy.feature.quest;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class QuestCommand implements CommandExecutor {

    private final QuestManager questManager;

    public QuestCommand(
            QuestManager questManager
    ) {
        this.questManager = questManager;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        // プレイヤー専用
        if (!(sender instanceof Player player)) {

            sender.sendMessage(
                    ChatColor.RED
                            + "このコマンドはプレイヤー専用です。"
            );

            return true;
        }

        // 引数なし
        if (args.length == 0) {

            sendHelp(player);

            return true;
        }

        String subCommand =
                args[0].toLowerCase();

        switch (subCommand) {

            case "create":
                createQuest(player, args);
                break;

            case "list":
                listQuests(player);
                break;

            case "my":
                myQuests(player);
                break;

            case "created":
                createdQuests(player);
                break;

            case "accept":
                acceptQuest(player, args);
                break;

            case "complete":
                completeQuest(player, args);
                break;

            case "approve":
                approveQuest(player, args);
                break;

            case "cancel":
                cancelQuest(player, args);
                break;

            default:
                sendHelp(player);
                break;
        }

        return true;
    }


    /**
     * ヘルプ表示
     */
    private void sendHelp(
            Player player
    ) {

        player.sendMessage(
                ChatColor.GOLD
                        + "===== 依頼システム ====="
        );

        player.sendMessage(
                ChatColor.YELLOW
                        + "/quest create <タイトル> <報酬> <内容>"
        );

        player.sendMessage(
                ChatColor.YELLOW
                        + "/quest list"
        );

        player.sendMessage(
                ChatColor.YELLOW
                        + "/quest my"
        );

        player.sendMessage(
                ChatColor.YELLOW
                        + "/quest created"
        );

        player.sendMessage(
                ChatColor.YELLOW
                        + "/quest accept <ID>"
        );

        player.sendMessage(
                ChatColor.YELLOW
                        + "/quest complete <ID>"
        );

        player.sendMessage(
                ChatColor.YELLOW
                        + "/quest approve <ID>"
        );

        player.sendMessage(
                ChatColor.YELLOW
                        + "/quest cancel <ID>"
        );
    }


    /**
     * 依頼作成
     */
    private void createQuest(
            Player player,
            String[] args
    ) {

        // create タイトル 報酬 内容
        if (args.length < 4) {

            player.sendMessage(
                    ChatColor.RED
                            + "使用方法: "
                            + "/quest create <タイトル> <報酬> <内容>"
            );

            return;
        }

        String title = args[1];

        double reward;

        try {

            reward = Double.parseDouble(
                    args[2]
            );

        } catch (NumberFormatException e) {

            player.sendMessage(
                    ChatColor.RED
                            + "報酬金額は数字で入力してください。"
            );

            return;
        }

        // 依頼内容を結合
        StringBuilder description =
                new StringBuilder();

        for (int i = 3; i < args.length; i++) {

            if (i > 3) {

                description.append(" ");
            }

            description.append(
                    args[i]
            );
        }

        // 依頼作成
        Quest quest =
                questManager.createQuest(
                        player.getUniqueId(),
                        title,
                        description.toString(),
                        reward
                );

        // 作成失敗
        if (quest == null) {

            player.sendMessage(
                    ChatColor.RED
                            + "依頼を作成できませんでした。"
                            + "報酬が足りないか、金額が不正です。"
            );

            return;
        }

        player.sendMessage(
                ChatColor.GREEN
                        + "依頼を作成しました！"
        );

        player.sendMessage(
                ChatColor.GRAY
                        + "ID: "
                        + quest.getQuestId()
        );

        player.sendMessage(
                ChatColor.GRAY
                        + "タイトル: "
                        + quest.getTitle()
        );

        player.sendMessage(
                ChatColor.GRAY
                        + "報酬: "
                        + quest.getReward()
                        + "円"
        );

        player.sendMessage(
                ChatColor.GRAY
                        + "内容: "
                        + quest.getDescription()
        );
    }


    /**
     * 公開中の依頼一覧
     */
    private void listQuests(
            Player player
    ) {

        List<Quest> quests =
                questManager.getOpenQuests();

        if (quests.isEmpty()) {

            player.sendMessage(
                    ChatColor.YELLOW
                            + "現在公開中の依頼はありません。"
            );

            return;
        }

        player.sendMessage(
                ChatColor.GOLD
                        + "===== 公開中の依頼 ====="
        );

        for (Quest quest : quests) {

            showQuest(
                    player,
                    quest
            );
        }
    }


    /**
     * 自分が受注した依頼
     */
    private void myQuests(
            Player player
    ) {

        List<Quest> quests =
                questManager.getMyQuests(
                        player.getUniqueId()
                );

        if (quests.isEmpty()) {

            player.sendMessage(
                    ChatColor.YELLOW
                            + "現在受注している依頼はありません。"
            );

            return;
        }

        player.sendMessage(
                ChatColor.GOLD
                        + "===== 自分が受注した依頼 ====="
        );

        for (Quest quest : quests) {

            showQuest(
                    player,
                    quest
            );
        }
    }


    /**
     * 自分が作成した依頼
     */
    private void createdQuests(
            Player player
    ) {

        List<Quest> quests =
                questManager.getCreatedQuests(
                        player.getUniqueId()
                );

        if (quests.isEmpty()) {

            player.sendMessage(
                    ChatColor.YELLOW
                            + "自分が作成した依頼はありません。"
            );

            return;
        }

        player.sendMessage(
                ChatColor.GOLD
                        + "===== 自分が作成した依頼 ====="
        );

        for (Quest quest : quests) {

            showQuest(
                    player,
                    quest
            );
        }
    }


    /**
     * 依頼情報表示
     */
    private void showQuest(
            Player player,
            Quest quest
    ) {

        player.sendMessage(
                ChatColor.YELLOW
                        + "ID: "
                        + quest.getQuestId()
        );

        player.sendMessage(
                ChatColor.WHITE
                        + "タイトル: "
                        + quest.getTitle()
        );

        player.sendMessage(
                ChatColor.WHITE
                        + "内容: "
                        + quest.getDescription()
        );

        player.sendMessage(
                ChatColor.GREEN
                        + "報酬: "
                        + quest.getReward()
                        + "円"
        );

        player.sendMessage(
                ChatColor.AQUA
                        + "状態: "
                        + quest.getStatus()
        );

        player.sendMessage(
                ChatColor.GRAY
                        + "--------------------"
        );
    }


    /**
     * 依頼受注
     */
    private void acceptQuest(
            Player player,
            String[] args
    ) {

        if (args.length < 2) {

            player.sendMessage(
                    ChatColor.RED
                            + "使用方法: "
                            + "/quest accept <ID>"
            );

            return;
        }

        UUID questId;

        try {

            questId = UUID.fromString(
                    args[1]
            );

        } catch (IllegalArgumentException e) {

            player.sendMessage(
                    ChatColor.RED
                            + "正しい依頼IDを入力してください。"
            );

            return;
        }

        boolean success =
                questManager.acceptQuest(
                        questId,
                        player.getUniqueId()
                );

        if (!success) {

            player.sendMessage(
                    ChatColor.RED
                            + "依頼を受注できませんでした。"
            );

            return;
        }

        player.sendMessage(
                ChatColor.GREEN
                        + "依頼を受注しました！"
        );
    }


    /**
     * 完了報告
     */
    private void completeQuest(
            Player player,
            String[] args
    ) {

        if (args.length < 2) {

            player.sendMessage(
                    ChatColor.RED
                            + "使用方法: "
                            + "/quest complete <ID>"
            );

            return;
        }

        UUID questId;

        try {

            questId = UUID.fromString(
                    args[1]
            );

        } catch (IllegalArgumentException e) {

            player.sendMessage(
                    ChatColor.RED
                            + "正しい依頼IDを入力してください。"
            );

            return;
        }

        boolean success =
                questManager.completeQuest(
                        questId,
                        player.getUniqueId()
                );

        if (!success) {

            player.sendMessage(
                    ChatColor.RED
                            + "依頼の完了報告に失敗しました。"
            );

            return;
        }

        player.sendMessage(
                ChatColor.GREEN
                        + "依頼を完了報告しました！"
        );
    }


    /**
     * 依頼承認
     */
    private void approveQuest(
            Player player,
            String[] args
    ) {

        if (args.length < 2) {

            player.sendMessage(
                    ChatColor.RED
                            + "使用方法: "
                            + "/quest approve <ID>"
            );

            return;
        }

        UUID questId;

        try {

            questId = UUID.fromString(
                    args[1]
            );

        } catch (IllegalArgumentException e) {

            player.sendMessage(
                    ChatColor.RED
                            + "正しい依頼IDを入力してください。"
            );

            return;
        }

        boolean success =
                questManager.approveQuest(
                        questId,
                        player.getUniqueId()
                );

        if (!success) {

            player.sendMessage(
                    ChatColor.RED
                            + "依頼を承認できませんでした。"
            );

            return;
        }

        player.sendMessage(
                ChatColor.GREEN
                        + "依頼を承認しました！"
        );

        player.sendMessage(
                ChatColor.GREEN
                        + "受注者に報酬が支払われました。"
        );
    }


    /**
     * 依頼キャンセル
     */
    private void cancelQuest(
            Player player,
            String[] args
    ) {

        if (args.length < 2) {

            player.sendMessage(
                    ChatColor.RED
                            + "使用方法: "
                            + "/quest cancel <ID>"
            );

            return;
        }

        UUID questId;

        try {

            questId = UUID.fromString(
                    args[1]
            );

        } catch (IllegalArgumentException e) {

            player.sendMessage(
                    ChatColor.RED
                            + "正しい依頼IDを入力してください。"
            );

            return;
        }

        Quest quest =
                questManager.getQuest(questId);

        if (quest == null) {

            player.sendMessage(
                    ChatColor.RED
                            + "依頼が見つかりません。"
            );

            return;
        }

        double reward =
                quest.getReward();

        boolean success =
                questManager.cancelQuest(
                        questId,
                        player.getUniqueId()
                );

        if (!success) {

            player.sendMessage(
                    ChatColor.RED
                            + "依頼をキャンセルできませんでした。"
            );

            player.sendMessage(
                    ChatColor.GRAY
                            + "受注された依頼はキャンセルできません。"
            );

            return;
        }

        player.sendMessage(
                ChatColor.GREEN
                        + "依頼をキャンセルしました！"
        );

        player.sendMessage(
                ChatColor.GREEN
                        + String.valueOf(reward)
                        + "円が返金されました。"
        );
}}