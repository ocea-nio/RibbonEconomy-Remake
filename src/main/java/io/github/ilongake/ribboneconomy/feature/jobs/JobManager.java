package io.github.ilongake.ribboneconomy.feature.jobs;

import io.github.ilongake.ribboneconomy.core.DataManager;
import io.github.ilongake.ribboneconomy.farmer.FoodBuffManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class JobManager {

    private final DataManager dataManager;

    /**
     * 農民特殊効果・Lv.50パッシブ管理
     *
     * FoodBuffManager側からJobManagerを使用しているため、
     * コンストラクタで直接渡さず後から設定する。
     */
    private FoodBuffManager foodBuffManager;


    /*
     * ==========================================================
     * コンストラクタ
     * ==========================================================
     */

    public JobManager(
            DataManager dataManager
    ) {

        this.dataManager = dataManager;
    }


    /*
     * ==========================================================
     * FoodBuffManager設定
     * ==========================================================
     */

    public void setFoodBuffManager(
            FoodBuffManager foodBuffManager
    ) {

        this.foodBuffManager =
                foodBuffManager;
    }


    /*
     * ==========================================================
     * Lv.50パッシブ更新
     * ==========================================================
     *
     * ログイン時など、
     * 「現在の状態を確認して適用する」場合に使用。
     *
     * ここでは満腹度+4は発生しない。
     */

    private void updatePassive(
            UUID uuid
    ) {

        if (foodBuffManager == null) {
            return;
        }

        Player player =
                Bukkit.getPlayer(uuid);

        if (player == null) {
            return;
        }

        foodBuffManager.applyLevel50Passive(
                player
        );
    }


    /*
     * ==========================================================
     * Lv.50到達時パッシブ発動
     * ==========================================================
     *
     * Lv.49 → Lv.50など、
     * 実際にLv.50へ到達した場合に使用。
     *
     * 最大体力 +4
     * 満腹度 +4
     */

    private void activateLevel50Passive(
            UUID uuid
    ) {

        if (foodBuffManager == null) {
            return;
        }

        Player player =
                Bukkit.getPlayer(uuid);

        if (player == null) {
            return;
        }

        foodBuffManager
                .applyLevel50PassiveWithFoodBonus(
                        player
                );
    }


    /*
     * ==========================================================
     * プレイヤーの職業を取得
     * ==========================================================
     */

    public JobType getJob(
            UUID uuid
    ) {

        JobData data =
                dataManager.getPlayerData(uuid);

        if (data == null) {
            return JobType.NONE;
        }

        return data.getJobType();
    }


    /*
     * ==========================================================
     * プレイヤーの職業を設定
     * ==========================================================
     */

    public void setJob(
            UUID uuid,
            JobType jobType
    ) {

        JobData data =
                dataManager.getPlayerData(uuid);

        if (data == null) {
            return;
        }

        if (jobType == null) {
            jobType = JobType.NONE;
        }

        /*
         * 変更前の職業
         */
        JobType oldJob =
                data.getJobType();

        /*
         * 同じ職業なら何もしない
         */
        if (oldJob == jobType) {
            return;
        }

        /*
         * 職業変更
         */
        data.setJobType(
                jobType
        );

        /*
         * 保存
         */
        dataManager.savePlayer(
                uuid
        );

        /*
         * ======================================================
         * 職業変更後のパッシブ更新
         * ======================================================
         *
         * FARMER Lv.50
         * ↓
         * MINER
         *
         * → 最大体力+4を削除
         *
         * MINER
         * ↓
         * FARMER Lv.50
         *
         * → 最大体力+4を付与
         */

        updatePassive(uuid);
    }


    /*
     * ==========================================================
     * 職業を辞める
     * ==========================================================
     */

    public void leaveJob(
            UUID uuid
    ) {

        setJob(
                uuid,
                JobType.NONE
        );
    }


    /*
     * ==========================================================
     * 職業の活動数を取得
     * ==========================================================
     */

    public long getJobProgress(
            UUID uuid,
            JobType jobType
    ) {

        JobData data =
                dataManager.getPlayerData(uuid);

        if (data == null) {
            return 0;
        }

        return data.getJobProgress(
                jobType
        );
    }


    /*
     * ==========================================================
     * 現在の職業の活動数を取得
     * ==========================================================
     */

    public long getCurrentJobProgress(
            UUID uuid
    ) {

        JobData data =
                dataManager.getPlayerData(uuid);

        if (data == null) {
            return 0;
        }

        return data.getCurrentJobProgress();
    }


    /*
     * ==========================================================
     * 職業レベルを取得
     * ==========================================================
     */

    public int getJobLevel(
            UUID uuid,
            JobType jobType
    ) {

        JobData data =
                dataManager.getPlayerData(uuid);

        if (data == null) {
            return 1;
        }

        return data.getJobLevel(
                jobType
        );
    }


    /*
     * ==========================================================
     * 現在の職業レベルを取得
     * ==========================================================
     */

    public int getCurrentJobLevel(
            UUID uuid
    ) {

        JobData data =
                dataManager.getPlayerData(uuid);

        if (data == null) {
            return 1;
        }

        return data.getCurrentJobLevel();
    }


    /*
     * ==========================================================
     * 職業レベルを直接設定
     * ==========================================================
     *
     * 管理者用。
     *
     * レベルそのものを保存するのではなく、
     * そのレベルに必要な累計活動数を保存する。
     */

    public boolean setJobLevel(
            UUID uuid,
            JobType jobType,
            int level
    ) {

        JobData data =
                dataManager.getPlayerData(uuid);

        if (data == null) {
            return false;
        }

        if (jobType == null) {
            return false;
        }

        if (jobType == JobType.NONE) {
            return false;
        }

        if (level < 1 || level > 50) {
            return false;
        }

        /*
         * 変更前のレベル
         */
        int oldLevel =
                data.getJobLevel(
                        jobType
                );

        /*
         * レベル設定
         */
        data.setJobLevel(
                jobType,
                level
        );

        /*
         * 保存
         */
        dataManager.savePlayer(
                uuid
        );

        /*
         * 現在の職業の場合
         */
        if (getJob(uuid) == jobType) {

            /*
             * Lv.49以下 → Lv.50
             *
             * 実際にLv.50へ到達した場合だけ
             * 満腹度+4を発生させる。
             */
            if (oldLevel < 50
                    && level >= 50) {

                activateLevel50Passive(uuid);

            } else {

                /*
                 * それ以外は通常更新
                 */
                updatePassive(uuid);
            }
        }

        return true;
    }


    /*
     * ==========================================================
     * 職業活動数を追加
     * ==========================================================
     *
     * ブロックを大量に壊すたびに
     * YAML保存はしない。
     */

    public void addJobProgress(
            UUID uuid,
            JobType jobType,
            long amount
    ) {

        JobData data =
                dataManager.getPlayerData(uuid);

        if (data == null) {
            return;
        }

        if (jobType == null) {
            return;
        }

        if (amount <= 0) {
            return;
        }

        /*
         * 変更前のレベル
         */
        int oldLevel =
                data.getJobLevel(
                        jobType
                );

        /*
         * 活動数追加
         */
        data.addJobProgress(
                jobType,
                amount
        );

        /*
         * 変更後のレベル
         */
        int newLevel =
                data.getJobLevel(
                        jobType
                );

        /*
         * レベルが変わった場合
         */
        if (oldLevel != newLevel) {

            /*
             * 現在の職業か確認
             */
            if (getJob(uuid) == jobType) {

                /*
                 * Lv.49以下 → Lv.50
                 */
                if (oldLevel < 50
                        && newLevel >= 50) {

                    activateLevel50Passive(uuid);

                } else {

                    /*
                     * その他のレベル変化
                     */
                    updatePassive(uuid);
                }
            }
        }
    }


    /*
     * ==========================================================
     * 現在の職業の活動数を追加
     * ==========================================================
     */

    public void addCurrentJobProgress(
            UUID uuid,
            long amount
    ) {

        JobType jobType =
                getJob(uuid);

        if (jobType == JobType.NONE) {
            return;
        }

        addJobProgress(
                uuid,
                jobType,
                amount
        );
    }


    /*
     * ==========================================================
     * 次のレベルまで必要な数
     * ==========================================================
     */

    public long getRemainingToNextLevel(
            UUID uuid
    ) {

        JobData data =
                dataManager.getPlayerData(uuid);

        if (data == null) {
            return 0;
        }

        long progress =
                data.getCurrentJobProgress();

        return JobLevel.getRemaining(
                progress
        );
    }


    /*
     * ==========================================================
     * 最大レベルか
     * ==========================================================
     */

    public boolean isMaxLevel(
            UUID uuid
    ) {

        JobData data =
                dataManager.getPlayerData(uuid);

        if (data == null) {
            return false;
        }

        return JobLevel.isMaxLevel(
                data.getCurrentJobProgress()
        );
    }
}