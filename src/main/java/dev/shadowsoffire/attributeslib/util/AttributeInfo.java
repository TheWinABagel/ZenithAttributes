package dev.shadowsoffire.attributeslib.util;

import dev.shadowsoffire.attributeslib.AttributesLib;
import dev.shadowsoffire.placebo.config.ConfigCategory;
import dev.shadowsoffire.placebo.config.Configuration;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;

public class AttributeInfo {
    protected final Attribute attribute;
    protected final boolean isModifiable, showsInMenu;

    public AttributeInfo(Attribute attribute, boolean showsInMenu, boolean isModifiable) {
        this.attribute = attribute;
        this.isModifiable = isModifiable;
        this.showsInMenu = showsInMenu;
    }

    public AttributeInfo(Attribute attribute) {
        this.attribute = attribute;
        this.isModifiable = true;
        this.showsInMenu = true;
    }

    public boolean getIsModfiable() {
        return isModifiable;
    }

    public boolean getShowsInMenu() {
        return showsInMenu;
    }

    public static AttributeInfo load(Attribute attribute, Configuration cfg) {
        try {
            ResourceLocation category = BuiltInRegistries.ATTRIBUTE.getKey(attribute);
            boolean modifiable = cfg.getBoolean("Modifiable", category.getNamespace().concat("."+category.getPath()), true, "If this attribute can be modified with Zenith's all attribute increase gem.");
            boolean shows = cfg.getBoolean("Shows in Menu", category.getNamespace().concat("."+category.getPath()), attribute.isClientSyncable(), "If this attribute shows in the inventory attribute menu.\nDefault value is based on if the attribute is synced to the client." + (attribute.isClientSyncable() ? "\nThis attribute is not client syncable, so changes made will not reflect in the menu even when enabled." : ""));

            String cat = BuiltInRegistries.ATTRIBUTE.getKey(attribute).toString();
            cfg.removeCategory(new ConfigCategory(cat));

            return new AttributeInfo(attribute, shows, modifiable);
        } catch (NullPointerException e) {
            AttributesLib.LOGGER.error("Attribute namespace for attribute {} is null! Attribute will not show up in player menu!", attribute.getDescriptionId());
            return new AttributeInfo(attribute, false, false);
        }
    }
}
