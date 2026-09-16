package io.github.ilongake.ribboneconomy.feature.slot;

import io.github.ilongake.ribboneconomy.core.DataManager;
import io.github.ilongake.ribboneconomy.core.EconomyService;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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

/**
 * スロットマシン看板の実装。
 *
 * RPGEconomyPlugin の onEnable で以下のように登録する:
 *   SlotMachineListener slotMachineListener = new SlotMachineListener(this, dataManager);
 *   getServer().getPluginManager().registerEvents(slotMachineListener, this);
 *   getCommand("giveslot").setExecutor(new GiveSlotCommand(slotMachineListener));
 *
 * 役と配当:
 *   ペア(隣り合う2つが同じ数字)  : 20% -> 400G   例: 622 / 887
 *   ぞろ目(777以外)             : 10% -> 800G   例: 222 / 888
 *   777ぞろ目                  :  1% -> 10000G (虹色に点滅)
 *   それ以外                    : 69% -> はずれ
 */
public class  SlotMachineListener implements Listener {

    // ==== 調整可能なパラメータ ====
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
    private final SlotService service;
    private final NamespacedKey itemKey;   // アイテム側の識別タグ
    private final NamespacedKey blockKey;  // 設置後のブロック側の識別タグ
    private final Random random = new Random();

    // 現在演出中(回転中/点滅中)の看板の位置。連打で二重に抽選されるのを防ぐ
    private final Set<Location> spinningSigns = ConcurrentHashMap.newKeySet();

    public SlotMachineListener(JavaPlugin plugin,EconomyBridge bridge ,SlotService service) {
        this.plugin = plugin;
        this.economy = bridge;
        this.itemKey = new NamespacedKey(plugin, "slot_machine_item");
        this.blockKey = new NamespacedKey(plugin, "slot_machine_block");
        this.service = service;
    }


    // =====================================================================
    // 設置時: 識別タグをブロックに焼き込み、初期表示にする
    // =====================================================================

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (!service.isSlotSignItem(event.getItemInHand())) return;
        if (!(event.getBlockPlaced().getState() instanceof Sign)) return;

        Sign sign = (Sign) event.getBlockPlaced().getState();
        sign.getPersistentDataContainer().set(blockKey, PersistentDataType.BYTE, (byte) 1);

        service.writeLines(sign, "§6§l[ SLOT ]", "§7- - -", "", "");
        sign.setWaxed(true); // 編集画面が開かないようにロック
        sign.update(true, false);

        event.getPlayer().sendMessage(ChatColor.GOLD + "スロットマシンを設置しました。右クリックで遊べます。");
    }

    // =====================================================================
    // 破壊防止
    // =====================================================================

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (service.isSlotSignBlock(event.getBlock())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "このスロットマシンは壊せません。");
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().removeIf(service::isSlotSignBlock);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(service::isSlotSignBlock);
    }

    // =====================================================================
    // 右クリックでプレイ
    // =====================================================================

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        if (block == null || !service.isSlotSignBlock(block)) return;

        event.setCancelled(true); // 看板編集画面などが開かないように
        Player player = event.getPlayer();
        Location loc = block.getLocation();

        if (spinningSigns.contains(loc)) {
            player.sendMessage(ChatColor.YELLOW + "抽選中です。少し待ってください。");
            return;
        }

        if (!economy.withdraw(player, BET_AMOUNT)) {
            player.sendMessage(ChatColor.RED + "残高が足りません。(必要: " + (int) BET_AMOUNT + "G)");
            return;
        }

        player.sendMessage(ChatColor.GOLD + "" + (int) BET_AMOUNT + "G を賭けました。スロット回転中...");
        spinningSigns.add(loc);
        service.startSpin(block, player);
    }
}