package dev.shadowsoffire.attributeslib.mixin;

import net.minecraft.world.damagesource.CombatRules;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = CombatRules.class, priority = 1500)
public class CombatRulesMixin {
    //TODO Update/redo combat changes
//    /**
//     * @see ALCombatRules#getDamageAfterProtection(net.minecraft.world.entity.LivingEntity, net.minecraft.world.damagesource.DamageSource, float, float)
//     */
//    @Inject(method ="getDamageAfterMagicAbsorb", at = @At("HEAD"), cancellable = true)
//    private static void zenith_attributes$getDamageAfterMagicAbsorb(float damage, float protPoints, CallbackInfoReturnable<Float> cir) {
//        cir.setReturnValue(damage * ALCombatRules.getProtDamageReduction(protPoints));
//    }
//
//    /**
//     * @see ALCombatRules#getDamageAfterArmor(LivingEntity, DamageSource, float, float, float)
//     */
//    @Inject(method ="getDamageAfterAbsorb", at = @At("HEAD"), cancellable = true)
//    private static void zenith_attributes$getDamageAfterAbsorb(float damage, float totalArmor, float toughnessAttribute, CallbackInfoReturnable<Float> cir) {
//        AttributesLib.LOGGER.trace("Invocation of CombatRules#getDamageAfterAbsorb is bypassing armor pen.");
//        cir.setReturnValue(damage * ALCombatRules.getArmorDamageReduction(damage, totalArmor, toughnessAttribute));
//    }
}
