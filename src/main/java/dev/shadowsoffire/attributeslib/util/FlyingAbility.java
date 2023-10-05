package dev.shadowsoffire.attributeslib.util;

import dev.shadowsoffire.attributeslib.AttributesLib;
import dev.shadowsoffire.attributeslib.api.ALObjects;
import io.github.ladysnake.pal.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerPlayer;

public class FlyingAbility {

    public static final AbilitySource FLIGHT_ATTRIBUTE = Pal.getAbilitySource(AttributesLib.loc("flight_attribute"));
    public static void init() {
        ServerTickEvents.START_WORLD_TICK.register(world -> {
            for (ServerPlayer player : world.players()) {
                if (player.getAttributeValue(ALObjects.Attributes.CREATIVE_FLIGHT) == 1) {
                    FLIGHT_ATTRIBUTE.grantTo(player, VanillaAbilities.ALLOW_FLYING);
                } else {
                    FLIGHT_ATTRIBUTE.revokeFrom(player, VanillaAbilities.ALLOW_FLYING);
                }
            }
        });
    }

}
