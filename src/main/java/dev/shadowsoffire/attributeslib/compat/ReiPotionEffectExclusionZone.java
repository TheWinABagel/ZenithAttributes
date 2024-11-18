package dev.shadowsoffire.attributeslib.compat;

import com.google.common.collect.Ordering;
import dev.shadowsoffire.attributeslib.client.AttributesGui;
import dev.shadowsoffire.attributeslib.mixin.accessors.AbstractContainerScreenAccessor;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.config.ConfigObject;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZonesProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ReiPotionEffectExclusionZone implements ExclusionZonesProvider<EffectRenderingInventoryScreen<?>> {
        @Override
        public Collection<Rectangle> provide(EffectRenderingInventoryScreen<?> screen) {
            if (!screen.canSeeEffects())
                return Collections.emptyList();
            boolean leftSideMobEffects = ConfigObject.getInstance().isLeftSideMobEffects();
            Collection<MobEffectInstance> activePotionEffects = Minecraft.getInstance().player.getActiveEffects();
            int x;
            boolean fullWidth;
            int left = ((AbstractContainerScreenAccessor) screen).getLeftPos();
            if (AttributesGui.wasOpen) {
                left -= AttributesGui.WIDTH;
            }

            if (!leftSideMobEffects) {
                x = ((AbstractContainerScreenAccessor) screen).getLeftPos() + ((AbstractContainerScreenAccessor) screen).getImageWidth() + 2;
                int availableWidth = screen.width - x;
                fullWidth = availableWidth >= 120;

                if (availableWidth < 32) {
                    return Collections.emptyList();
                }
            } else {
                fullWidth = left >= 120;
                x = left - (fullWidth ? 124 : 36);
            }

            if (activePotionEffects.isEmpty())
                return Collections.emptyList();
            List<Rectangle> zones = new ArrayList<>();
            int y = ((AbstractContainerScreenAccessor) screen).getTopPos();
            int height = 33;
            if (activePotionEffects.size() > 5)
                height = 132 / (activePotionEffects.size() - 1);
            for (MobEffectInstance instance : Ordering.natural().sortedCopy(activePotionEffects)) {
                zones.add(new Rectangle(x, y, fullWidth ? 120 : 32, 32));
                y += height;
            }
            return zones;
        }
}
