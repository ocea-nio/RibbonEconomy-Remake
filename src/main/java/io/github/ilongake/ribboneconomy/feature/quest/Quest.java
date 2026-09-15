package io.github.ilongake.ribboneconomy.feature.quest;

import java.util.UUID;

public class Quest {

    public enum Status {
        OPEN,
        ACCEPTED,
        COMPLETED,
        APPROVED
    }

    private final UUID questId;

    // 依頼主
    private final UUID creatorUuid;

    // 受注者
    private UUID workerUuid;

    // 依頼タイトル
    private final String title;

    // 依頼内容
    private final String description;

    // 報酬
    private final double reward;

    // 現在の状態
    private Status status;

    /**
     * 新規依頼作成用
     */
    public Quest(
            UUID questId,
            UUID creatorUuid,
            String title,
            String description,
            double reward
    ) {

        this.questId = questId;
        this.creatorUuid = creatorUuid;
        this.title = title;
        this.description = description;
        this.reward = reward;

        // 新規依頼はOPEN
        this.status = Status.OPEN;
    }

    /**
     * 保存データ読み込み用
     */
    public Quest(
            UUID questId,
            UUID creatorUuid,
            UUID workerUuid,
            String title,
            String description,
            double reward,
            Status status
    ) {

        this.questId = questId;
        this.creatorUuid = creatorUuid;
        this.workerUuid = workerUuid;
        this.title = title;
        this.description = description;
        this.reward = reward;
        this.status = status;
    }

    public UUID getQuestId() {
        return questId;
    }

    public UUID getCreatorUuid() {
        return creatorUuid;
    }

    public UUID getWorkerUuid() {
        return workerUuid;
    }

    public void setWorkerUuid(
            UUID workerUuid
    ) {

        this.workerUuid = workerUuid;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public double getReward() {
        return reward;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(
            Status status
    ) {

        this.status = status;
    }
}