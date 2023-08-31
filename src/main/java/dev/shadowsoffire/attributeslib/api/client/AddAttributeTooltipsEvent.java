package dev.shadowsoffire.attributeslib.api.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.ListIterator;

/**
 * This event is used to add additional attribute tooltip lines without having to manually locate the inject point.
 */
public interface AddAttributeTooltipsEvent {


    Event<AddAttributeTooltipsEvent> EVENT = EventFactory.createArrayBacked(AddAttributeTooltipsEvent.class, callbacks -> (stack, player, tooltip, attributeTooltipIterator, flag) -> {
        for (AddAttributeTooltipsEvent callback : callbacks) {
            callback.getTooltip(stack, player, tooltip, attributeTooltipIterator, flag);
        }
    });

    public void getTooltip(ItemStack stack, @Nullable Player player, List<Component> tooltip, ListIterator<Component> attributeTooltipIterator, TooltipFlag flag);



}
