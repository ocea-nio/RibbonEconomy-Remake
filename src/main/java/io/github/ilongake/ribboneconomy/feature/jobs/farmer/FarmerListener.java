package io.github.ilongake.ribboneconomy.feature.jobs.farmer;

import io.github.ilongake.ribboneconomy.feature.jobs.JobManager;
import io.github.ilongake.ribboneconomy.feature.jobs.JobType;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;


public class FarmerListener implements Listener {
    private final FarmerStation farmerStation;
    private final CookingStationGUI farmingGUI;
    private final JobManager jobs;
    private final int UNLOCK_LEVEL = 20;

    public FarmerListener(FarmerStation farmerStation, CookingStationGUI farmingGUI, JobManager jobs) {

        this.farmerStation = farmerStation;
        this.farmingGUI = farmingGUI;
        this.jobs = jobs;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        Block target = event.getClickedBlock();
        Player player = event.getPlayer();
        if (target == null){return;}
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!farmerStation.match(target.getLocation())){
            Bukkit.getLogger().info("not Match");
            return;
        }
        if (jobs.getJob(player.getUniqueId()) != JobType.FARMER){return;}
        if (!(jobs.getJobLevel(player.getUniqueId(),JobType.FARMER) >= UNLOCK_LEVEL)){
            Bukkit.getLogger().info("Level:" + jobs.getJobLevel(player.getUniqueId(),JobType.FARMER));
            return;
        }
        event.setCancelled(true);
        player.openInventory(farmingGUI.getInventory());
    }

    @EventHandler
    public void onClick(InventoryClickEvent event){
        if (!(event.getWhoClicked() instanceof Player player)) return;
        Inventory inv = event.getInventory();
        if (!(inv.getHolder() instanceof CookingStationGUI )) return;
        if (event.getRawSlot() >= inv.getSize()) {
            return;
        }
        // ドラッグや範囲クリックなど、想定外の取り方は無視する
        if (event.getClick() == ClickType.WINDOW_BORDER_LEFT
                || event.getClick() == ClickType.WINDOW_BORDER_RIGHT) {
            return;
        }
        switch (event.getRawSlot()){
            case 34 -> {

            }
            case 37 -> {

            }
            case 40 -> {

            }
            case 52 -> {

            }
            default -> event.setCancelled(true);
        }
    }
}
