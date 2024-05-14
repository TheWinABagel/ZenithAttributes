package dev.shadowsoffire.attributeslib.mixin;

import dev.shadowsoffire.attributeslib.api.client.ItemTooltipCallbackWithPlayer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(value = ItemStack.class, priority = 700)
public class ItemStackMixin {

    // Injects just before ItemStack.TooltipPart.MODIFIERS is written to the tooltip to remember where to rewind to.
    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(method = "getTooltipLines(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/TooltipFlag;)Ljava/util/List;", at = @At(value = "INVOKE", ordinal = 4, target = "net/minecraft/world/item/ItemStack.shouldShowInTooltip(ILnet/minecraft/world/item/ItemStack$TooltipPart;)Z"), locals = LocalCapture.CAPTURE_FAILHARD, require = 1)
    public void zenith_attributes$tooltipMarker(@Nullable Player pPlayer, TooltipFlag pIsAdvanced, CallbackInfoReturnable<List<Component>> cir, List<Component> list) {
        if (isPolymerItem()) return;
        list.add(Component.literal("ZENITH_REMOVE_MARKER"));
    }

    // Injects just after ItemStack.TooltipPart.MODIFIERS is written to the tooltip to remember where to rewind to.
    @SuppressWarnings("InvalidInjectorMethodSignature")
    @Inject(method = "getTooltipLines(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/TooltipFlag;)Ljava/util/List;", at = @At(value = "INVOKE", ordinal = 1, target = "net/minecraft/world/item/ItemStack.hasTag()Z"), locals = LocalCapture.CAPTURE_FAILHARD, require = 1)
    public void zenith_attributes$tooltipMarker2(@Nullable Player player, TooltipFlag isAdvanced, CallbackInfoReturnable<List<Component>> cir, List<Component> list) {
        if (isPolymerItem()) return;
        list.add(Component.literal("ZENITH_REMOVE_MARKER_2"));
    }

    @Inject(method = "getTooltipLines", at = @At("RETURN"))
    private void zenith_attributes$getTooltip(Player entity, TooltipFlag tooltipContext, CallbackInfoReturnable<List<Component>> info) {
        ItemTooltipCallbackWithPlayer.EVENT.invoker().getTooltip((ItemStack) (Object) this, tooltipContext, info.getReturnValue(), entity);
    }

    @Unique
    private boolean isPolymerItem() {
        if (!FabricLoader.getInstance().isModLoaded("polymer-core")) return false;
        try {
            return ((ItemStack) (Object) this).getItem().getClass().isInstance(Class.forName("eu.pb4.polymer.core.api.item"));
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }
}
