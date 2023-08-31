package dev.shadowsoffire.attributeslib.mixin;

import dev.shadowsoffire.attributeslib.api.client.GatherEffectScreenTooltipsEvent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collection;
import java.util.List;

@Mixin(EffectRenderingInventoryScreen.class)
public class EffectRenderingInventoryScreenMixin {

    @Unique private MobEffectInstance mobEffect;
    @ModifyVariable(method = "renderEffects",at = @At(value = "STORE",
            target = "java/util/List.of (Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;"), index = 12)
    public List<Component> apoth_sunderingHasEffect(List<Component> tooltip) {
        var event = new GatherEffectScreenTooltipsEvent.GatherEffectTooltipsEvent((EffectRenderingInventoryScreen) (Object) this, mobEffect, tooltip);
        GatherEffectScreenTooltipsEvent.GATHER_TOOLTIPS.invoker().gatherTooltips(event);
        return event.getTooltip();
    }

    @Inject(method = "renderEffects", at = @At(value = "INVOKE", target = "java/util/List.of (Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;", shift = At.Shift.BEFORE),locals = LocalCapture.CAPTURE_FAILHARD)
    public void apoth_sunderingHasEffect(GuiGraphics guiGraphics, int mouseX, int mouseY, CallbackInfo ci, int i, int j, Collection collection, boolean bl, int k, Iterable iterable, int l, MobEffectInstance mobEffectInstance) {
    this.mobEffect = mobEffectInstance;
    }
}
