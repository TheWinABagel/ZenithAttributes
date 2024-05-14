package dev.shadowsoffire.attributeslib.api.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.Entity;

public interface LivingHealEvent {
    Event<LivingHealEvent> EVENT = EventFactory.createArrayBacked(LivingHealEvent.class,
            (listeners) -> (entity, amount) -> {
                for (LivingHealEvent listener : listeners) {
                    return listener.onLivingHeal(entity, amount);
                }
                return amount;
            });

    float onLivingHeal(Entity entity, float amount);
}
