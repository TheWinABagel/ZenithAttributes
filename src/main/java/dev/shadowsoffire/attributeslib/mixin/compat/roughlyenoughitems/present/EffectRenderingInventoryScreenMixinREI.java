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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * This is just taken from REI, and slightly edited.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = EffectRenderingInventoryScreen.class, priority = 1500)
abstract public class EffectRenderingInventoryScreenMixinREI extends AbstractContainerScreen<AbstractContainerMenu> {

    public EffectRenderingInventoryScreenMixinREI(AbstractContainerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Unique
    private boolean leftSideEffects() {
        return ConfigObject.getInstance().isLeftSideMobEffects();
    }

    @ModifyVariable(method = "renderEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getActiveEffects()Ljava/util/Collection;", ordinal = 0), ordinal = 2)
    public int modifyK(int k) {
        if (!this.leftSideEffects()) {
            return k;
        } else {
            int offset = AttributesGui.wasOpen ? AttributesGui.WIDTH : 0;
            boolean bl = this.leftPos - offset >= 120;
            return bl ? this.leftPos - 120 - 4 - offset : this.leftPos - 32 - 4 - offset;
        }
    }

    @ModifyVariable(method = {"renderEffects"}, at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Ordering;sortedCopy(Ljava/lang/Iterable;)Ljava/util/List;", ordinal = 0), ordinal = 0)
    public boolean modifyBl(boolean bl) {
        if (!this.leftSideEffects()) {
            return bl;
        } else {
            int offset = AttributesGui.wasOpen ? AttributesGui.WIDTH : 0;
            return this.leftPos - offset >= 120;
        }
    }
}
