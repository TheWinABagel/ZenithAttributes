package dev.shadowsoffire.attributeslib.impl;

import java.util.Objects;
import java.util.Random;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import de.dafuqs.additionalentityattributes.AdditionalEntityAttributes;
import de.dafuqs.additionalentityattributes.Support;
import dev.shadowsoffire.attributeslib.AttributesLib;
import dev.shadowsoffire.attributeslib.api.*;
import dev.shadowsoffire.attributeslib.packet.CritParticleMessage;
import dev.shadowsoffire.attributeslib.util.AttributesUtil;
import io.github.fabricators_of_create.porting_lib.entity.events.EntityEvents;
import io.github.fabricators_of_create.porting_lib.entity.events.LivingEntityEvents;
import io.github.fabricators_of_create.porting_lib.entity.events.PlayerEvents;
import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents;
import net.fabricmc.fabric.api.entity.event.v1.FabricElytraItem;
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
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.EntityHitResult;

public class AttributeEvents {
/*
    @SubscribeEvent
    public void fixChangedAttributes(PlayerLoggedInEvent e) {
        AttributeMap map = e.getEntity().getAttributes();
        map.getInstance(ForgeMod.STEP_HEIGHT_ADDITION).setBaseValue(0.6);
    }*/

    private static boolean canBenefitFromDrawSpeed(ItemStack stack) {
        return stack.getItem() instanceof ProjectileWeaponItem || stack.getItem() instanceof TridentItem;
    }

    public static void init(){
        elytra();
        drawSpeed();
        lifeStealOverheal();
        meleeDamageAttributes();
        apothCriticalStrike();
        breakSpd();
        heal();
        mobXp();
        dodgeMelee();
        dodgeProjectile();
        affixModifiers();
        //trackCooldown();
    }

    /**
     * This handles the elytra attribute.
     */
    public static void elytra() {
        EntityElytraEvents.CUSTOM.register((entity, tickElytra) -> {
            if (entity instanceof Player p){
                return p.getAttributeValue(ALObjects.Attributes.ELYTRA_FLIGHT) == 1;
            }
            return false;
        });

    }

