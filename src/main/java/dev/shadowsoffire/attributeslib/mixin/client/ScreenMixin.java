package dev.shadowsoffire.attributeslib.mixin.client;

import dev.shadowsoffire.attributeslib.client.AttributesGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public abstract class ScreenMixin {
    @Shadow protected abstract <T extends GuiEventListener & Renderable> T addRenderableWidget(T widget);
    @Unique private boolean hasLoaded = false;

    @Inject(method = "rebuildWidgets", at = @At(value = "TAIL"), cancellable = true)
    private void postInit(CallbackInfo ci) {
        if ((Screen) (Object) this instanceof InventoryScreen scn){
            var atrComp = new AttributesGui(scn);
            this.addRenderableWidget(atrComp);
            this.addRenderableWidget(atrComp.toggleBtn);
            this.addRenderableWidget(atrComp.hideUnchangedBtn);
            if (AttributesGui.wasOpen) atrComp.toggleVisibility();
            hasLoaded = true;
        }

    }

        @Inject(method = "init(Lnet/minecraft/client/Minecraft;II)V", at = @At(value = "INVOKE", target = "net/minecraft/client/gui/screens/Screen.init ()V", shift = At.Shift.AFTER), cancellable = true)
        private void postInit(Minecraft minecraft, int width, int height, CallbackInfo ci) {
            if ((Screen) (Object) this instanceof InventoryScreen scn){
                var atrComp = new AttributesGui(scn);
                this.addRenderableWidget(atrComp);
                this.addRenderableWidget(atrComp.toggleBtn);
                this.addRenderableWidget(atrComp.hideUnchangedBtn);
                if (AttributesGui.wasOpen) atrComp.toggleVisibility();
                hasLoaded = true;
            }

        }

    @Inject(method = "init()V", at = @At("HEAD"))
    private void insertScreen(CallbackInfo ci){
        if ((Screen) (Object) this instanceof InventoryScreen scn && !hasLoaded){
            var atrComp = new AttributesGui(scn);
            this.addRenderableWidget(atrComp);
            this.addRenderableWidget(atrComp.toggleBtn);
            this.addRenderableWidget(atrComp.hideUnchangedBtn);
            if (AttributesGui.wasOpen) atrComp.toggleVisibility();
            this.hasLoaded = false;
        }
    }
}
