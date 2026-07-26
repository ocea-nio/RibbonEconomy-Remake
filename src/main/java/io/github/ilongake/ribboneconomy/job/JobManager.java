package io.github.ilongake.ribboneconomy.job;

import io.github.ilongake.ribboneconomy.core.DataManager;

import java.util.UUID;

public class JobManager {

    private final DataManager dataManager;

    public JobManager(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    /**
     * プレイヤーの職業を取得
     */
    public JobType getJob(UUID uuid) {

        return dataManager
                .getPlayerData(uuid)
                .getJobType();
    }

    /**
     * プレイヤーの職業を設定
     */
    public void setJob(
            UUID uuid,
            JobType jobType
    ) {

        dataManager
                .getPlayerData(uuid)
                .setJobType(jobType);

        // 変更したらすぐ保存
        dataManager.savePlayer(uuid);
    }

    /**
     * 職業を辞める
     */
    public void leaveJob(UUID uuid) {

        setJob(
                uuid,
                JobType.NONE
        );
    }
}