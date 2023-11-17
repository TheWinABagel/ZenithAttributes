package dev.shadowsoffire.attributeslib.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.fabricators_of_create.porting_lib.attributes.PortingLibAttributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class PortLibStepHeightFix {
    // TODO remove, needed as I can't update porting lib to latest version without moving to entity refactor branch

    @ModifyReturnValue(method = "createLivingAttributes", at = @At("RETURN"))
    private static AttributeSupplier.Builder zenith_attributes_addPortLibAttribute(AttributeSupplier.Builder builder) {
        return builder.add(PortingLibAttributes.STEP_HEIGHT_ADDITION);
    }

    @ModifyReturnValue(method = "maxUpStep", at = @At("RETURN"))
    private float zenith_attributes_modifyStepHeight(float vanillaStep) {
        return (float) (vanillaStep + ((LivingEntity) (Object) this).getAttribute(PortingLibAttributes.STEP_HEIGHT_ADDITION).getValue());
    }
}
