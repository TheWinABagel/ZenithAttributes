package dev.shadowsoffire.attributeslib.api.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public interface LivingHurtEvent {
    Event<LivingHurtEvent> EVENT = EventFactory.createArrayBacked(LivingHurtEvent.class, callbacks -> (source, damaged, amount) -> {
        for (LivingHurtEvent callback : callbacks) {
            float newAmount = callback.onLivingHurt(source, damaged, amount);
            if (newAmount != amount) return newAmount;
        }
        return amount;
    });

    float onLivingHurt(DamageSource source, LivingEntity damaged, float amount);
}
