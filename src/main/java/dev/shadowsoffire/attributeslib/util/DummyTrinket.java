package dev.shadowsoffire.attributeslib.util;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import dev.shadowsoffire.attributeslib.api.ALObjects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.UUID;

public class DummyTrinket extends TrinketItem {
    public DummyTrinket(Properties settings) {
        super(settings);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, UUID uuid) {
        Multimap<Attribute, AttributeModifier> map = super.getModifiers(stack, slot, entity, uuid);
        map.put(ALObjects.Attributes.ARROW_VELOCITY, new AttributeModifier(uuid, "zenith_attributes:test", 2d, AttributeModifier.Operation.ADDITION));
        return map;
    }
}
