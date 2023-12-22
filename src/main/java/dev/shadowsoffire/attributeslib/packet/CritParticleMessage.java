package dev.shadowsoffire.attributeslib.packet;

import dev.shadowsoffire.attributeslib.AttributesLib;
import dev.shadowsoffire.attributeslib.api.ALObjects;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class CritParticleMessage {
    public static ResourceLocation ID = AttributesLib.loc("crit_particles");
    protected final int entityId;

    public CritParticleMessage(int entityId) {
        this.entityId = entityId;
    }

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(ID, (server, player, handler, buf, responseSender) -> {
            int id = buf.readInt();
            apothCrit(id);
        });
    }

    public static void sendTo(int id, Player player) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeInt(id);
        player.displayClientMessage(Component.literal("critting"), false);
        ServerPlayNetworking.send((ServerPlayer) player, ID, buf);
    }

    public static void apothCrit(int entityId) {
        Entity entity = Minecraft.getInstance().level.getEntity(entityId);
        if (entity != null) {
            Minecraft.getInstance().particleEngine.createTrackingEmitter(entity, ALObjects.Particles.APOTH_CRIT);
        }
    }
}
