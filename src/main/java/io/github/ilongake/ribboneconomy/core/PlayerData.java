package io.github.ilongake.ribboneconomy.core;

import io.github.ilongake.ribboneconomy.job.JobLevel;
import io.github.ilongake.ribboneconomy.job.JobType;

import java.util.EnumMap;
import java.util.Map;

public class PlayerData {

    private double balance;

    private JobType jobType;

    /**
     * 職業ごとの累計活動数
     *
     * 例：
     * FARMER     → 農業活動数
     * MINER      → 採掘活動数
     * HUNTER     → 討伐活動数
     * LUMBERJACK  → 伐採活動数
     */
    private final Map<JobType, Long> jobProgress;

    /**
     * 新規プレイヤー用
     */
    public PlayerData(double balance) {

        this.balance = balance;
        this.jobType = JobType.NONE;

        this.jobProgress =
                new EnumMap<>(JobType.class);

        initializeJobProgress();
    }

    /**
     * 残高・職業を指定して作成
     */
    public PlayerData(
            double balance,
            JobType jobType
    ) {

        this.balance = balance;
        this.jobType = jobType;

        this.jobProgress =
                new EnumMap<>(JobType.class);

        initializeJobProgress();
    }

    /**
     * 職業進行度を初期化
     */
    private void initializeJobProgress() {

        for (JobType job : JobType.values()) {

            jobProgress.putIfAbsent(
                    job,
                    0L
            );
        }
    }

    /**
     * 残高取得
     */
    public double getBalance() {

        return balance;
    }

    /**
     * 残高設定
     */
    public void setBalance(double balance) {

        this.balance = balance;
    }

    /**
     * 職業取得
     */
    public JobType getJobType() {

        return jobType;
    }

    /**
     * 職業設定
     */
    public void setJobType(JobType jobType) {

        this.jobType = jobType;
    }

    /**
     * 指定した職業の累計活動数を取得
     */
    public long getJobProgress(
            JobType jobType
    ) {

        return jobProgress.getOrDefault(
                jobType,
                0L
        );
    }

    /**
     * 現在の職業の累計活動数を取得
     */
    public long getCurrentJobProgress() {

        return getJobProgress(jobType);
    }

    /**
     * 指定した職業の累計活動数を設定
     */
    public void setJobProgress(
            JobType jobType,
            long amount
    ) {

        if (amount < 0) {
            amount = 0;
        }

        jobProgress.put(
                jobType,
                amount
        );
    }

    /**
     * 職業レベルを直接設定
     *
     * 実際にはレベルを保存するのではなく、
     * そのレベルに必要な累計活動数を保存する。
     */
    public void setJobLevel(
            JobType jobType,
            int level
    ) {

        if (level < 1) {
            level = 1;
        }

        if (level > 50) {
            level = 50;
        }

        long requiredAmount =
                JobLevel.getRequiredAmount(level);

        setJobProgress(
                jobType,
                requiredAmount
        );
    }

    /**
     * 指定した職業のレベルを取得
     */
    public int getJobLevel(
            JobType jobType
    ) {

        long progress =
                getJobProgress(jobType);

        return JobLevel.getLevel(
                progress
        );
    }

    /**
     * 現在の職業のレベルを取得
     */
    public int getCurrentJobLevel() {

        return getJobLevel(jobType);
    }

    /**
     * 指定した職業の累計活動数を追加
     */
    public void addJobProgress(
            JobType jobType,
            long amount
    ) {

        if (amount <= 0) {
            return;
        }

        long current =
                getJobProgress(jobType);

        jobProgress.put(
                jobType,
                current + amount
        );
    }

    /**
     * 現在の職業の累計活動数を追加
     */
    public void addCurrentJobProgress(
            long amount
    ) {

        addJobProgress(
                jobType,
                amount
        );
    }

    /**
     * 全職業の進行度を取得
     */
    public Map<JobType, Long> getAllJobProgress() {

        return jobProgress;
    }
}