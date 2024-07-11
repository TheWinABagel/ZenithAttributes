package dev.shadowsoffire.attributeslib.impl;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.dafuqs.additionalentityattributes.AdditionalEntityAttributes;
import dev.shadowsoffire.attributeslib.ALConfig;
import dev.shadowsoffire.attributeslib.AttributesLib;
import dev.shadowsoffire.attributeslib.api.ALObjects;
import dev.shadowsoffire.attributeslib.api.AttributeHelper;
import dev.shadowsoffire.attributeslib.api.IFormattableAttribute;
import dev.shadowsoffire.attributeslib.api.events.LivingHealEvent;
import dev.shadowsoffire.attributeslib.api.events.LivingHurtEvent;
import dev.shadowsoffire.attributeslib.commands.ModifierCommand;
import dev.shadowsoffire.attributeslib.compat.ModCompat;
import dev.shadowsoffire.attributeslib.components.ZenithAttributesComponents;
import dev.shadowsoffire.attributeslib.packet.CritParticleMessage;
import dev.shadowsoffire.attributeslib.util.AttributesUtil;
import dev.shadowsoffire.attributeslib.util.FlyingAbility;
import dev.shadowsoffire.placebo.events.ReloadableServerEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.EntityEvents;
import io.github.fabricators_of_create.porting_lib.entity.events.PlayerEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents;
import net.fabricmc.fabric.api.entity.event.v1.FabricElytraItem;
import net.fabricmc.fabric.api.item.v1.ModifyItemAttributeModifiersCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Objects;
import java.util.Random;

public class AttributeEvents {

    private static boolean canBenefitFromDrawSpeed(ItemStack stack) {
        return stack.getItem() instanceof ProjectileWeaponItem || stack.getItem() instanceof TridentItem;
    }

    public static void init(){
        elytra();
        lifeStealOverheal();
        meleeDamageAttributes();
        apothCriticalStrike();
        breakSpd();
        heal();
        dodgeMelee();
        dodgeProjectile();
        affixModifiers();
        commands();
        reloads();
        ModCompat.init();
        FlyingAbility.initZenithFlyingAbility();
    }

    /**
     * This handles the elytra attribute.
     */
    public static void elytra() {
        EntityElytraEvents.CUSTOM.register((entity, tickElytra) -> {
            ItemStack chestplate = entity.getItemBySlot(EquipmentSlot.CHEST);
            if (chestplate.getItem() instanceof ElytraItem) {
                return false;
            }
            return entity.getAttributeValue(ALObjects.Attributes.ELYTRA_FLIGHT) > 0;
        });
    }

    /**
     * This is the implementation for {link ALObjects#DRAW_SPEED}.<br>
     * Each full point of draw speed provides an extra using tick per game tick.<br>
     * Each partial point of draw speed provides an extra using tick periodically.
     */
    public static int drawSpeed(Entity entity, ItemStack usingItem, int useItemRemaining) {
            if (entity instanceof Player player) {
                double t = player.getAttributeValue(ALObjects.Attributes.DRAW_SPEED) - 1;
                if (t == 0 || !canBenefitFromDrawSpeed(usingItem)) return useItemRemaining;

                // Handle negative draw speed.
                int offset = -1;
                if (t < 0) {
                    offset = 1;
                    t = -t;
                }

                if (t > 1) { // Every 100% triggers an immediate extra tick
                    t--;
                    return (useItemRemaining + offset);
                }

                if (t > 0.5F) { // Special case 0.5F so that values in (0.5, 1) don't round to 1.
                    if (player.tickCount % 2 == 0) return (useItemRemaining + offset);
                    t -= 0.5F;
                }

                int mod = (int) Math.floor(1 / Math.min(1, t));
                if (entity.tickCount % mod == 0) return (useItemRemaining + offset);
                t--;
            }
            return useItemRemaining;
    }

