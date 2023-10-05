package dev.shadowsoffire.attributeslib.packet;

import java.util.function.Supplier;

import dev.shadowsoffire.attributeslib.AttributesLib;
import dev.shadowsoffire.attributeslib.client.AttributesLibClient;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class CritParticleMessage {
    public static ResourceLocation ID = AttributesLib.loc("crit_particles");
    protected final int entityId;

    public CritParticleMessage(int entityId) {
        this.entityId = entityId;
    }

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(ID, (server, player, handler, buf, responseSender) -> {
            int id = buf.readInt();
            AttributesLibClient.apothCrit(id);
        });
    }

    public static void sendTo(int id) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeInt(id);
        ClientPlayNetworking.send(ID, buf);
    }

/* //TODO this
    public static class Provider implements MessageProvider<CritParticleMessage> {

        @Override
        public Class<CritParticleMessage> getMsgClass() {
            return CritParticleMessage.class;
        }

        @Override
        public void write(CritParticleMessage msg, FriendlyByteBuf buf) {
            buf.writeInt(msg.entityId);
        }

        @Override
        public CritParticleMessage read(FriendlyByteBuf buf) {
            return new CritParticleMessage(buf.readInt());
        }

        @Override
        public void handle(CritParticleMessage msg, Supplier<Context> ctx) {
            MessageHelper.handlePacket(() -> {
                AttributesLibClient.apothCrit(msg.entityId);
            }, ctx);
        }

    }*/

}
