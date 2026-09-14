package io.github.ilongake.ribboneconomy.feature.jobs;

public enum JobType {

    NONE("無職"),
    MINER("採掘師"),
    FARMER("農家"),
    HUNTER("狩人"),
    LUMBERJACK("木こり");

    private final String displayName;

    JobType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
