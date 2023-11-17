package dev.shadowsoffire.attributeslib.mixin;

import dev.shadowsoffire.attributeslib.api.ALObjects;
import dev.shadowsoffire.attributeslib.impl.AttributeEvents;
import io.github.fabricators_of_create.porting_lib.entity.events.EntityEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Projectile.class)
public class ProjectileMixin {

    @ModifyArgs(method = "shootFromRotation", at = @At(value = "INVOKE", target = "net/minecraft/world/entity/projectile/Projectile.shoot (DDDFF)V"))
    private void gatherComponents(Args args, Entity shooter, float x, float y, float z, float velocity, float inaccuracy) {
        if (((Projectile) (Object) this) instanceof AbstractArrow arrow) {
            AttributeEvents.modifyArrowVelocity(args, arrow, velocity);
        }
    }

}
