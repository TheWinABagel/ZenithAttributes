package dev.shadowsoffire.attributeslib.mixin.compat.artifacts.present;

import artifacts.item.wearable.AttributeModifyingItem;
import dev.shadowsoffire.attributeslib.compat.ZenithArtifactsItem;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

@Pseudo
@Mixin(AttributeModifyingItem.class)
public abstract class AttributeModifyingItemMixin implements ZenithArtifactsItem {
    @Shadow protected abstract AttributeModifier createModifier();

    @Override
    public AttributeModifier zenithAttributes$getModifier() {
        return this.createModifier();
    }
}
