package dev.shadowsoffire.attributeslib.compat;

import dev.shadowsoffire.attributeslib.ALConfig;
import dev.shadowsoffire.attributeslib.client.AttributesGui;
import dev.shadowsoffire.attributeslib.mixin.accessors.AbstractContainerScreenAccessor;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZones;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;

import java.util.ArrayList;
import java.util.List;

public class REICompat implements REIClientPlugin {

    @Override
    public void registerExclusionZones(ExclusionZones zones) {
        zones.register(InventoryScreen.class, screen -> {
            List<Rectangle> exclusions = new ArrayList<>();
            if (ALConfig.enableAttributesGui && AttributesGui.wasOpen) {
                int leftPos = ((AbstractContainerScreenAccessor) screen).getLeftPos() - AttributesGui.WIDTH;
                int topPos = ((AbstractContainerScreenAccessor) screen).getTopPos();
                exclusions.add(new Rectangle(leftPos, topPos, AttributesGui.WIDTH, ((AbstractContainerScreenAccessor) screen).getImageHeight()));
            }
            return exclusions;
        });
        zones.register(EffectRenderingInventoryScreen.class, new ReiPotionEffectExclusionZone());
    }
}
