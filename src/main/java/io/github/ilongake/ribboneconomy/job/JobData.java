package io.github.ilongake.ribboneconomy.job;

public class JobData {

    private JobType jobType;

    public JobData() {
        this.jobType = JobType.NONE;
    }

    public JobData(JobType jobType) {
        this.jobType = jobType;
    }

    public JobType getJobType() {
        return jobType;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }
}