    /**
     * This event handler manages the Life Steal and Overheal attributes.
     */
    public static void lifeStealOverheal() {
        LivingHurtEvent.EVENT.register((source, damaged, amount) -> {
            if (source.getDirectEntity() instanceof LivingEntity attacker && AttributesUtil.isPhysicalDamage(source)) {
                float lifesteal = (float) attacker.getAttributeValue(ALObjects.Attributes.LIFE_STEAL);
                float dmg = Math.min(amount, attacker.getHealth());
                if (lifesteal > 0.001) {
                    attacker.heal(dmg * lifesteal);
                }
                float overheal = (float) attacker.getAttributeValue(ALObjects.Attributes.OVERHEAL);
                float maxOverheal = attacker.getMaxHealth() * 0.5F;
                if (overheal > 0 && attacker.getAbsorptionAmount() < maxOverheal) {
                    attacker.setAbsorptionAmount(Math.min(maxOverheal, attacker.getAbsorptionAmount() + dmg * overheal));
                }
            }
            return amount;
        });

    }

    /**
     * Recursion guard for {@link #meleeDamageAttributes()}.<br>
     * Doesn't need to be ThreadLocal as attack logic is main-thread only.
     */
    private static boolean noRecurse = false;

    /**
     * Applies the following melee damage attributes:<br>
     * <ul>
     * <li>{link ALObjects#CURRENT_HP_DAMAGE}</li>
     * <li>{link ALObjects#FIRE_DAMAGE}</li>
     * <li>{link ALObjects#COLD_DAMAGE}</li>
     * </ul>
     */

