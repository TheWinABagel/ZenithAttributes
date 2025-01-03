package dev.shadowsoffire.attributeslib.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.dafuqs.additionalentityattributes.AdditionalEntityAttributes;
import dev.shadowsoffire.attributeslib.api.ALObjects;
import dev.shadowsoffire.attributeslib.api.events.LivingHealEvent;
import dev.shadowsoffire.attributeslib.api.events.LivingHurtEvent;
import dev.shadowsoffire.attributeslib.impl.AttributeEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LivingEntity.class, priority = 900)
public abstract class LivingEntityMixin extends Entity {

    @Shadow
    protected int useItemRemaining;

    @Shadow
    public abstract boolean hasEffect(MobEffect ef);

    @Shadow
    public abstract MobEffectInstance getEffect(MobEffect ef);

    public LivingEntityMixin(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    /**
     * @author Shadows
     * @reason Injection of the Sundering potion effect, which is applied during resistance calculations.
     * @param value  Damage modifier percentage after resistance has been applied [1.0, -inf]
     * @param max    Zero
     * @param source The damage source
     * @param damage The initial damage amount
     */
    @WrapOperation(at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(FF)F"), method = "getDamageAfterMagicAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F")
    public float zenith_attributes$sunderingApplyEffect(float value, float max, Operation<Float> original, DamageSource source, float damage) {
        if (this.hasEffect(ALObjects.MobEffects.SUNDERING) && !source.is(DamageTypeTags.BYPASSES_RESISTANCE)) {
            int level = this.getEffect(ALObjects.MobEffects.SUNDERING).getAmplifier() + 1;
            value += damage * level * 0.2F;
        }
        return original.call(value, max);
    }

    /**
     * @author Shadows
     * @reason Used to enter an if-condition so the above mixin always triggers.
     */
    @WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hasEffect(Lnet/minecraft/world/effect/MobEffect;)Z"), method = "getDamageAfterMagicAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F")
    public boolean zenith_attributes$sunderingHasEffect(LivingEntity instance, MobEffect effect, Operation<Boolean> original) {
        return true;
    }

    /**
     * @author Shadows
     * @reason Used to prevent an NPE since we're faking true on hasEffect
     */
    @WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffectInstance;getAmplifier()I"), method = "getDamageAfterMagicAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F")
    public int zenith_attributes$sunderingGetAmplifier(@Nullable MobEffectInstance inst, Operation<Integer> original) {
        return inst == null ? -1 : original.call(inst);
    }
    //Todo figure out armor modification...
//    @ModifyExpressionValue(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/CombatRules;getDamageAfterAbsorb(FFF)F"), method = "getDamageAfterArmorAbsorb", require = 1)
//    public float zenith_attributes$applyArmorPen(float amount, DamageSource src) {
//        return ALCombatRules.getDamageAfterArmor(ths(), src, amount, ths().getArmorValue(), (float) (ths()).getAttributeValue(Attributes.ARMOR_TOUGHNESS));
//    }
//
//    @ModifyExpressionValue(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/CombatRules;getDamageAfterMagicAbsorb(FF)F"), method = "getDamageAfterMagicAbsorb", require = 1)
//    public float zenith_attributes$applyProtPen(float amount, DamageSource src) {
//        return ALCombatRules.getDamageAfterProtection(ths(), src, amount, EnchantmentHelper.getDamageProtection((ths()).getArmorSlots(), src));
//    }

    @ModifyVariable(method = "heal", at = @At(value = "HEAD"), argsOnly = true)
    private float zenith_attributes$onHealEvent(float value){
        float amount = LivingHealEvent.EVENT.invoker().onLivingHeal(ths(), value);
        return amount >= 0 ? amount : 0;
    }

    @Inject(method = "updateUsingItem", at = @At("HEAD"))
    private void zenith_attributes$modifyDrawSpeed(ItemStack usingItem, CallbackInfo ci) {
        if (!usingItem.isEmpty())
            this.useItemRemaining = AttributeEvents.drawSpeed(ths(), usingItem, this.useItemRemaining);
    }

    @ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true)
    private float zenith_attributes$onHurtEvent(float amount, DamageSource source, float amount2) {
        return LivingHurtEvent.EVENT.invoker().onLivingHurt(source, ths(), amount);
    }

    @Inject(method = "createLivingAttributes", at = @At("RETURN"))
    private static void zenith_attributes$customAttributes(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        cir.getReturnValue().add(ALObjects.Attributes.DRAW_SPEED)
        .add(ALObjects.Attributes.CRIT_CHANCE)
        .add(ALObjects.Attributes.COLD_DAMAGE)
        .add(ALObjects.Attributes.FIRE_DAMAGE)
        .add(ALObjects.Attributes.LIFE_STEAL)
        .add(ALObjects.Attributes.CURRENT_HP_DAMAGE)
        .add(ALObjects.Attributes.OVERHEAL)
        .add(ALObjects.Attributes.GHOST_HEALTH)
        .add(ALObjects.Attributes.MINING_SPEED)
        .add(ALObjects.Attributes.ARROW_DAMAGE)
        .add(ALObjects.Attributes.ARROW_VELOCITY)
        .add(ALObjects.Attributes.HEALING_RECEIVED)
        .add(ALObjects.Attributes.ARMOR_PIERCE)
        .add(ALObjects.Attributes.ARMOR_SHRED)
        .add(ALObjects.Attributes.PROT_PIERCE)
        .add(ALObjects.Attributes.PROT_SHRED)
        .add(ALObjects.Attributes.DODGE_CHANCE)
        .add(ALObjects.Attributes.ELYTRA_FLIGHT)
        .add(ALObjects.Attributes.CREATIVE_FLIGHT)
        .add(AdditionalEntityAttributes.CRITICAL_BONUS_DAMAGE); //Done to fix crash, as it used to be applied to all entities and is now applied to only players
    }

    @Unique
    private LivingEntity ths() {
        return (LivingEntity) (Object) this;
    }
}
