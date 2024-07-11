package dev.shadowsoffire.attributeslib.mixin.compat.roughlyenoughitems.present;

import dev.shadowsoffire.attributeslib.client.AttributesGui;
import me.shedaniel.rei.api.client.config.ConfigObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Environment(EnvType.CLIENT)
@Mixin(value = EffectRenderingInventoryScreen.class, priority = 1500)
@Pseudo
abstract public class EffectRenderingInventoryScreenMixinREI extends AbstractContainerScreen<AbstractContainerMenu> {

    public EffectRenderingInventoryScreenMixinREI(AbstractContainerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @ModifyVariable(method = "renderEffects",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getActiveEffects()Ljava/util/Collection;", ordinal = 0),
            ordinal = 2) // 3rd int
    public int modifyK(int k) {
        if (!AttributesGui.wasOpen) return k;
        if (!ConfigObject.getInstance().isLeftSideMobEffects()) return k;
        return k - AttributesGui.WIDTH;
    }
}
