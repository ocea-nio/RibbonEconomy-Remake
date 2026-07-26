package io.github.ilongake.ribboneconomy.quest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class QuestCreateSession {

    private static final Map<UUID, String> titles =
            new HashMap<>();

    private static final Map<UUID, String> descriptions =
            new HashMap<>();

    private static final Map<UUID, Double> rewards =
            new HashMap<>();

    public static void setTitle(
            UUID uuid,
            String title
    ) {

        titles.put(uuid, title);
    }

    public static String getTitle(
            UUID uuid
    ) {

        return titles.get(uuid);
    }

    public static void setDescription(
            UUID uuid,
            String description
    ) {

        descriptions.put(uuid, description);
    }

    public static String getDescription(
            UUID uuid
    ) {

        return descriptions.get(uuid);
    }

    public static void setReward(
            UUID uuid,
            double reward
    ) {

        rewards.put(uuid, reward);
    }

    public static Double getReward(
            UUID uuid
    ) {

        return rewards.get(uuid);
    }

    public static void clear(
            UUID uuid
    ) {

        titles.remove(uuid);
        descriptions.remove(uuid);
        rewards.remove(uuid);
    }
}