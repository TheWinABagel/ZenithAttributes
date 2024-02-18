package dev.shadowsoffire.attributeslib.impl;

import com.glisco.conjuring.Conjuring;
import com.glisco.conjuring.items.ConjuringItems;
import com.glisco.conjuring.items.soul_alloy_tools.SoulAlloyTool;
import com.glisco.conjuring.mixin.ItemAccessor;
import dev.shadowsoffire.attributeslib.AttributesLib;
import dev.shadowsoffire.attributeslib.api.ItemAttributeModifierEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.LivingEntityEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.puffish.skillsmod.access.EntityAttributeInstanceAccess;
import net.puffish.skillsmod.server.setup.SkillsAttributes;

public class ModCompat {

    public static void init() {
        fixPufferfishSkills();
        fixConjuringSwordHaste();
    }


    public static void fixPufferfishSkills() {
        try {
            if (FabricLoader.getInstance().isModLoaded("puffish_skills") && FabricLoader.getInstance().getModContainer("puffish_skills").orElseThrow().getMetadata().getVersion().getFriendlyString().startsWith("0.11")) {
                LivingEntityEvents.HURT.register((source, damaged, amount) -> {
                    if (source.getEntity() instanceof Player player) {
                        if (source.is(DamageTypeTags.IS_PROJECTILE)) {
                            var attribute = ((EntityAttributeInstanceAccess) player.getAttribute(SkillsAttributes.RANGED_DAMAGE));
                            if (attribute != null) {
                                amount = (float) attribute.computeIncreasedValueForInitial(amount);
                            }
                        } else {
                            var attribute = ((EntityAttributeInstanceAccess) player.getAttribute(SkillsAttributes.MELEE_DAMAGE));
                            if (attribute != null) {
                                amount = (float) attribute.computeIncreasedValueForInitial(amount);
                            }
                        }
                    }
                    return amount;
                });
            }
        }
        catch (Exception e) {
            AttributesLib.LOGGER.error("Pufferfish skills compat is broken! Report on Zenith Attributes github!");
        }
    }

    public static void fixConjuringSwordHaste() {
        try {
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
        catch (Exception e) {
            AttributesLib.LOGGER.error("Conjuring compat is broken! Report on Zenith Attributes github!");
        }
    }


}
