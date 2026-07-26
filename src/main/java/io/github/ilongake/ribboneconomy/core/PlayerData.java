package io.github.ilongake.ribboneconomy.core;

import io.github.ilongake.ribboneconomy.job.JobType;

public class PlayerData {

    private double balance;

    private JobType jobType;

    // 新規プレイヤー用
    public PlayerData(double balance) {
        this.balance = balance;
        this.jobType = JobType.NONE;
    }

    // 残高と職業を指定して作成
    public PlayerData(double balance, JobType jobType) {
        this.balance = balance;
        this.jobType = jobType;
    }

    // 残高取得
    public double getBalance() {
        return balance;
    }

    // 残高設定
    public void setBalance(double balance) {
        this.balance = balance;
    }

    // 職業取得
    public JobType getJobType() {
        return jobType;
    }

    // 職業設定
    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }
}