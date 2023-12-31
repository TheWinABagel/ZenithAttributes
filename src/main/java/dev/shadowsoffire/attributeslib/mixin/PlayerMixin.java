package dev.shadowsoffire.attributeslib.mixin;

import dev.shadowsoffire.attributeslib.AttributesLib;
import net.minecraft.world.entity.Entity;
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

}
