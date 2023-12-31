package dev.shadowsoffire.attributeslib.api.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ItemTooltipCallbackWithPlayer {

    Event<ItemTooltipCallbackWithPlayer> EVENT = EventFactory.createArrayBacked(ItemTooltipCallbackWithPlayer.class, callbacks -> (stack, context, lines, player) -> {
        for (ItemTooltipCallbackWithPlayer callback : callbacks) {
            callback.getTooltip(stack, context, lines, player);
        }
    });

    void getTooltip(ItemStack stack, TooltipFlag context, List<Component> lines, @Nullable Player player);
}
