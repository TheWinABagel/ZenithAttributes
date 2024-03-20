package dev.shadowsoffire.attributeslib.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.shadowsoffire.attributeslib.AttributesLib;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin {

    @Inject(method = "attack", at = @At("HEAD"))
    private void zenith_attributes$setLocalAtkStrength(Entity target, CallbackInfo ci) {
        AttributesLib.localAtkStrength = ((Player) (Object) this).getAttackStrengthScale(.5F);
    }

    @ModifyExpressionValue(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", ordinal = 0), method = "attack(Lnet/minecraft/world/entity/Entity;)V")
    private boolean zenith_attributes$handleKilledByAuxDmg(boolean original, LivingEntity target, DamageSource src, float dmg) {
        return original || target.getCustomData().getBoolean("zenith.killed_by_aux_dmg");
    }
}
