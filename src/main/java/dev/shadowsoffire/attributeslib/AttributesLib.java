package dev.shadowsoffire.attributeslib;

import java.util.function.BiConsumer;

import dev.shadowsoffire.attributeslib.packet.CritParticleMessage;
import dev.shadowsoffire.attributeslib.util.FlyingAbility;
import io.github.fabricators_of_create.porting_lib.attributes.PortingLibAttributes;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.shadowsoffire.attributeslib.api.ALObjects;
import dev.shadowsoffire.attributeslib.compat.TrinketsCompat;
import dev.shadowsoffire.attributeslib.impl.AttributeEvents;
import dev.shadowsoffire.placebo.registry.DeferredHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;


public class AttributesLib implements ModInitializer {

    public static final String MODID = "zenith_attributes";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    /**
     * Static record of {@link Player#getAttackStrengthScale(float)} for use in damage events.<br>
     * Recorded in the {link PlayerAttackEvent} and valid for the entire chain, when a player attacks.
     */
    public static float localAtkStrength = 1;

    public static int knowledgeMult = 4;

    @Override
    public void onInitialize() {
        AttributeEvents.init();

        CritParticleMessage.init();
        ALObjects.bootstrap();
        FlyingAbility.init();

        Registry.register(BuiltInRegistries.PARTICLE_TYPE, loc("apoth_crit"), ALObjects.Particles.APOTH_CRIT);
        MobEffects.BLINDNESS.addAttributeModifier(Attributes.FOLLOW_RANGE, "f8c3de3d-1fea-4d7c-a8b0-22f63c4c3454", -0.75, Operation.MULTIPLY_TOTAL);
        if (MobEffects.SLOW_FALLING.getAttributeModifiers().isEmpty()) {
            MobEffects.SLOW_FALLING.addAttributeModifier(PortingLibAttributes.ENTITY_GRAVITY, "A5B6CF2A-2F7C-31EF-9022-7C3E7D5E6ABA", -0.07, Operation.ADDITION);
        }
        AttributeSupplier playerAttribs = DefaultAttributes.getSupplier(EntityType.PLAYER);
        for (Attribute attr : BuiltInRegistries.ATTRIBUTE.stream().toList()) {
            if (playerAttribs.hasAttribute(attr)) attr.setSyncable(true);
        }
        if (FabricLoader.getInstance().isModLoaded("trinkets")) TrinketsCompat.init();
    }

    @Environment(EnvType.CLIENT)
    public static TooltipFlag getTooltipFlag() {
        //if (World.isClient)
            return ClientAccess.getTooltipFlag();
        //return TooltipFlag.Default.NORMAL;
    }

    public static ResourceLocation loc(String path) {
        return new ResourceLocation(MODID, path);
    }



    private static class ClientAccess {
        static TooltipFlag getTooltipFlag() {
            return Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL;
        }
    }
}
