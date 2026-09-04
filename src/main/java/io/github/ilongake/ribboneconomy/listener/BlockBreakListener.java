package io.github.ilongake.ribboneconomy.listener;

import io.github.ilongake.ribboneconomy.core.DataManager;
import io.github.ilongake.ribboneconomy.core.PlayerData;
import io.github.ilongake.ribboneconomy.job.JobManager;
import io.github.ilongake.ribboneconomy.job.JobType;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class BlockBreakListener implements Listener {

    private final DataManager dataManager;
    private final JobManager jobManager;

    // 採掘師の報酬
    private final Map<Material, Double> miningRewards =
            new HashMap<>();

    // 農家の報酬
    private final Map<Material, Double> farmingRewards =
            new HashMap<>();

    // 木こりの報酬
    private final Map<Material, Double> lumberjackRewards =
            new HashMap<>();

    public BlockBreakListener(
            DataManager dataManager,
            JobManager jobManager
    ) {

        this.dataManager = dataManager;
        this.jobManager = jobManager;

        // =========================
        // 採掘師の報酬
        // =========================

        miningRewards.put(
                Material.COAL_ORE,
                5.0
        );

        miningRewards.put(
                Material.DEEPSLATE_COAL_ORE,
                7.0
        );

        miningRewards.put(
                Material.IRON_ORE,
                10.0
        );

        miningRewards.put(
                Material.DEEPSLATE_IRON_ORE,
                12.0
        );

        miningRewards.put(
                Material.GOLD_ORE,
                20.0
        );

        miningRewards.put(
                Material.DEEPSLATE_GOLD_ORE,
                25.0
        );

        miningRewards.put(
                Material.DIAMOND_ORE,
                100.0
        );

        miningRewards.put(
                Material.DEEPSLATE_DIAMOND_ORE,
                120.0
        );

        miningRewards.put(
                Material.EMERALD_ORE,
                150.0
        );

        miningRewards.put(
                Material.DEEPSLATE_EMERALD_ORE,
                180.0
        );

        miningRewards.put(
                Material.REDSTONE_ORE,
                15.0
        );

        miningRewards.put(
                Material.DEEPSLATE_REDSTONE_ORE,
                18.0
        );

        miningRewards.put(
                Material.LAPIS_ORE,
                15.0
        );

        miningRewards.put(
                Material.DEEPSLATE_LAPIS_ORE,
                18.0
        );

        miningRewards.put(
                Material.COPPER_ORE,
                8.0
        );

        miningRewards.put(
                Material.DEEPSLATE_COPPER_ORE,
                10.0
        );

        // =========================
        // 農家の報酬
        // =========================

        farmingRewards.put(
                Material.WHEAT,
                1.0
        );

        farmingRewards.put(
                Material.CARROTS,
                1.0
        );

        farmingRewards.put(
                Material.POTATOES,
                1.0
        );

        farmingRewards.put(
                Material.BEETROOTS,
                1.0
        );

        farmingRewards.put(
                Material.NETHER_WART,
                1.0
        );

        farmingRewards.put(
                Material.COCOA,
                2.0
        );

        farmingRewards.put(
                Material.SWEET_BERRY_BUSH,
                2.0
        );

        // =========================
        // 木こりの報酬
        // =========================

        // オーク
        lumberjackRewards.put(
                Material.OAK_LOG,
                5.0
        );

        lumberjackRewards.put(
                Material.OAK_WOOD,
                5.0
        );

        // トウヒ
        lumberjackRewards.put(
                Material.SPRUCE_LOG,
                5.0
        );

        lumberjackRewards.put(
                Material.SPRUCE_WOOD,
                5.0
        );

        // シラカバ
        lumberjackRewards.put(
                Material.BIRCH_LOG,
                5.0
        );

        lumberjackRewards.put(
                Material.BIRCH_WOOD,
                5.0
        );

        // ジャングル
        lumberjackRewards.put(
                Material.JUNGLE_LOG,
                5.0
        );

        lumberjackRewards.put(
                Material.JUNGLE_WOOD,
                5.0
        );

        // アカシア
        lumberjackRewards.put(
                Material.ACACIA_LOG,
                5.0
        );

        lumberjackRewards.put(
                Material.ACACIA_WOOD,
                5.0
        );

        // ダークオーク
        lumberjackRewards.put(
                Material.DARK_OAK_LOG,
                5.0
        );

        lumberjackRewards.put(
                Material.DARK_OAK_WOOD,
                5.0
        );

        // マングローブ
        lumberjackRewards.put(
                Material.MANGROVE_LOG,
                5.0
        );

        lumberjackRewards.put(
                Material.MANGROVE_WOOD,
                5.0
        );

        // 桜
        lumberjackRewards.put(
                Material.CHERRY_LOG,
                5.0
        );

        lumberjackRewards.put(
                Material.CHERRY_WOOD,
                5.0
        );

        // 真紅
        lumberjackRewards.put(
                Material.CRIMSON_STEM,
                5.0
        );

        lumberjackRewards.put(
                Material.CRIMSON_HYPHAE,
                5.0
        );

        // 歪んだ
        lumberjackRewards.put(
                Material.WARPED_STEM,
                5.0
        );

        lumberjackRewards.put(
                Material.WARPED_HYPHAE,
                5.0
        );
    }

    @EventHandler
    public void onBlockBreak(
            BlockBreakEvent event
    ) {

        Player player =
                event.getPlayer();

        // =========================
        // プレイヤーデータ取得
        // =========================

        PlayerData data =
                dataManager.getPlayerData(
                        player.getUniqueId()
                );

        if (data == null) {
            return;
        }

        // =========================
        // 壊したブロック
        // =========================

        Block block =
                event.getBlock();

        Material material =
                block.getType();

        // =========================
        // 採掘師
        // =========================

        if (data.getJobType()
                == JobType.MINER) {

            handleMining(
                    player,
                    block,
                    material
            );

            return;
        }

        // =========================
        // 農家
        // =========================

        if (data.getJobType()
                == JobType.FARMER) {

            handleFarming(
                    player,
                    block,
                    material
            );

            return;
        }

        // =========================
        // 木こり
        // =========================

        if (data.getJobType()
                == JobType.LUMBERJACK) {

            handleLumberjack(
                    player,
                    block,
                    material
            );
        }
    }

    /**
     * =========================
     * 採掘師の処理
     * =========================
     */
    private void handleMining(
            Player player,
            Block block,
            Material material
    ) {

        Double rewardPerItem =
                miningRewards.get(material);

        if (rewardPerItem == null) {
            return;
        }

        ItemStack tool =
                player.getInventory()
                        .getItemInMainHand();

        // =========================
        // シルクタッチなら報酬なし
        // =========================

        if (tool.containsEnchantment(
                Enchantment.SILK_TOUCH
        )) {

            return;
        }

        // =========================
        // 実際のドロップ数を取得
        // =========================

        int dropAmount = 0;

        for (ItemStack drop :
                block.getDrops(
                        tool,
                        player
                )) {

            dropAmount +=
                    drop.getAmount();
        }

        if (dropAmount <= 0) {
            return;
        }

        // =========================
        // 報酬計算
        // =========================

        double totalReward =
                rewardPerItem
                        * dropAmount;

        dataManager.addBalance(
                player.getUniqueId(),
                totalReward
        );

        // =========================
        // 通知
        // =========================

        player.sendMessage(
                "§a採掘報酬: §e+"
                        + totalReward
                        + "円 §7("
                        + dropAmount
                        + "個)"
        );
    }

    /**
     * =========================
     * 農家の処理
     * =========================
     */
    private void handleFarming(
            Player player,
            Block block,
            Material material
    ) {

        Double rewardPerItem =
                farmingRewards.get(material);

        if (rewardPerItem == null) {
            return;
        }

        // =========================
        // 成長状態を確認
        // =========================

        if (block.getBlockData()
                instanceof Ageable ageable) {

            if (ageable.getAge()
                    < ageable.getMaximumAge()) {

                return;
            }
        }

        // =========================
        // 使用しているツール
        // =========================

        ItemStack tool =
                player.getInventory()
                        .getItemInMainHand();

        // =========================
        // 実際のドロップ数を取得
        // =========================

        int dropAmount = 0;

        for (ItemStack drop :
                block.getDrops(
                        tool,
                        player
                )) {

            dropAmount +=
                    drop.getAmount();
        }

        if (dropAmount <= 0) {
            return;
        }

        // =========================
        // 報酬計算
        // =========================

        double totalReward =
                rewardPerItem
                        * dropAmount;

        // =========================
        // 農民の活動数を追加
        //
        // 実際に収穫した個数を
        // そのままカウントする
        // =========================

        int oldLevel =
                jobManager.getJobLevel(
                        player.getUniqueId(),
                        JobType.FARMER
                );

        jobManager.addJobProgress(
                player.getUniqueId(),
                JobType.FARMER,
                dropAmount
        );

        int newLevel =
                jobManager.getJobLevel(
                        player.getUniqueId(),
                        JobType.FARMER
                );

        // =========================
        // お金を追加
        // =========================

        dataManager.addBalance(
                player.getUniqueId(),
                totalReward
        );

        // =========================
        // レベルアップ通知
        // =========================

        if (newLevel > oldLevel) {

            player.sendMessage("");

            player.sendMessage(
                    "§6§l職業レベルアップ！"
            );

            player.sendMessage(
                    "§e農家 §fLv."
                            + oldLevel
                            + " §7→ §aLv."
                            + newLevel
            );

            player.sendMessage(
                    "§7農民としての活動を続けて"
                            + "さらにレベルを上げよう！"
            );

            player.sendMessage("");
        }

        // =========================
        // 報酬通知
        // =========================

        player.sendMessage(
                "§a農家報酬: §e+"
                        + totalReward
                        + "円 §7("
                        + dropAmount
                        + "個)"
        );
    }

    /**
     * =========================
     * 木こりの処理
     * =========================
     */
    private void handleLumberjack(
            Player player,
            Block block,
            Material material
    ) {

        // =========================
        // 報酬設定がないブロックは無視
        // =========================

        Double rewardPerItem =
                lumberjackRewards.get(material);

        if (rewardPerItem == null) {
            return;
        }

        // =========================
        // 使用しているツール
        // =========================

        ItemStack tool =
                player.getInventory()
                        .getItemInMainHand();

        // =========================
        // 実際のドロップ数を取得
        // =========================

        int dropAmount = 0;

        for (ItemStack drop :
                block.getDrops(
                        tool,
                        player
                )) {

            dropAmount +=
                    drop.getAmount();
        }

        // =========================
        // ドロップがない場合
        // =========================

        if (dropAmount <= 0) {
            return;
        }

        // =========================
        // 報酬計算
        // =========================

        double totalReward =
                rewardPerItem
                        * dropAmount;

        // =========================
        // お金を追加
        // =========================

        dataManager.addBalance(
                player.getUniqueId(),
                totalReward
        );

        // =========================
        // 通知
        // =========================

        player.sendMessage(
                "§a木こり報酬: §e+"
                        + totalReward
                        + "円 §7("
                        + dropAmount
                        + "個)"
        );
    }
}