package dev.shadowsoffire.attributeslib.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.HitResult;

public interface ProjectileImpactCallback {

    public static final Event<ProjectileImpact> PROJECTILE_IMPACT = EventFactory.createArrayBacked(ProjectileImpact.class, callbacks -> (proj, hit) -> {
        for (ProjectileImpact callback : callbacks) {
            if (callback.onImpact(proj, hit)) return true;
        }
        return false;
    });

    @FunctionalInterface
    public interface ProjectileImpact {
        /**
         * @return true to cancel vanilla hit logic
         */
        boolean onImpact(Projectile projectile, HitResult hitResult);
    }
}