    /**
     * This event handler is the implementation for {link ALObjects#DRAW_SPEED}.<br>
     * Each full point of draw speed provides an extra using tick per game tick.<br>
     * Each partial point of draw speed provides an extra using tick periodically.
     */
    public static void drawSpeed() {
        UseItemTickEvent.EVENT.register((entity, usingItem, useItemRemaining) -> {
            if (entity instanceof Player player) {
                double t = player.getAttribute(ALObjects.Attributes.DRAW_SPEED).getValue() - 1;
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
        });
    }

    /**
     * This event handler manages the Life Steal and Overheal attributes.
     */
    public static void lifeStealOverheal() {
        LivingEntityEvents.HURT.register((damageSource, entity, damageAmount) -> {
            if (damageSource.getDirectEntity() instanceof LivingEntity attacker && AttributesUtil.isPhysicalDamage(damageSource)) {
                float lifesteal = (float) attacker.getAttributeValue(ALObjects.Attributes.LIFE_STEAL);
                float dmg = Math.min(damageAmount, attacker.getHealth());
                if (lifesteal > 0.001) {
                    attacker.heal(dmg * lifesteal);
                }
                float overheal = (float) attacker.getAttributeValue(ALObjects.Attributes.OVERHEAL);
                float maxOverheal = attacker.getMaxHealth() * 0.5F;
                if (overheal > 0 && attacker.getAbsorptionAmount() < maxOverheal) {
                    attacker.setAbsorptionAmount(Math.min(maxOverheal, attacker.getAbsorptionAmount() + dmg * overheal));
                }
            }
            return damageAmount;
        });

    }

    /**
     * Recursion guard for {link #meleeDamageAttributes(LivingAttackEvent)}.<br>
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
        LivingEntityEvents.HURT.register((damageSource, damaged, damageAmount) -> {
            if (damaged.level().isClientSide) return damageAmount;
            if (noRecurse) return damageAmount;
            noRecurse = true;
            if (damageSource.getDirectEntity() instanceof LivingEntity attacker && AttributesUtil.isPhysicalDamage(damageSource)) {
                float hpDmg = (float) attacker.getAttributeValue(ALObjects.Attributes.CURRENT_HP_DAMAGE);
                float fireDmg = (float) attacker.getAttributeValue(ALObjects.Attributes.FIRE_DAMAGE);
                float coldDmg = (float) attacker.getAttributeValue(ALObjects.Attributes.COLD_DAMAGE);
                LivingEntity target = damaged;
                int time = target.invulnerableTime;
                target.invulnerableTime = 0;
                if (hpDmg > 0.001 && AttributesLib.localAtkStrength >= 0.85F) {
                    target.hurt(src(ALObjects.DamageTypes.CURRENT_HP_DAMAGE, attacker), AttributesLib.localAtkStrength * hpDmg * target.getHealth());
                }
                target.invulnerableTime = 0;
                if (fireDmg > 0.001 && AttributesLib.localAtkStrength >= 0.55F) {
                    target.hurt(src(ALObjects.DamageTypes.FIRE_DAMAGE, attacker), AttributesLib.localAtkStrength * fireDmg);
                    target.setRemainingFireTicks(target.getRemainingFireTicks() + (int) (10 * fireDmg));
                }
                target.invulnerableTime = 0;
                if (coldDmg > 0.001 && AttributesLib.localAtkStrength >= 0.55F) {
                    target.hurt(src(ALObjects.DamageTypes.COLD_DAMAGE, attacker), AttributesLib.localAtkStrength * coldDmg);
                    target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, (int) (15 * coldDmg), Mth.floor(coldDmg / 5)));
                }
                target.invulnerableTime = time;
            }
            noRecurse = false;
            return damageAmount;
        });

    }

    private static DamageSource src(ResourceKey<DamageType> type, LivingEntity entity) {
        return entity.level().damageSources().source(type, entity);
    }

    /**
     * Handles {link ALObjects#CRIT_CHANCE} and {link ALObjects#CRIT_DAMAGE}
     */

    public static void apothCriticalStrike() {
        LivingEntityEvents.HURT.register((damageSource, damaged, damageAmount) -> {
            LivingEntity attacker = damageSource.getEntity() instanceof LivingEntity le ? le : null;
            if (attacker == null) return damageAmount;

            double critChance = attacker.getAttributeValue(ALObjects.Attributes.CRIT_CHANCE);
            float critDmg = (float) attacker.getAttributeValue(AdditionalEntityAttributes.CRITICAL_BONUS_DAMAGE) + 1;

            RandomSource rand = damaged.getRandom();

            float critMult = 1.0F;

            // Roll for crits. Each overcrit reduces the effectiveness by 15%
            // We stop rolling when crit chance fails or the crit damage would reduce the total damage dealt.
            while (rand.nextFloat() <= critChance && critDmg > 1.0F) {
                critChance--;
                critMult *= critDmg;
                critDmg *= 0.85F;
            }

            damageAmount *= critMult;

            if (critMult > 1 && !attacker.level().isClientSide) {
                CritParticleMessage.sendTo(damaged.getId());
            }
            return damageAmount;
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

    public static void mobXp() {
        LivingEntityEvents.EXPERIENCE_DROP.register((i, attackingPlayer, entity) -> (int) (Support.getExperienceMod(attackingPlayer) * i));

    }

    /**
     * Handles {link ALObjects#HEALING_RECEIVED}
     */

    public static void heal() {
        HealEvent.EVENT.register((entity, amount) -> {
            if (!(entity instanceof Player player)) return amount;
            float factor = (float) player.getAttributeValue(ALObjects.Attributes.HEALING_RECEIVED);
            return (amount * factor);
        });

    }

    /**
     * Handles {link ALObjects#ARROW_DAMAGE} and {link ALObjects#ARROW_VELOCITY}
     */
 /*   public void arrow(EntityJoinLevelEvent e) { //TODO uh yeah this exists

        if (e.getEntity() instanceof AbstractArrow arrow) {
            if (arrow.level().isClientSide || arrow.getPersistentData().getBoolean("attributeslib.arrow.done")) return;
            if (arrow.getOwner() instanceof LivingEntity le) {
                arrow.setDeltaMovement(arrow.getDeltaMovement().scale(le.getAttributeValue(ALObjects.Attributes.ARROW_VELOCITY)));
            }
            arrow.getPersistentData().putBoolean("attributeslib.arrow.done", true);
        }
    }
*/
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
        LivingEntityEvents.HURT.register((damageSource, damaged, amount) -> {
            if (damaged.level().isClientSide) return amount;
            Entity attacker = damageSource.getDirectEntity();
            if (attacker instanceof LivingEntity) {
                if (!(damaged.getAttributes().hasAttribute(ALObjects.Attributes.DODGE_CHANCE))) return amount;
                double dodgeChance = damaged.getAttributeValue(ALObjects.Attributes.DODGE_CHANCE);
                double atkRangeSqr = attacker instanceof Player p ? getReach(p) * getReach(p) : getAttackReachSqr(attacker, damaged);
                dodgeRand.setSeed(damaged.tickCount);
                if (attacker.distanceToSqr(damaged) <= atkRangeSqr && dodgeRand.nextFloat() <= dodgeChance) {
                    onDodge(damaged);
                    return 0;
                }
            }
            return amount;
        });

    }

    private static double getReach(Player entity)
    {
        double range = entity.getAttributeValue(ReachEntityAttributes.ATTACK_RANGE);
        return range == 0 ? 0 : range + (entity.isCreative() ? 3 : 0);
    }
    /**
     * Handles {link ALObjects#DODGE_CHANCE} for projectiles.
     */

    public static void dodgeProjectile() {
        ProjectileImpactCallback.PROJECTILE_IMPACT.register((projectile, hitResult) -> {
            Entity target = hitResult instanceof EntityHitResult entRes ? entRes.getEntity() : null;
            if (target instanceof LivingEntity lvTarget) {
                double dodgeChance = lvTarget.getAttributeValue(ALObjects.Attributes.DODGE_CHANCE);
                // We can skip the distance check for projectiles, as "Projectile Impact" means the projectile is on the target.
                dodgeRand.setSeed(target.tickCount);
                if (dodgeRand.nextFloat() <= dodgeChance) {
                    onDodge(lvTarget);
                    return true;
                }
            }
            return false;
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
        ItemAttributeModifierEvent.GATHER_TOOLTIPS.register(e -> {
            boolean hasBaseAD = e.nonChangableModifiers.get(Attributes.ATTACK_DAMAGE).stream().filter(m -> ((IFormattableAttribute) Attributes.ATTACK_DAMAGE).getBaseUUID().equals(m.getId())).findAny().isPresent();
            if (hasBaseAD) {
                boolean hasBaseAR = false;
                if (e.nonChangableModifiers.containsKey(ReachEntityAttributes.REACH))
                    hasBaseAR = e.nonChangableModifiers.get(ReachEntityAttributes.REACH).stream().anyMatch(m -> Objects.equals(((IFormattableAttribute) ReachEntityAttributes.REACH).getBaseUUID(), m.getId()));
                if (!hasBaseAR) {
                    e.addModifier(ReachEntityAttributes.REACH, new AttributeModifier(AttributeHelper.BASE_ENTITY_REACH, () -> "attributeslib:fake_base_range", 0, Operation.ADDITION));
                }
            }
            if (e.slot == EquipmentSlot.CHEST && (e.stack.getItem() instanceof FabricElytraItem || e.stack.getItem() instanceof ElytraItem) && !e.nonChangableModifiers.containsKey(ALObjects.Attributes.ELYTRA_FLIGHT)) {
                e.addModifier(ALObjects.Attributes.ELYTRA_FLIGHT, new AttributeModifier(AttributeHelper.ELYTRA_FLIGHT_UUID, () -> "attributeslib:elytra_item_flight", 1, Operation.ADDITION));
            }
        });
    }

}