    public static void meleeDamageAttributes() {
        LivingHurtEvent.EVENT.register((source, damaged, amount) -> {
            if (damaged.level().isClientSide || damaged.isDeadOrDying()) return amount;
            if (noRecurse) return amount;
            noRecurse = true;
            if (source.getDirectEntity() instanceof LivingEntity attacker && AttributesUtil.isPhysicalDamage(source)) {
                float hpDmg = (float) attacker.getAttributeValue(ALObjects.Attributes.CURRENT_HP_DAMAGE);
                float fireDmg = (float) attacker.getAttributeValue(ALObjects.Attributes.FIRE_DAMAGE);
                float coldDmg = (float) attacker.getAttributeValue(ALObjects.Attributes.COLD_DAMAGE);
                int time = damaged.invulnerableTime;
                damaged.invulnerableTime = 0;
                if (hpDmg > 0.001 && AttributesLib.localAtkStrength >= 0.85F) {
                    damaged.hurt(src(ALObjects.DamageTypes.CURRENT_HP_DAMAGE, attacker), AttributesLib.localAtkStrength * hpDmg * damaged.getHealth());
                }
                damaged.invulnerableTime = 0;
                if (fireDmg > 0.001 && AttributesLib.localAtkStrength >= 0.55F) {
                    damaged.hurt(src(ALObjects.DamageTypes.FIRE_DAMAGE, attacker), AttributesLib.localAtkStrength * fireDmg);
                    damaged.setRemainingFireTicks(damaged.getRemainingFireTicks() + (int) (10 * fireDmg));
                }
                damaged.invulnerableTime = 0;
                if (coldDmg > 0.001 && AttributesLib.localAtkStrength >= 0.55F) {
                    damaged.hurt(src(ALObjects.DamageTypes.COLD_DAMAGE, attacker), AttributesLib.localAtkStrength * coldDmg);
                    damaged.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, (int) (15 * coldDmg), Mth.floor(coldDmg / 5)));
                }
                damaged.invulnerableTime = time;
                if (damaged.isDeadOrDying()) {
                    damaged.getCustomData().putBoolean("zenith.killed_by_aux_dmg", true);
                }
            }
            noRecurse = false;
            return amount;
        });


    }

    private static DamageSource src(ResourceKey<DamageType> type, LivingEntity entity) {
        return entity.level().damageSources().source(type, entity);
    }

    /**
     * Handles {@link ALObjects.Attributes#CRIT_CHANCE} and {link ALObjects#CRIT_DAMAGE}
     */

    public static void apothCriticalStrike() {
        LivingHurtEvent.EVENT.register((source, damaged, amount) -> {
            LivingEntity attacker = source.getEntity() instanceof LivingEntity le ? le : null;
            if (attacker == null) return amount;

            double critChance = attacker.getAttributeValue(ALObjects.Attributes.CRIT_CHANCE);
            float critDmg =  1 + (float) attacker.getAttributeValue(AdditionalEntityAttributes.CRITICAL_BONUS_DAMAGE);

            RandomSource rand = damaged.getRandom();
            float critMult = 1.0F;

            // Roll for crits. Each overcrit reduces the effectiveness by 15%
            // We stop rolling when crit chance fails or the crit damage would reduce the total damage dealt.
            while (rand.nextFloat() <= critChance && critDmg > 0.0F) {
                critChance--;
                critMult *= critDmg;
                critDmg *= 0.85F;
            }

            amount *= critMult;

            if (critMult > 1 && !attacker.level().isClientSide && attacker instanceof Player player) {
                CritParticleMessage.sendTo(damaged.getId(), player);
            }
            return amount;
        });

    }

    /**
     * Handles {link ALObjects#MINING_SPEED}
     */

    public static void breakSpd() {
        PlayerEvents.BREAK_SPEED.register(event -> {
            event.setNewSpeed(event.getOriginalSpeed() * (float) event.getPlayer().getAttributeValue(ALObjects.Attributes.MINING_SPEED));
        });
    }

    /**
     * Handles {link ALObjects#HEALING_RECEIVED}
     */

    public static void heal() {
        LivingHealEvent.EVENT.register((entity, amount) -> {
            if (!(entity instanceof Player player)) return amount;
            float factor = (float) player.getAttributeValue(ALObjects.Attributes.HEALING_RECEIVED);
            return (amount * factor);
        });
    }

    /**
     * Handles {@link ALObjects.Attributes#ARROW_DAMAGE}
     * See AbstractArrowMixin
     */
    public static double modifyArrowDamage(AbstractArrow arrow, LivingEntity shooter, double baseDamage) {
        if (ZenithAttributesComponents.ARROW_DAMAGE_DONE.get(arrow).getValue()) return baseDamage;

        double arrowDamage = shooter.getAttributeValue(ALObjects.Attributes.ARROW_DAMAGE);
        if (!Double.isNaN(arrowDamage)) {
            baseDamage *= arrowDamage;
        }

        ZenithAttributesComponents.ARROW_DAMAGE_DONE.get(arrow).setValue(true);
        return baseDamage;
    }

    /**
     * Handles {@link ALObjects.Attributes#ARROW_VELOCITY}
     * See ProjectileMixin
     */
    public static void modifyArrowVelocity(Args args, AbstractArrow arrow, float velocity) {
        if (arrow.level().isClientSide) return;
        if (ZenithAttributesComponents.ARROW_VELOCITY_DONE.get(arrow).getValue()) return;
        if (arrow.getOwner() instanceof LivingEntity le) {
            double arrowVelocity = le.getAttributeValue(ALObjects.Attributes.ARROW_VELOCITY);
            if (!Double.isNaN(arrowVelocity)) {
                args.set(3, (float) (velocity * arrowVelocity));
            }
        }
        ZenithAttributesComponents.ARROW_VELOCITY_DONE.get(arrow).setValue(true);
    }

    /**
     * Copied from {link MeleeAttackGoal#getAttackReachSqr}
     */
    private static double getAttackReachSqr(Entity attacker, LivingEntity pAttackTarget) {
        return attacker.getBbWidth() * 2.0F * attacker.getBbWidth() * 2.0F + pAttackTarget.getBbWidth();
    }

    /**
     * Random used for dodge calculations.<br>
     * This random is seeded with the target entity's tick count before use.
     */
    private static Random dodgeRand = new Random();

    /**
     * Handles {link ALObjects#DODGE_CHANCE} for melee attacks.
     */
    public static void dodgeMelee() {
        LivingHurtEvent.EVENT.register((source, damaged, amount) -> {
            if (damaged.level().isClientSide) return amount;
            Entity attacker = source.getDirectEntity();
            if (attacker instanceof LivingEntity) {
                if (!(damaged.getAttributes().hasAttribute(ALObjects.Attributes.DODGE_CHANCE))) return amount;
                double dodgeChance = damaged.getAttributeValue(ALObjects.Attributes.DODGE_CHANCE);
                double atkRangeSqr = attacker instanceof Player p ? getReach(p) * getReach(p) : getAttackReachSqr(attacker, damaged);
                dodgeRand.setSeed(damaged.tickCount);
                if (attacker.distanceToSqr(damaged) <= atkRangeSqr && dodgeRand.nextFloat() <= dodgeChance) {
                    onDodge(damaged);
                    return 0f;
                }
            }
            return amount;
        });
    }

    private static double getReach(Player entity) {
        double range = entity.getAttributeValue(ReachEntityAttributes.ATTACK_RANGE);
        return range == 0 ? 0 : range + (entity.isCreative() ? 3 : 0);
    }
    /**
     * Handles {link ALObjects#DODGE_CHANCE} for projectiles.
     */

    public static void dodgeProjectile() {
        EntityEvents.PROJECTILE_IMPACT.register(event -> {
            Entity target = event.getRayTraceResult() instanceof EntityHitResult entRes ? entRes.getEntity() : null;
            if (target instanceof LivingEntity lvTarget) {
                double dodgeChance = lvTarget.getAttributeValue(ALObjects.Attributes.DODGE_CHANCE);
                // We can skip the distance check for projectiles, as "Projectile Impact" means the projectile is on the target.
                dodgeRand.setSeed(target.tickCount);
                if (dodgeRand.nextFloat() <= dodgeChance) {
                    onDodge(lvTarget);
                    event.setCanceled(true);
                }
            }
        });

    }

    private static void onDodge(LivingEntity target) {
        target.level().playSound(null, target, ALObjects.Sounds.DODGE, SoundSource.NEUTRAL, 1, 0.7F + target.getRandom().nextFloat() * 0.3F);
        if (target.level() instanceof ServerLevel sl) {
            double height = target.getBbHeight();
            double width = target.getBbWidth();
            sl.sendParticles(ParticleTypes.LARGE_SMOKE, target.getX() - width / 4, target.getY(), target.getZ() - width / 4, 6, -width / 4, height / 8, -width / 4, 0);
        }
    }

    /**
     * Adds a fake modifier to show Attack Range to weapons with Attack Damage and Elytra flight info to items that allow for it.
     */

    public static void affixModifiers() {
        ModifyItemAttributeModifiersCallback.EVENT.register((stack, slot, attributeModifiers) -> {
            boolean hasBaseAD = attributeModifiers.get(Attributes.ATTACK_DAMAGE).stream().filter(m -> ((IFormattableAttribute) Attributes.ATTACK_DAMAGE).getBaseUUID().equals(m.getId())).findAny().isPresent();
            if (hasBaseAD) {
                boolean hasBaseAR = false;
                if (attributeModifiers.containsKey(ReachEntityAttributes.REACH))
                    hasBaseAR = attributeModifiers.get(ReachEntityAttributes.REACH).stream().anyMatch(m -> Objects.equals(((IFormattableAttribute) ReachEntityAttributes.REACH).getBaseUUID(), m.getId()));
                if (!hasBaseAR) {
                    attributeModifiers.put(ReachEntityAttributes.REACH, new AttributeModifier(AttributeHelper.BASE_ENTITY_REACH, () -> "zenith_attributes:fake_base_range", 0, Operation.ADDITION));
                }
            }
            if (slot == EquipmentSlot.CHEST && (stack.getItem() instanceof FabricElytraItem || stack.getItem() instanceof ElytraItem) && !attributeModifiers.containsKey(ALObjects.Attributes.ELYTRA_FLIGHT)) {
                attributeModifiers.put(ALObjects.Attributes.ELYTRA_FLIGHT, new AttributeModifier(AttributeHelper.ELYTRA_FLIGHT_UUID, () -> "zenith_attributes:elytra_item_flight", 1, Operation.ADDITION));
            }
        });
    }

    public static void commands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("zenith_attributes");
            ModifierCommand.register(root);
            dispatcher.register(root);
        });
    }

    public static void reloads() {
        ReloadableServerEvent.addListeners(ALConfig.makeReloader());
    }
}
