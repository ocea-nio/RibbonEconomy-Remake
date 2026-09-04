package io.github.ilongake.ribboneconomy.farmer;

import io.github.ilongake.ribboneconomy.core.DataManager;
import io.github.ilongake.ribboneconomy.job.JobManager;
import io.github.ilongake.ribboneconomy.job.JobType;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FarmerTradeManager {

    /*
     * ==========================================================
     * 設定
     * ==========================================================
     */

    /**
     * 出品・最低保証売買が解放されるファーマーレベル
     */
    private static final int UNLOCK_LEVEL = 10;

    /**
     * 最低価格
     */
    private static final double MIN_PRICE = 0.01D;

    /**
     * 最高価格
     */
    private static final double MAX_PRICE = 1_000_000_000D;

    /**
     * 1出品あたりの最大数量
     */
    private static final int MAX_AMOUNT = 64;

    /**
     * 最低保証価格が上昇するファーマーレベル
     */
    private static final int GUARANTEE_PRICE_UP_LEVEL = 40;

    /**
     * Lv.40以上の最低保証価格倍率
     */
    private static final double GUARANTEE_PRICE_MULTIPLIER = 1.25D;

    /**
     * 最低保証売買の基本価格
     */
    private static final Map<Material, Double> GUARANTEE_PRICES =
            new HashMap<>();

    /*
     * ==========================================================
     * フィールド
     * ==========================================================
     */

    private final JavaPlugin plugin;

    private final DataManager dataManager;

    private final JobManager jobManager;

    private final File file;

    private FileConfiguration config;

    private final Map<UUID, Trade> trades =
            new LinkedHashMap<>();

    /*
     * ==========================================================
     * コンストラクタ
     * ==========================================================
     */

    public FarmerTradeManager(
            JavaPlugin plugin,
            DataManager dataManager,
            JobManager jobManager
    ) {

        this.plugin = plugin;
        this.dataManager = dataManager;
        this.jobManager = jobManager;

        this.file = new File(
                plugin.getDataFolder(),
                "farmer-trades.yml"
        );

        initializeGuaranteePrices();
        loadTrades();
    }

    /*
     * ==========================================================
     * マーケット利用
     * ==========================================================
     */

    /**
     * マーケットを閲覧・購入できるか。
     *
     * 全職業・全レベルで可能。
     */
    public boolean canUseMarket(UUID uuid) {

        return uuid != null;
    }

    /**
     * ファーマーLv.10以上か確認。
     *
     * 出品・最低保証売買に使用する。
     */
    public boolean isUnlocked(UUID uuid) {

        if (uuid == null) {
            return false;
        }

        if (jobManager.getJob(uuid) != JobType.FARMER) {
            return false;
        }

        return jobManager.getJobLevel(
                uuid,
                JobType.FARMER
        ) >= UNLOCK_LEVEL;
    }

    /**
     * 出品できるか。
     */
    public boolean canSell(UUID uuid) {

        return isUnlocked(uuid);
    }

    /**
     * 最低保証売買を利用できるか。
     */
    public boolean canUseGuarantee(UUID uuid) {

        return isUnlocked(uuid);
    }

    /*
     * ==========================================================
     * 所持金
     * ==========================================================
     */

    public double getBalance(UUID uuid) {

        if (uuid == null) {
            return 0D;
        }

        return dataManager.getBalance(uuid);
    }

    /*
     * ==========================================================
     * 出品作成
     * ==========================================================
     */

    public synchronized Trade createTrade(
            UUID seller,
            ItemStack item,
            double price
    ) {

        if (seller == null) {
            return null;
        }

        /*
         * 出品はファーマーLv.10以上のみ
         */
        if (!canSell(seller)) {
            return null;
        }

        Player player =
                plugin.getServer()
                        .getPlayer(seller);

        if (player == null) {
            return null;
        }

        if (item == null
                || item.getType().isAir()) {

            return null;
        }

        if (!isFarmerItem(item.getType())) {
            return null;
        }

        int amount = item.getAmount();

        if (amount <= 0
                || amount > MAX_AMOUNT) {

            return null;
        }

        if (!isValidPrice(price)) {
            return null;
        }

        ItemStack tradeItem =
                item.clone();

        tradeItem.setAmount(amount);

        PlayerInventory inventory =
                player.getInventory();

        if (!hasEnoughItems(
                inventory,
                tradeItem
        )) {

            return null;
        }

        if (!removeItems(
                inventory,
                tradeItem
        )) {

            return null;
        }

        UUID id =
                UUID.randomUUID();

        Trade trade =
                new Trade(
                        id,
                        seller,
                        tradeItem,
                        price,
                        System.currentTimeMillis()
                );

        trades.put(
                id,
                trade
        );

        saveTrades();

        return trade;
    }

    /*
     * ==========================================================
     * 出品取得
     * ==========================================================
     */

    public synchronized Trade getTrade(UUID id) {

        if (id == null) {
            return null;
        }

        return trades.get(id);
    }

    public synchronized List<Trade> getTrades() {

        return new ArrayList<>(
                trades.values()
        );
    }

    public synchronized List<Trade> getTrades(
            UUID seller
    ) {

        List<Trade> result =
                new ArrayList<>();

        if (seller == null) {
            return result;
        }

        for (Trade trade :
                trades.values()) {

            if (trade.getSeller()
                    .equals(seller)) {

                result.add(trade);
            }
        }

        return result;
    }

    /*
     * ==========================================================
     * 出品取消
     * ==========================================================
     */

    public synchronized boolean cancelTrade(
            UUID seller,
            UUID tradeId
    ) {

        if (seller == null
                || tradeId == null) {

            return false;
        }

        Trade trade =
                trades.get(tradeId);

        if (trade == null) {
            return false;
        }

        if (!trade.getSeller()
                .equals(seller)) {

            return false;
        }

        Player player =
                plugin.getServer()
                        .getPlayer(seller);

        if (player == null) {
            return false;
        }

        ItemStack item =
                trade.getItem();

        trades.remove(tradeId);

        saveTrades();

        HashMap<Integer, ItemStack> leftover =
                player.getInventory()
                        .addItem(item);

        if (!leftover.isEmpty()) {

            for (ItemStack remaining :
                    leftover.values()) {

                if (remaining == null
                        || remaining.getType().isAir()) {

                    continue;
                }

                player.getWorld()
                        .dropItemNaturally(
                                player.getLocation(),
                                remaining
                        );
            }
        }

        return true;
    }

    /*
     * ==========================================================
     * 購入
     * ==========================================================
     */

    /**
     * 出品を購入する。
     *
     * 購入は全職業・全レベルで可能。
     */
    public synchronized boolean purchase(
            UUID buyer,
            UUID tradeId
    ) {

        if (buyer == null
                || tradeId == null) {

            return false;
        }

        /*
         * ここでは isUnlocked() を使用しない。
         *
         * ファーマーLv.10未満や
         * ハンターでも購入可能。
         */
        if (!canUseMarket(buyer)) {
            return false;
        }

        Trade trade =
                trades.get(tradeId);

        if (trade == null) {
            return false;
        }

        /*
         * 自分の商品は禁止
         */
        if (trade.getSeller()
                .equals(buyer)) {

            return false;
        }

        double price =
                trade.getPrice();

        if (!isValidPrice(price)) {
            return false;
        }

        Player player =
                plugin.getServer()
                        .getPlayer(buyer);

        if (player == null) {
            return false;
        }

        double balance =
                dataManager.getBalance(buyer);

        if (balance < price) {
            return false;
        }

        ItemStack item =
                trade.getItem();

        if (item == null
                || item.getType().isAir()) {

            return false;
        }

        if (!isFarmerItem(
                item.getType()
        )) {

            return false;
        }

        int amount =
                item.getAmount();

        if (amount <= 0
                || amount > MAX_AMOUNT) {

            return false;
        }

        if (!canFitItem(
                player.getInventory(),
                item
        )) {

            return false;
        }

        /*
         * 購入者からお金を引く
         */
        boolean withdrawn =
                dataManager.withdraw(
                        buyer,
                        price
                );

        if (!withdrawn) {
            return false;
        }

        /*
         * 商品を渡す
         */
        HashMap<Integer, ItemStack> leftover =
                player.getInventory()
                        .addItem(
                                item.clone()
                        );

        /*
         * 安全処理
         */
        if (!leftover.isEmpty()) {

            dataManager.addBalance(
                    buyer,
                    price
            );

            int remaining = 0;

            for (ItemStack stack :
                    leftover.values()) {

                if (stack != null) {

                    remaining +=
                            stack.getAmount();
                }
            }

            int inserted =
                    amount - remaining;

            if (inserted > 0) {

                ItemStack insertedItem =
                        item.clone();

                insertedItem.setAmount(
                        inserted
                );

                player.getInventory()
                        .removeItem(
                                insertedItem
                        );
            }

            return false;
        }

        /*
         * 出品者へ送金
         */
        dataManager.addBalance(
                trade.getSeller(),
                price
        );

        /*
         * 出品削除
         */
        trades.remove(tradeId);

        saveTrades();

        return true;
    }

    private boolean isFarmerItem(
            Material material
    ) {

        if (material == null) {
            return false;
        }

        return switch (material) {

            /*
             * ======================================================
             * 農作物
             * ======================================================
             */

            case WHEAT,
                 WHEAT_SEEDS,

                 CARROT,
                 POTATO,

                 BEETROOT,
                 BEETROOT_SEEDS,

                 NETHER_WART,

                 PUMPKIN,
                 PUMPKIN_SEEDS,

                 MELON,
                 MELON_SLICE,
                 MELON_SEEDS,

                 SUGAR_CANE,

                 COCOA_BEANS,

                 CACTUS,

                 SWEET_BERRIES,
                 GLOW_BERRIES,

                 RED_MUSHROOM,
                 BROWN_MUSHROOM,

                 KELP,
                 DRIED_KELP,

                 BAMBOO,

                 TORCHFLOWER,
                 TORCHFLOWER_SEEDS,

                 PITCHER_POD -> true;


            /*
             * ======================================================
             * 食料
             * ======================================================
             */

            case APPLE,
                 BREAD,

                 BAKED_POTATO,
                 POISONOUS_POTATO,

                 BEETROOT_SOUP,
                 MUSHROOM_STEW,
                 RABBIT_STEW,

                 COOKED_BEEF,
                 COOKED_CHICKEN,
                 COOKED_COD,
                 COOKED_MUTTON,
                 COOKED_PORKCHOP,
                 COOKED_RABBIT,
                 COOKED_SALMON,

                 BEEF,
                 CHICKEN,
                 COD,
                 MUTTON,
                 PORKCHOP,
                 RABBIT,
                 SALMON,

                 COOKIE,
                 CAKE,

                 PUMPKIN_PIE,

                 GOLDEN_CARROT,
                 GOLDEN_APPLE,

                 CHORUS_FRUIT,

                 HONEY_BOTTLE,

                 SUSPICIOUS_STEW,

                 ENCHANTED_GOLDEN_APPLE -> true;

            default -> false;
        };
    }

    /*
     * ==========================================================
     * 価格チェック
     * ==========================================================
     */

    private boolean isValidPrice(
            double price
    ) {

        if (Double.isNaN(price)) {
            return false;
        }

        if (Double.isInfinite(price)) {
            return false;
        }

        return price >= MIN_PRICE
                && price <= MAX_PRICE;
    }

    /*
     * ==========================================================
     * アイテム所持確認
     * ==========================================================
     */

    private boolean hasEnoughItems(
            PlayerInventory inventory,
            ItemStack target
    ) {

        if (inventory == null
                || target == null
                || target.getType().isAir()) {

            return false;
        }

        int required =
                target.getAmount();

        int found = 0;

        for (ItemStack current :
                inventory.getStorageContents()) {

            if (current == null
                    || current.getType().isAir()) {

                continue;
            }

            if (!current.isSimilar(target)) {
                continue;
            }

            found +=
                    current.getAmount();

            if (found >= required) {
                return true;
            }
        }

        return false;
    }

    /*
     * ==========================================================
     * アイテム削除
     * ==========================================================
     */

    private boolean removeItems(
            PlayerInventory inventory,
            ItemStack target
    ) {

        if (!hasEnoughItems(
                inventory,
                target
        )) {

            return false;
        }

        int remaining =
                target.getAmount();

        ItemStack[] contents =
                inventory.getStorageContents();

        for (int i = 0;
             i < contents.length
                     && remaining > 0;
             i++) {

            ItemStack current =
                    contents[i];

            if (current == null
                    || current.getType().isAir()) {

                continue;
            }

            if (!current.isSimilar(target)) {
                continue;
            }

            int remove =
                    Math.min(
                            current.getAmount(),
                            remaining
                    );

            current.setAmount(
                    current.getAmount()
                            - remove
            );

            if (current.getAmount() <= 0) {
                contents[i] = null;
            }

            remaining -= remove;
        }

        inventory.setStorageContents(
                contents
        );

        return remaining == 0;
    }

    /*
     * ==========================================================
     * インベントリ容量
     * ==========================================================
     */

    private boolean canFitItem(
            PlayerInventory inventory,
            ItemStack target
    ) {

        if (inventory == null
                || target == null
                || target.getType().isAir()) {

            return false;
        }

        int remaining =
                target.getAmount();

        int maxStack =
                target.getMaxStackSize();

        for (ItemStack current :
                inventory.getStorageContents()) {

            if (remaining <= 0) {
                return true;
            }

            if (current == null
                    || current.getType().isAir()) {

                remaining -= maxStack;

                continue;
            }

            if (!current.isSimilar(target)) {
                continue;
            }

            int space =
                    current.getMaxStackSize()
                            - current.getAmount();

            if (space > 0) {
                remaining -= space;
            }
        }

        return remaining <= 0;
    }

    /*
     * ==========================================================
     * 最低保証売買
     * ==========================================================
     */

    private void initializeGuaranteePrices() {

        GUARANTEE_PRICES.clear();

        GUARANTEE_PRICES.put(Material.WHEAT, 5.0D);
        GUARANTEE_PRICES.put(Material.WHEAT_SEEDS, 1.0D);
        GUARANTEE_PRICES.put(Material.CARROT, 5.0D);
        GUARANTEE_PRICES.put(Material.POTATO, 5.0D);
        GUARANTEE_PRICES.put(Material.BEETROOT, 6.0D);
        GUARANTEE_PRICES.put(Material.BEETROOT_SEEDS, 1.0D);
        GUARANTEE_PRICES.put(Material.NETHER_WART, 15.0D);
        GUARANTEE_PRICES.put(Material.PUMPKIN, 12.0D);
        GUARANTEE_PRICES.put(Material.PUMPKIN_SEEDS, 1.0D);
        GUARANTEE_PRICES.put(Material.MELON, 8.0D);
        GUARANTEE_PRICES.put(Material.MELON_SLICE, 1.0D);
        GUARANTEE_PRICES.put(Material.MELON_SEEDS, 1.0D);
        GUARANTEE_PRICES.put(Material.SUGAR_CANE, 4.0D);
        GUARANTEE_PRICES.put(Material.COCOA_BEANS, 8.0D);
        GUARANTEE_PRICES.put(Material.CACTUS, 5.0D);
        GUARANTEE_PRICES.put(Material.SWEET_BERRIES, 7.0D);
        GUARANTEE_PRICES.put(Material.GLOW_BERRIES, 8.0D);
        GUARANTEE_PRICES.put(Material.RED_MUSHROOM, 8.0D);
        GUARANTEE_PRICES.put(Material.BROWN_MUSHROOM, 8.0D);
        GUARANTEE_PRICES.put(Material.KELP, 3.0D);
        GUARANTEE_PRICES.put(Material.DRIED_KELP, 5.0D);
        GUARANTEE_PRICES.put(Material.BAMBOO, 4.0D);
        GUARANTEE_PRICES.put(Material.TORCHFLOWER, 10.0D);
        GUARANTEE_PRICES.put(Material.TORCHFLOWER_SEEDS, 2.0D);
        GUARANTEE_PRICES.put(Material.PITCHER_POD, 15.0D);
    }

    public boolean hasGuaranteePrice(
            Material material
    ) {

        if (material == null) {
            return false;
        }

        return GUARANTEE_PRICES.containsKey(material);
    }

    public double getGuaranteePrice(
            UUID uuid,
            Material material
    ) {

        if (uuid == null
                || material == null) {

            return 0D;
        }

        /*
         * 最低保証はファーマーLv.10以上のみ
         */
        if (!canUseGuarantee(uuid)) {
            return 0D;
        }

        Double basePrice =
                GUARANTEE_PRICES.get(material);

        if (basePrice == null
                || basePrice <= 0D) {

            return 0D;
        }

        int level =
                jobManager.getJobLevel(
                        uuid,
                        JobType.FARMER
                );

        if (level >= GUARANTEE_PRICE_UP_LEVEL) {

            return basePrice
                    * GUARANTEE_PRICE_MULTIPLIER;
        }

        return basePrice;
    }

    public synchronized double sellToGuarantee(
            UUID uuid,
            ItemStack item
    ) {

        if (uuid == null
                || item == null
                || item.getType().isAir()) {

            return 0D;
        }

        if (!canUseGuarantee(uuid)) {
            return 0D;
        }

        Player player =
                plugin.getServer()
                        .getPlayer(uuid);

        if (player == null) {
            return 0D;
        }

        Material material =
                item.getType();

        if (!isFarmerItem(material)
                || !hasGuaranteePrice(material)) {

            return 0D;
        }

        int amount =
                item.getAmount();

        if (amount <= 0) {
            return 0D;
        }

        double unitPrice =
                getGuaranteePrice(
                        uuid,
                        material
                );

        if (unitPrice <= 0D
                || Double.isNaN(unitPrice)
                || Double.isInfinite(unitPrice)) {

            return 0D;
        }

        double totalPrice =
                unitPrice * amount;

        if (Double.isNaN(totalPrice)
                || Double.isInfinite(totalPrice)
                || totalPrice <= 0D) {

            return 0D;
        }

        ItemStack target =
                item.clone();

        target.setAmount(amount);

        boolean removed =
                removeItems(
                        player.getInventory(),
                        target
                );

        if (!removed) {
            return 0D;
        }

        dataManager.addBalance(
                uuid,
                totalPrice
        );

        return totalPrice;
    }

    public synchronized double sellHeldItemToGuarantee(
            UUID uuid
    ) {

        if (uuid == null) {
            return 0D;
        }

        Player player =
                plugin.getServer()
                        .getPlayer(uuid);

        if (player == null) {
            return 0D;
        }

        ItemStack held =
                player.getInventory()
                        .getItemInMainHand();

        if (held == null
                || held.getType().isAir()) {

            return 0D;
        }

        return sellToGuarantee(
                uuid,
                held.clone()
        );
    }

    public boolean hasGuaranteePriceBonus(
            UUID uuid
    ) {

        if (uuid == null) {
            return false;
        }

        if (jobManager.getJob(uuid)
                != JobType.FARMER) {

            return false;
        }

        return jobManager.getJobLevel(
                uuid,
                JobType.FARMER
        ) >= GUARANTEE_PRICE_UP_LEVEL;
    }

    /*
     * ==========================================================
     * 保存
     * ==========================================================
     */

    public synchronized void saveTrades() {

        config =
                new YamlConfiguration();

        for (Trade trade :
                trades.values()) {

            String path =
                    "trades."
                            + trade.getId()
                            .toString();

            config.set(
                    path + ".seller",
                    trade.getSeller()
                            .toString()
            );

            config.set(
                    path + ".item",
                    trade.getItem()
            );

            config.set(
                    path + ".price",
                    trade.getPrice()
            );

            config.set(
                    path + ".created-at",
                    trade.getCreatedAt()
            );
        }

        File parent =
                file.getParentFile();

        if (parent != null
                && !parent.exists()) {

            parent.mkdirs();
        }

        try {

            config.save(file);

        } catch (IOException e) {

            plugin.getLogger().severe(
                    "farmer-trades.yml の保存に失敗しました。"
            );

            e.printStackTrace();
        }
    }

    /*
     * ==========================================================
     * 読み込み
     * ==========================================================
     */

    public synchronized void loadTrades() {

        trades.clear();

        if (!file.exists()) {

            File parent =
                    file.getParentFile();

            if (parent != null) {
                parent.mkdirs();
            }

            try {

                file.createNewFile();

            } catch (IOException e) {

                plugin.getLogger().severe(
                        "farmer-trades.yml の作成に失敗しました。"
                );

                e.printStackTrace();

                return;
            }
        }

        config =
                YamlConfiguration
                        .loadConfiguration(file);

        ConfigurationSection section =
                config.getConfigurationSection(
                        "trades"
                );

        if (section == null) {
            return;
        }

        for (String idString :
                section.getKeys(false)) {

            try {

                UUID id =
                        UUID.fromString(idString);

                String base =
                        "trades."
                                + idString;

                String sellerString =
                        config.getString(
                                base + ".seller"
                        );

                if (sellerString == null) {
                    continue;
                }

                UUID seller =
                        UUID.fromString(
                                sellerString
                        );

                ItemStack item =
                        config.getItemStack(
                                base + ".item"
                        );

                if (item == null
                        || item.getType().isAir()) {

                    continue;
                }

                if (!isFarmerItem(
                        item.getType()
                )) {

                    continue;
                }

                int amount =
                        item.getAmount();

                if (amount <= 0
                        || amount > MAX_AMOUNT) {

                    continue;
                }

                double price =
                        config.getDouble(
                                base + ".price",
                                0D
                        );

                if (!isValidPrice(price)) {
                    continue;
                }

                long createdAt =
                        config.getLong(
                                base + ".created-at",
                                0L
                        );

                Trade trade =
                        new Trade(
                                id,
                                seller,
                                item,
                                price,
                                createdAt
                        );

                trades.put(
                        id,
                        trade
                );

            } catch (Exception e) {

                plugin.getLogger().warning(
                        "不正なファーマー出品データを"
                                + "スキップしました: "
                                + idString
                );
            }
        }

        plugin.getLogger().info(
                "ファーマー出品を "
                        + trades.size()
                        + "件読み込みました。"
        );
    }

    /*
     * ==========================================================
     * 停止
     * ==========================================================
     */

    public void shutdown() {

        saveTrades();
    }

    /*
     * ==========================================================
     * Trade
     * ==========================================================
     */

    public static class Trade {

        private final UUID id;

        private final UUID seller;

        private final ItemStack item;

        private final double price;

        private final long createdAt;

        public Trade(
                UUID id,
                UUID seller,
                ItemStack item,
                double price,
                long createdAt
        ) {

            this.id = id;
            this.seller = seller;
            this.item = item.clone();
            this.price = price;
            this.createdAt = createdAt;
        }

        public UUID getId() {
            return id;
        }

        public UUID getSeller() {
            return seller;
        }

        public ItemStack getItem() {
            return item.clone();
        }

        public double getPrice() {
            return price;
        }

        public long getCreatedAt() {
            return createdAt;
        }
    }
}