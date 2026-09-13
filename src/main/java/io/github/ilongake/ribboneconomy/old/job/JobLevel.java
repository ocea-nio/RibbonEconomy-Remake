package io.github.ilongake.ribboneconomy.job;

/**
 * 職業レベル管理
 *
 * 職業ごとの累計活動数によってレベルが決まる。
 *
 * Lv10 = 100
 * Lv20 = 2,300
 * Lv30 = 6,500
 * Lv40 = 10,000
 * Lv50 = 20,000
 */
public final class JobLevel {

    private JobLevel() {
    }

    /**
     * レベルから必要な累計活動数を取得
     */
    public static long getRequiredAmount(int level) {

        if (level <= 1) {
            return 0;
        }

        if (level >= 50) {
            return 20_000;
        }

        int lowerLevel;
        int upperLevel;

        long lowerAmount;
        long upperAmount;

        if (level <= 10) {

            lowerLevel = 1;
            upperLevel = 10;

            lowerAmount = 0;
            upperAmount = 100;

        } else if (level <= 20) {

            lowerLevel = 10;
            upperLevel = 20;

            lowerAmount = 100;
            upperAmount = 2_300;

        } else if (level <= 30) {

            lowerLevel = 20;
            upperLevel = 30;

            lowerAmount = 2_300;
            upperAmount = 6_500;

        } else if (level <= 40) {

            lowerLevel = 30;
            upperLevel = 40;

            lowerAmount = 6_500;
            upperAmount = 10_000;

        } else {

            lowerLevel = 40;
            upperLevel = 50;

            lowerAmount = 10_000;
            upperAmount = 20_000;
        }

        double ratio =
                (double) (level - lowerLevel)
                        / (upperLevel - lowerLevel);

        return Math.round(
                lowerAmount
                        + (upperAmount - lowerAmount) * ratio
        );
    }

    /**
     * 累計活動数から職業レベルを取得
     */
    public static int getLevel(long progress) {

        if (progress <= 0) {
            return 1;
        }

        for (int level = 50; level >= 1; level--) {

            if (progress >= getRequiredAmount(level)) {
                return level;
            }
        }

        return 1;
    }

    /**
     * 次のレベルまであと何個必要か
     */
    public static long getRemaining(long progress) {

        int level = getLevel(progress);

        if (level >= 50) {
            return 0;
        }

        return Math.max(
                0,
                getRequiredAmount(level + 1) - progress
        );
    }

    /**
     * 最大レベルか
     */
    public static boolean isMaxLevel(long progress) {

        return getLevel(progress) >= 50;
    }
}