package dev.shadowsoffire.attributeslib.mixin;

import dev.shadowsoffire.attributeslib.impl.AttributeEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin {

    @ModifyVariable(method = "setBaseDamage", at = @At("HEAD"), argsOnly = true)
    private double zenith_attributes$modifyArrowDamage(double baseDamage) {
        AbstractArrow arrow = (AbstractArrow) (Object) this;
        if (arrow.getOwner() instanceof LivingEntity shooter) {
            return AttributeEvents.modifyArrowDamage(arrow, shooter, baseDamage);
        }
        return baseDamage;
    }
}