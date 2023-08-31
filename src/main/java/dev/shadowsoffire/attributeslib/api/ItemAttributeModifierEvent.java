package dev.shadowsoffire.attributeslib.api;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class ItemAttributeModifierEvent {
    public static final Event<ItemAttributeModifier> GATHER_TOOLTIPS = EventFactory.createArrayBacked(ItemAttributeModifier.class, callbacks -> (event) -> {
        for (ItemAttributeModifier callback : callbacks) {
            callback.gatherTooltips(event);
        }
    });

    @FunctionalInterface
    public interface ItemAttributeModifier {
        public void gatherTooltips(AttributeModifierEvent event);
    }

    public static class AttributeModifierEvent {

        public final Multimap<Attribute, AttributeModifier> originalModifiers;
        public Multimap<Attribute, AttributeModifier> nonChangableModifiers;
        @Nullable
        private Multimap<Attribute, AttributeModifier> changableModifiers;
        public final ItemStack stack;
        public final EquipmentSlot slot;

        public AttributeModifierEvent(ItemStack stack, EquipmentSlot slot, Multimap<Attribute, AttributeModifier> modifiers) {
            this.stack = stack;
            this.slot = slot;
            this.nonChangableModifiers = this.originalModifiers = modifiers;
        }

        private Multimap<Attribute, AttributeModifier> getModifiableMap() {
            if (this.changableModifiers == null) {
                this.changableModifiers = HashMultimap.create(this.originalModifiers);
                this.nonChangableModifiers = Multimaps.unmodifiableMultimap(this.changableModifiers);
            }
            return this.changableModifiers;
        }

        public boolean addModifier(Attribute attribute, AttributeModifier modifier) {
            return getModifiableMap().put(attribute, modifier);
        }

        public boolean removeModifier(Attribute attribute, AttributeModifier modifier) {
            return getModifiableMap().remove(attribute, modifier);
        }

        public Collection<AttributeModifier> removeAttribute(Attribute attribute) {
            return getModifiableMap().removeAll(attribute);
        }

        public void clearModifiers() {
            getModifiableMap().clear();
        }

    }
}
