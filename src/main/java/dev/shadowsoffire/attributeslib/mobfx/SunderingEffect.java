package dev.shadowsoffire.attributeslib.mobfx;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * Applied via {@link dev.shadowsoffire.attributeslib.mixin.LivingEntityMixin}
 */
public class SunderingEffect extends MobEffect {

    public SunderingEffect() {
        super(MobEffectCategory.HARMFUL, 0x989898);
    }

}
