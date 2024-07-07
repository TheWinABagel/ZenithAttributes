package dev.shadowsoffire.attributeslib.components;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.shadowsoffire.attributeslib.AttributesLib;
import net.minecraft.world.entity.projectile.AbstractArrow;

public class ZenithAttributesComponents implements EntityComponentInitializer {
    public static final ComponentKey<BooleanComponent> ARROW_VELOCITY_DONE = ComponentRegistry.getOrCreate(AttributesLib.loc("arrow_velocity_done"), BooleanComponent.class);
    public static final ComponentKey<BooleanComponent> ARROW_DAMAGE_DONE = ComponentRegistry.getOrCreate(AttributesLib.loc("arrow_damage_done"), BooleanComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(AbstractArrow.class, ARROW_VELOCITY_DONE, arrow -> new BooleanComponent(AttributesLib.loc("arrow_velocity_done").toString()));
        registry.registerFor(AbstractArrow.class, ARROW_DAMAGE_DONE, arrow -> new BooleanComponent(AttributesLib.loc("arrow_damage_done").toString()));
    }
}