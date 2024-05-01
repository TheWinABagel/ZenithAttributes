package dev.shadowsoffire.attributeslib.mixin.compat.artifacts.present;

import artifacts.item.wearable.ArtifactAttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Invoker;

@Pseudo
@Mixin(ArtifactAttributeModifier.class)
public interface ArtifactAttributeModifierMixin {
	@Invoker
	AttributeModifier invokeCreateModifier();
}
