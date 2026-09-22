package io.github.ilongake.ribboneconomy.feature.jobs.farmer;

import io.github.ilongake.ribboneconomy.core.PlayerHeadCreator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;

public class CookingStationGUI implements InventoryHolder {
    private final Inventory inventory;

    public CookingStationGUI(JavaPlugin plugin) {
        this.inventory = plugin.getServer().createInventory(this, 54);
        setup();
    }

    public void setup(){
        //Frame
        ItemStack green = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta greenMeta = green.getItemMeta();
        greenMeta.displayName(Component.text(""));
        ItemStack lime = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta limeMeta = lime.getItemMeta();
        limeMeta.displayName(Component.text(""));
        ItemStack yellow = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
        ItemMeta yellowMeta = yellow.getItemMeta();
        yellowMeta.displayName(Component.text(""));
        ItemStack cyan = new ItemStack(Material.CYAN_STAINED_GLASS_PANE);
        ItemMeta cyanMeta = cyan.getItemMeta();
        cyanMeta.displayName(Component.text(""));
        ItemStack white = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        ItemMeta whiteMeta = white.getItemMeta();
        whiteMeta.displayName(Component.text(""));
        ItemStack orange = new ItemStack(Material.ORANGE_STAINED_GLASS_PANE);
        ItemMeta orangeMeta = orange.getItemMeta();
        orangeMeta.displayName(Component.text(""));
        ItemStack pink = new ItemStack(Material.PINK_STAINED_GLASS_PANE);
        ItemMeta pinkMeta = pink.getItemMeta();
        pinkMeta.displayName(Component.text(""));

        //displayItemMetaStamping
        green.setItemMeta(greenMeta);
        lime.setItemMeta(limeMeta);
        yellow.setItemMeta(yellowMeta);
        cyan.setItemMeta(cyanMeta);
        white.setItemMeta(whiteMeta);
        orange.setItemMeta(orangeMeta);
        pink.setItemMeta(pinkMeta);

        //outputItem
        ItemStack outputOverlay = new ItemStack(Material.STRUCTURE_VOID);
        ItemMeta outputOverlayMeta = outputOverlay.getItemMeta();
        outputOverlayMeta.displayName(Component.text("完成品がありません").color(NamedTextColor.RED));
        outputOverlay.setItemMeta(outputOverlayMeta);

        //createSpecialFood
        ItemStack createButton = new ItemStack(Material.CRAFTER);
        ItemMeta createButtonMeta = createButton.getItemMeta();
        createButtonMeta.displayName(MiniMessage.miniMessage().deserialize("<gradient:#F56F53:#F7836A>これで作る</gradient>"));
        createButton.setItemMeta(createButtonMeta);

        //setBuff
        ItemStack arrowUp = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta arrowUpMeta = PlayerHeadCreator.createCustomHead(arrowUp,"http://textures.minecraft.net/texture/55149dddaded20d244e0bb62a2d9fa0dc6c6a7862559328a94f77725f53c358");
        arrowUpMeta.displayName(Component.text("バフのレベルを上げる").color(NamedTextColor.GOLD));
        arrowUp.setItemMeta(arrowUpMeta);

        ItemStack arrowDawn = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta arrowDawnMeta = PlayerHeadCreator.createCustomHead(arrowDawn,"http://textures.minecraft.net/texture/55149dddaded20d244e0bb62a2d9fa0dc6c6a7862559328a94f77725f53c358");
        arrowDawnMeta.displayName(Component.text("バフのレベルを下げる").color(NamedTextColor.GOLD));
        arrowDawn.setItemMeta(arrowUpMeta);

        ItemStack buffInfo = new ItemStack(Material.POTION);
        PotionMeta buffInfoMeta = (PotionMeta) buffInfo.getItemMeta();
        buffInfoMeta.setBasePotionType(PotionType.STRENGTH);
        buffInfoMeta.displayName(Component.text("現在のバフのレベル").color(NamedTextColor.WHITE));
        buffInfo.setItemMeta(buffInfoMeta);

        int[] green_slot = {3,5};
        int[] lime_slot = {0,2,6,8,18,26};
        int[] yellow_slot = {19,20,21,22,23,24,25,30,31,32,48,50};
        int[] cyan_slot = {1,4,7,9,11,12,14,15,17};
        int[] white_slot = {27,28,29,36,38,45,46,47};
        int[] orange_slot = {39,41,49};
        int[] pink_slot = {33,35,42,44,51,53};
        for (int slot : green_slot) {
            inventory.setItem(slot, green);
        }
        for (int slot : lime_slot) {
            inventory.setItem(slot, lime);
        }
        for (int slot : yellow_slot) {
            inventory.setItem(slot, yellow);
        }
        for (int slot : cyan_slot) {
            inventory.setItem(slot, cyan);
        }
        for (int slot : white_slot) {
            inventory.setItem(slot, white);
        }
        for (int slot : orange_slot) {
            inventory.setItem(slot, orange);
        }
        for (int slot : pink_slot) {
            inventory.setItem(slot, pink);
        }
        inventory.setItem(37,outputOverlay);
        inventory.setItem(40,createButton);
        inventory.setItem(34,arrowUp);
        inventory.setItem(43,buffInfo);
        inventory.setItem(52,arrowDawn);

    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }
}
