package dev.shadowsoffire.attributeslib.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.shadowsoffire.attributeslib.api.client.GatherEffectScreenTooltipsEvent;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;

@Mixin(value = EffectRenderingInventoryScreen.class, priority = 300)
public class EffectRenderingInventoryScreenMixin {

    @ModifyArgs(method = "renderEffects", at = @At(value = "INVOKE", target = "net/minecraft/client/gui/GuiGraphics.renderTooltip (Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;II)V"))
    public void zenith_attributes$ModifyRenderEffectsArgs(Args args, @Local MobEffectInstance mob_effect) {
        List<Component> tooltipList = args.get(1);
        var event = new GatherEffectScreenTooltipsEvent.GatherEffectTooltipsEvent((EffectRenderingInventoryScreen<?>) (Object) this, mob_effect, tooltipList);
        GatherEffectScreenTooltipsEvent.GATHER_TOOLTIPS.invoker().gatherTooltips(event);
        args.set(1, tooltipList);
    }
}
