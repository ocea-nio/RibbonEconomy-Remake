package io.github.ilongake.ribboneconomy.feature.slot;

import io.github.ilongake.ribboneconomy.core.EconomyService;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SlotService {
    private static final double BET_AMOUNT = 100.0; // 1回の賭け金

    private static final double JACKPOT_CHANCE = 0.01; // 777ぞろ目
    private static final double ZOROME_CHANCE = 0.07;  // 777以外のぞろ目
    private static final double PAIR_CHANCE = 0.15;    // 隣り合う2つが同じ

    // 期待配当 = 0.15*100 + 0.07*300 + 0.01*5000 = 86G (賭け金100Gに対し、胴元が約14%の取り分)
    private static final double JACKPOT_PAYOUT = 5000.0;
    private static final double ZOROME_PAYOUT = 300.0;
    private static final double PAIR_PAYOUT = 100.0;

    private static final int SPIN_TICKS = 3;  // 何tickごとに数字を切り替えるか
    private static final int SPIN_COUNT = 18; // 何回切り替えたら止まるか(演出の長さ)

    private static final int JACKPOT_BLINK_TICKS = 2;   // 虹色点滅の切り替え間隔(tick)
    private static final int JACKPOT_BLINK_COUNT = 30;  // 虹色点滅の回数

    private static final ChatColor[] RAINBOW = {
            ChatColor.RED, ChatColor.GOLD, ChatColor.YELLOW,
            ChatColor.GREEN, ChatColor.AQUA, ChatColor.LIGHT_PURPLE
    };

    private enum Result { LOSE, PAIR, ZOROME, JACKPOT }

    private final JavaPlugin plugin;
    private final EconomyBridge economy;
    private final NamespacedKey itemKey;   // アイテム側の識別タグ
    private final NamespacedKey blockKey;  // 設置後のブロック側の識別タグ
    private final Random random = new Random();


    private final Set<Location> spinningSigns = ConcurrentHashMap.newKeySet();


    public SlotService(JavaPlugin plugin, EconomyService economyService){
        this.plugin = plugin;
        this.economy = new EconomyBridge(economyService);
        this.itemKey = new NamespacedKey(plugin, "slot_machine_item");
        this.blockKey = new NamespacedKey(plugin, "slot_machine_block");
    }

    // =====================================================================
    // アイテム生成
    // =====================================================================

    public boolean isSlotSignItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().has(itemKey, PersistentDataType.BYTE);
    }

    public boolean isSlotSignBlock(Block block) {
        if (!(block.getState() instanceof Sign)) return false;
        Sign sign = (Sign) block.getState();
        return sign.getPersistentDataContainer().has(blockKey, PersistentDataType.BYTE);
    }
    public ItemStack createSlotSignItem() {
        ItemStack item = new ItemStack(Material.OAK_SIGN);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "スロットマシン");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "" + (int) BET_AMOUNT + "G を賭けて抽選できる");
        lore.add(ChatColor.GRAY + "ペア(15%) 100G / ぞろ目(7%) 300G");
        lore.add(ChatColor.GRAY + "777ぞろ目(1%) 5000G");
        lore.add(ChatColor.GRAY + "コマンドでしか入手できない特別なアイテム");
        meta.setLore(lore);
        meta.getPersistentDataContainer().set(itemKey, PersistentDataType.BYTE, (byte) 1);
        meta.setUnbreakable(true);
        item.setItemMeta(meta);
        return item;
    }

    // =====================================================================
    // 抽選演出(回転)
    // =====================================================================

    public void startSpin(Block block, Player player) {
        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                if (!(block.getState() instanceof Sign) || !isSlotSignBlock(block)) {
                    spinningSigns.remove(block.getLocation());
                    cancel();
                    return;
                }
                Sign sign = (Sign) block.getState();

                if (count < SPIN_COUNT) {
                    // 回転中: ランダムな数字を横並びで表示するだけの演出
                    int a = 1 + random.nextInt(9);
                    int b = 1 + random.nextInt(9);
                    int c = 1 + random.nextInt(9);
                    writeLines(sign, "§6§l[ SLOT ]", "§f" + a + " " + b + " " + c, "", "");
                    sign.update(true, false);
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1.2f);
                    count++;
                    return;
                }

                // 停止: 結果を確定
                cancel();
                resolveResult(block, player);
            }
        }.runTaskTimer(plugin, 0L, SPIN_TICKS);
    }

    // =====================================================================
    // 結果確定
    // =====================================================================

    private void resolveResult(Block block, Player player) {
        if (!(block.getState() instanceof Sign) || !isSlotSignBlock(block)) {
            spinningSigns.remove(block.getLocation());
            return;
        }
        Sign sign = (Sign) block.getState();

        double r = random.nextDouble();
        SlotService.Result result;
        int[] digits;

        if (r < JACKPOT_CHANCE) {
            result = SlotService.Result.JACKPOT;
            digits = new int[]{7, 7, 7};
        } else if (r < JACKPOT_CHANCE + ZOROME_CHANCE) {
            result = SlotService.Result.ZOROME;
            int d;
            do {
                d = 1 + random.nextInt(9);
            } while (d == 7);
            digits = new int[]{d, d, d};
        } else if (r < JACKPOT_CHANCE + ZOROME_CHANCE + PAIR_CHANCE) {
            result = SlotService.Result.PAIR;
            digits = rollPairDigits();
        } else {
            result = SlotService.Result.LOSE;
            digits = rollLoseDigits();
        }

        String numberLine = digits[0] + " " + digits[1] + " " + digits[2];

        switch (result) {
            case JACKPOT: {
                economy.deposit(player, JACKPOT_PAYOUT);
                player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD
                        + "★★★ 777ジャックポット！！ " + (int) JACKPOT_PAYOUT + "G 獲得！ ★★★");
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                writeLines(sign, "§6§l[ SLOT ]", numberLine, "§6§l★JACKPOT★", "");
                sign.update(true, false);
                startJackpotBlink(block, player, numberLine);
                return; // 点滅演出側でspinningSignsを解除する
            }
            case ZOROME: {
                economy.deposit(player, ZOROME_PAYOUT);
                writeLines(sign, "§b§l[ SLOT ]", "§b§l" + numberLine, "§b§lぞろ目！", "+" + (int) ZOROME_PAYOUT + "G");
                player.sendMessage(ChatColor.AQUA + "ぞろ目！ " + (int) ZOROME_PAYOUT + "G 獲得しました！");
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.4f);
                break;
            }
            case PAIR: {
                economy.deposit(player, PAIR_PAYOUT);
                writeLines(sign, "§a§l[ SLOT ]", "§a§l" + numberLine, "§a§lペア！", "+" + (int) PAIR_PAYOUT + "G");
                player.sendMessage(ChatColor.GREEN + "ペア！ " + (int) PAIR_PAYOUT + "G 獲得しました！");
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                break;
            }
            case LOSE:
            default: {
                writeLines(sign, "§6§l[ SLOT ]", "§f" + numberLine, "§7はずれ", "");
                player.sendMessage(ChatColor.GRAY + "残念、はずれです。");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                break;
            }
        }

        sign.update(true, false);
        spinningSigns.remove(block.getLocation());
    }

    /** 隣り合う2つだけが同じ数字になる組み合わせを作る (例: 622, 887) */
    public int[] rollPairDigits() {
        int pairDigit = 1 + random.nextInt(9);
        int otherDigit;
        do {
            otherDigit = 1 + random.nextInt(9);
        } while (otherDigit == pairDigit);

        if (random.nextBoolean()) {
            // 前の2つが同じ (例: 8 8 7)
            return new int[]{pairDigit, pairDigit, otherDigit};
        } else {
            // 後ろの2つが同じ (例: 6 2 2)
            return new int[]{otherDigit, pairDigit, pairDigit};
        }
    }

    /** ペア・ぞろ目にならない組み合わせを作る(はずれ用) */
    private int[] rollLoseDigits() {
        int[] digits = new int[3];
        do {
            digits[0] = 1 + random.nextInt(9);
            digits[1] = 1 + random.nextInt(9);
            digits[2] = 1 + random.nextInt(9);
        } while (digits[0] == digits[1] || digits[1] == digits[2]);
        return digits;
    }

    // =====================================================================
    // 777ジャックポット: 虹色に点滅する演出
    // =====================================================================

    private void startJackpotBlink(Block block, Player player, String numberLine) {
        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                if (!(block.getState() instanceof Sign) || !isSlotSignBlock(block)) {
                    spinningSigns.remove(block.getLocation());
                    cancel();
                    return;
                }
                Sign sign = (Sign) block.getState();

                if (count >= JACKPOT_BLINK_COUNT) {
                    // 点滅終了: 金色で固定表示
                    writeLines(sign, "§6§l[ SLOT ]", "§6§l" + numberLine, "§6§l★JACKPOT★", "");
                    sign.update(true, false);
                    spinningSigns.remove(block.getLocation());
                    cancel();
                    return;
                }

                ChatColor color = RAINBOW[count % RAINBOW.length];
                writeLines(sign, color + "" + ChatColor.BOLD + "[ SLOT ]",
                        color + "" + ChatColor.BOLD + numberLine,
                        color + "" + ChatColor.BOLD + "★JACKPOT★", "");
                sign.update(true, false);
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1.5f);
                count++;
            }
        }.runTaskTimer(plugin, 0L, JACKPOT_BLINK_TICKS);
    }

    /** front面(表面)の4行を書き換える(1.20以降のSide APIを使用) */
    public void writeLines(Sign sign, String l0, String l1, String l2, String l3) {
        sign.getSide(Side.FRONT).setLine(0, l0);
        sign.getSide(Side.FRONT).setLine(1, l1);
        sign.getSide(Side.FRONT).setLine(2, l2);
        sign.getSide(Side.FRONT).setLine(3, l3);
    }
}
