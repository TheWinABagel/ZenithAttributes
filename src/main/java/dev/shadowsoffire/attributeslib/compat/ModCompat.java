package dev.shadowsoffire.attributeslib.compat;

import com.glisco.conjuring.Conjuring;
import com.glisco.conjuring.items.ConjuringItems;
import com.glisco.conjuring.items.soul_alloy_tools.SoulAlloyTool;
import com.glisco.conjuring.mixin.ItemAccessor;
import dev.shadowsoffire.attributeslib.api.ItemAttributeModifierEvent;
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
                ItemAttributeModifierEvent.GATHER_TOOLTIPS.register(event -> {
                    if (event.stack.getItem() != ConjuringItems.SOUL_ALLOY_SWORD) return;
                    if (event.slot != EquipmentSlot.MAINHAND) return;

                    if (SoulAlloyTool.getModifierLevel(event.stack, SoulAlloyTool.SoulAlloyModifier.HASTE) < 1) return;

                    event.removeAttribute(Attributes.ATTACK_SPEED);

                    event.addModifier(Attributes.ATTACK_SPEED, new AttributeModifier(ItemAccessor.getAttackSpeedModifierID(), "Weapon modifier", -2.4f + Math.pow(SoulAlloyTool.getModifierLevel(event.stack, SoulAlloyTool.SoulAlloyModifier.HASTE), Conjuring.CONFIG.tools_config.sword_haste_exponent()), AttributeModifier.Operation.ADDITION));
                });
            }

    }


}
