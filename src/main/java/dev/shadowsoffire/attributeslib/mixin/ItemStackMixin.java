package dev.shadowsoffire.attributeslib.mixin;

import java.util.List;

import com.google.common.collect.Multimap;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.shadowsoffire.attributeslib.api.ItemAttributeModifierEvent;
import dev.shadowsoffire.attributeslib.api.client.ItemTooltipCallbackWithPlayer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
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

@Mixin(ItemStack.class)
public class ItemStackMixin {

    // Injects just before ItemStack.TooltipPart.MODIFIERS is written to the tooltip to remember where to rewind to.
    @Inject(method = "getTooltipLines(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/TooltipFlag;)Ljava/util/List;", at = @At(value = "INVOKE", ordinal = 4, target = "net/minecraft/world/item/ItemStack.shouldShowInTooltip(ILnet/minecraft/world/item/ItemStack$TooltipPart;)Z"), locals = LocalCapture.CAPTURE_FAILHARD, require = 1)
    public void apoth_tooltipMarker(@Nullable Player pPlayer, TooltipFlag pIsAdvanced, CallbackInfoReturnable<List<Component>> cir, List<Component> list) {
        list.add(Component.literal("APOTH_REMOVE_MARKER"));
    }

    // Injects just after ItemStack.TooltipPart.MODIFIERS is written to the tooltip to remember where to rewind to.
    @Inject(method = "getTooltipLines(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/TooltipFlag;)Ljava/util/List;", at = @At(value = "INVOKE", ordinal = 1, target = "net/minecraft/world/item/ItemStack.hasTag()Z"), locals = LocalCapture.CAPTURE_FAILHARD, require = 1)
    public void apoth_tooltipMarker2(@Nullable Player player, TooltipFlag isAdvanced, CallbackInfoReturnable<List<Component>> cir, List<Component> list) {
        list.add(Component.literal("APOTH_REMOVE_MARKER_2"));
    }

    @Inject(method = "getTooltipLines", at = @At("RETURN"))
    private void getTooltip(Player entity, TooltipFlag tooltipContext, CallbackInfoReturnable<List<Component>> info) {
        ItemTooltipCallbackWithPlayer.EVENT.invoker().getTooltip((ItemStack) (Object) this, tooltipContext, info.getReturnValue(), entity);
    }

    @ModifyReturnValue(method = "getAttributeModifiers", at = @At("TAIL"))
    private Multimap<Attribute, AttributeModifier> attributeModifierEvent(Multimap<Attribute, AttributeModifier> multimap , EquipmentSlot slot) {
        ItemAttributeModifierEvent.AttributeModifierEvent event = new ItemAttributeModifierEvent.AttributeModifierEvent(((ItemStack) (Object) this), slot, multimap);
        ItemAttributeModifierEvent.GATHER_TOOLTIPS.invoker().gatherTooltips(event);
        return event.getModifiers();
    }
}
