package dev.shadowsoffire.attributeslib.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public interface UseItemTickEvent {

    Event<UseItemTickEvent> EVENT = EventFactory.createArrayBacked(UseItemTickEvent.class,
            (listeners) -> (entity, usingItem, useItemRemaining) -> {
                for (UseItemTickEvent listener : listeners) {
                    int result = listener.onLivingUse(entity, usingItem, useItemRemaining);
                    return result;
                }
                return useItemRemaining;
            });

    int onLivingUse(Entity entity, ItemStack usingItem, int useItemRemaining);
}
