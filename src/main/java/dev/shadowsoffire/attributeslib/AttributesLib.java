package dev.shadowsoffire.attributeslib;

import dev.shadowsoffire.attributeslib.api.ALObjects;
import dev.shadowsoffire.attributeslib.compat.TrinketsCompat;
import dev.shadowsoffire.attributeslib.impl.AttributeEvents;
import dev.shadowsoffire.attributeslib.mixin.accessors.AttributeSupplierBuilderAccessor;
import dev.shadowsoffire.attributeslib.packet.CritParticleMessage;
import dev.shadowsoffire.attributeslib.util.AttributeInfo;
import dev.shadowsoffire.attributeslib.util.DummyLootModifier;
import dev.shadowsoffire.attributeslib.util.FlyingAbility;
import dev.shadowsoffire.placebo.config.Configuration;
import io.github.fabricators_of_create.porting_lib.attributes.PortingLibAttributes;
import io.github.fabricators_of_create.porting_lib.loot.PortingLibLoot;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class AttributesLib implements ModInitializer {

    public static final String MODID = "zenith_attributes";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static File configDir;
    static Configuration attributeInfoConfig;
    public static final Map<Attribute, AttributeInfo> ATTRIBUTE_INFO = new HashMap<>();

    /**
     * Static record of {@link Player#getAttackStrengthScale(float)} for use in damage events.<br>
     * Obtained from {@link dev.shadowsoffire.attributeslib.mixin.PlayerMixin} and valid for the entire chain, when a player attacks.
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

        if (FabricLoader.getInstance().getEnvironmentType().equals(EnvType.SERVER)) {
            ServerLifecycleEvents.SERVER_STARTING.register(server -> {
                AttributesLib.reload(false);
            });
        }
        //This is to prevent potential issues with this registry not being registered properly, will be removed when fixed
        Registry.register(PortingLibLoot.GLOBAL_LOOT_MODIFIER_SERIALIZERS.get(), new ResourceLocation(MODID, "dummy"), DummyLootModifier.CODEC);
    }

    @Environment(EnvType.CLIENT)
    public static TooltipFlag getTooltipFlag() {
            return ClientAccess.getTooltipFlag();
    }

    static {
        configDir = new File(FabricLoader.getInstance().getConfigDir().toFile(), "zenith");
    }

    public static ResourceLocation loc(String path) {
        return new ResourceLocation(MODID, path);
    }

    public static void reload(boolean e) {
        attributeInfoConfig = new Configuration(new File(configDir, "attributes.cfg"));
        attributeInfoConfig.setTitle("Zenith Attributes Information");
        attributeInfoConfig.setComment("This file contains configurable data for each attribute.\nThe names of each category correspond to the registry names of every loaded attribute.");
        ATTRIBUTE_INFO.clear();
        ((AttributeSupplierBuilderAccessor) Player.createAttributes()).getBuilder().forEach((attribute, attributeInstance) -> {
            ATTRIBUTE_INFO.put(attribute, AttributeInfo.load(attribute, attributeInfoConfig));
        });


        if (!e && attributeInfoConfig.hasChanged()) attributeInfoConfig.save();
    }

    @SuppressWarnings("deprecation")
    public static AttributeInfo getAttrInfo(Attribute attribute) {
        AttributeInfo info = ATTRIBUTE_INFO.get(attribute);

        if (attributeInfoConfig == null) {
            return new AttributeInfo(attribute);
        }
        if (info == null) {
            info = AttributeInfo.load(attribute, attributeInfoConfig);
            ATTRIBUTE_INFO.put(attribute, info);
            if (attributeInfoConfig.hasChanged()) attributeInfoConfig.save();
        }

        return info;
    }

    private static class ClientAccess {
        static TooltipFlag getTooltipFlag() {
            return Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL;
        }
    }
}
