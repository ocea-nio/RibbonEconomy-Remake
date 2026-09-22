package io.github.ilongake.ribboneconomy.core;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;

import java.net.URI;
import java.util.UUID;

public class PlayerHeadCreator {
    public static SkullMeta createCustomHead(ItemStack item,String URL){
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = (PlayerProfile) Bukkit.createPlayerProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();
        try {
            textures.setSkin(
                    URI.create(
                            URL
                    ).toURL()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        profile.setTextures(textures);
        meta.setPlayerProfile(profile);
        return meta;
    }
}
