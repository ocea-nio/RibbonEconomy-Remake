package io.github.ilongake.ribboneconomy.gui;

import io.github.ilongake.ribboneconomy.quest.Quest;
import io.github.ilongake.ribboneconomy.quest.QuestManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class QuestCreateListener implements Listener {

    private final QuestManager questManager;
    private final JavaPlugin plugin;

    public QuestCreateListener(
            QuestManager questManager,
            JavaPlugin plugin
    ) {
        this.questManager = questManager;
        this.plugin = plugin;
    }

    /*
     * プレイヤーごとの依頼作成状態
     */
    private final Map<UUID, CreateState> creating =
            new HashMap<>();


    /**
     * 依頼作成を開始
     */
    public void startCreate(Player player) {

        creating.put(
                player.getUniqueId(),
                new CreateState()
        );

        QuestCreateGUI.open(
                player
        );
    }


    /**
     * 依頼作成GUIのクリック処理
     */
    @EventHandler
    public void onInventoryClick(
            InventoryClickEvent event
    ) {

        if (!(event.getWhoClicked()
                instanceof Player player)) {

            return;
        }

        /*
         * 依頼作成GUI以外は無視
         */
        if (!event.getView()
                .getTitle()
                .equals(
                        ChatColor.DARK_GREEN
                                + "依頼を作成"
                )) {

            return;
        }

        /*
         * GUIアイテムを持てなくする
         */
        event.setCancelled(true);

        /*
         * アイテムがない
         */
        if (event.getCurrentItem() == null) {

            return;
        }

        /*
         * 作成状態を取得
         */
        UUID uuid =
                player.getUniqueId();

        CreateState state =
                creating.get(uuid);

        /*
         * まだ作成状態がない場合
         */
        if (state == null) {

            state =
                    new CreateState();

            creating.put(
                    uuid,
                    state
            );
        }


        /*
         * クリックしたアイテム
         */
        switch (
                event.getCurrentItem()
                        .getType()
        ) {

            /*
             * =========================
             * タイトル
             * =========================
             */

            case NAME_TAG:

                player.closeInventory();

                state.inputType =
                        InputType.TITLE;

                player.sendMessage(
                        ChatColor.YELLOW
                                + "依頼のタイトルを入力してください。"
                );

                break;


            /*
             * =========================
             * 内容
             * =========================
             */

            case WRITABLE_BOOK:

                player.closeInventory();

                state.inputType =
                        InputType.DESCRIPTION;

                player.sendMessage(
                        ChatColor.AQUA
                                + "依頼内容を入力してください。"
                );

                break;


            /*
             * =========================
             * 報酬
             * =========================
             */

            case EMERALD:

                player.closeInventory();

                state.inputType =
                        InputType.REWARD;

                player.sendMessage(
                        ChatColor.GREEN
                                + "報酬金額を数字で入力してください。"
                );

                break;


            /*
             * =========================
             * 依頼公開
             * =========================
             */

            case EMERALD_BLOCK:

                /*
                 * 3つすべて設定されているか確認
                 */
                if (state.title == null
                        || state.description == null
                        || state.reward == null) {

                    player.sendMessage(
                            ChatColor.RED
                                    + "タイトル・内容・報酬をすべて設定してください。"
                    );

                    return;
                }

                /*
                 * 依頼作成
                 */
                Quest quest =
                        questManager.createQuest(
                                uuid,
                                state.title,
                                state.description,
                                state.reward
                        );

                /*
                 * 作成失敗
                 */
                if (quest == null) {

                    player.sendMessage(
                            ChatColor.RED
                                    + "依頼を作成できませんでした。"
                    );

                    player.sendMessage(
                            ChatColor.RED
                                    + "所持金が足りない可能性があります。"
                    );

                    return;
                }

                /*
                 * 作成状態を削除
                 */
                creating.remove(
                        uuid
                );

                player.closeInventory();

                player.sendMessage(
                        ChatColor.GREEN
                                + "依頼を作成しました！"
                );

                player.sendMessage(
                        ChatColor.YELLOW
                                + "タイトル: "
                                + quest.getTitle()
                );

                player.sendMessage(
                        ChatColor.WHITE
                                + "内容: "
                                + quest.getDescription()
                );

                player.sendMessage(
                        ChatColor.GOLD
                                + "報酬: "
                                + quest.getReward()
                                + "円"
                );

                player.sendMessage(
                        ChatColor.GREEN
                                + "報酬は依頼が承認されるまでロックされています。"
                );

                break;


            /*
             * =========================
             * 戻る
             * =========================
             */

            case BARRIER:

                creating.remove(
                        uuid
                );

                player.closeInventory();

                QuestGUI.open(
                        player
                );

                break;

            default:

                break;
        }
    }


    /**
     * チャット入力処理
     */
    @EventHandler
    public void onChat(
            AsyncPlayerChatEvent event
    ) {

        Player player =
                event.getPlayer();

        UUID uuid =
                player.getUniqueId();

        CreateState state =
                creating.get(uuid);

        /*
         * 依頼作成中ではない
         */
        if (state == null) {

            return;
        }

        /*
         * 入力待ちではない
         */
        if (state.inputType
                == InputType.NONE) {

            return;
        }

        /*
         * 通常チャットをキャンセル
         */
        event.setCancelled(true);

        String message =
                event.getMessage();


        /*
         * =========================
         * タイトル
         * =========================
         */

        if (state.inputType
                == InputType.TITLE) {

            if (message.isBlank()) {

                player.sendMessage(
                        ChatColor.RED
                                + "タイトルを入力してください。"
                );

                return;
            }

            state.title =
                    message;

            state.inputType =
                    InputType.NONE;

            player.sendMessage(
                    ChatColor.GREEN
                            + "タイトルを設定しました！"
            );

            reopenGUI(
                    player,
                    state
            );

            return;
        }


        /*
         * =========================
         * 内容
         * =========================
         */

        if (state.inputType
                == InputType.DESCRIPTION) {

            if (message.isBlank()) {

                player.sendMessage(
                        ChatColor.RED
                                + "依頼内容を入力してください。"
                );

                return;
            }

            state.description =
                    message;

            state.inputType =
                    InputType.NONE;

            player.sendMessage(
                    ChatColor.GREEN
                            + "依頼内容を設定しました！"
            );

            reopenGUI(
                    player,
                    state
            );

            return;
        }


        /*
         * =========================
         * 報酬
         * =========================
         */

        if (state.inputType
                == InputType.REWARD) {

            double reward;

            try {

                reward =
                        Double.parseDouble(
                                message
                        );

            } catch (
                    NumberFormatException e
            ) {

                player.sendMessage(
                        ChatColor.RED
                                + "報酬額は数字で入力してください。"
                );

                return;
            }

            if (reward <= 0) {

                player.sendMessage(
                        ChatColor.RED
                                + "報酬額は0より大きくしてください。"
                );

                return;
            }

            state.reward =
                    reward;

            state.inputType =
                    InputType.NONE;

            player.sendMessage(
                    ChatColor.GREEN
                            + "報酬金額を設定しました！"
            );

            reopenGUI(
                    player,
                    state
            );
        }
    }


    /**
     * 入力後にGUIを再表示
     */
    private void reopenGUI(
            Player player,
            CreateState state
    ) {

        player.getServer()
                .getScheduler()
                .runTask(
                        plugin,
                        () ->
                                QuestCreateGUI.open(
                                        player,
                                        state.title,
                                        state.description,
                                        state.reward
                                )
                );
    }


    /**
     * 入力種類
     */
    private enum InputType {

        NONE,

        TITLE,

        DESCRIPTION,

        REWARD
    }


    /**
     * 依頼作成状態
     */
    private static class CreateState {

        private String title;

        private String description;

        private Double reward;

        private InputType inputType =
                InputType.NONE;
    }
}