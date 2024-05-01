package dev.shadowsoffire.attributeslib.compat;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.widget.Bounds;
import dev.shadowsoffire.attributeslib.ALConfig;
import dev.shadowsoffire.attributeslib.client.AttributesGui;
import dev.shadowsoffire.attributeslib.mixin.accessors.AbstractContainerScreenAccessor;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;

public class EMICompat implements EmiPlugin {
    @Override
    public void register(EmiRegistry registry) {
        registry.addExclusionArea(InventoryScreen.class, (screen, out) -> {
            if (screen != null) {
                if (ALConfig.enableAttributesGui && AttributesGui.wasOpen) {
                    int leftPos = ((AbstractContainerScreenAccessor) screen).getLeftPos() - AttributesGui.WIDTH;
                    int topPos = ((AbstractContainerScreenAccessor) screen).getTopPos();
                    out.accept(new Bounds(leftPos, topPos, AttributesGui.WIDTH, screen.height / 2));
                }
            }
        });
    }
}
