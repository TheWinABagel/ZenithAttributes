package dev.shadowsoffire.attributeslib.api.client;

import java.util.Set;
import java.util.UUID;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import org.jetbrains.annotations.Nullable;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/**
 * This event is used to collect UUIDs of attribute modifiers that will not be displayed in item tooltips.
 * <p>
 * This allows hiding specific modifiers for whatever reason. They will still be shown in the attributes GUI.
 */
public interface GatherSkippedAttributeTooltipsEvent {
    Event<GatherSkippedAttributeTooltipsEvent> EVENT = EventFactory.createArrayBacked(GatherSkippedAttributeTooltipsEvent.class, callbacks -> (stack, player, skips, flag) -> {
        for (GatherSkippedAttributeTooltipsEvent callback : callbacks) {
            callback.gather(stack, player, skips, flag);
        }
    });

    public void gather(ItemStack stack, @Nullable Player player, Set<UUID> skips, TooltipFlag flag);

}
