package dev.shadowsoffire.attributeslib.mixin;

import dev.shadowsoffire.attributeslib.AttributesLib;
import dev.shadowsoffire.attributeslib.api.ALCombatRules;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CombatRules.class, priority = 1500)
public class CombatRulesMixin {

    /**
     * @see ALCombatRules#getDamageAfterProtection(net.minecraft.world.entity.LivingEntity, net.minecraft.world.damagesource.DamageSource, float, float)
     */
    @Inject(method ="getDamageAfterMagicAbsorb", at = @At("HEAD"), cancellable = true)
    private static void zenith_attributes$getDamageAfterMagicAbsorb(float damage, float protPoints, CallbackInfoReturnable<Float> cir) {
        AttributesLib.LOGGER.warn("Overriding getDamageAfterMagicAbsorb!");
        cir.setReturnValue(damage * ALCombatRules.getProtDamageReduction(protPoints));
    }

    /**
     * @see ALCombatRules#getDamageAfterArmor(LivingEntity, DamageSource, float, float, float)
     */
    @Inject(method ="getDamageAfterAbsorb", at = @At("HEAD"), cancellable = true)
    private static void zenith_attributes$getDamageAfterAbsorb(float damage, float totalArmor, float toughnessAttribute, CallbackInfoReturnable<Float> cir) {
        AttributesLib.LOGGER.trace("Invocation of CombatRules#getDamageAfterAbsorb is bypassing armor pen.");
        AttributesLib.LOGGER.warn("Overriding getDamageAfterAbsorb!");
        cir.setReturnValue(damage * ALCombatRules.getArmorDamageReduction(damage, totalArmor));
    }
}
