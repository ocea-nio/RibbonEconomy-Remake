package io.github.ilongake.ribboneconomy.feature.jobs.farmer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.List;

public class FarmerStation {
    private final List<StractureRecord> cookingStation;

    public FarmerStation() {
        this.cookingStation =  List.of(
                new StractureRecord(0, 0, 0, Material.CRAFTING_TABLE),
                new StractureRecord(1, 0, 0, Material.FURNACE),
                new StractureRecord(-1, 0, 0, Material.BARREL)
        );
    }

    public boolean match(Location origin){
        for (StractureRecord record : cookingStation) {
            Block block = origin.clone()
                    .add(record.x(), record.y(), record.z())
                    .getBlock();
            if (block.getType() != record.material()) {
                Bukkit.getLogger().info("false");
                return false;
            }
        }
        return true;
    }
}
