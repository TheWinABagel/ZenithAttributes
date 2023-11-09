package dev.shadowsoffire.attributeslib.mixin;

import java.util.List;

import com.google.common.collect.Multimap;
import dev.shadowsoffire.attributeslib.api.ItemAttributeModifierEvent;
import dev.shadowsoffire.attributeslib.api.client.ItemTooltipCallbackWithPlayer;
import eu.pb4.polymer.core.api.item.PolymerItem;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

@Mixin(value = ItemStack.class, priority = 700)
public class ItemStackMixin {

    // Injects just before ItemStack.TooltipPart.MODIFIERS is written to the tooltip to remember where to rewind to.
    @Inject(method = "getTooltipLines(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/TooltipFlag;)Ljava/util/List;", at = @At(value = "INVOKE", ordinal = 4, target = "net/minecraft/world/item/ItemStack.shouldShowInTooltip(ILnet/minecraft/world/item/ItemStack$TooltipPart;)Z"), locals = LocalCapture.CAPTURE_FAILHARD, require = 1)
    public void zenith_tooltipMarker(@Nullable Player pPlayer, TooltipFlag pIsAdvanced, CallbackInfoReturnable<List<Component>> cir, List<Component> list) {
        if (FabricLoader.getInstance().isModLoaded("polymer-core") && ((ItemStack) (Object) this).getItem() instanceof PolymerItem) return;
        list.add(Component.literal("ZENITH_REMOVE_MARKER"));
    }

    // Injects just after ItemStack.TooltipPart.MODIFIERS is written to the tooltip to remember where to rewind to.
    @Inject(method = "getTooltipLines(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/TooltipFlag;)Ljava/util/List;", at = @At(value = "INVOKE", ordinal = 1, target = "net/minecraft/world/item/ItemStack.hasTag()Z"), locals = LocalCapture.CAPTURE_FAILHARD, require = 1)
    public void zenith_tooltipMarker2(@Nullable Player player, TooltipFlag isAdvanced, CallbackInfoReturnable<List<Component>> cir, List<Component> list) {
        if (FabricLoader.getInstance().isModLoaded("polymer-core") && ((ItemStack) (Object) this).getItem() instanceof PolymerItem) return;
        list.add(Component.literal("ZENITH_REMOVE_MARKER_2"));
    }

    @Inject(method = "getTooltipLines", at = @At("RETURN"))
    private void getTooltip(Player entity, TooltipFlag tooltipContext, CallbackInfoReturnable<List<Component>> info) {
        ItemTooltipCallbackWithPlayer.EVENT.invoker().getTooltip((ItemStack) (Object) this, tooltipContext, info.getReturnValue(), entity);
    }

    @Inject(method = "getAttributeModifiers", at = @At("RETURN"), cancellable = true)
    private void attributeModifierEvent(EquipmentSlot slot, CallbackInfoReturnable<Multimap<Attribute, AttributeModifier>> cir) {
        ItemAttributeModifierEvent.AttributeModifierEvent event = new ItemAttributeModifierEvent.AttributeModifierEvent(((ItemStack) (Object) this), slot, cir.getReturnValue());
        ItemAttributeModifierEvent.GATHER_TOOLTIPS.invoker().gatherTooltips(event);
        cir.setReturnValue(event.nonChangableModifiers);
    }
}
