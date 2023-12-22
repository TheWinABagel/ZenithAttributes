package dev.shadowsoffire.attributeslib.mixin.accessors;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractContainerScreen.class)
public interface AbstractContainerScreenAccessor {
    @Accessor
    int getLeftPos();

    @Accessor
    int getTopPos();

    @Accessor
    void setTopPos(int topPos);

    @Accessor
    void setLeftPos(int leftPos);

    @Accessor
    int getImageWidth();

    @Accessor
    void setImageWidth(int imageWidth);

    @Accessor
    int getImageHeight();

    @Accessor
    void setImageHeight(int imageHeight);
}
