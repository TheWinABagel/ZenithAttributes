package dev.shadowsoffire.attributeslib.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.Entity;

public interface HealEvent {
    Event<HealEvent> EVENT = EventFactory.createArrayBacked(HealEvent.class,
            (listeners) -> (entity, amount) -> {
                for (HealEvent listener : listeners) {
                    float result = listener.onLivingHeal(entity, amount);
                    return result;
                }
                return amount;
            });

    float onLivingHeal(Entity entity, float amount);
}
