package io.github.ilongake.ribboneconomy.listener;

import io.github.ilongake.ribboneconomy.core.DataManager;
import io.github.ilongake.ribboneconomy.core.PlayerData;
import io.github.ilongake.ribboneconomy.job.JobType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class MobKillListener implements Listener {

    private final DataManager dataManager;

    public MobKillListener(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @EventHandler
    public void onMobKill(EntityDeathEvent event) {

        // Mobを倒したプレイヤーを取得
        Player player = event.getEntity().getKiller();

        // プレイヤーが倒していなければ終了
        if (player == null) {
            return;
        }

        // プレイヤーデータを取得
        PlayerData data = dataManager.getPlayerData(
                player.getUniqueId()
        );

        // データが存在しなければ終了
        if (data == null) {
            return;
        }

        // ハンター職か確認
        if (data.getJobType() != JobType.HUNTER) {
            return;
        }

        // 倒したMobの種類を取得
        EntityType entityType = event.getEntity().getType();

        // Mobごとの報酬
        double reward;

        switch (entityType) {

            case ZOMBIE:
                reward = 5;
                break;

            case SKELETON:
                reward = 7;
                break;

            case CREEPER:
                reward = 10;
                break;

            case SPIDER:
                reward = 5;
                break;

            case ENDERMAN:
                reward = 20;
                break;

            case BLAZE:
                reward = 25;
                break;

            case WITHER_SKELETON:
                reward = 30;
                break;

            case ENDER_DRAGON:
                reward = 1000;
                break;

            default:
                // 設定されていないMobは報酬なし
                return;
        }

        // お金を追加
        dataManager.addBalance(
                player.getUniqueId(),
                reward
        );

        // メッセージ表示
        player.sendMessage(
                "§a[ハンター] §f"
                        + getMobName(entityType)
                        + "を倒した！ §6+"
                        + reward
                        + "円"
        );
    }

    /**
     * Mobの表示名を取得
     */
    private String getMobName(EntityType entityType) {

        switch (entityType) {

            case ZOMBIE:
                return "ゾンビ";

            case SKELETON:
                return "スケルトン";

            case CREEPER:
                return "クリーパー";

            case SPIDER:
                return "クモ";

            case ENDERMAN:
                return "エンダーマン";

            case BLAZE:
                return "ブレイズ";

            case WITHER_SKELETON:
                return "ウィザースケルトン";

            case ENDER_DRAGON:
                return "エンダードラゴン";

            default:
                return "Mob";
        }
    }
}

