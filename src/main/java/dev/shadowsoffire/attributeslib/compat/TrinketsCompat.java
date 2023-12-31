package dev.shadowsoffire.attributeslib.compat;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import dev.shadowsoffire.attributeslib.client.ModifierSource;
import dev.shadowsoffire.attributeslib.client.ModifierSourceType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;
import java.util.function.BiConsumer;

public class TrinketsCompat {

    static {
        if (!FabricLoader.getInstance().isModLoaded("trinkets")) {
            throw new UnsupportedOperationException("This optional compat class requires Trinkets to be loaded.");
        }
    }

    public static void init() {
        ModifierSourceType.register(new ModifierSourceType<>() {

            @Override
            public void extract(LivingEntity entity, BiConsumer<AttributeModifier, ModifierSource<?>> map) {
                if (!(entity instanceof Player)) return;
                TrinketsApi.getTrinketComponent(entity).ifPresent(trinketComponent -> {
                    trinketComponent.getInventory().forEach((slotGroupId, stringTrinketInventoryMap) -> {
                        stringTrinketInventoryMap.forEach((slotId, trinketInventory) -> {
                            for (int i = 0; i < trinketInventory.getContainerSize(); i++) {
                                ItemStack stack = trinketInventory.getItem(i);
                                if (!stack.isEmpty() && stack.getItem() instanceof TrinketItem trinket) {
                                    SlotReference ref = new SlotReference(trinketInventory, i);
                                    UUID uuid = UUID.nameUUIDFromBytes((slotId + i).getBytes());
                                    com.google.common.collect.Multimap<Attribute, AttributeModifier> modifiers = trinket.getModifiers(stack, ref, entity, uuid);
                                    ModifierSource<?> src = new ModifierSource.ItemModifierSource(stack);
                                    modifiers.values().forEach(m -> map.accept(m, src));
                                }
                            }
                        });
                    });
                });
            }

            @Override
            public int getPriority() {
                return 20;
            }
        });

    }
}
