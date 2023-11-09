package dev.shadowsoffire.attributeslib.util;

import dev.shadowsoffire.attributeslib.AttributesLib;
import dev.shadowsoffire.placebo.config.Configuration;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.enchantment.Enchantment;

public class AttributeInfo {
    protected final Attribute attribute;
    protected final boolean isModifiable, showsInMenu;

    public AttributeInfo(Attribute attribute, boolean showsInMenu, boolean isModifiable){
        this.attribute = attribute;
        this.isModifiable = isModifiable;
        this.showsInMenu = showsInMenu;
    }

    public AttributeInfo(Attribute attribute){
        this.attribute = attribute;
        this.isModifiable = true;
        this.showsInMenu = true;
    }

    public boolean getIsModfiable(){
        return isModifiable;
    }

    public boolean getShowsInMenu(){
        return showsInMenu;
    }

    public static AttributeInfo load(Attribute attribute, Configuration cfg) {
        String category = BuiltInRegistries.ATTRIBUTE.getKey(attribute).toString();
        boolean modifiable = cfg.getBoolean("Modifiable", category, true, "If this attribute can be modified with Zenith's all attribute increase.");

        boolean shows = cfg.getBoolean("Shows in Menu", category, true, "If this attribute shows in the inventory attribute menu.");

        return new AttributeInfo(attribute, shows, modifiable);
    }
}
