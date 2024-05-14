package dev.shadowsoffire.attributeslib.compat;

import com.glisco.conjuring.Conjuring;
import com.glisco.conjuring.items.ConjuringItems;
import com.glisco.conjuring.items.soul_alloy_tools.SoulAlloyTool;
import com.glisco.conjuring.mixin.ItemAccessor;
import net.fabricmc.fabric.api.item.v1.ModifyItemAttributeModifiersCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class ModCompat {

    public static void init() {
        fixConjuringSwordHaste();
    }

    public static void fixConjuringSwordHaste() {
            if (FabricLoader.getInstance().isModLoaded("conjuring")) {
                ModifyItemAttributeModifiersCallback.EVENT.register((stack, slot, attributeModifiers) -> {
                    if (stack.getItem() != ConjuringItems.SOUL_ALLOY_SWORD) return;
                    if (slot != EquipmentSlot.MAINHAND) return;

                    if (SoulAlloyTool.getModifierLevel(stack, SoulAlloyTool.SoulAlloyModifier.HASTE) < 1) return;

                    attributeModifiers.removeAll(Attributes.ATTACK_SPEED);

                    attributeModifiers.put(Attributes.ATTACK_SPEED,
                            new AttributeModifier(ItemAccessor.getAttackSpeedModifierID(), "Weapon modifier",
                                    -2.4f + Math.pow(SoulAlloyTool.getModifierLevel(stack, SoulAlloyTool.SoulAlloyModifier.HASTE), Conjuring.CONFIG.tools_config.sword_haste_exponent()), AttributeModifier.Operation.ADDITION));
                });
            }

    }


}
