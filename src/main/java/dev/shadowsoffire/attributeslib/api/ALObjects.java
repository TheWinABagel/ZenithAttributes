package dev.shadowsoffire.attributeslib.api;

import static dev.shadowsoffire.attributeslib.AttributesLib.R;

import dev.shadowsoffire.attributeslib.impl.BooleanAttribute;
import dev.shadowsoffire.attributeslib.mobfx.*;
import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import org.jetbrains.annotations.ApiStatus;

import dev.shadowsoffire.attributeslib.AttributesLib;
import dev.shadowsoffire.attributeslib.impl.PercentBasedAttribute;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public class ALObjects {

    public static class Attributes {

        /**
         * Flat armor penetration. Base value = (0.0) = 0 armor reduced during damage calculations.
         */
        public static final Attribute ARMOR_PIERCE = new RangedAttribute("attributeslib:armor_pierce", 0.0D, 0.0D, 1000.0D).setSyncable(true);

        /**
         * Percentage armor reduction. Base value = (0.0) = 0% of armor reduced during damage calculations.
         */
        public static final Attribute ARMOR_SHRED = new RangedAttribute("attributeslib:armor_shred", 0.0D, 0.0D, 2.0D).setSyncable(true);

        /**
         * Arrow Damage. Base value = (1.0) = 100% default arrow damage
         */
        public static final Attribute ARROW_DAMAGE = new RangedAttribute("attributeslib:arrow_damage", 1.0D, 0.0D, 10.0D).setSyncable(true);

        /**
         * Arrow Velocity. Base value = (1.0) = 100% default arrow velocity
         * <p>
         * Arrow damage scales with the velocity as well as {@link #ARROW_DAMAGE} and the base damage of the arrow entity.
         */
        public static final Attribute ARROW_VELOCITY = new RangedAttribute("attributeslib:arrow_velocity", 1.0D, 0.0D, 10.0D).setSyncable(true);

        /**
         * Bonus magic damage that slows enemies hit. Base value = (0.0) = 0 damage
         */
        public static final Attribute COLD_DAMAGE = new RangedAttribute("attributeslib:cold_damage", 0.0D, 0.0D, 1000.0D).setSyncable(true);

        /**
         * Chance that any attack will critically strike. Base value = (0.05) = 5% chance to critically strike.<br>
         * Not related to vanilla (jump) critical strikes.
         */
        public static final Attribute CRIT_CHANCE = new RangedAttribute("attributeslib:crit_chance", 0.05D, 0.0D, 10.0D).setSyncable(true);

        /**
         * Amount of damage caused by critical strikes. Base value = (1.5) = 150% normal damage dealt.<br>
         * Also impacts vanilla (jump) critical strikes.
         */
        public static final Attribute CRIT_DAMAGE = new RangedAttribute("attributeslib:crit_damage", 1.5D, 1.0D, 100.0D).setSyncable(true);

        /**
         * Bonus physical damage dealt equal to enemy's current health. Base value = (0.0) = 0%
         */
        public static final Attribute CURRENT_HP_DAMAGE = new RangedAttribute("attributeslib:current_hp_damage", 0.0D, 0.0D, 1.0D).setSyncable(true);

        /**
         * Chance to dodge incoming melee damage. Base value = (0.0) = 0% chance to dodge.<br>
         * "Melee" damage is considered as damage from another entity within the player's attack range.
         * <p>
         * This includes projectile attacks, as long as the projectile actually impacts the player.
         */
        public static final Attribute DODGE_CHANCE = new RangedAttribute("attributeslib:dodge_chance", 0.0D, 0.0D, 1.0D).setSyncable(true);

        /**
         * How fast a ranged weapon is charged. Base Value = (1.0) = 100% default draw speed.
         */
        public static final Attribute DRAW_SPEED = new RangedAttribute("attributeslib:draw_speed", 1.0D, 0.0D, 4.0D).setSyncable(true);

        /**
         * Experience mulitplier, from killing mobs or breaking ores. Base value = (1.0) = 100% xp gained.
         */
        public static final Attribute EXPERIENCE_GAINED = new RangedAttribute("attributeslib:experience_gained", 1.0D, 0.0D, 1000.0D).setSyncable(true);

        /**
         * Bonus magic damage that burns enemies hit. Base value = (0.0) = 0 damage
         */
        public static final Attribute FIRE_DAMAGE = new RangedAttribute("attributeslib:fire_damage", 0.0D, 0.0D, 1000.0D).setSyncable(true);

        /**
         * Extra health that regenerates when not taking damage. Base value = (0.0) = 0 damage
         */
        public static final Attribute GHOST_HEALTH = new RangedAttribute("attributeslib:ghost_health", 0.0D, 0.0D, 1000.0D).setSyncable(true);

        /**
         * Adjusts all healing received. Base value = (1.0) = 100% xp gained.
         */
        public static final Attribute HEALING_RECEIVED = new RangedAttribute("attributeslib:healing_received", 1.0D, 0.0D, 1000.0D).setSyncable(true);

        /**
         * Percent of physical damage converted to health. Base value = (0.0) = 0%
         */
        public static final Attribute LIFE_STEAL = new RangedAttribute("attributeslib:life_steal", 0.0D, 0.0D, 10.0D).setSyncable(true);

        /**
         * Mining Speed. Base value = (1.0) = 100% default break speed
         */
        public static final Attribute MINING_SPEED = new RangedAttribute("attributeslib:mining_speed", 1.0D, 0.0D, 10.0D).setSyncable(true);

        /**
         * Percent of physical damage converted to absorption hearts. Base value = (0.0) = 0%
         */
        public static final Attribute OVERHEAL = new RangedAttribute("attributeslib:overheal", 0.0D, 0.0D, 10.0D).setSyncable(true);

        /**
         * Flat protection penetration. Base value = (0.0) = 0 protection points bypassed during damage calculations.
         */
        public static final Attribute PROT_PIERCE = new RangedAttribute("attributeslib:prot_pierce", 0.0D, 0.0D, 34.0D).setSyncable(true);

        /**
         * Percentage protection reduction. Base value = (0.0) = 0% of protection points bypassed during damage calculations.
         */
        public static final Attribute PROT_SHRED = new RangedAttribute("attributeslib:prot_shred", 0.0D, 0.0D, 1.0D).setSyncable(true);

        /**
         * Boolean attribute for if elytra flight is enabled. Default value = false.
         */
        public static final Attribute ELYTRA_FLIGHT = new BooleanAttribute("attributeslib:elytra_flight", false).setSyncable(true);

        /**
         * Boolean attribute for if creative flight is enabled. Default value = false.
         */
        public static final Attribute CREATIVE_FLIGHT = new BooleanAttribute("attributeslib:creative_flight", false).setSyncable(true);

        @ApiStatus.Internal
        public static void bootstrap() {
            register();
        }

        private static void register(){
            Registry.register(BuiltInRegistries.ATTRIBUTE, AttributesLib.loc("draw_speed"), DRAW_SPEED);
            Registry.register(BuiltInRegistries.ATTRIBUTE, AttributesLib.loc("crit_chance"), CRIT_CHANCE);
            Registry.register(BuiltInRegistries.ATTRIBUTE, AttributesLib.loc("crit_damage"), CRIT_DAMAGE);
            Registry.register(BuiltInRegistries.ATTRIBUTE, AttributesLib.loc("cold_damage"), COLD_DAMAGE);
            Registry.register(BuiltInRegistries.ATTRIBUTE, AttributesLib.loc("fire_damage"), FIRE_DAMAGE);
            Registry.register(BuiltInRegistries.ATTRIBUTE, AttributesLib.loc("life_steal"), LIFE_STEAL);
            Registry.register(BuiltInRegistries.ATTRIBUTE, AttributesLib.loc("current_hp_damage"), CURRENT_HP_DAMAGE);
            Registry.register(BuiltInRegistries.ATTRIBUTE, AttributesLib.loc("overheal"), OVERHEAL);
            Registry.register(BuiltInRegistries.ATTRIBUTE, AttributesLib.loc("ghost_health"), GHOST_HEALTH);
            Registry.register(BuiltInRegistries.ATTRIBUTE, AttributesLib.loc("mining_speed"), MINING_SPEED);
            Registry.register(BuiltInRegistries.ATTRIBUTE, AttributesLib.loc("arrow_damage"), ARROW_DAMAGE);
            Registry.register(BuiltInRegistries.ATTRIBUTE, AttributesLib.loc("arrow_velocity"), ARROW_VELOCITY);
            Registry.register(BuiltInRegistries.ATTRIBUTE, AttributesLib.loc("experience_gained"), EXPERIENCE_GAINED);
            Registry.register(BuiltInRegistries.ATTRIBUTE, AttributesLib.loc("healing_received"), HEALING_RECEIVED);
            Registry.register(BuiltInRegistries.ATTRIBUTE, AttributesLib.loc("armor_pierce"), ARMOR_PIERCE);
            Registry.register(BuiltInRegistries.ATTRIBUTE, AttributesLib.loc("armor_shred"), ARMOR_SHRED);
            Registry.register(BuiltInRegistries.ATTRIBUTE, AttributesLib.loc("prot_pierce"), PROT_PIERCE);
            Registry.register(BuiltInRegistries.ATTRIBUTE, AttributesLib.loc("prot_shred"), PROT_SHRED);
            Registry.register(BuiltInRegistries.ATTRIBUTE, AttributesLib.loc("dodge_chance"), DODGE_CHANCE);
            Registry.register(BuiltInRegistries.ATTRIBUTE, AttributesLib.loc("elytra_flight"), ELYTRA_FLIGHT);
            Registry.register(BuiltInRegistries.ATTRIBUTE, AttributesLib.loc("creative_flight"), CREATIVE_FLIGHT);
        }
    }

    public static class MobEffects {

        /**
         * Bleeding inflicts 1 + level damage every two seconds. Things that apply bleeding usually stack.
         */
        public static final BleedingEffect BLEEDING = new BleedingEffect();

        /**
         * Flaming Detonation, when it expires, consumes all fire ticks and deals armor-piercing damage based on the duration.
         */
        public static final DetonationEffect DETONATION = new DetonationEffect();

        /**
         * Grievous Wounds reduces healing received by 40%/level.
         */
        public static final GrievousEffect GRIEVOUS = new GrievousEffect();

        /**
         * Ancient Knowledge multiplies experience dropped by mobs by level * {link MobFxLib#knowledgeMult}.<br>
         * The multiplier is configurable.
         */
        public static final KnowledgeEffect KNOWLEDGE = new KnowledgeEffect();

        /**
         * Sundering is the inverse of resistance. It increases damage taken by 20%/level.<br>
         * Each point of sundering cancels out a single point of resistance, if present.
         */
        public static final SunderingEffect SUNDERING = new SunderingEffect();

        /**
         * Bursting Vitality increases healing received by 20%/level.
         */
        public static final VitalityEffect VITALITY = new VitalityEffect();

        /**
         * Grants Creative Flight
         */
        public static final FlyingEffect FLYING = new FlyingEffect();

        @ApiStatus.Internal
        public static void bootstrap() {
            register();
        }

        private static void register(){
            Registry.register(BuiltInRegistries.MOB_EFFECT, AttributesLib.loc("bleeding"), BLEEDING);
            Registry.register(BuiltInRegistries.MOB_EFFECT, AttributesLib.loc("detonation"), DETONATION);
            Registry.register(BuiltInRegistries.MOB_EFFECT, AttributesLib.loc("grievous"), GRIEVOUS);
            Registry.register(BuiltInRegistries.MOB_EFFECT, AttributesLib.loc("knowledge"), KNOWLEDGE);
            Registry.register(BuiltInRegistries.MOB_EFFECT, AttributesLib.loc("sundering"), SUNDERING);
            Registry.register(BuiltInRegistries.MOB_EFFECT, AttributesLib.loc("vitality"), VITALITY);
            Registry.register(BuiltInRegistries.MOB_EFFECT, AttributesLib.loc("flying"), FLYING);
        }

    }

    public static class Particles {

    //   public static final RegistryObject<SimpleParticleType> APOTH_CRIT = R.particle("apoth_crit", () -> new SimpleParticleType(false));

        @ApiStatus.Internal
        public static void bootstrap() {}

    }

    public static class Sounds {

        public static final RegistryObject<SoundEvent> DODGE = R.sound("dodge");

        @ApiStatus.Internal
        public static void bootstrap() {}

    }

    public static class DamageTypes {

        /**
         * Damage type used by {@link MobEffects#BLEEDING}. Bypasses armor.
         */
        public static final ResourceKey<DamageType> BLEEDING = ResourceKey.create(Registries.DAMAGE_TYPE, AttributesLib.loc("bleeding"));

        /**
         * Damage type used by {@link MobEffects#DETONATION}. Bypasses armor, and is marked as magic damage.
         */
        public static final ResourceKey<DamageType> DETONATION = ResourceKey.create(Registries.DAMAGE_TYPE, AttributesLib.loc("detonation"));

        /**
         * Damage type used by {@link Attributes#CURRENT_HP_DAMAGE}. Same properties as generic physical damage. Has attacker context.
         */
        public static final ResourceKey<DamageType> CURRENT_HP_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, AttributesLib.loc("current_hp_damage"));

        /**
         * Damage type used by {@link Attributes#FIRE_DAMAGE}. Bypasses armor, and is marked as magic damage. Has attacker context.<br>
         * Not marked as fire damage until fire resistance is reworked to not block all fire damage.
         */
        public static final ResourceKey<DamageType> FIRE_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, AttributesLib.loc("fire_damage"));

        /**
         * Damage type used by {@link Attributes#COLD_DAMAGE}. Bypasses armor, and is marked as magic damage. Has attacker context.
         */
        public static final ResourceKey<DamageType> COLD_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, AttributesLib.loc("cold_damage"));

        @ApiStatus.Internal
        public static void bootstrap() {}
    }

    @ApiStatus.Internal
    public static void bootstrap() {
        Attributes.bootstrap();
        MobEffects.bootstrap();
        Particles.bootstrap();
        Sounds.bootstrap();
        DamageTypes.bootstrap();
    }
}
