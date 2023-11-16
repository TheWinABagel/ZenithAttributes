package dev.shadowsoffire.attributeslib.impl;

import com.glisco.conjuring.Conjuring;
import com.glisco.conjuring.items.ConjuringItems;
import com.glisco.conjuring.items.soul_alloy_tools.SoulAlloyTool;
import com.glisco.conjuring.mixin.ItemAccessor;
import com.google.common.collect.Multimap;
import dev.shadowsoffire.attributeslib.api.ItemAttributeModifierEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingEntityDamageEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.puffish.skillsmod.access.EntityAttributeInstanceAccess;
import net.puffish.skillsmod.server.PlayerAttributes;

public class ModCompat {

    public static void init() {
        fixPufferfishSkills();
        fixConjuringSwordHaste();
    }


    public static void fixPufferfishSkills() {
        if (FabricLoader.getInstance().isModLoaded("puffish_skills")) {
            LivingEntityDamageEvents.HURT.register(e -> {
                if (e.damageSource.getEntity() instanceof Player player) {
                    if (e.damageSource.is(DamageTypeTags.IS_PROJECTILE)) {
                        var attribute = ((EntityAttributeInstanceAccess) player.getAttribute(PlayerAttributes.RANGED_DAMAGE));
                        if (attribute != null) {
                            e.damageAmount = (float) attribute.computeIncreasedValueForInitial(e.damageAmount);
                        }
                    } else {
                        var attribute = ((EntityAttributeInstanceAccess) player.getAttribute(PlayerAttributes.MELEE_DAMAGE));
                        if (attribute != null) {
                            e.damageAmount = (float) attribute.computeIncreasedValueForInitial(e.damageAmount);
                        }
                    }
                }
            });
        }
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
