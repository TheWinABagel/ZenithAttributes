package dev.shadowsoffire.attributeslib.compat;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public interface ZenithArtifactsItem {

    public default AttributeModifier zenithAttributes$getModifier() {
        return null;
    }
}
