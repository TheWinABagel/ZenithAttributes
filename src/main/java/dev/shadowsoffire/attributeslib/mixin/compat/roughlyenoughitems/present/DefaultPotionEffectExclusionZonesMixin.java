package dev.shadowsoffire.attributeslib.mixin.compat.roughlyenoughitems.present;

import dev.shadowsoffire.attributeslib.client.AttributesGui;
import me.shedaniel.rei.plugin.client.exclusionzones.DefaultPotionEffectExclusionZones;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Environment(EnvType.CLIENT)
@Mixin(value = DefaultPotionEffectExclusionZones.class, remap = false)
public class DefaultPotionEffectExclusionZonesMixin {
    @ModifyVariable(method = "provide(Lnet/minecraft/client/gui/screens/inventory/EffectRenderingInventoryScreen;)Ljava/util/Collection;",
            at = @At(value = "STORE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;leftPos:I", ordinal = 1, remap = true), index = 4)
    private int modifyLeftZone(int original) {
        if (!AttributesGui.wasOpen) return original;
        return original - AttributesGui.WIDTH;
    }
}
